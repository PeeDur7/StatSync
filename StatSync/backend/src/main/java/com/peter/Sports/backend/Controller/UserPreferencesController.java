package com.peter.Sports.backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peter.Sports.backend.DTO.NBAPlayerFavorite;
import com.peter.Sports.backend.DTO.NFLPlayerFavorite;
import com.peter.Sports.backend.Model.NBAPlayer;
import com.peter.Sports.backend.Model.NFLPlayer;
import com.peter.Sports.backend.Service.NBAPlayerService;
import com.peter.Sports.backend.Service.NFLPlayerService;

@RestController
@RequestMapping("/user")
public class UserPreferencesController {
    private final NFLPlayerService nflPlayerService;
    private final NBAPlayerService nbaPlayerService;

    public UserPreferencesController(NFLPlayerService nflPlayerService, NBAPlayerService nbaPlayerService){
        this.nflPlayerService = nflPlayerService;
        this.nbaPlayerService = nbaPlayerService;
    }

    @PostMapping("/nflPlayers/favorite/add")
    public ResponseEntity<?> addPlayerToFavorite(@RequestBody NFLPlayerFavorite request,
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nflPlayerService.favoriteNFLPlayer(request.getPlayerId(), name);
    }

    @PutMapping("/nflPlayers/favorite/remove")
    public ResponseEntity<?> removePlayerFromFavorites(@RequestBody NFLPlayerFavorite request,
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nflPlayerService.removeNFLPlayerFromFavorites(request.getPlayerId(), name);
    }

    @PostMapping("/nflPlayers/favorite")
    public List<NFLPlayer> getUserFavorites(@AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nflPlayerService.getFavoriteNFLPlayers(name);
    }

    @PostMapping("/nflPlayers/favorite/playername")
    public List<NFLPlayer> getPlayerInUserFavorite(@RequestParam String playerName, 
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nflPlayerService.getPlayerInFavorites(playerName, name);
    }

    @PostMapping("/nflPlayers/favorite/position")
    public List<NFLPlayer> getPositionsInUserFavorite(@RequestParam String pos, 
    @AuthenticationPrincipal UserDetails userDetails){
        return nflPlayerService.getPositionInFavorites(pos, userDetails.getUsername());
    }
    
    @PostMapping("/nflPlayers/favorite/team")
    public List<NFLPlayer> getTeamPlayersInUserFavorites(@RequestParam String team,
    @AuthenticationPrincipal UserDetails userDetails){
        return nflPlayerService.getTeamInFavorites(team, userDetails.getUsername());
    }

    @PostMapping("/nflPlayers/favorite/team/position")
    public List<NFLPlayer> getTeamPositionInUserFavorites(@RequestParam String team,
    @AuthenticationPrincipal UserDetails userDetails, @RequestParam String pos){
        String name = userDetails.getUsername();
        return nflPlayerService.getTeamAndPositionInFavorites(team, 
        pos, name);
    }

    @PostMapping("/nbaPlayers/favorite/add")
    public ResponseEntity<?> addNBAPlayerToFavorite(@RequestBody NBAPlayerFavorite request,
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nbaPlayerService.favoriteNBAPlayer(request.getPlayerId(), name);
    }

    @PutMapping("/nbaPlayers/favorite/remove")
    public ResponseEntity<?> removeNBAPlayerFromFavorite(@RequestBody NBAPlayerFavorite request, 
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nbaPlayerService.removePlayerFromNBAFavorites(request.getPlayerId(),name);
    }

    @PostMapping("/nbaPlayers/favorite")
    public List<NBAPlayer> getUserNBAPlayerFavorite(@AuthenticationPrincipal UserDetails userDetails){
        return nbaPlayerService.getNBAPlayerFavorites(userDetails.getUsername());
    }

    @PostMapping("/nbaPlayers/favorite/position")
    public List<NBAPlayer> getUserNBAPlayerFavoriteByPosition(@RequestParam String pos, 
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nbaPlayerService.getNBAPlayerPositionInFavorites(name, pos);
    }

    @PostMapping("/nbaPlayers/favorite/team")
    public List<NBAPlayer> getUserNBAPlayerFavoriteByTeam(@RequestParam String team, 
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nbaPlayerService.getNBAPlayerTeamInFavorites(name, team);
    }

    @PostMapping("/nbaPlayers/favorite/team/position")
    public List<NBAPlayer> getUserNBAPlayerFavoriteByTeamAndPosition(@RequestParam String team, 
    @RequestParam String pos, @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nbaPlayerService.getNBAPlayerTeamAndPositionInFavorites(
            name, team, pos);
    }

    @PostMapping("/nbaPlayers/favorite/playername")
    public List<NBAPlayer> getUserNBAPlayerFavoriteByPlayerName(@RequestParam String playerName, 
    @AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        return nbaPlayerService.getNBAPlayerInFavorites(playerName, name);
    }
}
