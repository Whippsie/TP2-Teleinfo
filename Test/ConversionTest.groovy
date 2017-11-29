package Test

import Tools.Conversion
class ConversionTest extends GroovyTestCase {
    Conversion con = new Conversion();
    String result;
    void testTypeToBinary() {
        result = con.typeToBinary('Z',true);
        assertEquals(result, "01011010");

        result = con.typeToBinary('Z',false);
        assertEquals(result, "1011010");


    }

    void testBinaryToType() {
        result = con.binaryToType("01010011");
        assertEquals(result, "S");
    }

    void testCompleteByte() {
        result = "111";
        result = con.completeByte(result,8);
        assertEquals(result, "00000111");
    }

    void testBinaryStringToDecimal() {
        result = "00000001";
        int temp = con.binaryStringToDecimal(result)
        assertEquals(temp,1);
    }

    void testDecimalToBinary() {
        int temp = 22;
        result = con.decimalToBinary(temp)
        assertEquals(result,"10110");
    }
}
