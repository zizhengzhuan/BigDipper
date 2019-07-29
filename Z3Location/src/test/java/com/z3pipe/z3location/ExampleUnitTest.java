package com.z3pipe.z3location;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {

        byte[] sourceFlag = "0a#".getBytes();
        int i = 0;
        int n = sourceFlag.length;
        byte[] checkFlag = "0A#".getBytes();
        while (n-- != 0) {
            if (Character.toLowerCase(sourceFlag[i]) != Character.toLowerCase(checkFlag[i])) {
                System.out.println("!=");
            } else {
                System.out.println("==");
            }
            i++;
        }
    }


}