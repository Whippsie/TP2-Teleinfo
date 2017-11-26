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

    public String typeToBinary (String charac){
        StringBuilder result = new StringBuilder();
        char[] messChar = charac.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            result.append(Integer.toBinaryString(messChar[i]));
        }
        result = completeByte(result,8);
        //byte[] encoded = charac.getBytes(StandardCharsets.UTF_8);
        //System.out.println(encoded);
        //System.out.println ("Converted " + charac + " into " + result);
        return result.toString();
    }

    private StringBuilder completeByte(StringBuilder curr, int multiple){
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
}
