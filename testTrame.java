import java.nio.charset.StandardCharsets;

public class testTrame {
    public static void main(String [ ] args){
        System.out.println(binaryToType("00100010"));
        String typeC = typeToBinary("C");
        System.out.println(completeByte(Integer.toBinaryString(8),8));

        //Trame de connexion
        Trame trameConnexion = new Trame(typeC, completeByte(Integer.toString(0),8), "", "101100110100");
        trameConnexion.print();

        String typeI = typeToBinary("I");
        //Trame de connexion
        Trame trameInfo = new Trame(typeI, completeByte(Integer.toString(1),8), "00100101", "101100110100");
        trameInfo.print();

    }
    private static String binaryToType (String charac){
        String str = "";
        for (int i = 0; i < charac.length()/8; i++) {
            int a = Integer.parseInt(charac.substring(8*i,(i+1)*8),2);
            str += (char)(a);
        }
        return str;
    }

    //https://stackoverflow.com/questions/917163/convert-a-string-like-testing123-to-binary-in-java
    private static String typeToBinary (String charac){
        StringBuilder result = new StringBuilder();
        char[] messChar = charac.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            result.append(Integer.toBinaryString(messChar[i]));
        }
        //byte[] encoded = charac.getBytes(StandardCharsets.UTF_8);
        //System.out.println(Integer.toBinaryString(encoded[0]));
        result = completeByte(result,8);
        return result.toString();
    }
    private static StringBuilder completeByte(StringBuilder curr, int multiple){
        while (curr.length() != multiple){
            curr.insert(0, '0');
        }
        return curr;
    }

    private static String completeByte(String curr, int multiple){
        StringBuilder sb = new StringBuilder();
        int temp = curr.length();
        while (temp != multiple){
            sb.append('0');
            temp += 1;
        }
        sb.append(curr);
        return sb.toString();
    }

}
