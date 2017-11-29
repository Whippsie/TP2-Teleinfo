package Tools;

import java.nio.charset.StandardCharsets;

public class Conversion {

    //Va chercher tous les chars (ici un seul) et le transforme en binary string
    public String typeToBinary (String charac, boolean complete){
        StringBuilder result = new StringBuilder();
        char[] resChar = charac.toCharArray();

        for (int i = 0; i < resChar.length; i++) {
            result.append(Integer.toBinaryString(resChar[i]));
        }
        if (complete) {
            result = completeByte(result, 8);
        }
        return result.toString();
    }

    //Inspiré de https://stackoverflow.com/questions/8634527/converting-binary-data-to-characters-in-java
    public String binaryToType (String charac){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charac.length()/8; i++) {
            //Prend chaque suite de 8 zéro ou un et va chercher sa valeur en int
            //Parse back en char
            sb.append((char) Integer.parseInt(charac.substring(8*i,(i+1)*8),2));
        }
        return sb.toString();
    }


    // Prend des chars et les transforme en tableau de bytes
    public byte[] toBinaryFromString(String s){
        return s.getBytes(StandardCharsets.UTF_8);

    }

    //Complète le chiffre avec des zéros à gauche
    public StringBuilder completeByte(StringBuilder curr, int multiple){
        while (curr.length() != multiple){
            curr.insert(0, '0');
        }
        return curr;
    }

    //Idem mais avec des String
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

    //Prend un string binaire et le traduit en nombre décimal
    public static int binaryStringToDecimal(String biString){
        return Integer.parseInt(biString, 2);
    }

    //Prend un nombre décimal et le traduit en binaire
    public static String decimalToBinary(int decimal){
        return Integer.toBinaryString(decimal);
    }
}
