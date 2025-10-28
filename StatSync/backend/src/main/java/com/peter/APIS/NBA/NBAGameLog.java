package com.peter.APIS.NBA;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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

public class NBAGameLog {
    @SuppressWarnings({ "unchecked", "null" })
    public static HashMap<String,Object> getCurrentSeasonGameLog(String id){
        String url = String.format("https://site.web.api.espn.com/apis/common/v3/sports/basketball/nba/athletes/%s/gamelog",id);
        HashMap<String,Object> currentSeasonGameLogAPI = jsonResponseAPI(url);
        HashMap<String,Object> currentGameLog = new HashMap<>();
        
        if(currentSeasonGameLogAPI.containsKey("code") || !currentSeasonGameLogAPI.containsKey("events")){
            currentGameLog.put("gameLog","N/A");
            return currentGameLog;
        }
        
        if(!currentSeasonGameLogAPI.containsKey("labels") || !currentSeasonGameLogAPI.containsKey("seasonTypes")){
            currentGameLog.put("gameLog","N/A");
            return currentGameLog;
        }
        
        List<String> labels = (List<String>) currentSeasonGameLogAPI.get("labels");
        List<Map<String,Object>> seasonTypes = (List<Map<String,Object>>) currentSeasonGameLogAPI.get("seasonTypes");
        
        // Find the regular season data
        boolean foundMatchingSeason = false;
        List<Map<String,Object>> eventsList = new ArrayList<>();
        Set<String> regularSeasonGameIds = new HashSet<>(); // Track regular season game IDs
        
        for(Map<String,Object> seasonType : seasonTypes){
            String displayName = String.valueOf(seasonType.get("displayName"));
            int nbaYear = getNBASeasonYear() % 100;
            
            // Only process Regular Season games
            if(displayName.contains(String.valueOf(nbaYear)) && displayName.contains("Regular")){
                eventsList = (List<Map<String,Object>>) seasonType.get("categories");
                foundMatchingSeason = true;
                
                // Collect all regular season game IDs from categories
                if(eventsList != null){
                    for(Map<String,Object> category : eventsList){
                        List<Map<String,Object>> categoryEvents = (List<Map<String,Object>>) category.get("events");
                        if(categoryEvents != null){
                            for(Map<String,Object> event : categoryEvents){
                                regularSeasonGameIds.add(String.valueOf(event.get("eventId")));
                            }
                        }
                    }
                }
                break;
            }
        }
        
        if(!foundMatchingSeason || regularSeasonGameIds.isEmpty()){
            currentGameLog.put("gameLog","N/A");
            return currentGameLog;
        }
        
        // Now process only regular season games from the events object
        Map<String,Object> gameEventsList = (Map<String,Object>) currentSeasonGameLogAPI.get("events");
        List<Map<String,Object>> gamesToSort = new ArrayList<>();
        
        for(String key : gameEventsList.keySet()){
            if(!regularSeasonGameIds.contains(key)){
                continue;
            }
            
            String gameId = key;
            Map<String,Object> gameEventInfo = (Map<String,Object>) gameEventsList.get(key);
            
            // Additional playoff filtering (just in case)
            if(gameEventInfo.containsKey("eventNote")){
                String eventNote = gameEventInfo.get("eventNote") == null ? "" :
                    String.valueOf(gameEventInfo.get("eventNote"));
                Set<String> playoffKeyWords = new HashSet<>(Arrays.asList(
                    "East 1st Round - Game 1", "East 1st Round - Game 2", "East 1st Round - Game 3", 
                    "East 1st Round - Game 4", "East 1st Round - Game 5", "East 1st Round - Game 6", 
                    "East 1st Round - Game 7",
                    "West 1st Round - Game 1", "West 1st Round - Game 2", "West 1st Round - Game 3", 
                    "West 1st Round - Game 4", "West 1st Round - Game 5", "West 1st Round - Game 6", 
                    "West 1st Round - Game 7",
                    "East Semifinals - Game 1", "East Semifinals - Game 2", "East Semifinals - Game 3", 
                    "East Semifinals - Game 4", "East Semifinals - Game 5", "East Semifinals - Game 6", 
                    "East Semifinals - Game 7",
                    "West Semifinals - Game 1", "West Semifinals - Game 2", "West Semifinals - Game 3", 
                    "West Semifinals - Game 4", "West Semifinals - Game 5", "West Semifinals - Game 6", 
                    "West Semifinals - Game 7",
                    "East Finals - Game 1", "East Finals - Game 2", "East Finals - Game 3", 
                    "East Finals - Game 4", "East Finals - Game 5", "East Finals - Game 6", 
                    "East Finals - Game 7",
                    "West Finals - Game 1", "West Finals - Game 2", "West Finals - Game 3", 
                    "West Finals - Game 4", "West Finals - Game 5", "West Finals - Game 6", 
                    "West Finals - Game 7",
                    "NBA Finals - Game 1", "NBA Finals - Game 2", "NBA Finals - Game 3", 
                    "NBA Finals - Game 4", "NBA Finals - Game 5", "NBA Finals - Game 6", 
                    "NBA Finals - Game 7"
                ));
                
                if(playoffKeyWords.contains(eventNote.trim()) || eventNote.contains("NBA All-Star")
                    || eventNote.contains("Play In")){
                    continue;
                }
            }
            
            Map<String,Object> gameOpponent = new HashMap<>();
            OffsetDateTime dateTime = OffsetDateTime.parse(String.valueOf(gameEventInfo.get("gameDate")));
            String formattedGameDate = String.valueOf(dateTime.getMonthValue()) + "/" + String.valueOf(dateTime.getDayOfMonth());
            
            gameOpponent.put("atVs",String.valueOf(gameEventInfo.get("atVs")));
            gameOpponent.put("gameDate",formattedGameDate);
            gameOpponent.put("gameDateSort", dateTime); 
            gameOpponent.put("score",String.valueOf(gameEventInfo.get("score")));
            gameOpponent.put("gameResult",String.valueOf(gameEventInfo.get("gameResult")));
            gameOpponent.put("gameId", gameId);

            Map<String,Object> opponentMap = (Map<String,Object>) gameEventInfo.get("opponent");
            gameOpponent.put("opponent",String.valueOf(opponentMap.get("abbreviation")));
            
            gamesToSort.add(gameOpponent);
        }
        
        // Sort games by date (oldest to newest)
        gamesToSort.sort((g1, g2) -> {
            OffsetDateTime date1 = (OffsetDateTime) g1.get("gameDateSort");
            OffsetDateTime date2 = (OffsetDateTime) g2.get("gameDateSort");
            return date1.compareTo(date2);
        });
        
        // Add sorted games to currentGameLog
        for(Map<String,Object> game : gamesToSort){
            String gameId = (String) game.remove("gameId");
            game.remove("gameDateSort"); 
            currentGameLog.put(gameId, game);
        }

        for(Map<String,Object> event : eventsList){
            List<Map<String,Object>> eventListForMonth = (List<Map<String,Object>>) event.get("events");
            
            if(eventListForMonth == null || eventListForMonth.isEmpty()){
                continue; 
            }
            
            for(Map<String,Object> gameID : eventListForMonth){
                String eventID = String.valueOf(gameID.get("eventId"));

                if(!currentGameLog.containsKey(eventID)){
                    continue;
                }

                HashMap<String,Object> statHashMap = new HashMap<>();
                List<String> statValues = (List<String>) gameID.get("stats");
                
                if(statValues == null){
                    continue;
                }

                for(int i = 0; i < labels.size(); i++){
                    statHashMap.put(labels.get(i),statValues.get(i));
                }
                HashMap<String,Object> existingInfo = (HashMap<String,Object>) currentGameLog.get(eventID);
                existingInfo.put("stats", statHashMap);
                currentGameLog.put(eventID,existingInfo); 
            }
        }

        HashMap<String,Object> gameLogMap = new HashMap<>();
        gameLogMap.put("gameLog",currentGameLog);
        return gameLogMap;
    }

    private static int getNBASeasonYear(){
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear(); 
        return currentDate.getMonthValue() < 10 ? currentYear : currentYear + 1;
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

            int delayMs = 2000 + new Random().nextInt(3000);
            Thread.sleep(delayMs);

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response.body(), new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
