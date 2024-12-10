/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.util;

// Written in java because I don't like kotlin's bit operators
public class MurmurHash2 {
    private static boolean isWhiteSpaceCharacter(byte b) {
        return b == 9 || b == 10 || b == 13 || b == 32;
    }

    public static long cfHash(byte[] data, int length) {
        // "Normalize" the byte array
        // Adapted from https://github.com/CurseForgeCommunity/.NET-APIClient/blob/2c4f5f613d20025f9286fdd53592f8519022918f/Murmur2.cs
        // To avoid creating a copy in memory we will shift the existing data in the array
        int shiftCount = 0;
        for (int i = 0; i < length; i++) {
            if (isWhiteSpaceCharacter(data[i])) {
                shiftCount++;
            } else {
                data[i - shiftCount] = data[i];
            }
        }
        int hash = hash32(data, length - shiftCount, 1);
        // Extend to long without extending the sign, avoid getting negative numbers
        return hash & 0xFFFFFFFFL;
    }

    /**
     * Taken from https://github.com/tnm/murmurhash-java/blob/1cef5b1bdb1856d1d4d48b5572f35baacb57d0f5/src/main/java/ie/ucd/murmur/MurmurHash.java#L33-L67
     * Under the public domain
     */
    public static int hash32(final byte[] data, int length, int seed) {
        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        final int m = 0x5bd1e995;
        final int r = 24;

        // Initialize the hash to a random value
        int h = seed ^ length;
        int length4 = length / 4;

        for (int i = 0; i < length4; i++) {
            final int i4 = i * 4;
            int k = (data[i4] & 0xff)
                    + ((data[i4 + 1] & 0xff) << 8)
                    + ((data[i4 + 2] & 0xff) << 16)
                    + ((data[i4 + 3] & 0xff) << 24);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // Handle the last few bytes of the input array
        switch (length % 4) {
            case 3:
                h ^= (data[(length & ~3) + 2] & 0xff) << 16;
            case 2:
                h ^= (data[(length & ~3) + 1] & 0xff) << 8;
            case 1:
                h ^= (data[length & ~3] & 0xff);
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }
}
