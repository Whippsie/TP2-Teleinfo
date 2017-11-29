package Tools;
public class Utility {
    Conversion cs = new Conversion();
    CheckSum ch = new CheckSum();

    /* Crée l'objet trame et retourne la concaténation binaire
        Num est reçu en binaire,type en char, donnee en binaire
     */
    public String makeTrame(String type, String num,String donnee, String polyGen){
        //On complète le numéro pour fit dans 1 octet
        String numS = cs.completeByte(num,8);
        //On traduit le type en binaire
        type = cs.typeToBinary(type,false);
        //On calcule le CRC
        String checkValue = ch.applyCheckSum(type+numS+donnee,polyGen);
        //On complète le type pour fit en 1 octet
        type = cs.completeByte(type,8);
        Trame trameMade = new Trame(type, numS, donnee, checkValue);
        trameMade.print();
        // Retourne la concaténation des valeurs binaires
        return trameMade.makeTrame();
    }

    /* S'occupe de prendre la trame en format binaire et de fetch les données pour créer un objet Tools.Trame*/
    public Trame readTrame(String trame){
        int iflag = 8;
        int itype = 8;
        int inum = 8;
        int icrc = 16;
        int idonnee = trame.length()-iflag*2-inum-icrc-itype;
        int curr = 0;
        int next = iflag;
        boolean bug = false;

        // FLAG type num donnee crc flag
        StringBuilder flag = new StringBuilder();
        for (int i=curr;i<next;i++){
            flag.append(trame.charAt(i));
        }

        // Si on n'a pas le flag au début, on affiche une erreur
        if (!flag.toString().equals("01111110")){
            System.out.println("Invalid flag at the beginning!");
            bug = true;
        }

        curr = iflag;
        next += itype;
        StringBuilder type = new StringBuilder();
        // Flag TYPE num donnee crc flag
        for (int i=curr;i<next;i++){
            type.append(trame.charAt(i));
        }

        curr += itype;
        next += inum;
        StringBuilder num = new StringBuilder();
        // Flag type NUM donnee crc flag
        for (int i=curr;i<next;i++){
            num.append(trame.charAt(i));
        }

        curr += inum;
        next += idonnee;
        StringBuilder donnee = new StringBuilder();
        // Flag type num DONNEE crc flag
        for (int i=curr;i<next;i++){
            donnee.append(trame.charAt(i));
        }

        curr += idonnee;
        next += icrc;
        StringBuilder crc = new StringBuilder();
        // Flag type num donnee CRC flag
        for (int i=curr;i<next;i++){
            crc.append(trame.charAt(i));
        }

        curr += icrc;
        next += iflag;
        flag.setLength(0);
        // Flag type num donnee crc FLAG
        for (int i=curr;i<next;i++){
            flag.append(trame.charAt(i));
        }
        if (!flag.toString().equals("01111110")){
            System.out.println("Invalid flag at the end!");
            bug = true;
        }
        if (!bug) {
            return new Trame(type.toString(), num.toString(), donnee.toString(), crc.toString());
        }else{
            return null;
        }
    }
}
