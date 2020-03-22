package ch.epfl.polychef;

import org.junit.Test;
import java.io.*;

import static junit.framework.TestCase.assertTrue;

public class TestingUnitTest {

    @Test
    public void fooTest(){
        assertTrue(true);
    }

    @Test
    public void fooTest2(){

        try{

            System.out.println("I am executed !");

            if(true) {
                throw new IllegalArgumentException("This is unbearable !");
            }

            System.out.println("I am exec");

        } catch (IllegalArgumentException e){
            System.out.println(e);
        }



        assertTrue(true);
    }
}
