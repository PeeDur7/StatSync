package com.peter.Sports.backend.Model;

import java.util.HashMap;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("NFLPlayer")
public class NFLPlayer {
    private String id; 
    private String name;
    private String team;
    private String pos;
    private String height;
    private int experience; 
    private String jersey; 
    private int age;
    private String weight;
    private String headshotUrl;
    private String currentTeamLogoUrl;
    private HashMap<String,Object> seasonStats;

    public NFLPlayer(){

    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public String getPos() {
        return pos;
    }

    public int getAge() {
        return age;
    }

    public String getWeight() {
        return weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getJersey() {
        return jersey;
    }

    public void setJersey(String jersey) {
        this.jersey = jersey;
    }

    public HashMap<String,Object> getSeasonStats(){
        return seasonStats;
    }

    public void setSeasonStats(HashMap<String, Object> seasonStats) {
        this.seasonStats = seasonStats;
    }

    public String getHeadshotUrl(){
        return headshotUrl;
    }
    public void setHeadshotUrl(String url){
        this.headshotUrl = url;
    }

    public String getCurrentTeamLogoUrl(){  
        return currentTeamLogoUrl;
    }
    
    public void setCurrentTeamLogoUrl(String url){  
        this.currentTeamLogoUrl = url;
    }

}
