package com.peter.Sports.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.peter.Sports.backend.Model.NBAPlayer;

public interface NBAPlayerRepository extends MongoRepository<NBAPlayer,String> {
    List<NBAPlayer> findTop50ByNameIgnoreCase(String name);
    List<NBAPlayer> findTop50ByTeam(String team);
    List<NBAPlayer> findTop50ByPos(String pos);
    List<NBAPlayer> findTop50ByTeamAndPos(String team, String pos);
    List<NBAPlayer> findTop50By();
}
