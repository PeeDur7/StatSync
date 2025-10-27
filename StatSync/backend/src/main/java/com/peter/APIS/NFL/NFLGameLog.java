package com.peter.APIS.NFL;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NFLGameLog {
    @SuppressWarnings("unchecked")
    public static HashMap<String,Object> getCurrentSeasonGameLog(String id){
        int nflSeasonYear = getNFLSeasonYear();
        String currentYear = String.valueOf(nflSeasonYear);
        String url = String.format("https://site.web.api.espn.com/apis/common/v3/sports/football/nfl/athletes/%s/gamelog", id);
        HashMap<String,Object> jsonResponse = jsonResponseAPI(url);
        HashMap<String,Object> currentSeasonGameLog = new HashMap<>();
        if(!jsonResponse.containsKey("events") || jsonResponse.containsKey("code")){
            currentSeasonGameLog.put("gameLog","N/A");
            return currentSeasonGameLog;
        }

        List<String> labels = labelsBasedOnPosition(jsonResponse);

        List<Map<String,Object>> seasonTypes = (List<Map<String,Object>>) jsonResponse.get("seasonTypes");
        boolean foundMatchingSeason = false;
        List<Map<String,Object>> weekList = new ArrayList<>();

        for(Map<String,Object> seasonType : seasonTypes){
            String displayName = String.valueOf(seasonType.get("displayName"));
            if(displayName.contains(currentYear) && displayName.contains("Regular")){
                List<Map<String,Object>> categories = (List<Map<String,Object>>) seasonType.get("categories");
                weekList = (List<Map<String,Object>>) categories.get(0).get("events");
                foundMatchingSeason = true;
                break;
            }
        }
        if(!foundMatchingSeason){
            currentSeasonGameLog.put("gameLog","N/A");
            return currentSeasonGameLog;
        }
        Set<String> week1to18 = new HashSet<>(Arrays.asList(
            "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18"
        ));
        HashMap<String,Object> events = (HashMap<String,Object>) jsonResponse.get("events");
        for(String key : events.keySet()){
            String gameId = String.valueOf(key);
            HashMap<String,Object> gameInformation = (HashMap<String,Object>) events.get(key);
            if(gameInformation.containsKey("eventNote")){
                Set<String> playOffKeyWords = new HashSet<>(Arrays.asList(
                    "Wild Card", "Divisonal", "Championship", "Super Bowl", "Playoffs","AFC","NFC"
                ));
                if(playOffKeyWords.contains(String.valueOf(gameInformation.get("eventNote")))){
                    continue;
                }
            }
            HashMap<String,Object> gameOpponent = new HashMap<>();

            OffsetDateTime dateTime = OffsetDateTime.parse(String.valueOf(gameInformation.get("gameDate")));
            String formattedGameDate = String.valueOf(dateTime.getMonthValue()) + "/" + String.valueOf(dateTime.getDayOfMonth());
            String week = String.valueOf(gameInformation.get("week"));
            week1to18.remove(week);
            gameOpponent.put("week",week);
            gameOpponent.put("atVs", String.valueOf(gameInformation.get("atVs")));
            gameOpponent.put("gameDate", String.valueOf(formattedGameDate));
            gameOpponent.put("score", String.valueOf(gameInformation.get("score")));
            gameOpponent.put("gameResult", String.valueOf(gameInformation.get("gameResult")));

            HashMap<String,Object> opponentInfo = (HashMap<String,Object>) gameInformation.get("opponent");
            gameOpponent.put("opponent", String.valueOf(opponentInfo.get("abbreviation")));
            currentSeasonGameLog.put(gameId,gameOpponent);
        }

        for(Map<String,Object> week : weekList){
            String eventId = String.valueOf(week.get("eventId"));
            List<String> stats = (List<String>) week.get("stats");
            HashMap<String,Object> statHashMap = new HashMap<>();
            for(int i = 0; i < labels.size(); i++){
                statHashMap.put(labels.get(i),stats.get(i));
            }
            HashMap<String, Object> existingWeekInfo = (HashMap<String, Object>) 
            currentSeasonGameLog.getOrDefault(eventId, new HashMap<>());
            existingWeekInfo.put("stats",statHashMap);
            String currentWeek = "Week " + String.valueOf(existingWeekInfo.get("week"));
            currentSeasonGameLog.put(currentWeek,existingWeekInfo);
            currentSeasonGameLog.remove(eventId);
        }
        if(week1to18.size() > 0){
            for(String week : week1to18){
                HashMap<String,Object> dnpWeek = new HashMap<>();
                dnpWeek.put("week","N/A");
                dnpWeek.put("atVs","N/A");
                dnpWeek.put("gameDate","N/A");
                dnpWeek.put("score","N/A");
                dnpWeek.put("gameResult","N/A");
                dnpWeek.put("opponent","N/A");
                HashMap<String,String> statHashMap = new HashMap<>();
                for(int i = 0; i<labels.size(); i++){
                    statHashMap.put(labels.get(i),"N/A");
                }
                dnpWeek.put("stats",statHashMap);
                currentSeasonGameLog.put("Week " + week,dnpWeek);
            }
        }
        LinkedHashMap<String, Object> sortedWeekMap = new LinkedHashMap<>();
        for (int i = 1; i <= 18; i++) {
            String weekKey = "Week " + i;
            if (currentSeasonGameLog.containsKey(weekKey)) {
                sortedWeekMap.put(weekKey, currentSeasonGameLog.get(weekKey));
            }
        }
        currentSeasonGameLog = sortedWeekMap;
        HashMap<String,Object> currentSeasonGameLog2 = new HashMap<>();
        currentSeasonGameLog2.put("gameLog", currentSeasonGameLog);
        return currentSeasonGameLog2;
    }

    private static int getNFLSeasonYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        return currentDate.getMonthValue() < 9 ? currentYear - 1 : currentYear;
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
    private static List<String> labelsBasedOnPosition(HashMap<String,Object> jsonAPI){
        List<String> labels = (List<String>)  jsonAPI.get("labels");
        List<Map<String,Object>> categories = (List<Map<String,Object>>) jsonAPI.get("categories");
        String categoryName = String.valueOf(categories.get(0).get("name"));
        if(categoryName.equalsIgnoreCase("passing")){
            labels.remove(2);
            labels.add(2,"PASSYDS");
        } else if(categoryName.equalsIgnoreCase("receiving")){
            labels.remove(2);
            labels.add(2,"RECYDS");
        } else if(categoryName.equalsIgnoreCase("rushing")){
            labels.remove(1);
            labels.add(1,"RUSHYDS");
        }
        return labels;
    }
}
