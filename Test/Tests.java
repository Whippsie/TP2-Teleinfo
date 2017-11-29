package Test;

public class Tests {
    boolean first = true;
    int times = 0;
    /* Force le socket à attendre 3 secondes*/
    public void bufferOut(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Modifie le bit à la position posToShift de la séquence value */
    /* 0 devient 1 et 1 devient 0 */
    public String bitShift(int posToShift, String value){
        StringBuilder sb = new StringBuilder();
        int temp = 0;
        for (int i=0;i<value.length();i++){
            temp = Integer.parseInt(String.valueOf(value.charAt(i)));
            if (i==posToShift){
                //value.charAt(i) = (1 - Integer.parseInt(value.charAt(i)));
                temp ^= 1;
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /* N'envoie pas la trame, ne fonctionne qu'une fois pour permettre la retransmission*/
    public boolean destroyTrame(int num) {
        //Modifier le numero de la trame à refuser au besoin
        if (num == 6) {
            if (this.first) {
                this.first = false;
                return true;
            }
        }
        return false;
    }

    // Pour tester plusieurs destroy,
    public boolean destroyTrames(int num, int times){
        if (num == 4 && this.times < times){
            this.times +=1;
            return true;
        }
        return false;
    }
}
