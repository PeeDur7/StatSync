package com.peter.Sports.backend.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peter.Sports.backend.Model.NBAPlayer;
import com.peter.Sports.backend.Service.NBAPlayerService;

@RestController
@RequestMapping("/nba")
public class NBAPlayerController {
    private final NBAPlayerService nbaPlayerService;

    public NBAPlayerController(NBAPlayerService nbaPlayerService){
        this.nbaPlayerService = nbaPlayerService;
    }

    @GetMapping("/players")
    public List<NBAPlayer> getNBAPlayers(){
        return nbaPlayerService.getNBAPlayers();
    }

    @GetMapping("/players/player/name")
    public List<NBAPlayer> getNBAPlayerByName(@RequestParam String name){
        return nbaPlayerService.getNBAPlayerByName(name);
    }

    @GetMapping("/team/players")
    public List<NBAPlayer> getNBAPlayersByTeam(@RequestParam String teamName){
        return nbaPlayerService.getNBAPlayerByTeam(teamName);
    }

    @GetMapping("/position/players")
    public List<NBAPlayer> getNBAPlayersByPosition(@RequestParam String position){
        return nbaPlayerService.getNBAPlayerByPos(position);
    }

    @GetMapping("/team/position/players")
    public List<NBAPlayer> getNBAPlayersByTeamAndPosition(@RequestParam String team, @RequestParam String position){
        return nbaPlayerService.getNBAPlayerByTeamAndPos(team, position);
    }

    @GetMapping("/player/data")
    public NBAPlayer getNBAPlayerData(@RequestParam String id){
        return nbaPlayerService.getNBAPlayerData(id);
    }

    @GetMapping("/news")
    public List<Map<String,String>> getNBANews(){
        return nbaPlayerService.getNBANews("25");
    }

    @GetMapping("/news/home")
    public List<Map<String,String>> getNBANews(String limit)
    {
        return nbaPlayerService.getNBANews(limit);
    }

    @GetMapping("/player/news")
    public List<Map<String,String>> getNBANewsForPlayer(@RequestParam String playerName){
        return nbaPlayerService.getNBANewsForPlayer(playerName);
    }

}
