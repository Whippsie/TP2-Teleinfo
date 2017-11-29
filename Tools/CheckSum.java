package Tools;

/* Cette classe gère le calcul du checksum pour la vérification des données */
public class CheckSum {
    Conversion cs = new Conversion();

    /* On génère des zéros pour le degré du polynôme -1 */
    /* On utilise aussi cette fonction pour générer les zéros quand la séquence commence à zéro */
    private StringBuilder genCheckSumZeroes(int debut, String polyGen){
        StringBuilder sb = new StringBuilder();
        for (int i = debut ; i < polyGen.length(); i++){
            sb.append('0');
        }
        return sb;
    }

    /* On génère la première partie du String à comparer pour la division */
    private StringBuilder genCheckSumStart(String polyGen, String data){
        StringBuilder curr = new StringBuilder();
        for (int i = 0 ; i < polyGen.length(); i++){
            curr.append(data.charAt(i));
        }
        return curr;
    }

    /* Contient la logique et les appels pour le checksum */
    public String applyCheckSum(String data, String polyGen){
        //Garde la valeur initiale
        String dataInit = data;

        //Gen le degré du checksum
        data += genCheckSumZeroes(1,polyGen).toString();

        //Fait la division
        StringBuilder curr = makeDivision(polyGen,data);

        //Obtient le Tx, on ne l'utilise pas dans ce TP
        String tX = genTx(dataInit,curr.toString());
        //System.out.println("T(x) = " + tX);

        //Retourne uniquement le reste de la division pour fit sur 16 bits
        return curr.toString();
    }


    private StringBuilder makeDivision(String polyGen, String data){
        /* Calcule le nombre d'étapes effectuées dans la division*/
        int compteur = 0;
        String polyInit = polyGen;

        /*  curr : la nombre courant que l'on compare
            next : le prochain nombre à comparer
            resultat : le résultat de la division
         */
        StringBuilder curr = genCheckSumStart(polyGen,data);
        StringBuilder next = new StringBuilder();
        StringBuilder resultat = new StringBuilder();

        // Quand le compteur a atteint cette valeur, on est à la fin de la séquence
        while (compteur <= (data.length() - polyGen.length())) {

            polyGen = polyInit;
            next.setLength(0);

            //On commence par regarder le premier chiffre
            if (curr.charAt(0) == '0'){
                //On a un zero
                resultat.append("0");
                //On modifie le polynôme pour la comparaison
                polyGen = genCheckSumZeroes (0,polyGen).toString();
            } else if (curr.charAt(0) == '1' && polyGen.charAt(0) == '1'){
                //On a un 1
                resultat.append("1");
            }else{
                System.out.println("Le polynôme commence par 0, veuillez réessayer avec un polynôme commençant par 1.");
            }

            // Pour chaque caractère de current, on compare avec le polynôme générateur
            // S'il est égal, on fait le xor et on ajoute zero au prochain
            // Sinon, on ajoute 1
            for (int i = 1; i < polyGen.length(); i++) {
                if (curr.charAt(i) == polyGen.charAt(i)) {
                    next.append('0');
                } else {
                    next.append('1');
                }
            }

            // S'il nous reste des chiffres à ajouter dans la séquence, on en ajoute un à la prochaine séquence
            if (data.length() - polyGen.length() != compteur){
                next.append((data.charAt(compteur+ polyGen.length())));
            }
            compteur += 1;

            // Change curr pour le next
            curr.setLength(0);
            for (int j = 0;j < next.length(); j++) {
                curr.append(next.charAt(j));
            }
        }
        // Retourne le reste (le dernier current)
        return curr;
    }

    // Pas utilisé dans ce TP
    private static String genTx(String mX, String rX){
        for (int i =0; i< rX.length();i++){
            mX += rX.charAt(i);
        }
        return mX;
    }

    /* Utilisé par serveur pour valider le CRC */
    public boolean verifyCheckSum(String polyGen,String data){
        StringBuilder curr = makeDivision(polyGen,data);

        // Si différent de zéro, retourne faux
        if (cs.binaryStringToDecimal(curr.toString())!= 0){
            return false;
        }
        return true;
    }
}
