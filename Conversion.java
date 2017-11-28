import java.nio.charset.StandardCharsets;

public class Conversion {

    //https://stackoverflow.com/questions/8634527/converting-binary-data-to-characters-in-java
    public String binaryToType (String charac){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < charac.length()/8; i++) {
            int a = Integer.parseInt(charac.substring(8*i,(i+1)*8),2);
            str.append((char)(a));
        }
        return str.toString();
    }

    public String typeToBinary (String charac, boolean complete){
        StringBuilder result = new StringBuilder();
        char[] messChar = charac.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            result.append(Integer.toBinaryString(messChar[i]));
        }
        if (complete) {
            result = completeByte(result, 8);
        }
        //System.out.println ("Converted " + charac + " into " + result);
        return result.toString();
    }

    public StringBuilder completeByte(StringBuilder curr, int multiple){
        while (curr.length() != multiple){
            curr.insert(0, '0');
        }
        return curr;
    }

    public String completeByte(String curr, int multiple){
        StringBuilder sb = new StringBuilder();
        int temp = curr.length();
        while (temp != multiple){
            sb.append('0');
            temp += 1;
        }
        sb.append(curr);
        return sb.toString();
    }

    public static int binaryStringToDecimal(String biString){
        int n = biString.length();
        int decimal = 0;
        for (int d = 0; d < n; d++){
            // append a bit=0 (i.e. shift left)
            decimal = decimal << 1;

            // if biStr[d] is 1, flip last added bit=0 to 1
            if (biString.charAt(d) == '1'){
                decimal = decimal | 1; // e.g. dec = 110 | (00)1 = 111
            }
        }
        return decimal;
    }

    public static String decimalToBinary(int decimal){
        return Integer.toBinaryString(decimal);
    }
}
