package co.alizay.calendar.helpers;

import android.graphics.Color;

import java.util.Random;

public class Util {

    public static int getColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        return color;
    }
}
