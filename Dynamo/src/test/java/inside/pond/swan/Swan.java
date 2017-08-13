package inside.pond.swan;

import inside.pond.shore.Bird;

import java.util.ArrayList;

/**
 * Created by mtrujillo on 4/25/2016.
 */
public class Swan extends Bird{

    public static int a = 0;
    public static int b = 0;

    static {
        a++;
        System.out.println("a:" + a);
    }

    {
        b++;
        System.out.println("b:" + b);
    }

    public static void test(){
        ArrayList a;
    }

    public void swin(){
        floatInWater();
        System.out.println(text);
    }

    public void helpOtherSwanSwim(){
        Swan other = new Swan();
        other.floatInWater();
        System.out.println(text);
    }

    public void helpOtherBirdToSwim(){
        Bird other = new Bird();
    }
}
