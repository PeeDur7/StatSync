package com.peter.Sports.backend.Model;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("NBAPlayer")
public class NBAPlayer {
    private String id;
    private String name;
    private String team;
    private String pos;
    private String height;
    private String weight;
    private int age;
    private int experience;
    private String jersey;
    private String headshotUrl; 
    private String currentTeamLogoUrl;
    private Map<String,Object> stats;

    public NBAPlayer(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public Map<String,Object> getStats(){
        return stats;
    }

    public void setStats(Map<String,Object> stats){
        this.stats = stats;
    }

    public String getHeadshotUrl(){
        return headshotUrl;
    }
    public void setHeadshotUrl(String url){
        this.headshotUrl = url;
    }

    public String getCurrentTeamLogo(){
        return currentTeamLogoUrl;
    }
    public void setCurrentTeamLogo(String url){
        this.currentTeamLogoUrl = url;
    }
}
