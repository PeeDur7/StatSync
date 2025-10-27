package com.peter.Sports.backend.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.peter.Sports.backend.Model.Users;

@Repository
public interface UsersRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmailIgnoreCase(String email);
    Optional<Users> findByName(String name);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByID(Integer id);
}
