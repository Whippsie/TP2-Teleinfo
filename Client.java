import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Timer;
import java.io.*;
import java.net.*;
import java.util.TimerTask;

public class Client {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "CheckSum: java Client <host name> <port number>");
            System.exit(1);
        }
        //First command line argument
        String hostName = args[0];
        //Second, must be full IP
        int portNumber = Integer.parseInt(args[1]);

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
            String test = "allo bonjour ca va bien?";
            String polyGen = "10001000000100001";
            CheckSum chck = new CheckSum();
            Conversion convert = new Conversion();
            BitStuff bitstuff = new BitStuff();
            int testoo = 0;
            while (testoo<3){
                if (!connected){
                    // Demande connexion, création trame C
                    String typeC = convert.typeToBinary("C");
                    String num = convert.completeByte(Integer.toString(0),8);
                    String donnee = "";
                    String checkValue = chck.applyCheckSum(typeC+num+donnee,polyGen);

                    //Trame de connexion
                    Trame trameConnexion = new Trame(typeC, num, donnee, checkValue);
                    trameConnexion.print();
                    String toSend = trameConnexion.makeTrame();
                    toSend = bitstuff.bitstuffIn(toSend);
                    System.out.println("Sending to server:" + toSend);
                    out.println(toSend);
                    String response = in.readLine();

                    System.out.println("Received from server: " + in.readLine());



                }else{
                    //Envoi des paquets I
                }
                testoo += 1;


            }

            //Envoi fin de la connexion
            //Trame F
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
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
    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
