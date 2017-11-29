package Tools;

/* Cette classe contient toutes les informations des trames envoyées par le sender et le receiver */
public class Trame {
    String flag, num, crc, donnee, type;

    //Polynome CRC-CCITT
    String polygen = "10001000000100001";

    /* Tous les paramètres sont en format binaire */
    public Trame(String type, String num,  String donnee, String crc){
        this.flag = "01111110";
        this.num = num;
        this.crc = crc;
        this.donnee = donnee;
        this.type = type;
    }

    /* Pretty print de l'objet */
    public void print(){
        System.out.println(" -TRAME- | FlagD: " + this.flag + " | Type: " + this.type + " | Num: " + this.num + " | Donnees: " + this.donnee + " | CRC: " + crc + " | FlagF: " + this.flag + " |");
    }

    /* Retourne une concaténation des paramètres */
    public String makeTrame(){
        return (this.flag+this.type+this.num+this.donnee+this.crc+this.flag);
    }

    /* Getters */
    public String getCRC(){
        return this.crc;
    }

    public String getNum(){
        return this.num;
    }

    public String getType(){
        return this.type;
    }

    public String getDonnee(){
        return this.donnee;
    }
}
