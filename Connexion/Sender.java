package Connexion;
import Test.Tests;
import Tools.*;
import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class Sender {
    private static Conversion convert = new Conversion();
    private static BitStuff bitstuff = new BitStuff();
    private static Utility util = new Utility();
    private static Tests test = new Tests();
    private static boolean once = true;
    private static String polyGen = "10001000000100001";
    private static int timeout = 0;
    //3 bits
    private static int windows = 7;


    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println(
                    "Tools.CheckSum: java Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String nomFichier = args[2];
        String protocole = args[3];

        if (protocole.equals(Integer.toString(0))){
            while (timeout<3) {
                int res = makeSocket(hostName, portNumber, protocole, nomFichier);
                if (res ==0){
                    timeout+=1;
                }
            }
        }else{
            System.out.println("Seul le protocole GO Back N (0) est accepté dans ce projet.");
        }
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
            if (timeout > 0){
                String toSend = util.makeTrame("P",convert.decimalToBinary(0),"",polyGen);
                toSend = bitstuff.bitstuffIn(toSend);
                out.println(toSend);
            }
            boolean connected = false;
            int countTrameI = 1;
            int countSent = 0;
            int countCurr = 0;
            int currWindow = 0;
            ArrayList <String> tableauData = readFile(nomFile);
            ArrayList <Paquet> currentData = getAllData(tableauData, 0);
            while (countSent < tableauData.size()){
                line();
                System.out.println("Trame confirmed:" + countSent);
                if (!connected){
                    //TEST 1 : TIME OUT
                    mySocket.setSoTimeout(3000);
                    System.out.println("++  Attempting to connect... ++");
                    space();
                    String toSend = util.makeTrame("C",protocole,"",polyGen);
                    toSend = bitstuff.bitstuffIn(toSend);
                    System.out.println("Sending to server: " + toSend);
                    line();
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
                        timeout=0;
                        space();
                        System.out.println("++  Connexion established!! ++");
                        space();

                        connected = true;

                    }
                }else{
                    String donnee = "";
                    String toSend = "";
                    while (currWindow < windows && countCurr < currentData.size()){
                        //Windows est de 0 à 7, mais les trames sont de 1 à 8
                        if (countTrameI == windows+2){
                            countTrameI = ((countTrameI-1) % windows);
                        }
                        System.out.println("SENDING information...");
                        //Envoi trame I
                        donnee = convertDonnee(currentData.get(countCurr).getTrame());
                        toSend = util.makeTrame("I",convert.decimalToBinary(countTrameI),donnee,polyGen);
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

                        if (once) {
                            //Switch dans le type
                            //toSend = test.bitShift(12, toSend);
                            //Switch dans le numéro
                            //toSend = test.bitShift(22,toSend);
                            //Switch dans les données
                            //toSend = test.bitShift(25,toSend);
                            once = false;
                        }

                        out.println(toSend);

                    }

                    //Fetching answer
                    String resp = in.readLine();
                    System.out.println("RECEIVED from server: " + resp);
                    timeout=0;
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
                            if (numTrame > countTrameI){
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
            space();
            line();
            System.out.println("Closing the connexion");
            String toSend = util.makeTrame("F",convert.decimalToBinary(0),"",polyGen);
            toSend = bitstuff.bitstuffIn(toSend);
            out.println(toSend);
            System.exit(0);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Buffer timed out; Server " +
                    hostName + " not responding. We need an answer, sending Pbit..");
            return 0;
        }
        return 1;
    }

    /*Parcourt la liste et va chercher la première trame qui a le même numéro de trame et qui n’a pas été confirmée encore. */
    private static Paquet findPaquet(ArrayList<Paquet> p, int num){
        for (int i=0;i<p.size();i++){
            if (p.get(i).getNum()==num && !p.get(i).getDead()){
                return p.get(i);
            }
        }
        return null;
    }

    /*Pour chaque entrée dans la liste de données, crée un objet Paquet.*/
    private static ArrayList<Paquet> getAllData(ArrayList<String> tab, int debut){
        int taille = tab.size();
        ArrayList<Paquet> s = new ArrayList<>();
        for (int i = debut; i<taille;i++){
            s.add(new Paquet(tab.get(i),(i%8)+1,i));
        }
        return s;
    }

    /*Prend le string dans le fichier texte et le convertit en String binaire.*/
    private static String convertDonnee(String donnee){
        byte[] tableau = convert.toBinaryFromString(donnee);
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


    //Code modifié de https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
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
