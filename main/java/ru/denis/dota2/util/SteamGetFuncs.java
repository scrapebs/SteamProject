package ru.denis.dota2.util;

import de.inkvine.dota2stats.Dota2Stats;
import de.inkvine.dota2stats.domain.GameMode;
import de.inkvine.dota2stats.domain.MatchOverview;
import de.inkvine.dota2stats.domain.filter.MatchHistoryFilter;
import de.inkvine.dota2stats.domain.matchdetail.MatchDetail;
import de.inkvine.dota2stats.domain.matchdetail.MatchDetailPlayer;
import de.inkvine.dota2stats.domain.matchhistory.MatchHistory;
import de.inkvine.dota2stats.domain.playersearch.PlayerSearchResult;
import de.inkvine.dota2stats.exceptions.Dota2StatsAccessException;
import de.inkvine.dota2stats.impl.Dota2StatsImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class SteamGetFuncs {
    static Dota2Stats stats = new Dota2StatsImpl("7E7EAF09792FFEEADCF399506962FCB2");


    public static void getSteamIDforPlayername(String name)
    {
        try {
            List<PlayerSearchResult> results = stats.searchByPlayerName(name);

            // Look at the results!
            for(PlayerSearchResult item : results)
                System.out.println(item);
        } catch (Dota2StatsAccessException e) {
            // Do something if an error occured
        }
    }

    public static void InfoofLastMatches(){
        //Get the 25 most recent matches
        try {

            MatchHistory history = stats.getMostRecentMatchHistory();
            List<MatchOverview> overviews = history.getMatchOverviews();

            // print all match overviews found
            for (MatchOverview match : overviews)
                System.out.println(match);

        } catch (Dota2StatsAccessException e1) {
            // Do something if an error occurs
        }
    }

    public static void DetailInfoofMatch(long idMatch) throws Dota2StatsAccessException {
        InfoofLastMatches();
        try {

            // Get the details for a given match id
            MatchDetail detail = stats.getMatchDetails(idMatch);

            // extract all player stats from this game
            List<MatchDetailPlayer> playersStatsOfTheMatch = detail.getPlayers();

            // Show 'em
            for(MatchDetailPlayer player : playersStatsOfTheMatch)
                System.out.println(player);
        } catch (Dota2StatsAccessException e1) {
            // Do something if an error occurs
        }

        MatchDetail matchDetail = stats.getMatchDetails(idMatch);

        System.out.println(matchDetail.didRadianWin());


        System.out.println("Время начала матча -10:00h : "+(long)(double)(matchDetail.getMatchOverview().getStartTime()));

        Date d = new Date((long)(double)(matchDetail.getMatchOverview().getStartTime()) * 1000);

        System.out.println(d);


    }

    public static void GamesofPlayer(long SteamId) throws Dota2StatsAccessException {
        try {
            MatchHistory history = stats.getMatchHistory(new MatchHistoryFilter().forAccountId(SteamId).forGameMode(GameMode.All_Pick));
            List<MatchOverview> overviews = history.getMatchOverviews();

            for (MatchOverview match : overviews)
                System.out.println(match);

        } catch (Dota2StatsAccessException e1) {
            // Do something if an error occurs
        }
    }

    public static long getTimeStamp()
    {
        long unixtime = 0;

        java.util.Date utilDate = new java.util.Date();
        java.sql.Timestamp sq = new java.sql.Timestamp(utilDate.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:sss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+10:00"));//Specify your timezone

        //System.out.println(sdf.format(sq));
       // System.out.println(sq);
        Date currentDate = new Date();
        //unixtime = currentDate.getTime() / 1000;
        unixtime = sq.getTime()/1000;
        //System.out.println(unixtime);
        return unixtime;
    }
}
