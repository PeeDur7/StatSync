package com.peter.Sports.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.peter.Sports.backend.Model.NBAPlayer;

public interface NBAPlayerRepository extends MongoRepository<NBAPlayer,String> {
    List<NBAPlayer> findTop100ByNameIgnoreCase(String name);
    List<NBAPlayer> findTop100ByTeam(String team);
    List<NBAPlayer> findTop100ByPos(String pos);
    List<NBAPlayer> findTop100ByTeamAndPos(String team, String pos);
    List<NBAPlayer> findTop100By();
}
