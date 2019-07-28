
package ru.denis.dota2.util;


import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

/**
 * Created by root on 24.06.16.
 */

// Convert a 32 steam id to 64 bit in C#, Java, C etc. - most typed languages will be easy to convert.
// You get the 32 bit id from dev console (typing in "status") in Valve games.
// Annoyingly, the Steam REST API expected 64 bit ids for player lookups.

// Reference ids:
// 64 bit: 76561197967556737
// 32 bit: STEAM_0:1:3645504
// string split the 32 bit string into an array, using ":"
// (Ignore the STEAM_0 part, it's the public Steam 'universe')
// [0] = "1"
// [1] = "3645504"

public class SteamID {
    public static  long convertto64(long SteamID) {
        long expected64BitId = 76561197967556737L;

        long convertedTo64Bit = SteamID * 2L; // [1] from above

        convertedTo64Bit += 76561197960265728L; // Valve's magic constant
        convertedTo64Bit += 1; // [0] from above

        System.out.println(convertedTo64Bit);
        return convertedTo64Bit;
    }

    public static long convertSteamIdTOSteamId3(long SteamID){
        long result = 0;

        result = (SteamID * 2) + 1;

        System.out.println(result);

        return result;
    }


    public static long convertCommunityIdToSteamId3(long communityId)
            throws SteamCondenserException {
        // Only the public universe (1) is supported
        int steamId1 = 1;
        long steamId2 = communityId - 76561197960265728L;

        if (steamId2 <= 0) {
            throw new SteamCondenserException(String.format("SteamID %d is too small.", communityId));
        }

        return steamId2;
    }
}

