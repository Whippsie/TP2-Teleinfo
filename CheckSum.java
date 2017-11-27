public class CheckSum {
    Conversion cs = new Conversion();
/*
    final class Builders {
        private StringBuilder zeros = new StringBuilder();
        private StringBuilder current = new StringBuilder();

        public Builders(StringBuilder t1, StringBuilder t2) {
            this.zeros = t1;
            this.current = t2;
        }

        public StringBuilder getGenZero() {
            return zeros;
        }

        public StringBuilder getCurrent() {
            return current;
        }
    }
    */
    private StringBuilder genCheckSumZeroes(String polyGen){
        StringBuilder sb = new StringBuilder();
        for (int i = 1 ; i < polyGen.length(); i++){
            sb.append('0');
        }
        return sb;
    }

    private StringBuilder genCheckSumStart(String polyGen, String data){
        StringBuilder curr = new StringBuilder();
        for (int i = 0 ; i < polyGen.length(); i++){
            curr.append(data.charAt(i));
        }
        return curr;
    }

    public String applyCheckSum(String data, String polyGen){
        //Garde la valeur initiale
        String dataInit = data;

        //Gen le degré du checksum
        //Builders result = genCheckSumDegree(polyGen,data);
        data += genCheckSumZeroes(polyGen).toString();

        StringBuilder curr = makeDivision(polyGen,data);

        String tX = genTx(dataInit,curr.toString());
        //System.out.println("T(x) = " + tX);

        return curr.toString();
    }

    private StringBuilder makeDivision(String polyGen, String data){
        int compteur = 0;
        String polyInit = polyGen;

        StringBuilder curr = genCheckSumStart(polyGen,data);
        StringBuilder next = new StringBuilder();
        StringBuilder resultat = new StringBuilder();

        //System.out.println("data:" + data);
        //System.out.println("polynome:"+polyGen);

        while (compteur <= (data.length() - polyGen.length())) {

            //Cas de base avec zero
            polyGen = polyInit;
            next.setLength(0);
            if (curr.charAt(0) == '0'){
                resultat.append("0");
                polyGen = genPolyZeros (polyGen);
            } else if (curr.charAt(0) == '1' && polyGen.charAt(0) == '1'){
                resultat.append("1");
            }else{
                System.out.println("cas particulier puant");
            }

            for (int i = 1; i < polyGen.length(); i++) {
                if (curr.charAt(i) == polyGen.charAt(i)) {
                    next.append('0');
                } else {
                    next.append('1');
                }
            }
            if (data.length() - polyGen.length() != compteur){
                next.append((data.charAt(compteur+ polyGen.length())));
            }
            compteur += 1;
            curr.setLength(0);
            for (int j = 0;j < next.length(); j++) {
                curr.append(next.charAt(j));
            }
        }
        //System.out.println("Res:" + resultat.toString());
        //System.out.println("Reste :" + curr.toString());
        return curr;
    }

    private static String genTx(String mX, String rX){
        for (int i =0; i< rX.length();i++){
            mX += rX.charAt(i);
        }
        return mX;
    }

    private static String genPolyZeros(String polyGen){
        StringBuilder sb = new StringBuilder();
        for (int i =0; i<polyGen.length(); i++){
            sb.append("0");
        }
        return sb.toString();
    }

    public boolean verifyCheckSum(String polyGen,String data){
        StringBuilder curr = makeDivision(polyGen,data);
        System.out.println(curr.toString());
        if (cs.binaryStringToDecimal(curr.toString())!= 0){
            return false;
        }
        return true;
    }
}
