package Connexion;

import Test.Tests;
import Tools.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.io.*;
import java.net.*;
import java.util.TimerTask;

import static java.lang.Math.max;
import static java.lang.Math.min;

//http://www.ee.unb.ca/cgi-bin/tervo/calc.pl?num=010010010000000111000011101100110110011011110111001110010000&den=10001000000100001&f=d&e=1&m=1
public class Sender {
    private static CheckSum chck = new CheckSum();
    private static Conversion convert = new Conversion();
    private static BitStuff bitstuff = new BitStuff();
    private static Utility util = new Utility();
    private static Tests test = new Tests();
    private static boolean once = true;
    //3 bits, start at 1
    private static int windows = 7;
    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println(
                    "Tools.CheckSum: java Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
            System.exit(1);
        }
        //First command line argument
        String hostName = args[0];
        //Second, must be full IP
        int portNumber = Integer.parseInt(args[1]);
        String nomFichier = args[2];
        String protocole = args[3];

        if (protocole.equals(Integer.toString(0))){
            int res = makeSocket(hostName,portNumber, protocole, nomFichier);
        }else{
            System.out.println("Seul le protocole GO Back N (0) est accepté dans ce projet.");
        }
        //int attempt = 0;
        //while (attempt < 3){

          //if (res ==0){
            //  attempt +=1;
          //}
        //}
        //System.exit(1);
    }
    private static class Paquet{
        String trame;
        int num;
        boolean dead;
        int indexTableau;

        public Paquet(String tr, int nu, int indexTableau){
            this.num = nu;
            this.trame = tr;
            this.dead = false;
            this.indexTableau = indexTableau;
        }
        public String getTrame(){
            return trame;
        }
        public int getNum(){
            return num;
        }
        public boolean getDead(){
            return this.dead;
        }
        public void setDead(boolean b){
            this.dead = b;
        }
        public int getIndexTableau(){
            return this.indexTableau;
        }
    }
    private static int makeSocket(String hostName, int portNumber, String protocole, String nomFile){

        try (
                //Name of computer, port to connect
                Socket mySocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(mySocket.getInputStream()));
        ) {
            boolean connected = false;
            boolean mytest = true;
            String polyGen = "10001000000100001";
            boolean wait = false;
            boolean end = false;
            int countTrameI = 1;
            int countSent = 0;
            int countCurr = 0;
            int currWindow = 0;
            ArrayList <String> tableauData = readFile(nomFile);
            ArrayList <Paquet> currentData = getAllData(tableauData, 0);
            while (countSent < tableauData.size()){
            //while (trameAlive(currentData)){
                System.out.println("sent:" + countSent);
                if (!connected){
                    //TEST 1 : TIME OUT
                    mySocket.setSoTimeout(3000);
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
                    Trame trameReceived = util.readTrame(response);
                    if (trameReceived==null){
                        break;
                    }
                    trameReceived.print();
                    if (convert.binaryToType(trameReceived.getType()).charAt(0) == 'A' && Integer.parseInt(trameReceived.getNum(),2) == 0){
                        //On a reçu une confirmation pour l'ouverture de session

                        space();
                        System.out.println("++  Connexion established!! ++");
                        space();

                        connected = true;

                    }
                }else{
                    //Reprendre le message
                    //String donnee = new String(tableau, "UTF-8");
                    String donnee = "";
                    String toSend = "";
                    //while (countTrameI != windows && countSent != tableauData.size()){
                    while (currWindow < windows && countCurr < currentData.size()){
                        System.out.println("curr tableau" + countCurr);
                        if (countTrameI == windows+1){
                            countTrameI = (countTrameI % windows);
                        }
                        System.out.println("SENDING information...");
                        //Envoi trame I
                        //System.out.println("sent:" + countSent + " | curr:" + countCurr);
                        donnee = convertDonnee(currentData.get(countCurr).getTrame());
                        System.out.println("count trame i" + countTrameI);
                        toSend = util.makeTrame("I",util.toBinaryFromInt(countTrameI),donnee,polyGen);
                        countCurr +=1;
                        countTrameI +=1;
                        currWindow +=1;
                        System.out.println("++ Window left : " + Integer.toString(windows - currWindow) + " ++");
                        toSend = bitstuff.bitstuffIn(toSend);
                        System.out.println("Sending to server:" + toSend);
                        space();
                        line();
                        //TEST 2 : REJECT
                        /*
                        if (!test.destroyTrame(countTrameI)) {
                            out.println(toSend);
                        }*/
                        /*if (!test.destroyTrames(countTrameI,2)) {
                            out.println(toSend);
                        }*/
                        //TEST 3 : Switch de bits
                        //Switch dans le type
                        if (once) {
                            toSend = test.bitShift(12, toSend);
                            once = false;
                        }
                        //Switch dans le numéro
                        //toSend = test.bitShift(18,toSend);
                        //Switch dans les données
                        //toSend = test.bitShift(25,toSend);
                        out.println(toSend);



                    }

                    //Fetching answer
                    String resp = in.readLine();
                    System.out.println("RECEIVED from server: " + resp);
                    resp = bitstuff.bitstuffOut(resp);
                    Trame trameReceived = util.readTrame(resp);
                    if (trameReceived == null){
                        break;
                    }
                    trameReceived.print();
                    int numTrame = convert.binaryStringToDecimal(trameReceived.getNum());
                    //Accept of I trame
                    if (convert.binaryToType(trameReceived.getType()).equals("A") && numTrame!=0){
                        countSent +=1;
                        System.out.println("ACK " + convert.binaryStringToDecimal(trameReceived.getNum()) + " - " + trameReceived.getNum());
                        currWindow -=1;
                        currentData.get(numTrame-1).setDead(true);
                        System.out.println(" ++ Added one window ++ Total : " + Integer.toString(windows - currWindow));
                        space();
                    }else if (convert.binaryToType(trameReceived.getType()).equals("R")){
                        if (trameReceived.getNum().equals(convert.completeByte("0",8))){

                            //On a un reject sur une demande de connexion
                            space();
                            System.out.println("R - 0 : Server connexion refused");
                            space();
                        }else{
                            //On a un rejet de trame
                            //currWindow -=1;
                            if (numTrame > countTrameI){
                                //TODO: Verifier si ok ici
                                int diff = numTrame - countTrameI;
                                currWindow = windows - diff;
                            }else{
                                currWindow = countTrameI - numTrame;
                                currWindow = windows - currWindow;
                            }
                            System.out.println(" ++ Added window ++ Total : " + Integer.toString(windows - currWindow));
                            space();
                            System.out.println("RJ - " + numTrame + " - Server REJECT - Asking for GO BACK N");
                            line();
                            Paquet p = findPaquet(currentData,numTrame);

                            countTrameI = numTrame;
                            if (p != null){
                                countCurr = p.getIndexTableau();
                            }else{
                                System.out.println(" Unknown trame");
                            }
                        }

                    }

                }

            }

            //Envoi fin de la connexion
            //Tools.Trame F
            //TODO: Print pas la trame?
            space();
            line();
            System.out.println("Closing the connexion");
            String toSend = util.makeTrame("F",util.toBinaryFromInt(0),"",polyGen);
            toSend = bitstuff.bitstuffIn(toSend);
            out.println(toSend);
            System.exit(0);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Buffer timed out; Server " +
                    hostName + " not responding =(.");
            return 0;
        }
        return 1;
    }

    private static boolean trameAlive(ArrayList<Paquet> p){
        for (int i=0; i<p.size();i++){
            if (!p.get(i).getDead()){
                return true;
            }
        }
        return false;
    }
    private static Paquet findPaquet(ArrayList<Paquet> p, int num){
        for (int i=0;i<p.size();i++){
            if (p.get(i).getNum()==num && !p.get(i).getDead()){
                return p.get(i);
            }
        }
        return null;
    }

    private static ArrayList<Paquet> getAllData(ArrayList<String> tab, int debut){
        int taille = tab.size();
        ArrayList<Paquet> s = new ArrayList<>();
        for (int i = debut; i<taille;i++){
            s.add(new Paquet(tab.get(i),(i%8)+1,i));
        }
        return s;
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
            //TODO: REMOVE
            path = "/home/whip/TP2_Téléinfo/src/Test/test.txt";
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
