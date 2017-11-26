/**
 * Created by whip on 17-11-25.
 */
public class testcheksum {
    //Real poly : 10001000000100001
    private static String polyGen = "10011";
    private static String backupPoly = "";
    public static void main(String [ ] args){
        backupPoly = polyGen;
        applyCheckSum("10110011");
        polyGen = "11";
        backupPoly = polyGen;
        applyCheckSum("10011");

    }
    public static void applyCheckSum(String data){
        StringBuilder sb = new StringBuilder();
        int compteur = 0;
        String temp = "";
        String temp2 = "";
        StringBuilder resultat = new StringBuilder();
        String dataInit = data;
        //Gen le degré du checksum
        for (int i = 0 ; i < polyGen.length(); i++){
            if (i!=0){
                sb.append('0');
            }
            temp += (data.charAt(i));
        }
        data += sb.toString();
        System.out.println("data:" + data);
        System.out.println("polynome:"+polyGen);
        while (compteur <= (data.length() - polyGen.length())) {

            //Cas de base avec zero
            polyGen = backupPoly;
            temp2 = "";
            if (temp.charAt(0) == '0'){
                resultat.append("0");
                polyGen = genPolyZeros (polyGen);
            } else if (temp.charAt(0) == '1' && polyGen.charAt(0) == '1'){
                resultat.append("1");
            }else{
                System.out.println("cas particulier");
            }

            for (int i = 1; i < polyGen.length(); i++) {
                if (temp.charAt(i) == polyGen.charAt(i)) {
                    temp2 += "0";
                } else {
                    temp2 += ("1");
                }
            }
            if (data.length() - polyGen.length() != compteur){
                temp2 += (data.charAt(compteur+ polyGen.length()));
            }
            compteur += 1;
            temp = "";
            for (int j = 0;j < temp2.length(); j++) {
                temp += temp2.charAt(j);
            }
        }
        System.out.println("Res:" + resultat);
        System.out.println("Reste :" + temp);
        String tX = genTx(dataInit,temp);
        System.out.println("T(x) = " + tX);
    }

    private static String genTx(String mX, String rX){
        for (int i =0; i< rX.length();i++){
            mX += rX.charAt(i);
        }
        return mX;
    }
    private static void print(String p){
        System.out.println(p);
    }
    private static void printI(int p){
        System.out.println(Integer.toString(p));
    }
    private static String genPolyZeros(String polyGen){
        StringBuilder sb = new StringBuilder();
        for (int i =0; i<polyGen.length(); i++){
            sb.append("0");
        }
        return sb.toString();
    }
}
