import javax.rmi.CORBA.Util;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.io.*;
import java.net.*;
import java.util.TimerTask;

//http://www.ee.unb.ca/cgi-bin/tervo/calc.pl?num=010010010000000111000011101100110110011011110111001110010000&den=10001000000100001&f=d&e=1&m=1
public class Sender {
    private static CheckSum chck = new CheckSum();
    private static Conversion convert = new Conversion();
    private static BitStuff bitstuff = new BitStuff();
    private static Utility util = new Utility();
    //3 bits, start at 1
    private static int windows = 8;
    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println(
                    "CheckSum: java Sender <Nom_Machine> <Numero_Port>" +
                            "Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
            System.exit(1);
        }
        //First command line argument
        String hostName = args[0];
        //Second, must be full IP
        int portNumber = Integer.parseInt(args[1]);
        String nomFichier = args[2];
        String protocole = args[3];

        int attempt = 0;
        while (attempt < 3){
          int res = makeSocket(hostName,portNumber, protocole, nomFichier);
          if (res ==0){
              attempt +=1;
          }
        }
        System.exit(1);
    }

    private static int makeSocket(String hostName, int portNumber, String protocole, String nomFile){
        try (
                //Name of computer, port to connect
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            //DO STUFF HERE
                /*while ((userInput = stdIn.readLine()) != null) {

                    out.println(userInput);
                    System.out.println("echo: " + in.readLine());
                }*/

            boolean connected = false;
                            /*
                String myFile = readFile("test.txt");
                System.out.println(myFile);
                */
            String polyGen = "10001000000100001";
            boolean wait = false;
            boolean end = false;
            int countTrameI = 1;
            int countSent = 0;
            int countCurr = 0;
            ArrayList <String> tableauData = readFile(nomFile);
            while (!end){
                if (!connected){
                    echoSocket.setSoTimeout(5000);
                    System.out.println("++  Attempting to connect... ++");
                    space();
                    String toSend = util.makeTrame("C",protocole,"",polyGen);
                    toSend = bitstuff.bitstuffIn(toSend);
                    System.out.println("Sending to server: " + toSend);
                    out.println(toSend);
                    String response = in.readLine();
                    space();
                    line();
                    System.out.println("Received from server: " + response);
                    response = bitstuff.bitstuffOut(response);
                    Trame trameReceived = bitstuff.readTrame(response);
                    trameReceived.print();
                    if (convert.binaryToType(trameReceived.getType()).charAt(0) == 'A' && Integer.parseInt(trameReceived.getNum(),2) == 0){
                        //On a reçu une confirmation pour l'ouverture de session

                        space();
                        System.out.println("++  Connexion established!! ++");
                        space();

                        connected = true;

                    }
                }else{
                    System.out.println("Sending information...");


                    //Reprendre le message
                    //String donnee = new String(tableau, "UTF-8");
                    String donnee = "";
                    String toSend = "";
                    while (countSent != tableauData.size()){
                        //Envoi trame I
                        //System.out.println("sent:" + countSent + " | curr:" + countCurr);
                        donnee = convertDonnee(tableauData.get(countCurr));
                        if (countTrameI==windows){
                            countTrameI=1;
                        }
                        toSend = util.makeTrame("I",util.toBinaryFromInt(countTrameI),donnee,polyGen);
                        countCurr +=1;
                        countTrameI +=1;
                        toSend = bitstuff.bitstuffIn(toSend);
                        System.out.println("Sending to server:" + toSend);
                        space();
                        line();
                        out.println(toSend);
                        echoSocket.setSoTimeout(3000);

                        //Fetching answer
                        String resp = in.readLine();
                        System.out.println("Received from server: " + resp);
                        resp = bitstuff.bitstuffOut(resp);
                        Trame trameReceived = bitstuff.readTrame(resp);
                        trameReceived.print();
                        int numTrame = convert.binaryStringToDecimal(trameReceived.getNum());
                        //Accept of I trame
                        if (convert.binaryToType(trameReceived.getType()).equals("A") && numTrame!=0){
                            countSent +=1;
                            System.out.println("ACK " + Integer.parseInt(trameReceived.getNum()));
                        }else if (convert.binaryToType(trameReceived.getType()).equals("R")){
                            //aussi ack se répète si resend
                            countTrameI = numTrame;
                            countCurr = countCurr - countTrameI;
                            //2 EXEMPLES OU ÇA MARCHE PAS
                        }
                    }

                    //TODO: REMOVE
                    end = true;

                }

            }

            //Envoi fin de la connexion
            //Trame F
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Buffer timed out; Server " +
                    hostName + " not responding.");
            return 0;
        }
        return 1;
    }

    private static String convertDonnee(String donnee){
        byte[] tableau = util.toBinaryFromString(donnee);
        StringBuilder sbDonnee = new StringBuilder();
        for (int i =0; i<tableau.length;i++){
            String too=Byte.toString(tableau[i]);
            sbDonnee.append(convert.decimalToBinary(Integer.parseInt(too)));
        }
        return sbDonnee.toString();
    }
    private static void space(){
        System.out.println(" ");
    }

    private static void line(){
        System.out.println(" --------------------------------------- ");
    }

    public void sendToReceiver(){
        /*ExecutorService service = Executors.newSingleThreadExecutor();

try {
    Runnable r = new Runnable() {
        @Override
        public void run() {
            // Database task
        }
    };

    Future<?> f = service.submit(r);

    f.get(2, TimeUnit.MINUTES);     // attempt the task for two minutes
}
catch (final InterruptedException e) {
    // The thread was interrupted during sleep, wait or join
}
catch (final TimeoutException e) {
    // Took too long!
}
catch (final ExecutionException e) {
    // An exception from within the Runnable task
}
finally {
    service.shutdown();
}*/
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Your database code here
            }
        }, 2*60*1000);
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < 2*60*1000) {
            elapsedTime = (new Date()).getTime() - startTime;
        }
    }


    //https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
    private static ArrayList<String> readFile(String name) throws IOException {
        ArrayList<String> tableau = new ArrayList<>();
        try {
            File file = new File(name);
            String path = file.getAbsolutePath();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

            String line;

            while ((line = br.readLine()) != null) {
                tableau.add(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableau;
    }


}
