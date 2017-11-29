package Test

import Tools.BitStuff
class BitStuffTest extends GroovyTestCase {
    BitStuff bs = new BitStuff();
    void testBitstuffIn() {
        String curr = "0111110000001011110111111"
        curr = bs.bitstuffIn(curr);
        assertEquals(curr,"011111000000010111101111101")
    }

    void testBitstuffOut() {
        String curr = "1111101110000010111101111101"
        curr = bs.bitstuffOut(curr);
        assertEquals(curr,"11111111000001011110111111")
    }
}
