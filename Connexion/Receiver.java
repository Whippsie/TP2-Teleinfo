package Connexion;
import Test.Tests;
import Tools.*;
import java.net.*;
import java.io.*;

public class Receiver {
    private static String polyGen = "10001000000100001";
    private static int windows = 7;
    private static boolean once = true;


    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Utilisation: java % Receiver <Numero_Port>");
            System.exit(1);
        }
        try (
                ServerSocket serverSocket =
                        new ServerSocket(Integer.parseInt(args[0]));
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {

            String inputLine;
            boolean end = false;
            boolean wait = false;
            int trameNum = 1;

            //Classes utilitaires
            Conversion cs = new Conversion();
            CheckSum chk = new CheckSum();
            Utility util = new Utility();
            Tests tests = new Tests();
            BitStuff bitstuff = new BitStuff();

            //Tant que ce n'est pas la fin et qu'on reçoit quelque chose
            while (!end && (inputLine = in.readLine()) != null) {

                System.out.println("Server received: "+inputLine);

                //tests.bitShift(15,inputLine);


                // On traduit la trame en objet
                inputLine = bitstuff.bitstuffOut(inputLine);
                Trame trameReceived = util.readTrame(inputLine);

                //Si on a un problème avec les fanions
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
                /*
                //TEST BUFFER
                if (once) {
                    tests.bufferOut();
                    once = false;
                }
                */
                //Si une erreur dans la trame, on refuse directement
                if (!result) {
                    System.out.println("ERROR - CRC INVALID - Data was modified");
                    //On envoie un REJECT
                    toSend = util.makeTrame("R", cs.decimalToBinary(trameNum), donnee, polyGen);
                    toSend = bitstuff.bitstuffIn(toSend);
                    System.out.println("Sending to client: " + toSend);
                    space();
                    line();
                    tag = false;
                    out.println(toSend);
                //Si on attend une trame spécifique
                }else if (wait) {
                    if (type.charAt(0) != 'F'){
                        String temp = cs.decimalToBinary(trameNum);
                        temp = cs.completeByte(temp, 8);
                        //Si c'est la trame qu'on voulait
                        if (num.equals(temp)) {
                            wait = false;
                        } else {
                            //On doit reject jusqu'à avoir la bonne trame
                            System.out.println ("REFUSING everything until Trame "+trameNum + " is resent");
                            line();
                            tag = false;
                        }
                    }
                }
                if (tag){

                    //On verifie le type de la trame
                    switch (type.charAt(0)) {
                        case 'C':
                            //On a une demande de connexion
                            System.out.println("Connexion attempt received");
                            System.out.println("++ Conexion accepted ++ Sending to client... ");
                            toSend = util.makeTrame("A", num, "", polyGen);
                            toSend = bitstuff.bitstuffIn(toSend);
                            System.out.println("Sent: " + toSend);
                            out.println(toSend);
                            break;
                        case 'F':
                            //On a la fermeture de connexion
                            end = true;
                            System.out.println("Connexion closed");
                            break;
                        case 'P':
                            //On veut une réponse immédiate suite à un pbit
                            line();
                            System.out.println("Received Pbit, sending answer");
                            toSend = util.makeTrame("A", num, "", polyGen);
                            toSend = bitstuff.bitstuffIn(toSend);
                            System.out.println("Sent: " + toSend);
                            out.println(toSend);
                            break;
                        case 'I':
                            //On a un envoi d'information
                            String temp = cs.decimalToBinary(trameNum);
                            temp = cs.completeByte(temp,8);
                            //Si le numéro de la trame ne correspond pas au prochain que l'on attendait
                            if (!num.equals(temp)){
                                line();
                                //On rejette, on n'a pas reçu une trame
                                System.out.println("REJECT - DID NOT RECEIVE TRAME " + trameNum);
                                line();
                                toSend = util.makeTrame("R", cs.decimalToBinary(trameNum), donnee, polyGen);
                                toSend = bitstuff.bitstuffIn(toSend);
                                System.out.println("Sending to client: " + toSend);
                                wait = true;
                                out.println(toSend);
                            }else {
                                //On accepte la trame
                                System.out.println("Information received");
                                System.out.println("Sending to client... ");
                                //TEST BUFFER
                                /*if (once) {
                                    tests.bufferOut();
                                    once = false;
                                }
                                */
                                trameNum += 1;
                                wait = false;
                                //On envoie une confirmation
                                toSend = util.makeTrame("A", num, "", polyGen);
                                toSend = bitstuff.bitstuffIn(toSend);
                                System.out.println("Sent: " + toSend);
                                //On modifie le numéro de la trame selon la fenêtre
                                if (trameNum == windows+2){
                                    trameNum = ((trameNum-1) % windows);
                                }
                                out.println(toSend);
                            }
                            break;
                        default:
                            System.out.println("--None of the catch--");
                    }
                    space();
                    line();
                }
            }
        } catch (IOException e) {
            System.err.println("Buffer timed out.");
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
        sb.append(type).append(num).append(donnee).append(fullc);
        return sb.toString();
    }
}
