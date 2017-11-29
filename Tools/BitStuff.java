package Tools;

public class BitStuff {
    /* Ajoute bit stuffing après chaque 5th '1' pour différencier du flag */
    public String bitstuffIn(String data){
        int compteur = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < data.length() ; i ++){
            // On a 5 '1' consécutifs
            if (compteur == 5){
                sb.append('0');
                compteur = 0;
            }
            // Chaque fois qu'on a un '1' on incrémente le compteur
            if (data.charAt(i) == '1'){
                sb.append(data.charAt(i));
                compteur += 1;
            } else {
                // Dès qu'on a un zéro, on reset
                sb.append(data.charAt(i));
                compteur = 0;
            }
        }
        return sb.toString();
    }

    /* Enlève le bit stuffing de chaque 5 '1' consécutifs */
    public String bitstuffOut(String data){
        int compteur = 0;
        StringBuilder sb = new StringBuilder();
        if (data == null){
            System.out.println("Data vide");
            return "erreur";
        }
        for (int i = 0 ; i < data.length() ; i ++){
            if (compteur == 5){
                compteur = 0;
                // On poursuit la loop et on ajoute pas le zéro dans le string
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

}
