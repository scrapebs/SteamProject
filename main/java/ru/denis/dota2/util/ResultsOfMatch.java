package ru.denis.dota2.util;

import com.github.koraktor.steamcondenser.community.SteamId;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import de.inkvine.dota2stats.Dota2Stats;
import de.inkvine.dota2stats.domain.MatchOverview;
import de.inkvine.dota2stats.domain.filter.MatchHistoryFilter;
import de.inkvine.dota2stats.domain.matchdetail.MatchDetail;
import de.inkvine.dota2stats.domain.matchdetail.MatchDetailPlayer;
import de.inkvine.dota2stats.domain.matchhistory.MatchHistory;
import de.inkvine.dota2stats.domain.playerstats.PlayerStats;
import de.inkvine.dota2stats.exceptions.Dota2StatsAccessException;
import de.inkvine.dota2stats.impl.Dota2StatsImpl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import ru.denis.dota2.db.ConnectDB;
import static ru.denis.dota2.util.SteamGetFuncs.stats;


/**
 * Created by root on 25.06.16.
 */
public class ResultsOfMatch {

    static Dota2Stats stats = new Dota2StatsImpl("9DEA987E861AF6FA5A94FB2434F3B05C");  //B354BD214FA4E840CB5974FC604A1776

    public ResultsOfMatch() throws SteamCondenserException {
    }

    public static HashMap<String, Boolean> query(long SteamCommunityID, long TimeStamp) throws Dota2StatsAccessException, ParseException, SteamCondenserException {

        boolean win = false;
        boolean winplayer;
        boolean error = false;
        long LastMatchId, PreviousMatchId = 0;
        int playerSlot = -1;
        String ErrorDescription;
        boolean result = false;
        long SteamID = 0;
        checks checks = new checks();

        if(checks.CheckPrivacy(SteamCommunityID)==true)
        {
            SteamID = checks.getSteam3ID();
            System.out.println(SteamID);
        }
        else {
            return null;
        }

        PlayerStats player = stats.getStats(SteamID, 1);
        System.out.println(player);

        MatchHistory history = stats.getMatchHistory(new MatchHistoryFilter().forAccountId(SteamID));

        long matchID = 0;

        matchID = checks.getfirstmatchafterexpectDate(TimeStamp,SteamID);

        if (matchID!=0) {
            

            MatchDetail DetailofLastMatch = stats.getMatchDetails(matchID);

            long DateOfLastMatch = ((long) (double) (DetailofLastMatch.getMatchOverview().getStartTime()) * 1000);

            win = DetailofLastMatch.didRadianWin();

            if (win) {
                System.out.println("Свет победил");
            } else {
                System.out.println("Тьма победила");
            }

            List<MatchDetailPlayer> playersStatsOfTheMatch = DetailofLastMatch.getPlayers();

            for (MatchDetailPlayer PlayerID : playersStatsOfTheMatch) {
                if (SteamID == PlayerID.getAccountId()) {
                    playerSlot = PlayerID.getPlayerSlots();
                }
            }

            if (playerSlot > 5 && playerSlot != -1) {
                System.out.println("Игрок играл за тьму");
                winplayer = false;
            } else if (playerSlot == -1) {
                System.out.println("Игрок не найден");
                return null;
            } else {
                System.out.println("Игрок играл за свет");
                winplayer = true;
            }

            if (win == winplayer) {
                System.out.println("Игрок победил");
                result = true;
            }
            else {
                System.out.println("Игрок проиграл");
                result = false;
            }

            HashMap<String, Boolean> tmpHashMap = new HashMap<String, Boolean>();
            tmpHashMap.put("winradiant", win);
            tmpHashMap.put("playerradiant", winplayer);
            tmpHashMap.put("result", result);
            return tmpHashMap;
        } else {
            error = true;
            return null;
        }
    }
    
    public static Boolean result(long SteamCommunityID, long TimeStamp) throws Dota2StatsAccessException, ParseException, SteamCondenserException {
        boolean win = false;
        boolean winplayer;
        boolean error = false;
        long LastMatchId, PreviousMatchId = 0;
        int playerSlot = -1;
        String ErrorDescription;
        boolean result = false;
        long SteamID = 0;
        checks checks = new checks();

        if(checks.CheckPrivacy(SteamCommunityID)==true)
        {
            SteamID = checks.getSteam3ID();
            System.out.println(SteamID);
        }
        else {
            return null;
        }

        PlayerStats player = stats.getStats(SteamID, 1);
        System.out.println(player);

        MatchHistory history = stats.getMatchHistory(new MatchHistoryFilter().forAccountId(SteamID));

        long matchID = 0;
        matchID = checks.getfirstmatchafterexpectDate(TimeStamp,SteamID);

        if (matchID!=0) {
            MatchDetail DetailofLastMatch = stats.getMatchDetails(matchID);
            long DateOfLastMatch = ((long) (double) (DetailofLastMatch.getMatchOverview().getStartTime()) * 1000);
            win = DetailofLastMatch.didRadianWin();

            if (win) {
                System.out.println("Свет победил");
            } else {
                System.out.println("Тьма победила");
            }

            List<MatchDetailPlayer> playersStatsOfTheMatch = DetailofLastMatch.getPlayers();

            for (MatchDetailPlayer PlayerID : playersStatsOfTheMatch) {
                if (SteamID == PlayerID.getAccountId()) {
                    playerSlot = PlayerID.getPlayerSlots();
                }
            }

            if (playerSlot > 5 && playerSlot != -1) {
                System.out.println("Игрок играл за тьму");
                winplayer = false;
            } else if (playerSlot == -1) {
                System.out.println("Игрок не найден");
                return null;
            } else {
                System.out.println("Игрок играл за свет");
                winplayer = true;
            }

            if (win == winplayer)
            {
                System.out.println("Игрок победил");
                result = true;
            }
            else{
                System.out.println("Игрок проиграл");
                result = false;
                }
            return result;
        } else {
            error = true;
            return null;
        }
    }


    //открыта ли история игр в Дота 2
    public static void openhistory(long SteamCommunityID) throws Dota2StatsAccessException, ParseException, SteamCondenserException {

        long SteamID = 0;
        checks checks = new checks();

       
        if(checks.CheckPrivacy(SteamCommunityID)==true)
        {
            SteamID = checks.getSteam3ID();
            System.out.println(SteamID);
        }
        else {
           
        }
        

        PlayerStats player = stats.getStats(SteamID, 1);
    }
}



class checks{
    private long SteamID = 145918807L;

        public checks(){

        }

    public boolean CheckPrivacy(long SteamCommunityID) throws SteamCondenserException {

        SteamId steamId = new SteamId(SteamCommunityID, true);
        System.out.println(steamId.getPrivacyState());
        if (steamId.getPrivacyState().matches("public")){
            SteamID = ru.denis.dota2.util.SteamID.convertCommunityIdToSteamId3(SteamCommunityID);
            System.out.println(steamId.getPrivacyState());
            System.out.println(steamId.getId());
            System.out.println(steamId.getRealName());
            System.out.println(steamId.isOnline());
        return true;
        }
        else {
            System.out.println("Аккаунт не публичный");
            return false;

        }
        }
    public long getSteam3ID(){
        return SteamID;
    }

    public boolean Checklobbys(){
        return false;
    }

    public long getfirstmatchafterexpectDate(long timestamp, long SteamID) {
        long matchnext = 0;

        try {
            MatchHistory history = stats.getMatchHistory(new MatchHistoryFilter().forAccountId(SteamID));
            List<MatchOverview> overviews = history.getMatchOverviews();
            for (MatchOverview match : overviews) {
                MatchDetail DetailofLastMatch = stats.getMatchDetails(match.getMatchId());
                //else эксепшн матч еще не сыгран
                if (DetailofLastMatch.getMatchOverview().getStartTime() >= timestamp) {
                   
                    matchnext = match.getMatchId();
                    //System.out.println(DetailofLastMatch.getMatchOverview().getStartTime());
                    

                }
                else { break;}
            }
        } catch (Dota2StatsAccessException e1) {
            // Do something if an error occurs
        }
       return matchnext;
    }
}


