package Test

import Tools.CheckSum
class CheckSumTest extends GroovyTestCase {

    CheckSum cs = new CheckSum();

    void testApplyCheckSum() {
        //Soit les données suivantes
        //Type de la trame : I
        //Num : 0
        //Donnee : Aucune
        //Polynome : 10001000000100001
        String res = cs.applyCheckSum("100100100000000","10001000000100001");
        //Le reste que l'on devrait obtenir selon le site http://www.ee.unb.ca/cgi-bin/tervo/calc.pl
        //1001001000000000000000000000000 / 10001000000100001 = reste :1011011101010100
        assertEquals(res,"1011011101010100");
    }

    void testVerifyCheckSum() {
        //Calculons l'inverse
        boolean res = cs.verifyCheckSum("10001000000100001","1001001000000001011011101010100")
        assertTrue(res);
    }

    void testVerifyCheckSumType() {
        boolean res = cs.verifyCheckSum("10001000000100001","1001000000000001011011101010100")
        assertFalse(res);
    }
    void testVerifyCheckSumNumber() {
        boolean res = cs.verifyCheckSum("10001000000100001","1001001000001001011011101010100")
        assertFalse(res);
    }
    void testVerifyCheckSumDonnee() {
        boolean res = cs.verifyCheckSum("10001000000100001","1001001000000001011011101000100")
        assertFalse(res);
    }
}
