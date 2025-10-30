package com.peter.Sports.backend.Repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.peter.Sports.backend.Model.NFLPlayer;


@Repository
public interface NFLPlayerRepository extends MongoRepository<NFLPlayer,String>{
    List<NFLPlayer> findTop100ByNameIgnoreCase(String name);
    List<NFLPlayer> findTop100ByTeam(String team);
    List<NFLPlayer> findTop100ByPos(String pos);
    List<NFLPlayer> findTop100ByTeamAndPos(String team, String pos);
    List<NFLPlayer> findTop100By();
}
