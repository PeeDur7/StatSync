package com.peter.APIS.NBA;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.Sports.backend.Model.NBAPlayer;

public class NBAPlayerInfo {

    @SuppressWarnings("unchecked")
    public static List<NBAPlayer> fetchNBAPlayersInfo(){
        HashMap<String,Object> playersAPIMap = loopTeamsAndGetAllPlayers();
        List<HashMap<String,Object>> playerList = new ArrayList<>();
        for(String key : playersAPIMap.keySet()){
            HashMap<String,Object> playersInfo = (HashMap<String,Object>) playersAPIMap.get(key);
            String id = key;
            int experience = Integer.valueOf(String.valueOf(playersInfo.get("experience")));
            if(experience == 0){
                String currentYearString = String.valueOf(getNBASeasonYear());
                HashMap<String,Object> seasonStats = getSeasonStats(currentYearString, id);
                if(!String.valueOf(seasonStats.get(currentYearString)).equalsIgnoreCase("N/A")){
                    Map<String,Object> seasonData = (Map<String,Object>) seasonStats.get(currentYearString);
                    seasonData.putAll(getSeasonTeam(id, 0));
                } else {
                    HashMap<String,Object> didNotplay = new HashMap<>();
                    didNotplay.put("team", "N/A");
                    didNotplay.put("teamLogo","N/A");
                    didNotplay.put("stats", "N/A");
                    seasonStats.put(currentYearString,didNotplay);
                }
                playersInfo.put(currentYearString, seasonStats.get(currentYearString));
            } else {
                int nbaSeason = getNBASeasonYear();
                int years = experience > 5 ? 5 : experience;
                int listIndex = 0;
                for(int year = 0; year < years; year++){
                    String currentYearSearch = String.valueOf(nbaSeason-year);
                    HashMap<String,Object> currentPreviousSeasonStats = getSeasonStats(currentYearSearch, id);
                    if(!String.valueOf(currentPreviousSeasonStats.get(currentYearSearch))
                    .equalsIgnoreCase("N/A")){
                        Map<String,Object> seasonData = (Map<String,Object>) currentPreviousSeasonStats.get(currentYearSearch);
                        seasonData.putAll(getSeasonTeam(id, listIndex));
                        listIndex++;
                    } else {
                        HashMap<String,Object> didNotplay = new HashMap<>();
                        didNotplay.put("team", "N/A");
                        didNotplay.put("teamLogo","N/A");
                        didNotplay.put("stats", "N/A");
                        currentPreviousSeasonStats.put(currentYearSearch,didNotplay);
                    }
                    playersInfo.put(String.valueOf(nbaSeason), currentPreviousSeasonStats.get(currentYearSearch));
                    nbaSeason--;
                }
            }
            playerList.add(playersInfo);
        }

        List<NBAPlayer> allPlayers = new ArrayList<>();
        for(HashMap<String,Object> playerInfo : playerList){
            NBAPlayer player = buildNBAPlayer(playerInfo);
            allPlayers.add(player);
        }
        return allPlayers;
    }

    private static HashMap<String,Object> jsonResponseAPI(String url){
        HashMap<String,Object> result = new HashMap<>();
        HttpClient client = HttpClient.newHttpClient();
        System.out.println("Fetching: " + url);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Failed to fetch: " + url + " | Status: " + response.statusCode());
                return result;
            }

            int delayMs = 2000 + new Random().nextInt(1500);
            Thread.sleep(delayMs);

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response.body(), new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String,Object> loopTeamsAndGetAllPlayers(){
        String firstHalfUrl = "https://site.api.espn.com/apis/site/v2/sports/basketball/nba/teams/";
        String secondHalfUrl = "/roster";
        HashMap<String,Object> playersBaseInfo = new HashMap<>();
        for(int i = 1; i <= 30; i++){
            String teamUrl = firstHalfUrl + i;
            HashMap<String,Object> teamInfoMap = jsonResponseAPI(teamUrl);
            Map<String,Object> teamDict = (Map<String,Object>) teamInfoMap.get("team");
            if(teamDict == null){
                continue;
            }
            List<Map<String,Object>> logos = (List<Map<String,Object>>) teamDict.get("logos");

            String teamLogoUrl = logos != null && !logos.isEmpty() ? 
            String.valueOf(logos.get(0).get("href")) : "N/A";

            String teamName = String.valueOf(teamDict.get("displayName"));

            String rosterUrl = teamUrl + secondHalfUrl;
            HashMap<String,Object> playerRoster = jsonResponseAPI(rosterUrl);
            if(playerRoster.containsKey("athletes")){
                List<Map<String,Object>> atheleteList = (List<Map<String,Object>>) playerRoster.get("athletes");
                for(Map<String,Object> player: atheleteList){
                    Map<String,Object> dictionary = new HashMap<>();
                    Map<String,Object> headshotDict = (Map<String,Object>) player.get("headshot");
                    Map<String,Object> positionDict = (Map<String,Object>) player.get("position");
                    Map<String,Object> experienceDict = (Map<String,Object>) player.get("experience");
                        
                    String headshotUrl = headshotDict != null ? String.valueOf(headshotDict.getOrDefault("href", "N/A")) : "N/A";
                    int experience = experienceDict != null && experienceDict.get("years") != null ? 
                    Integer.valueOf(String.valueOf(experienceDict.get("years"))) : -1;
                    String jerseyNum = player.get("jersey") != null ? 
                    String.valueOf(player.getOrDefault("jersey", "?")) : "?";
                    int age = player.get("age") != null ? Integer.valueOf(String.valueOf(player.get("age"))) : -1;

                    dictionary.put("id",String.valueOf(player.get("id")));
                    dictionary.put("name", String.valueOf(player.get("displayName")));
                    dictionary.put("weight",String.valueOf(player.get("displayWeight")));
                    dictionary.put("height",String.valueOf(player.get("displayHeight")));
                    dictionary.put("age", age);
                    dictionary.put("headshot",headshotUrl);
                    dictionary.put("jersey", jerseyNum);
                    dictionary.put("position",String.valueOf(positionDict.get("abbreviation")));
                    dictionary.put("experience", experience);
                    dictionary.put("currentTeam",teamName);
                    dictionary.put("currentTeamLogo",teamLogoUrl);
                    playersBaseInfo.put(String.valueOf(dictionary.get("id")),dictionary);

                }
            }
        }
        return playersBaseInfo;
    }
    
    @SuppressWarnings("unchecked")
    public static HashMap<String,Object> getSeasonStats(String year, String id){
        Set<String> neededStats = new HashSet<>(Arrays.asList(
            "blocks", "defensiveRebounds","steals", "avgDefensiveRebounds", "avgBlocks", "avgSteals",
            "fouls","disqualifications", "rebounds", "avgMinutes", "NBARating",
            "avgRebounds", "avgFouls", "gamesPlayed", "gamesStarted", "doubleDouble", "tripleDouble", "assists",
            "fieldGoalsAttempted", "fieldGoalsMade", "fieldGoalPct", "freeThrowPct", "freeThrowsAttempted", "freeThrowsMade",
            "offensiveRebounds", "points", "turnovers", "threePointPct", "threePointFieldGoalsAttempted", "threePointFieldGoalsMade",
            "totalTurnovers", "avgPoints", "avgOffensiveRebounds", "avgAssists", "avgTurnovers"
        ));
        String url = String.format("https://sports.core.api.espn.com/v2/sports/basketball/leagues/nba/seasons/%s/types/2/athletes/%s/statistics/0?lang=en&region=us", year,id);
        HashMap<String,Object> seasonStats = new HashMap<>();
        HashMap<String,Object> seasonStatsAPI = jsonResponseAPI(url);
        if(seasonStatsAPI.containsKey("error") || !seasonStatsAPI.containsKey("splits")){
            seasonStats.put(year,"N/A");
            return seasonStats;
        }
        HashMap<String,Object> splits = (HashMap<String,Object>) seasonStatsAPI.get("splits");
        List<Map<String,Object>> categories = (List<Map<String,Object>>) splits.get("categories");
        HashMap<String,Object> currentStats = new HashMap<>();
        for(Map<String,Object> category : categories){
            List<Map<String,Object>> stats = (List<Map<String,Object>>) category.get("stats");
            for(Map<String,Object> stat : stats){
                if(!neededStats.contains(String.valueOf(stat.get("name")))){
                    continue;
                } else {
                    HashMap<String,Object> valueAndRank = new HashMap<>();
                    valueAndRank.put("value", stat.get("value"));
                    valueAndRank.put("rank", stat.get("rank"));                    
                    currentStats.put(String.valueOf(stat.get("name")),valueAndRank);
                }
            }
        }
        HashMap<String,Object> seasonTotalAndRank = new HashMap<>();
        seasonTotalAndRank.put("seasonTotalsAndRank",currentStats);
        seasonStats.put(year,seasonTotalAndRank);

        return seasonStats;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String,Object> getSeasonTeam(String id, int listIndex){
        HashMap<String,Object> previousSeasonTeam = new HashMap<>();
        String url = String.format("https://sports.core.api.espn.com/v2/sports/basketball/leagues/nba/athletes/%s/statisticslog?lang=en&region=us", id);
        HashMap<String,Object> jsonAPI = jsonResponseAPI(url);
        if(!jsonAPI.containsKey("error") && jsonAPI.containsKey("entries")){
            List<Map<String,Object>> entries = (List<Map<String,Object>>) jsonAPI.get("entries");
            if(listIndex >= entries.size()){
                previousSeasonTeam.put("team","N/A");
                previousSeasonTeam.put("teamLogo", "N/A");
                return previousSeasonTeam;
            }
            HashMap<String,Object> seasonHashMap = (HashMap<String,Object>) entries.get(listIndex);
            List<Map<String,Object>> statistics = (List<Map<String,Object>>) seasonHashMap.get("statistics");
            if(statistics.size() > 1){
                HashMap<String,Object> teamRef = (HashMap<String,Object>) statistics.get(1);
                HashMap<String,Object> teamHashMap = (HashMap<String,Object>) teamRef.get("team");
                String teamUrl = String.valueOf(teamHashMap.get("$ref"));
                HashMap<String,Object> teamAPI = jsonResponseAPI(teamUrl);
                previousSeasonTeam.put("team",String.valueOf(teamAPI.get("displayName")));
                List<Map<String,Object>> logos = (List<Map<String,Object>>) teamAPI.get("logos");
                previousSeasonTeam.put("teamLogo",logos.get(0).get("href"));
                return previousSeasonTeam;
            }
        }
        previousSeasonTeam.put("team","N/A");
        previousSeasonTeam.put("teamLogo","N/A");
        return previousSeasonTeam;
    }

    private static NBAPlayer buildNBAPlayer(HashMap<String,Object> playerInfo){
        NBAPlayer player = new NBAPlayer();
        player.setId(String.valueOf(playerInfo.get("id")));
        player.setName(String.valueOf(playerInfo.get("name")));
        player.setWeight(String.valueOf(playerInfo.get("weight")));
        player.setAge(Integer.valueOf(String.valueOf(playerInfo.get("age"))));
        player.setJersey(String.valueOf(playerInfo.get("jersey")));
        player.setPos(String.valueOf(playerInfo.get("position")));
        player.setExperience(Integer.valueOf(String.valueOf(playerInfo.get("experience"))));
        player.setHeadshotUrl(String.valueOf(playerInfo.get("headshot")));
        player.setCurrentTeamLogo(String.valueOf(playerInfo.get("currentTeamLogo")));
        player.setTeam(String.valueOf(playerInfo.get("currentTeam")));

        HashMap<String, Object> statsOnly = new HashMap<>(playerInfo);
        statsOnly.keySet().removeAll(Arrays.asList(
            "name", "currentTeam", "weight", "height", "age", "position", "jersey", "experience", "headshot",
            "currentTeamLogo","id"
        ));
        player.setStats(statsOnly);
        return player;
    }

    private static int getNBASeasonYear(){
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear(); 
        return currentDate.getMonthValue() < 10 ? currentYear : currentYear + 1;
    }

}
