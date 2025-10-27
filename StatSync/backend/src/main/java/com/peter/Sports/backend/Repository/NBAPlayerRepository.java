package com.peter.Sports.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.peter.Sports.backend.Model.NBAPlayer;

public interface NBAPlayerRepository extends MongoRepository<NBAPlayer,String> {
    List<NBAPlayer> findByNameIgnoreCase(String name);
    List<NBAPlayer> findByTeam(String team);
    List<NBAPlayer> findByPos(String pos);
    List<NBAPlayer> findByTeamAndPos(String team, String pos);
}
