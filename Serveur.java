import java.net.*;
import java.io.*;
import java.util.zip.Checksum;

//Demande connexion, choix Go Back N?

//Bit stuffing = 01111110

//Numéro trame 3 bit = peut avoir 8 numéros en tout? Donc taille sending windows = 7? 2^n-1
//Quand on dit que la demander de connexion choisit go back N, on veut dire quoi exactement?
/*On dit que la durée du temporisateur utilisé pour la perte des trames est de 3 secondes.
//Si le temporisateur reçoit quelque chose il restart automatiquement à 0? Même c'est un reject?
C'est quoi p-bit? p après disconnect pour avoir réponse
# SI pas reponse apres 3 sec envoie pbit
//Temporisateur : si reçoit un reject, recommence le 3 secondes
//On suppose 3 essais de pbit et ensuite on finit la connexion

// Dans Go back N, est-ce qu'on est obligé d'avoir un RR à la fin pour mettre fin à la connexion? disconnect
// Est-ce qu'un REJ agit comme un AA puisque si le receveur rejette C car il attend B, c'est que nécessairement il a eu A, sinon aurait rejeté B --> OUI
*/

//http://www.oracle.com/technetwork/java/socket-140484.html
public class Serveur {
    private static String polyGen = "10001000000100001";
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("CheckSum: java Serveur <port number>");
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
            //DO STUFF
            BitStuff bitstuff = new BitStuff();
            String inputLine;
            boolean end = false;
            while (!end && (inputLine = in.readLine()) != null) {

                //Classes utilitaires
                Conversion cs = new Conversion();
                CheckSum chk = new CheckSum();
                Utility util = new Utility();


                System.out.println("Server received: "+inputLine);
                inputLine = bitstuff.bitstuffOut(inputLine);
                Trame trameReceived = bitstuff.readTrame(inputLine);
                trameReceived.print();
                //On commence par aller chercher le type de la trame
                String type = trameReceived.getType();
                //Ensuite son numero
                String num = trameReceived.getNum();

                //Son CRC
                String fullCRC = trameReceived.getCRC();
                System.out.println("BEFORE"+fullCRC);
                fullCRC = fillCRC(fullCRC,type,trameReceived.getNum());
                boolean result = chk.verifyCheckSum(polyGen, fullCRC);
                System.out.println("FULLCRC"+fullCRC);

                //Type vaut le char correspondant
                type = cs.binaryToType(type);
                //Ses données
                String donnee = trameReceived.getDonnee();
                String toSend;
                //On verifie le type de la trame
                switch (type.charAt(0)) {
                    case 'C':
                        toSend = util.makeTrame("A", num, "", polyGen);
                        toSend = bitstuff.bitstuffIn(toSend);
                        out.println(toSend);
                        break;
                    case 'F':
                        end = true;
                        break;
                    case 'P':
                        //do stuff
                        break;
                    case 'I':
                        //Trame sans erreur
                        if (result) {
                            toSend = util.makeTrame("A", num, "", polyGen);
                            toSend = bitstuff.bitstuffIn(toSend);
                            out.println(toSend);
                        } else {
                            toSend = util.makeTrame("R", num, donnee, polyGen);
                            toSend = bitstuff.bitstuffIn(toSend);
                            out.println(toSend);
                        }
                        break;
                    default:
                        System.out.println("None of the catch");
                }


                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private static String fillCRC(String fullc, String type, String num){
        StringBuilder sb = new StringBuilder();
        for (int i =0 ; i < type.length();i++){
            sb.append(type.charAt(i));
        }

        for (int i =0 ; i < num.length();i++){
            sb.append(num.charAt(i));
        }
        sb.append(fullc);

        return sb.toString();
    }
}
