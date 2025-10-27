package com.peter.APIS.NFL;

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
import com.peter.Sports.backend.Model.NFLPlayer;

public class NFLPlayerId {
    @SuppressWarnings("unchecked")
    public static List<NFLPlayer> fetchNFLPlayerInfo(){
        Set<String> positionsWanted = new HashSet<>(Arrays.asList(
                 "WR","TE","RB","QB", "PK"
        ));
        HashMap<String,Object> playersInfoId = new HashMap<>();
        List<String> ids = getNFLTeamsId();
        for(String id : ids){
            String url = String.format("https://site.api.espn.com/apis/site/v2/sports/football/nfl/teams/%s?enable=roster,projection,stats", id);
            HashMap<String,Object> currentTeamRoster = jsonResponseAPI(url);
            Object itemsObj = currentTeamRoster.get("team");
            if(itemsObj instanceof Map<?, ?>){
                HashMap<String,Object> currentTeamMap = (HashMap<String,Object>) itemsObj;
                List<Map<String,Object>> logos = (List<Map<String,Object>>) currentTeamMap.get("logos");
                String teamName = String.valueOf(currentTeamMap.get("displayName"));
                String teamLogoUrl = logos != null && !logos.isEmpty() ? 
                String.valueOf(logos.get(0).get("href")) : "N/A";

                List<Map<String,Object>> athletes = (List<Map<String,Object>>) currentTeamMap.get("athletes");
                for(Map<String,Object> athlete : athletes){
                    HashMap<String,Object> positonMap = (HashMap<String,Object>) athlete.get("position");
                    if(positonMap == null || !positionsWanted.contains(String.valueOf(positonMap.get("abbreviation")))){
                        continue;
                    }
                    HashMap<String,Object> playersInfo = new HashMap<>();
                    HashMap<String,Object> headshot = (HashMap<String,Object>) athlete.get("headshot");
                    String headshotUrl = headshot != null ? String.valueOf(headshot.getOrDefault("href","N/A")) : "N/A";
                    HashMap<String,Object> experienceMap = (HashMap<String,Object>) athlete.get("experience");

                    int age = safeParseInt(athlete.get("age"));
                    int experience = safeParseInt(experienceMap.get("years"));
                    String jerseyNum = athlete.get("jersey") != null ? 
                    String.valueOf(athlete.getOrDefault("jersey", "?")) : "?";

                    playersInfo.put("id", String.valueOf(athlete.get("id")));
                    playersInfo.put("displayName", String.valueOf(athlete.get("displayName")));
                    playersInfo.put("currentTeam",teamName);
                    playersInfo.put("weight",String.valueOf(athlete.get("displayWeight")));
                    playersInfo.put("height",String.valueOf(athlete.get("displayHeight")));
                    playersInfo.put("age",age);
                    playersInfo.put("position", String.valueOf(positonMap.get("abbreviation")));
                    playersInfo.put("jersey",jerseyNum);
                    playersInfo.put("experience", experience);
                    playersInfo.put("headshot",headshotUrl);
                    playersInfo.put("currentTeamLogo",teamLogoUrl);
                    playersInfoId.put(String.valueOf(playersInfo.get("id")),playersInfo);
                }
            }
        }
        List<HashMap<String,Object>> playerList = new ArrayList<>();
        for(String key : playersInfoId.keySet()){
            HashMap<String,Object> playerHashMapInfo = (HashMap<String,Object>) playersInfoId.get(key);
            String id = key;
            String position = String.valueOf(playerHashMapInfo.get("position"));
            int experience = Integer.valueOf(String.valueOf(playerHashMapInfo.get("experience")));
            playerList.add(playerHashMapInfo);

            if(experience > 1){
                int years = experience >= 5 ? 5 : experience - 1;
                int previousYear = previousSeasonYearStart();
                int listIndex = 0;
                for(int year = 0; year < years; year++){
                    String previousSeasonYear = String.valueOf(previousYear - year);
                    HashMap<String,Object> previousSeasonStats = getSeasonStats(previousSeasonYear, id, position);
                    if(!String.valueOf(previousSeasonStats.get(previousSeasonYear))
                    .equalsIgnoreCase("N/A")){
                        Map<String,Object> seasonData = (Map<String,Object>) previousSeasonStats.get(previousSeasonYear);
                        seasonData.putAll(getSeasonTeamName(id, listIndex));
                        listIndex++;
                    } else {
                        HashMap<String,Object> didNotplay = new HashMap<>();
                        didNotplay.put("team", "N/A");
                        didNotplay.put("teamLogo","N/A");
                        didNotplay.put("stats", "N/A");
                        previousSeasonStats.put(previousSeasonYear,didNotplay);
                    }
                    playerHashMapInfo.put(previousSeasonYear, previousSeasonStats.get(previousSeasonYear));
                }
            }
        }

        List<NFLPlayer> allPlayers = new ArrayList<>();
        for(HashMap<String,Object> player : playerList){
            String position = String.valueOf(player.get("position"));
            Set<String> neededStats = getNeededStats(position);
            NFLPlayer nflPlayer = buildNFLPlayer(player, position, neededStats);
            allPlayers.add(nflPlayer);
        }
        return allPlayers;

    }

    private static HashMap<String,Object> jsonResponseAPI(String url){
        HashMap<String,Object> result = new HashMap<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        System.out.println(url);
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return result;
            }

            int delayMs = 1000 + new Random().nextInt(1000);
            Thread.sleep(delayMs);

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response.body(), new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String,Object> getSeasonStats(String year, String id, String position){
        HashMap<String,Object> previousSeasonStats = new HashMap<>();

        Set<String> neededStats = getNeededStats(position);
        Set<String> namesNeeded = getNeedNames(position);
        HashMap<String,Object> currentSeasonStats = new HashMap<>();
        

        String url = String.format("https://sports.core.api.espn.com/v2/sports/football/leagues/nfl/seasons/%s/types/2/athletes/%s/statistics/0?lang=en&region=us", year, id);
        HashMap<String,Object> statisticLog = jsonResponseAPI(url);
        if(statisticLog.containsKey("error") || !statisticLog.containsKey("splits")){
            previousSeasonStats.put(year, "N/A");
            return previousSeasonStats;
        }
        HashMap<String,Object> statisticLogSplits = (HashMap<String,Object>) statisticLog.get("splits");
        List<Map<String,Object>> categories = (List<Map<String,Object>>) statisticLogSplits.get("categories");

        for(Map<String,Object> category : categories){ 

            if(namesNeeded.contains(String.valueOf(category.get("name")))){
                List<Map<String,Object>> statList = (List<Map<String,Object>>) category.get("stats");
                for(Map<String,Object> stat : statList){
                    if(neededStats.contains(String.valueOf(stat.get("name")))){
                        HashMap<String,Object> valueAndRank = new HashMap<>();
                        valueAndRank.put("value", stat.get("value"));
                        valueAndRank.put("rank", stat.get("rank"));                        
                        currentSeasonStats.put(String.valueOf(stat.get("name")),valueAndRank);
                    } else {
                        continue; 
                    }
                }
            } else {
                continue;
            }
        }
        HashMap<String,Object> currentSeasonTotalsAndRank = new HashMap<>();
        currentSeasonTotalsAndRank.put("seasonTotalsAndRank",currentSeasonStats);
        previousSeasonStats.put(year,currentSeasonTotalsAndRank);
        return previousSeasonStats;
    }


    @SuppressWarnings("unchecked")
    public static HashMap<String,Object> getSeasonTeamName(String id, int listIndex){
        HashMap<String,Object> previousSeasonTeam = new HashMap<>();
        String url = String.format("https://sports.core.api.espn.com/v2/sports/football/leagues/nfl/athletes/%s/statisticslog?lang=en&region=us", id);
        HashMap<String,Object> entriesAPI = jsonResponseAPI(url);
        if(entriesAPI.containsKey("entries") && !entriesAPI.containsKey("error")){
            List<Map<String,Object>> entries = (List<Map<String,Object>>) entriesAPI.get("entries");
            if (listIndex >= entries.size()) {
                previousSeasonTeam.put("team","N/A");
                previousSeasonTeam.put("teamLogo", "N/A");
                return previousSeasonTeam;
            }
            HashMap<String,Object> seasonStatistics = (HashMap<String,Object>) entries.get(listIndex);
            List<Map<String,Object>> statistics = (List<Map<String,Object>>) seasonStatistics.get("statistics");
            if(statistics.size() > 1){
                HashMap<String,Object> teamStatisticsReferences = (HashMap<String,Object>) statistics.get(1);
                HashMap<String,Object> teamHashMap = (HashMap<String,Object>) teamStatisticsReferences.get("team");
                String teamUrl = String.valueOf(teamHashMap.get("$ref"));
                HashMap<String,Object> teamInfoAPI = jsonResponseAPI(teamUrl);
                previousSeasonTeam.put("team",String.valueOf(teamInfoAPI.get("displayName")));
                List<Map<String,Object>> logos = (List<Map<String,Object>>) teamInfoAPI.get("logos");
                previousSeasonTeam.put("teamLogo", String.valueOf(logos.get(0).get("href")));
                return previousSeasonTeam;
            }
        }
        previousSeasonTeam.put("team","N/A");
        previousSeasonTeam.put("teamLogo", "N/A");
        return previousSeasonTeam;
    }

    private static NFLPlayer buildNFLPlayer(HashMap<String,Object> playerInfo, String Position, Set<String> neededStats){
        NFLPlayer player = new NFLPlayer();
        player.setId(String.valueOf(playerInfo.get("id")));
        player.setName(String.valueOf(playerInfo.get("displayName")));
        player.setTeam(String.valueOf(playerInfo.get("currentTeam")));
        player.setWeight(String.valueOf(playerInfo.get("weight")));
        player.setHeight(String.valueOf(playerInfo.get("height")));
        player.setAge(Integer.valueOf(String.valueOf(playerInfo.get("age"))));
        player.setPos(String.valueOf(playerInfo.get("position")));
        player.setJersey(String.valueOf(playerInfo.get("jersey")));
        player.setExperience(Integer.valueOf(String.valueOf(playerInfo.get("experience"))));
        player.setHeadshotUrl(String.valueOf(playerInfo.get("headshot")));
        player.setCurrentTeamLogoUrl(String.valueOf(playerInfo.get("currentTeamLogo")));

        HashMap<String, Object> statsOnly = new HashMap<>(playerInfo);
        statsOnly.keySet().removeAll(Arrays.asList(
        "id","displayName", "currentTeam", "weight", "height", "age", "position", "jersey", "experience", "headshot",
        "currentTeamLogo"
        ));
        player.setSeasonStats(statsOnly);
        return player;
    }

    private static Set<String> getNeedNames(String position){
        Set<String> namesNeeded = new HashSet<>();
        Set<String> qbNamesNeeded = new HashSet<>(Arrays.asList("general", "passing", "rushing"));
        Set<String> rbAndWrNamesNeeded = new HashSet<>(Arrays.asList("general", "rushing", "receiving"));
        if(position.equalsIgnoreCase("QB")){
            namesNeeded = qbNamesNeeded;
        } else if(position.equalsIgnoreCase("WR") || position.equalsIgnoreCase("TE")){
            namesNeeded = rbAndWrNamesNeeded;
        } else if(position.equalsIgnoreCase("RB")){
            namesNeeded = rbAndWrNamesNeeded;
        } else if(position.equalsIgnoreCase("PK")){
            namesNeeded = new HashSet<>(Arrays.asList("kicking"));
        }
        return namesNeeded;
    }

    private static Set<String> getNeededStats(String position){
        Set<String> neededStats = new HashSet<>();
        Set<String> wrNeededStats = new HashSet<>(Arrays.asList("gamesPlayed",
            "receivingBigPlays","receivingTargets",
            "receivingTouchdowns","receivingYards","receivingYardsPerGame",
            "receptions","yardsPerReception", "receivingFumbles","receivingFumblesLost",
            "totalTouchdowns", "rushingYards",
            "rushingTouchdowns"
        )); 
        Set<String> rbNeededStats = new HashSet<>(Arrays.asList("gamesPlayed",
            "rushingAttempts","rushingBigPlays","rushingFumbles",
            "rushingFumblesLost", "rushingTouchdowns", "rushingYards", 
            "rushingYardsPerGame","totalTouchdowns","receptions",
            "yardsPerReception","receivingYards", "receivingTouchdowns"
        ));

        Set<String> qbStatsNeeded = new HashSet<>(Arrays.asList("gamesPlayed","completionPct", "completions", 
            "QBRating", "interceptionPct", "interceptions",
            "passingAttempts", "passingTouchdownPct", "passingTouchdowns", "passingYards", "passingYardsPerGame",
            "totalTouchdowns", "yardsPerCompletion","yardsPerGame", 
            "yardsPerPassAttempt", "rushingTouchdowns", "rushingYards", "rushingYardsPerGame"
        ));
        Set<String> kickerStatsNeeded = new HashSet<>(Arrays.asList("gamesPlayed","extraPointAttempts", 
            "extraPointPct", "extraPointsMade", "fieldGoalAttempts","fieldGoalAttempts1_19",
            "fieldGoalAttempts20_29", "fieldGoalAttempts30_39", "fieldGoalAttempts40_49", "fieldGoalAttempts50",
            "fieldGoalPct", "fieldGoalsMade", "fieldGoalsMade1_19", "fieldGoalsMade20_29", "fieldGoalsMade30_39",
            "fieldGoalsMade40_49", "fieldGoalsMade50"));
        if(position.equalsIgnoreCase("QB")){
            neededStats = qbStatsNeeded;
        } else if(position.equalsIgnoreCase("WR") || position.equalsIgnoreCase("TE")){
            neededStats = wrNeededStats;
        } else if(position.equalsIgnoreCase("RB")){
            neededStats = rbNeededStats;
        } else if(position.equalsIgnoreCase("PK")){
            neededStats = kickerStatsNeeded;
        }
        return neededStats;
    }

    @SuppressWarnings("unchecked")
    private static List<String> getNFLTeamsId(){
        List<String> ids = new ArrayList<>();
        String url = "https://site.api.espn.com/apis/site/v2/sports/football/nfl/teams";
        HashMap<String,Object> teamAPI = jsonResponseAPI(url);
        List<Map<String,Object>> sports = (List<Map<String,Object>>) teamAPI.get("sports");
        List<Map<String,Object>> leagues = (List<Map<String,Object>>) sports.get(0).get("leagues");
        List<Map<String,Object>> teams = (List<Map<String,Object>>) leagues.get(0).get("teams");
        for(Map<String,Object> team : teams){
            HashMap<String,Object> currentTeam = (HashMap<String,Object>) team.get("team");
            ids.add(String.valueOf(currentTeam.get("id")));
        }
        return ids;
        
    }

    private static int previousSeasonYearStart(){
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        return currentDate.getMonthValue() < 3 ? currentYear - 2 : currentYear - 1;
    }
    private static int safeParseInt(Object value){
        if(value == null){
            return -1;
        }
        try{
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e){
            return -1;
        }
    }
}