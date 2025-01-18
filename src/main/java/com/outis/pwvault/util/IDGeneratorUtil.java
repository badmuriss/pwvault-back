package com.outis.pwvault.util;

import java.util.Random;

public  class IDGeneratorUtil {
    private static final char[] _base62chars =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                    .toCharArray();

    private static final Random _random = new Random();

    public static String GetBase62(int length)
    {
        var sb = new StringBuilder(length);

        for (int i=0; i<length; i++)
            sb.append(_base62chars[_random.nextInt(62)]);

        return sb.toString();
    }

    public static String GetBase36(int length)
    {
        var sb = new StringBuilder(length);

        for (int i=0; i<length; i++)
            sb.append(_base62chars[_random.nextInt(36)]);

        return sb.toString();
    }
}