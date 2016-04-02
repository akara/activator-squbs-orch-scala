/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.cube;

public class LyricsHelper {


    public static String lyric(int i) {
        return
                String.format("%1$s of beer on the wall, %1$s of beer.\n", bottles(i)) +
                        String.format("Take one down and pass it around, %s of beer on the wall.", bottles(i - 1));
    }

    public static String lyricEnd(int start) {
        return
                "No more bottles of beer on the wall, no more bottles of beer.\n" +
                        String.format("Go to the store and buy some more, %s of beer on the wall.", bottles(start));
    }

    private static String bottles(int count) {
        switch (count) {
            case 0:
                return "no more bottles";
            case 1:
                return "1 bottle";
            default:
                return count + " bottles";
        }
    }


}
