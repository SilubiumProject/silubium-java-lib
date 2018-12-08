package org.jhblockchain.crypto.bitcoincash;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.subgraph.orchid.TorException;

public class BCHBase32 {

    private static final String BASE32_CHARS = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    public BCHBase32() {
    }

    public static String base32Encode(byte[] source) {
        return base32Encode(source, 0, source.length);
    }

    public static String base32Encode(byte[] source, int offset, int length) {
        int nbits = length * 8;
        if(nbits % 5 != 0) {
            throw new TorException("Base32 input length must be a multiple of 5 bits");
        } else {
            int outlen = nbits / 5;
            StringBuffer outbuffer = new StringBuffer();
            int bit = 0;

            for(int i = 0; i < outlen; ++i) {
                int v = (source[bit / 8] & 255) << 8;
                if(bit + 5 < nbits) {
                    v += source[bit / 8 + 1] & 255;
                }

                int u = v >> 11 - bit % 8 & 31;
                outbuffer.append(BASE32_CHARS.charAt(u));
                bit += 5;
            }

            return outbuffer.toString();
        }
    }

    /**
     * Encode a byte array as base32 string. This method assumes that all bytes
     * are only from 0-31
     *
     * @param byteArray
     * @return
     */
    public static String encode(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            int val = (int) byteArray[i];

            if (val < 0 || val > 31) {
                throw new RuntimeException("This method assumes that all bytes are only from 0-31. Was: " + val);
            }

            sb.append(BASE32_CHARS.charAt(val));
        }

        return sb.toString();
    }


    public static byte[] base32Decode(String source) {
        int[] v = stringToIntVector(source);
        int nbits = source.length() * 5;
        if(nbits % 8 != 0) {
            throw new TorException("Base32 decoded array must be a muliple of 8 bits");
        } else {
            int outlen = nbits / 8;
            byte[] outbytes = new byte[outlen];
            int bit = 0;

            for(int i = 0; i < outlen; ++i) {
                int bb = bit / 5;
                outbytes[i] = (byte)decodeByte(bit, v[bb], v[bb + 1], v[bb + 2]);
                bit += 8;
            }

            return outbytes;
        }
    }

    private static int decodeByte(int bitOffset, int b0, int b1, int b2) {
        switch(bitOffset % 40) {
            case 0:
                return ls(b0, 3) + rs(b1, 2);
            case 8:
                return ls(b0, 6) + ls(b1, 1) + rs(b2, 4);
            case 16:
                return ls(b0, 4) + rs(b1, 1);
            case 24:
                return ls(b0, 7) + ls(b1, 2) + rs(b2, 3);
            case 32:
                return ls(b0, 5) + (b1 & 255);
            default:
                throw new TorException("Illegal bit offset");
        }
    }

    private static int ls(int n, int shift) {
        return n << shift & 255;
    }

    private static int rs(int n, int shift) {
        return n >> shift & 255;
    }

    private static int[] stringToIntVector(String s) {
        int[] ints = new int[s.length() + 1];

        for(int i = 0; i < s.length(); ++i) {
            int b = s.charAt(i) & 255;
            if(b > 96 && b < 123) {
                ints[i] = b - 97;
            } else if(b > 49 && b < 56) {
                ints[i] = b - 24;
            } else {
                if(b <= 64 || b >= 91) {
                    throw new TorException("Illegal character in base32 encoded string: " + s.charAt(i));
                }

                ints[i] = b - 65;
            }
        }

        return ints;
    }
}
