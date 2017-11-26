public class Trame {
    String flag, num, crc, donnee, type;
    String polygen = "10001000000100001";
    public Trame(String type, String num,  String donnee, String crc){
        this.flag = "01111110";
        this.num = num;
        this.crc = crc;
        this.donnee = donnee;
        this.type = type;
    }

    public void print(){
        System.out.println(" -TRAME- | FlagD: " + this.flag + " | Type: " + this.type + " | Num: " + this.num + " | Donnees: " + this.donnee + " | CRC: " + crc + " | FlagF: " + this.flag + " |");
    }

    public String makeTrame(){
        return (this.flag+this.type+this.num+this.donnee+this.crc+this.flag);
    }
}
