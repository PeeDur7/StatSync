package com.peter.Sports.backend.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peter.Sports.backend.Model.NFLPlayer;
import com.peter.Sports.backend.Service.NFLPlayerService;

@RestController
@RequestMapping("/nfl")
public class NFLPlayersController {
    private final NFLPlayerService nflPlayerService;

    public NFLPlayersController(NFLPlayerService nflPlayerService){
        this.nflPlayerService = nflPlayerService;
    }

    @GetMapping("/players")
    public List<NFLPlayer> getAllPlayers(){
        return nflPlayerService.getPlayers();
    }

    @GetMapping("/players/player/name")
    public List<NFLPlayer> getPlayerName(@RequestParam String name){
        return nflPlayerService.getPlayerName(name);
    }

    @GetMapping("/team/players")
    public List<NFLPlayer> getTeamPlayers(@RequestParam String teamName){
        return nflPlayerService.getTeamPlayers(teamName);
    }

    @GetMapping("/position/players")
    public List<NFLPlayer> getPositionPlayers(@RequestParam String position){
        return nflPlayerService.getPosition(position);
    }

    @GetMapping("/team/position/players")
    public List<NFLPlayer> getTeamPositionPlayers(@RequestParam String teamName, @RequestParam String position){
        return nflPlayerService.getTeamAndPosition(teamName, position);
    }

    @GetMapping("/player/data")
    public NFLPlayer getNFLPlayerData(@RequestParam String id){
        return nflPlayerService.getNFLPlayerData(id);
    }

    @GetMapping("/news")
    public List<Map<String,String>> getNFLNews(){
        //limit is 25 by default
        return nflPlayerService.getNFLNews("25");
    }

    @GetMapping("/news/home")
    public List<Map<String,String>> getNFLNews(@RequestParam String limit){
        return nflPlayerService.getNFLNews(limit);
    }

    @GetMapping("/player/news")
    public List<Map<String,String>> getNFLNewsForPlayer(@RequestParam String playerName){
        return nflPlayerService.getNFLNewsForPlayer(playerName);
    }
}
