package com.peter.Sports.backend.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.peter.APIS.NBA.NBAGameLog;
import com.peter.APIS.NBA.NBANews;
import com.peter.APIS.NBA.NBAPlayerInfo;
import com.peter.Sports.backend.Model.NBAPlayer;
import com.peter.Sports.backend.Model.Users;
import com.peter.Sports.backend.Repository.NBAPlayerRepository;
import com.peter.Sports.backend.Repository.UsersRepository;

@Service
public class NBAPlayerService {
    private final NBAPlayerRepository nbaPlayerRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public NBAPlayerService(NBAPlayerRepository nbaPlayerRepository, UsersRepository usersRepository){
        this.nbaPlayerRepository = nbaPlayerRepository;
        this.usersRepository = usersRepository;
    }

    @Scheduled(cron = "0 0 0 1 10 *", zone = "America/New_York") 
    public void loadAllNBAPlayers(){
        List<NBAPlayer> players = NBAPlayerInfo.fetchNBAPlayersInfo();
        nbaPlayerRepository.deleteAll();
        nbaPlayerRepository.saveAll(players);
        System.out.println("success loading nba players");
    }
    
    @SuppressWarnings("unchecked")
    @Scheduled(cron = "0 0 2 * * ?", zone = "America/New_York") //2 am everyday
    public void getOrUpdateNBAPlayerCurrentSeason(){
        String gameLogYear = String.valueOf(getNBASeasonYear());
        int month = LocalDate.now().getMonthValue();
        int pageSize = 50;  // Process 50 players at a time
        int batchSize = 20;  // Keep your original batch processing within each page
        int pageNumber = 0;
        
        Page<NBAPlayer> page;
        do {
            System.out.println("Processing NBA page " + pageNumber + "...");
            
            // Fetch one page of players from database
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            page = nbaPlayerRepository.findAll(pageable);
            List<NBAPlayer> players = page.getContent();
            
            // Process this page in batches (your original batching logic)
            for(int i = 0; i < players.size(); i += batchSize){
                int end = Math.min(i + batchSize, players.size());
                List<NBAPlayer> batch = players.subList(i, end);
                
                List<NBAPlayer> playersUpdatedList = new ArrayList<>();
                List<NBAPlayer> playersUpdatedGameLogs = new ArrayList<>();
                
                // First loop - update team info (O(batchSize))
                for(NBAPlayer player : batch){
                    HashMap<String,Object> currentTeam = NBAPlayerInfo.getSeasonTeam(player.getId(), 0);
                    HashMap<String,Object> seasonStatsMap = (HashMap<String,Object>) player.getStats();
                    String team = String.valueOf(currentTeam.get("team"));
                    String teamLogo = String.valueOf(currentTeam.get("teamLogo"));
                    
                    if(seasonStatsMap.containsKey(gameLogYear)){
                        if(month >= 10 || 
                            (getNBASeasonYear() < LocalDate.now().getYear() && month < 7)){
                            HashMap<String,Object> seasonStats = (HashMap<String,Object>) seasonStatsMap.get(gameLogYear);
                            seasonStats.put("team", team);
                            seasonStats.put("teamLogo", teamLogo);
                        }
                    } else {
                        HashMap<String,Object> newSeasonStats = new HashMap<>();
                        newSeasonStats.put("team", team);
                        newSeasonStats.put("teamLogo", teamLogo);
                        seasonStatsMap.put(gameLogYear, newSeasonStats);
                    }
                    
                    player.setTeam(team);
                    player.setCurrentTeamLogo(teamLogo);
                    playersUpdatedList.add(player);
                }
            
                // Second loop - update game logs (O(batchSize))
                for(NBAPlayer player : playersUpdatedList){
                    HashMap<String,Object> gameLog = NBAGameLog.getCurrentSeasonGameLog(player.getId());
                    HashMap<String,Object> seasonStatsMap = (HashMap<String,Object>) player.getStats();
                    
                    if(!seasonStatsMap.containsKey(gameLogYear)){
                        seasonStatsMap.put(gameLogYear, new HashMap<>());
                    }
                    
                    HashMap<String,Object> seasonStats = (HashMap<String,Object>) seasonStatsMap.get(gameLogYear);
                    seasonStats.put("gameLog", gameLog.get("gameLog"));
                    playersUpdatedGameLogs.add(player);
                }
            
                // Third loop - update season totals (O(batchSize))
                List<NBAPlayer> updatedPlayers = new ArrayList<>();
                for(NBAPlayer player : playersUpdatedGameLogs){
                    HashMap<String,Object> seasonTotalsAndRank = NBAPlayerInfo.getSeasonStats(gameLogYear, player.getId());
                    HashMap<String,Object> seasonStatsMap = (HashMap<String,Object>) player.getStats();
                    
                    if(!seasonStatsMap.containsKey(gameLogYear)){
                        seasonStatsMap.put(gameLogYear, new HashMap<>());
                    }
                    
                    HashMap<String,Object> seasonStats = (HashMap<String,Object>) seasonStatsMap.get(gameLogYear);
                    if(String.valueOf(seasonTotalsAndRank.get(gameLogYear)).equalsIgnoreCase("N/A")){
                        seasonStats.put("seasonTotalsAndRank", "N/A");
                    } else {
                        HashMap<String,Object> seasonTotalsAndRankExtracted = 
                            (HashMap<String,Object>) seasonTotalsAndRank.get(gameLogYear);
                        seasonStats.put("seasonTotalsAndRank", 
                            seasonTotalsAndRankExtracted.get("seasonTotalsAndRank"));
                    }
                    updatedPlayers.add(player);
                }
                
                // Save this batch
                nbaPlayerRepository.saveAll(updatedPlayers);
                System.out.println("Saved batch of " + updatedPlayers.size() + " NBA players");
                
                // Wait between batches within the page
                if(end < players.size()){
                    try {
                        System.out.println("Waiting 30 seconds before next batch...");
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            // Wait between pages
            if(page.hasNext()){
                try {
                    System.out.println("Waiting 30 seconds before next page...");
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Update interrupted!");
                    break;
                }
            }
            
            pageNumber++;
        } while(page.hasNext());
        
        System.out.println("NBA Update Complete! Processed " + pageNumber + " pages.");
    }

    public List<NBAPlayer> getNBAPlayers(){
        return nbaPlayerRepository.findTop50By();
    }
    
    public List<NBAPlayer> getNBAPlayerByName(String name){
        return nbaPlayerRepository.findTop50ByNameContainingIgnoreCase(name);
    }

    public List<NBAPlayer> getNBAPlayerByTeam(String team){
        return nbaPlayerRepository.findTop50ByTeam(team);
    }

    public List<NBAPlayer> getNBAPlayerByPos(String pos){
        return nbaPlayerRepository.findTop50ByPos(pos);
    }

    public List<NBAPlayer> getNBAPlayerByTeamAndPos(String team, String pos){
        return nbaPlayerRepository.findTop50ByTeamAndPos(team, pos);
    }

    public NBAPlayer getNBAPlayerData(String id){
        Optional<NBAPlayer> player = nbaPlayerRepository.findById(id);
        if(player.isPresent()){
            return player.get();
        }
        return new NBAPlayer();
    }

    public ResponseEntity<?> favoriteNBAPlayer(String id, String name){
        Optional<Users> userOpt = usersRepository.findByName(name);
        if(userOpt.isEmpty()){
            return new ResponseEntity<>("User cannot be found",HttpStatus.ALREADY_REPORTED);
        }
        Users user = userOpt.get();
        List<String> nbaFavoritesId = user.getFavoriteNBA();
        if(!nbaFavoritesId.contains(id)){
            nbaFavoritesId.add(id);
            user.setFavoriteNBA(nbaFavoritesId);
            usersRepository.save(user);
            return new ResponseEntity<>("Added Player to Favorites",HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
    }

    public ResponseEntity<?> removePlayerFromNBAFavorites(String id, String name){
        Optional<Users> userOpt = usersRepository.findByName(name);
        if(userOpt.isEmpty()){
            return new ResponseEntity<>("User cannot be found",HttpStatus.ALREADY_REPORTED);
        }
        Users user = userOpt.get();
        List<String> favoriteList = user.getFavoriteNBA();
        for(int i = 0; i<favoriteList.size(); i++){
            if(favoriteList.get(i).equalsIgnoreCase(id)){
                favoriteList.remove(i);
                break;
            }
        }
        user.setFavoriteNBA(favoriteList);
        usersRepository.save(user);
        return new ResponseEntity<>("success removing player from favorites", HttpStatus.ALREADY_REPORTED);
    }

    public List<NBAPlayer> getNBAPlayerFavorites(String name){
        Optional<Users> user = usersRepository.findByName(name);
        if(user.isEmpty()){
            return Collections.emptyList();
        }
        List<String> playerIds = user.get().getFavoriteNBA();
        return playerIds.stream()
                .map(id -> nbaPlayerRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<NBAPlayer> getNBAPlayerPositionInFavorites(String name, String pos){
        List<NBAPlayer> nbaPlayerFavorites = getNBAPlayerFavorites(name);
        return nbaPlayerFavorites.stream()
                    .filter(position -> position.getPos().equalsIgnoreCase(pos))
                    .collect(Collectors.toList());
    }

    public List<NBAPlayer> getNBAPlayerTeamInFavorites(String name, String team){
        List<NBAPlayer> nbaPlayerFavorites = getNBAPlayerFavorites(name);
        return nbaPlayerFavorites.stream()
                    .filter(player -> player.getTeam().equalsIgnoreCase(team))
                    .collect(Collectors.toList());
    }

    public List<NBAPlayer> getNBAPlayerTeamAndPositionInFavorites(String name, String team, String pos){
        List<NBAPlayer> nbaPlayerFavorites = getNBAPlayerFavorites(name);
        return nbaPlayerFavorites.stream()
                    .filter(player -> player.getTeam().equalsIgnoreCase(team) && 
                    player.getPos().equalsIgnoreCase(pos))
                    .collect(Collectors.toList());

    }

    public List<NBAPlayer> getNBAPlayerInFavorites(String playerName, String name){
        List<NBAPlayer> nbaPlayerFavorites = getNBAPlayerFavorites(name);
        if(nbaPlayerFavorites.isEmpty()){
            return Collections.emptyList();
        }
        return nbaPlayerFavorites.stream()
                .filter(player -> player.getName().toLowerCase().contains(playerName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Map<String,String>> getNBANews(String limit){
        return NBANews.fetchNBANews(limit);
    }

    public List<Map<String,String>> getNBANewsForPlayer(String name){
        List<Map<String,String>> news = getNBANews("25");
        return news.stream()
                .filter(headline -> headline.get("headline").toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    private static int getNBASeasonYear(){
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear(); 
        return currentDate.getMonthValue() < 10 ? currentYear : currentYear + 1;
    }
}
