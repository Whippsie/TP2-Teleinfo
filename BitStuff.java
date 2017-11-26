public class BitStuff {
    /* Add bit stuffing after every 5th '1' to differenciate from the flag */
    public String bitstuffIn(String data){
        int compteur = 0;
        //For performance issues
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < data.length() ; i ++){
            if (compteur == 5){
                sb.append('0');
                compteur = 0;
            }
            if (data.charAt(i) == '1'){
                sb.append(data.charAt(i));
                compteur += 1;
            } else {
                sb.append(data.charAt(i));
                compteur = 0;
            }
        }
        return sb.toString();
    }

    public String bitstuffOut(String data){
        int compteur = 0;
        //For performance issues
        StringBuilder sb = new StringBuilder();
        String bitstuffedData = "";
        for (int i = 0 ; i < data.length() ; i ++){
            if (compteur == 5){
                compteur = 0;
                continue;
            }
            if (data.charAt(i) == '1'){
                sb.append(data.charAt(i));
                compteur += 1;
            } else {
                sb.append(data.charAt(i));
                compteur = 0;
            }
        }
        return sb.toString();
    }

    public Trame readTrame(String trame){
        int iflag = 8;
        int itype = 8;
        int inum = 8;
        int icrc = 16;
        int idonnee = trame.length()-iflag*2-inum-icrc-itype;
        int curr = 0;
        int next = iflag;

        StringBuilder flag = new StringBuilder();
        // Flag type num donnee crc flag
        for (int i=curr;i<next;i++){
            flag.append(trame.charAt(i));
        }
        System.out.println(flag.toString());
        if (!flag.toString().equals("01111110")){
            System.out.println("On a un probleme debut");
        }

        curr = iflag;
        next += itype;
        StringBuilder type = new StringBuilder();
        // Flag type num donnee crc flag
        for (int i=curr;i<next;i++){
            type.append(trame.charAt(i));
        }

        curr += itype;
        next += inum;
        StringBuilder num = new StringBuilder();
        // Flag type num donnee crc flag
        for (int i=curr;i<next;i++){
            num.append(trame.charAt(i));
        }

        curr += inum;
        next += idonnee;
        StringBuilder donnee = new StringBuilder();
        // Flag type num donnee crc flag
        for (int i=curr;i<next;i++){
            donnee.append(trame.charAt(i));
        }

        curr += idonnee;
        next += icrc;
        StringBuilder crc = new StringBuilder();
        // Flag type num donnee crc flag
        for (int i=curr;i<next;i++){
            crc.append(trame.charAt(i));
        }

        curr += icrc;
        next += iflag;
        flag.setLength(0);
        for (int i=curr;i<next;i++){
            flag.append(trame.charAt(i));
        }
        if (!flag.toString().equals("01111110")){
            System.out.println("On a un probleme fin");
        }

        return new Trame(type.toString(),num.toString(),donnee.toString(),crc.toString());
    }

}
