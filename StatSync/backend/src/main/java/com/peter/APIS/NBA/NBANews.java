package com.peter.APIS.NBA;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NBANews {
    @SuppressWarnings("unchecked")
    public static List<Map<String,String>> fetchNBANews(){
        List<Map<String,String>> nbaNews = new ArrayList<>();
        String url = "https://site.api.espn.com/apis/site/v2/sports/basketball/nba/news?limit=100";
        Map<String,Object> nbaNewsAPI = jsonResponseAPI(url);
        List<Map<String,Object>> articles = (List<Map<String,Object>>) nbaNewsAPI.get("articles");
        if(articles == null){
            return nbaNews;
        }
        for(Map<String,Object> article : articles){
            Map<String,String> dictionary = new HashMap<>();
            HashMap<String,Object> links = (HashMap<String,Object>) article.get("links");
            if(links.containsKey("mobile")){
                HashMap<String,Object> linkRef = (HashMap<String,Object>) links.get("mobile");
                String formattedDate = formatDate(String.valueOf(article.get("published")));
                List<Map<String,Object>> images = (List<Map<String,Object>>) article.get("images");
                String image = (images != null && !images.isEmpty()) ? 
                String.valueOf(images.get(0).get("url")) : "N/A";
                dictionary.put("headline" , String.valueOf(article.get("headline")));
                dictionary.put("published",formattedDate);
                dictionary.put("image", image);
                dictionary.put("espnLink",String.valueOf(linkRef.get("href")));
                nbaNews.add(dictionary);
            } else {
                continue;
            }
        }
        return nbaNews;
    }

    private static HashMap<String,Object> jsonResponseAPI(String url){
        HashMap<String,Object> result = new HashMap<>();
        HttpClient client = HttpClient.newHttpClient();
        System.out.println("Fetching: " + url);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(5))
            .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Failed to fetch: " + url + " | Status: " + response.statusCode());
                return result;
            }

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response.body(), new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String formatDate(String date){
        Instant instant = Instant.parse(date);
        ZonedDateTime est = instant.atZone(ZoneId.of("America/New_York"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss");
        String formattedDate = est.format(formatter);
        String [] splitFormattedDate = formattedDate.split(" - ");
        String dateFormat = splitFormattedDate[0];
        String [] timeSplit = splitFormattedDate[1].split(":");
        String amOrPm = "";
        String hourlyTime = "";
        if(Integer.valueOf(timeSplit[0]) > 12){
            amOrPm = "P.M.";
            int hour = Integer.valueOf(timeSplit[0]) % 12;
            hourlyTime = String.valueOf(hour) + ":" + timeSplit[1];
        } else if(Integer.valueOf(timeSplit[0]) == 0){
            amOrPm = "A.M.";
            hourlyTime = "12" + ":" + timeSplit[1];
        } else {
            amOrPm = "A.M.";
            hourlyTime = timeSplit[0] + ":" + timeSplit[1];
        }

        formattedDate = dateFormat + " - " + hourlyTime + " " + amOrPm;

        return formattedDate;
    }
}
