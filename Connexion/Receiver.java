package Connexion;

import Test.Tests;
import Tools.*;

import java.net.*;
import java.io.*;

//C'est quoi p-bit? p après disconnect pour avoir réponse
//SI pas reponse apres 3 sec envoie pbit
//Temporisateur : si reçoit un reject, recommence le 3 secondes
//On suppose 3 essais de pbit et ensuite on finit la connexion

// Dans Go back N, est-ce qu'on est obligé d'avoir un RR à la fin pour mettre fin à la connexion? disconnect
// Est-ce qu'un REJ agit comme un AA puisque si le receveur rejette C car il attend B, c'est que nécessairement il a eu A, sinon aurait rejeté B --> OUI


//http://www.oracle.com/technetwork/java/socket-140484.html
public class Receiver {
    private static String polyGen = "10001000000100001";
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Utilisation: java % Receiver <Numero_Port>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (
                ServerSocket serverSocket =
                        new ServerSocket(Integer.parseInt(args[0]));
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {

            BitStuff bitstuff = new BitStuff();
            String inputLine;
            boolean end = false;
            boolean wait = false;
            int trameNum = 1;

            //Classes utilitaires
            Conversion cs = new Conversion();
            CheckSum chk = new CheckSum();
            Utility util = new Utility();
            Tests tests = new Tests();
            while (!end && (inputLine = in.readLine()) != null) {


                System.out.println("Server received: "+inputLine);
                //tests.bitShift(15,inputLine);
                // On traduit la trame en objet
                inputLine = bitstuff.bitstuffOut(inputLine);
                Trame trameReceived = util.readTrame(inputLine);
                if (trameReceived == null){
                    break;
                }
                trameReceived.print();

                // On commence par aller chercher les paramètres de la trame
                String type = trameReceived.getType();
                String num = trameReceived.getNum();
                String donnee = trameReceived.getDonnee();
                String fullCRC = trameReceived.getCRC();
                fullCRC = fillCRC(fullCRC,type,trameReceived.getNum(),donnee);
                // On vérifie le CRC
                boolean result = chk.verifyCheckSum(polyGen, fullCRC);

                //Type vaut le char correspondant
                type = cs.binaryToType(type);

                String toSend = "";
                space();
                boolean tag = true;
                //tests.bufferOut();

                //Si une erreur dans la trame, on refuse directement
                if (!result) {
                    System.out.println("ERROR - CRC INVALID - Data was modified");
                    toSend = util.makeTrame("R", num, donnee, polyGen);
                    toSend = bitstuff.bitstuffIn(toSend);
                    System.out.println("Sending to client: " + toSend);
                    space();
                    line();
                    tag = false;
                    out.println(toSend);
                }else if (wait) {
                    String temp = cs.decimalToBinary(trameNum);
                    temp = cs.completeByte(temp, 8);
                    if (num.equals(temp)) {
                        wait = false;
                    } else {
                        System.out.println ("REFUSING everything until Trame "+trameNum + " is resent");
                        line();
                        tag = false;
                    }
                }
                if (tag){

                    //On verifie le type de la trame
                    switch (type.charAt(0)) {
                        case 'C':
                            System.out.println("Connexion attempt received");
                            System.out.println("Sending to client... ");
                            toSend = util.makeTrame("A", num, "", polyGen);
                            toSend = bitstuff.bitstuffIn(toSend);
                            System.out.println("Sent: " + toSend);
                            out.println(toSend);
                            break;
                        case 'F':
                            end = true;
                            System.out.println("Connexion closed");
                            break;
                        case 'P':
                            //do stuff
                            break;
                        case 'I':
                            String temp = cs.decimalToBinary(trameNum);
                            temp = cs.completeByte(temp,8);
                            if (!num.equals(temp)){
                                line();
                                System.out.println("REJECT - DID NOT RECEIVE TRAME " + trameNum);
                                line();
                                toSend = util.makeTrame("R", cs.decimalToBinary(trameNum), donnee, polyGen);
                                toSend = bitstuff.bitstuffIn(toSend);
                                System.out.println("Sending to client: " + toSend);
                                wait = true;
                                out.println(toSend);
                            }else {
                                System.out.println("Information received");
                                //Tools.Trame sans erreur
                                System.out.println("Sending to client... ");
                                trameNum += 1;
                                wait = false;
                                toSend = util.makeTrame("A", num, "", polyGen);
                                toSend = bitstuff.bitstuffIn(toSend);
                                System.out.println("Sent: " + toSend);
                                out.println(toSend);
                            }
                            break;
                        default:
                            System.out.println("--None of the catch--");
                    }
                    space();
                    line();
                }


                //out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    private static void space(){
        System.out.println(" ");
    }

    private static void line(){
        System.out.println(" --------------------------------------- ");
    }

    /* Concatène les données binaires du CRC */
    private static String fillCRC(String fullc, String type, String num, String donnee){
        StringBuilder sb = new StringBuilder();
        /*
        for (int i =0 ; i < type.length();i++){
            sb.append(type.charAt(i));
        }

        for (int i =0 ; i < num.length();i++){
            sb.append(num.charAt(i));
        }*/
        sb.append(type).append(num).append(donnee).append(fullc);
        //sb.append(donnee);
        //sb.append(fullc);

        return sb.toString();
    }
}
