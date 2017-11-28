import java.nio.charset.StandardCharsets;

public class Utility {
    Conversion cs = new Conversion();
    CheckSum ch = new CheckSum();

    //Num en binaire,type en char, donnee en binaire
    public String makeTrame(String type, String num,String donnee, String polyGen){
        String numS = cs.completeByte(num,8);
        type = cs.typeToBinary(type,false);
        String checkValue = ch.applyCheckSum(type+numS+donnee,polyGen);
        type = cs.completeByte(type,8);
        Trame trameMade = new Trame(type, numS, donnee, checkValue);
        trameMade.print();
        return trameMade.makeTrame();
    }

    public byte[] toBinaryFromString(String s){
        return s.getBytes(StandardCharsets.UTF_8);

    }

    public String toBinaryFromInt(int i){
        return (Integer.toBinaryString(i));
    }
}
