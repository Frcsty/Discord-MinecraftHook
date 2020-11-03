package com.github.frcsty.discordminecrafthook.cache;

import java.util.SplittableRandom;

@SuppressWarnings("SpellCheckingInspection")
public final class CodeBuilder {

    private static final SplittableRandom RANDOM = new SplittableRandom();

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERAL = "1234567890";
    private static final String SYMBOL = "/*-+,$#%&!";

    private static final String ALPHANUM = ALPHABET + NUMERAL + SYMBOL;

    /**
     * Constructs and returns a random String code
     * with the desired size
     *
     * @param size  The size of returned code
     * @return      A random String Code
     */
    private static String getRandomCode(final int size) {
        String result = "";

        for (int i = 0; i < size; i++) {
            result = result.concat(String.valueOf(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length() - 1))));
        }

        return result;
    }

    /**
     * Constructs and returns a random String code
     * with the desired size
     *
     * @param size  The size of returned code
     * @return      A random String code
     */
    public static String getRandomCode(final String size) {
        return getRandomCode(Integer.valueOf(size));
    }

}
