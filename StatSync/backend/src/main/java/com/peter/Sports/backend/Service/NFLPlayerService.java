package com.peter.Sports.backend.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.peter.APIS.NFL.NFLGameLog;
import com.peter.APIS.NFL.NFLNews;
import com.peter.APIS.NFL.NFLPlayerId;
import com.peter.Sports.backend.Model.NFLPlayer;
import com.peter.Sports.backend.Model.Users;
import com.peter.Sports.backend.Repository.NFLPlayerRepository;
import com.peter.Sports.backend.Repository.UsersRepository;

@Service
public class NFLPlayerService {
    private final NFLPlayerRepository nflPlayerRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public NFLPlayerService(NFLPlayerRepository nflPlayerRepository, UsersRepository usersRepository){
        this.nflPlayerRepository = nflPlayerRepository;
        this.usersRepository = usersRepository;
    }

    @Scheduled(cron = "0 0 0 15 8 *", zone = "America/New_York")
    public void scheduledNFLPlayerLoad(){
        List<NFLPlayer> nflPlayerList = NFLPlayerId.fetchNFLPlayerInfo();
        nflPlayerRepository.deleteAll();
        nflPlayerRepository.saveAll(nflPlayerList);
        System.out.println("Success loading schedule nfl player");
    }
    
    @SuppressWarnings("unchecked")
    @Scheduled(cron = "0 0 0 ? * TUE", zone = "America/New_York")
    //@Scheduled(cron = "0 0 1 ? * THU", zone = "America/New_York")
    public void getOrUpdateNFLPlayerCurrentSeason(){
        List<NFLPlayer> players = nflPlayerRepository.findAll();
        List<NFLPlayer> playersListUpdated = new ArrayList<>(); 
        List<NFLPlayer> updatedPlayerGameLogs = new ArrayList<>();
        String gameLogYear = String.valueOf(getNFLSeasonYear());
        int month = LocalDate.now().getMonthValue();
    
        // First loop - update team info
        for(NFLPlayer player : players){
            HashMap<String,Object> currentTeam = NFLPlayerId.getSeasonTeamName(player.getId(), 0);
            HashMap<String,Object> seasonStatsMap = player.getSeasonStats();
            String team = String.valueOf(currentTeam.get("team"));
            String teamLogo = String.valueOf(currentTeam.get("teamLogo"));
            
            if(seasonStatsMap.containsKey(gameLogYear)){
                // Only update the gamelog team info if they're in season not when out of season
                if(month >= 9 || 
                    (getNFLSeasonYear() < LocalDate.now().getYear() && month < 3)){
                    HashMap<String,Object> seasonStats = (HashMap<String,Object>) seasonStatsMap.get(gameLogYear);
                    seasonStats.put("teamLogo", teamLogo);
                    seasonStats.put("team", team);
                }
            } else {
                // Create new season entry if it doesn't exist
                HashMap<String,Object> newSeasonStats = new HashMap<>();
                newSeasonStats.put("teamLogo", teamLogo);
                newSeasonStats.put("team", team);
                seasonStatsMap.put(gameLogYear, newSeasonStats);
            }
            
            player.setCurrentTeamLogoUrl(teamLogo);
            player.setTeam(team);
            playersListUpdated.add(player);
        }
    
        // Second loop - update game logs
        for(NFLPlayer player : playersListUpdated){
            HashMap<String,Object> gameLog = NFLGameLog.getCurrentSeasonGameLog(player.getId());
            HashMap<String,Object> seasonStatsMap = player.getSeasonStats();
            
            // Ensure season exists
            if(!seasonStatsMap.containsKey(gameLogYear)){
                seasonStatsMap.put(gameLogYear, new HashMap<>());
            }
            
            HashMap<String,Object> seasonStats = (HashMap<String,Object>) seasonStatsMap.get(gameLogYear);
            seasonStats.put("gameLog", gameLog.get("gameLog"));
            updatedPlayerGameLogs.add(player);
        }
    
        // Third loop - update season totals and rank
        List<NFLPlayer> updatedPlayers = new ArrayList<>();
        for(NFLPlayer player : updatedPlayerGameLogs){
            HashMap<String,Object> seasonTotalsAndRank = NFLPlayerId.getSeasonStats(
                gameLogYear, player.getId(), player.getPos());
            HashMap<String,Object> seasonStatsMap = player.getSeasonStats();
            
            // Ensure season exists
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
        
        nflPlayerRepository.saveAll(updatedPlayers);
        System.out.println("successs!");
    }

    public List<NFLPlayer> getPlayers(){
       return nflPlayerRepository.findAll();
    }

    public List<NFLPlayer> getPlayerName(String name){
        return nflPlayerRepository.findByNameIgnoreCase(name);
    }

    public List<NFLPlayer> getTeamPlayers(String teamName){
        return nflPlayerRepository.findByTeam(teamName);
    }

    public List<NFLPlayer> getPosition(String position) {
        List<NFLPlayer> playerList = nflPlayerRepository.findByPos(position);
        List<String> orderList = orderPlayersByPosition(position).stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());

        return playerList.stream()
            .sorted(Comparator.comparingInt(player -> {
            int index = orderList.indexOf(player.getName().toLowerCase());
            return index == -1 ? Integer.MAX_VALUE : index;
        }))
        .collect(Collectors.toList());
    }
    
    public List<NFLPlayer> getTeamAndPosition(String team, String position){
        return nflPlayerRepository.findByTeamAndPos(team, position);
    }

    public ResponseEntity<?> favoriteNFLPlayer(String id,String name){
        Optional<Users> userOpt = usersRepository.findByName(name);
        if(userOpt.isEmpty()){
            return new ResponseEntity<>("User cannot be found",HttpStatus.ALREADY_REPORTED);
        }

        Users user = userOpt.get();
        List<String> favorites = user.getFavoriteNFL();
        if(!favorites.contains(id)){
            favorites.add(id);
            user.setFavoriteNFL(favorites);
            usersRepository.save(user);
            return new ResponseEntity<>("Added Player to Favorites",HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
        }
    }

    public ResponseEntity<?> removeNFLPlayerFromFavorites(String id, String name){
        Optional<Users> userOpt = usersRepository.findByName(name);
        if(userOpt.isEmpty()){
            return new ResponseEntity<>("User cannot be found",HttpStatus.ALREADY_REPORTED);
        }

        Users user = userOpt.get();
        List<String> favorite = user.getFavoriteNFL();
        for(int i = 0; i < favorite.size(); i++){
            if(favorite.get(i).equalsIgnoreCase(id)){
                favorite.remove(i);
                break;
            }
        }
        user.setFavoriteNFL(favorite);
        usersRepository.save(user);
        return new ResponseEntity<>("success removing player from favorites", HttpStatus.ALREADY_REPORTED);
    }

    public List<NFLPlayer> getFavoriteNFLPlayers(String name){
        Optional<Users> userOpt = usersRepository.findByName(name);
        if(userOpt.isEmpty()){
            return Collections.emptyList();
        }
        Users user = userOpt.get();
        List<String> favorites = user.getFavoriteNFL();
        return favorites.stream()
                .map(id -> nflPlayerRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<NFLPlayer> getPlayerInFavorites(String playerName,String name){
        List<NFLPlayer> favorites = getFavoriteNFLPlayers(name);
        if(favorites.isEmpty()){
            return Collections.emptyList();
        }
        return favorites.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(playerName))
                    .collect(Collectors.toList());
    }

    public List<NFLPlayer> getPositionInFavorites(String position,String name){
        List<NFLPlayer> favorites = getFavoriteNFLPlayers(name);
        if(favorites.isEmpty()){
            return Collections.emptyList();
        }
        return favorites.stream()
                .filter(p -> p.getPos().equalsIgnoreCase(position))
                .collect(Collectors.toList());
    }

    public List<NFLPlayer> getTeamInFavorites(String teamName,String name){
        List<NFLPlayer> favorites = getFavoriteNFLPlayers(name);
        if(favorites.isEmpty()){
            return Collections.emptyList();
        }
        return favorites.stream()
                .filter(p -> p.getTeam().equalsIgnoreCase(teamName))
                .collect(Collectors.toList());
    }

    public List<NFLPlayer> getTeamAndPositionInFavorites(String teamName, String position, String name){
        List<NFLPlayer> favorites = getFavoriteNFLPlayers(name);
        if(favorites.isEmpty()){
            return Collections.emptyList();
        }
        return favorites.stream()
                .filter(p -> p.getTeam().equalsIgnoreCase(teamName) && p.getPos().equalsIgnoreCase(position))
                .collect(Collectors.toList());
    }

    public NFLPlayer getNFLPlayerData(String id){
        Optional<NFLPlayer> player = nflPlayerRepository.findById(id);
        if(player.isPresent()){
            NFLPlayer p = player.get();
            return p;
        }
        return new NFLPlayer();
    }

    public List<Map<String,String>> getNFLNews(){
        return NFLNews.fetchNFLNews();
    }
    
    public List<Map<String,String>> getNFLNewsForPlayer(String name){
        List<Map<String,String>> nflNews = getNFLNews();
        return nflNews.stream()
                .filter(headline -> headline.get("headline").toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    private static List<String> orderPlayersByPosition(String position) {
        if (position.equalsIgnoreCase("QB")) {
            return Arrays.asList(
                "Patrick Mahomes", "Lamar Jackson", "Josh Allen", "Joe Burrow", "Jayden Daniels", "Jalen Hurts",
                "Justin Herbert", "Baker Mayfield", "Jared Goff", "Dak Prescott", "C.J. Stroud", "Aaron Rodgers", "Matthew Stafford",
                "Caleb Williams", "J.J. McCarthy", "Jordan Love", "Brock Purdy", "Bo Nix", "Trevor Lawrence", "Geno Smith",
                "Tua Tagovailoa", "Anthony Richardson", "Drake Maye", "Justin Fields", "Cameron Ward", "Kyler Murray", "Michael Penix Jr.", "Bryce Young",
                "Tyler Shough", "Russell Wilson", "Sam Darnold", "Joe Flacco"
            );
        } else if (position.equalsIgnoreCase("WR")) {
            return Arrays.asList(
                "Ja'Marr Chase", "Justin Jefferson", "CeeDee Lamb",
                "Puka Nacua", "Malik Nabers", "Brian Thomas Jr.",
                "Amon-Ra St. Brown", "Nico Collins", "Drake London",
                "Garrett Wilson",
                "Ladd McConkey", "A.J. Brown", "Jaxon Smith-Njigba",
                "Tee Higgins", "Tyreek Hill", "Davante Adams",
                "D.J. Moore", "Mike Evans",
                "Xavier Worthy", "Terry McLaurin", "Marvin Harrison Jr.",
                "Courtland Sutton", "DK Metcalf", "DeVonta Smith",
                "Zay Flowers", "Calvin Ridley",
                "Tetairoa McMillan", "Chris Olave", "Chris Godwin",
                "Jakobi Meyers", "Jauan Jennings", "Jaylen Waddle", "George Pickens",
                "Deebo Samuel", "Jordan Addison", "Khalil Shakir",
                "Travis Hunter", "Jerry Jeudy", "Rome Odunze",
                "Jameson Williams", "Stefon Diggs", "Rashee Rice",
                "Josh Downs", "Christian Kirk", "Darnell Mooney", "Cooper Kupp",
                "Michael Pittman", "Jayden Reed", "Ricky Pearsall",
                "Matthew Golden", "Brandon Aiyuk", "Emeka Egbuka",
                "Rashid Shaheed", "Keon Coleman", "Cedric Tillman",
                "Rashod Bateman",
                "Luther Burden III", "Marquise Brown", "Adam Thielen",
                "Marvin Mims Jr.", "Tre Harris", "Jack Bech",
                "Jalen Coker", "Demario Douglas", "Jalen McMillan",
                "Kyle Williams", "Josh Palmer", "Greg Dortch",
                "Jayden Higgins", "Darius Slayton", "DeAndre Hopkins", "Quentin Johnston", "Tyler Lockett",
                "Wan'Dale Robinson"
            );
        } else if (position.equalsIgnoreCase("TE")) {
            return Arrays.asList(
                "Brock Bowers", "Trey McBride", "George Kittle", "Travis Kelce", "Mark Andrews", "Sam LaPorta",
                "T.J. Hockenson", "Tucker Kraft", "Dalton Kincaid", "Kyle Pitts", "Mike Gesicki", "David Njoku",
                "Evan Engram", "Dalton Schultz", "Tyler Warren", "Brenton Strange", "Will Dissly", "Darren Waller", "Hunter Henry",
                "Mason Taylor", "Pat Freiermuth", "Chigoziem Okonkwo", "Tommy Tremble", "Cole Kmet", "Jake Ferguson", "Tyler Higbee",
                "Juwan Johnson", "Theo Johnson", "Dallas Goedert", "AJ Barner","Cade Otton", "Zach Ertz"
            );
        } else if (position.equalsIgnoreCase("RB")) {
            return Arrays.asList(
                "Saquon Barkley", "Derrick Henry", "Jahmyr Gibbs", "Bijan Robinson", "De'Von Achane",
                "Christian McCaffrey", "Ashton Jeanty", "Josh Jacobs", "Bucky Irving", "Jonathan Taylor", "James Cook",
                "Chase Brown", "Kyren Williams", "Joe Mixon", "Nick Chubb", "RJ Harvey", "Travis Etienne", "Isiah Pacheco",
                "Breece Hall", "Chuba Hubbard", "Alvin Kamara", "Tyrone Tracy Jr.", "Kenneth Walker III",
                "Javonte Williams", "D'Andre Swift", "James Conner", "Jaylen Warren", "Najee Harris", "Tony Pollard", 
                "Rhamondre Stevenson", "Jerome Ford"
            );
        } else if (position.equalsIgnoreCase("PK")) {
            return Arrays.asList(
                "Brandon Aubrey", "Chris Boswell", "Jake Bates", "Jake Elliott", "Younghoe Koo", "Harrison Butker", "Evan McPherson",
                "Tyler Bass", "Tyler Loop", "Dustin Hopkins", "Will Lutz", "Ka'imi Fairbairn", "Spencer Shrader", "Cam Little",
                "Cameron Dicker", "Daniel Carlson", "Jason Sanders", "Andres Borregales", "Caden Davis", "Joey Slye",
                "Chad Ryland", "Matthew Wright", "Cairo Santos", "Brandon McManus", "Joshua Karty", "Will Reichard",
                "Blake Grupe", "Graham Gano", "Jason Myers", "Jake Moody", "Chase McLaughlin", "Matt Gay"
            );
        }
        return Collections.emptyList();
    }
    
    private static int getNFLSeasonYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        return currentDate.getMonthValue() < 9 ? currentYear - 1 : currentYear;
    }
}
