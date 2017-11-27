public class Utility {
    Conversion cs = new Conversion();
    CheckSum ch = new CheckSum();

    public String makeTrame(String type, String num,String donnee, String polyGen){
        String numS = cs.completeByte(num,8);
        type = cs.typeToBinary(type,false);
        String checkValue = ch.applyCheckSum(type+num+donnee,polyGen);
        type = cs.completeByte(type,8);
        Trame trameMade = new Trame(type, numS, donnee, checkValue);
        trameMade.print();
        return trameMade.makeTrame();
    }
}
