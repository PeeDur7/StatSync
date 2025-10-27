package com.peter.Sports.backend.Repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.peter.Sports.backend.Model.NFLPlayer;


@Repository
public interface NFLPlayerRepository extends MongoRepository<NFLPlayer,String>{
    List<NFLPlayer> findByNameIgnoreCase(String name);
    List<NFLPlayer> findByTeam(String team);
    List<NFLPlayer> findByPos(String pos);
    List<NFLPlayer> findByTeamAndPos(String team, String pos);
}
