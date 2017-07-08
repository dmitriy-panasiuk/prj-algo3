package dmitriypanasiuk.util;

import java.util.Random;

public class RandomString {

    private static final char[] symbols;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }

    private final Random random = new Random();

    public String nextString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        char[] buf = new char[length];
        for (int i = 0; i < length; i++) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}
