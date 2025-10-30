import { useEffect, useState } from "react";
import Navbar from "./Navbar";
import NFLNavBar from "./NFLNavBar";
import {User} from "lucide-react";
import { Link } from "react-router-dom";
import NBANavBar from "./NBANavBar";

function PlayerLists({sportName}){
    const [dbPlayers, setDBPlayers] = useState([]); //all players from the DB
    const [searchPlayer, setSearchPlayer] = useState("");
    const [players, setPlayers] = useState([]); //this is the list of players to loop through
    const [filterText, setFilterText] = useState("Filter");
    const [searchCategory, setSearchCategory] = useState("Filter"); //the actual value of the category
    const [selectedPosition, setSelectedPosition] = useState("All");
    const [error,setError] = useState("");

    const API_URL = import.meta.env.VITE_API_URL; 
    const access = sessionStorage.getItem("accessToken");

    useEffect(() => {
        async function loadAllPlayers(){
            if(!access){
                return;
            }

            try{
                const allPlayerResponse = await fetch(`${API_URL}/${sportName}/players`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization" : `Bearer ${access}`
                    }
                });
                if(!allPlayerResponse.ok){
                    setPlayers([]);
                    return;
                }
                const allPlayerData = await allPlayerResponse.json();
                setDBPlayers(allPlayerData);
                setPlayers(allPlayerData);
                setError("");
            } catch(error){
            }
        }
        loadAllPlayers();
    },[access]);

    async function loadPlayerBySelectedPosition(){
        if(selectedPosition === "All" && searchCategory === "Filter"){
            setPlayers(dbPlayers);
            setError("");
            return;
        } 
        else if(searchCategory === "Favorite" && selectedPosition === "All"){
            try{
                const players = await fetch(`${API_URL}/user/${sportName}Players/favorite`,{
                    method : "POST",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization" : `Bearer ${access}`
                    }
                });
                if(!players.ok){
                    setError("No players could be found");
                    setPlayers([]);
                    return;
                }
                const playersList = await players.json();
                if(playersList.length === 0){
                    setError("No favorite players found");
                    setPlayers([]);
                    return;
                }
                setPlayers(playersList);
                setError("");
            } catch(error){
            }
        }
        else if(searchCategory === "Favorite" && selectedPosition !== "All"){
            try{
                const players = await fetch(`${API_URL}/user/${sportName}Players/favorite/position?pos=${selectedPosition}`,{
                    method : "POST",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization" : `Bearer ${access}`
                    },
                });
                if(!players.ok){
                    setError("No players could be found");
                    setPlayers([]);
                    return;
                }
                const playerList = await players.json();
                if(playerList.length === 0){
                    setError("No favorite players found");
                    setPlayers([]);
                    return;
                }
                setPlayers(playerList);
                setError("");
            } catch(error){
            }
        }
        else if(searchCategory !== "Filter" && searchCategory !== "Favorite"){
            if(selectedPosition === "All"){
                try{
                    const players = await fetch(
                        `${API_URL}/${sportName}/team/players?teamName=${searchCategory}`,{
                            method : "GET",
                            headers : {
                                "Content-Type" : "application/json",
                                "Authorization" : `Bearer ${access}`
                            }
                    })
                    if(!players.ok){
                        setError("No players could be found");
                        setPlayers([]);
                        return;
                    }
                    const playerList = await players.json();
                    setPlayers(playerList);
                    setError("");
                } catch(error) {
                }
            } else {
                try{
                    const players = await fetch(
                        `${API_URL}/${sportName}/team/position/players?teamName=${searchCategory}&position=${selectedPosition}`,{
                            method : "GET",
                            headers : {
                                "Content-Type" : "application/json",
                                "Authorization" : `Bearer ${access}`
                            }
                    });
                    if(!players.ok){
                        setError("No players could be found");
                        setPlayers([]);
                        return;
                    }
                    const playersList = await players.json();
                    setPlayers(playersList);
                    setError("");
                } catch(error){
                }
            }
        } else {
            try{
                const players = await fetch(`${API_URL}/${sportName}/position/players?position=${selectedPosition}`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization" : `Bearer ${access}`
                    }
                });
                if(!players.ok){
                    setError("No players could be found");
                    setPlayers([]);
                    return;
                }
                const playersList = await players.json();
                setPlayers(playersList);
                setError("");
            } catch(error){
            }
        }
    }

    async function loadPlayerByName(playerName){
        if(searchCategory === "Filter"){
            try{
                const playerResponse = await fetch(`${API_URL}/${sportName}/players/player/name?name=${playerName}`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization" : `Bearer ${access}`
                    }
                });
    
                if(!playerResponse.ok){
                    setError("Player could not be found");
                    setPlayers([]);
                    return;
                }
    
                const playerData = await playerResponse.json();
                setPlayers(playerData);
                if(playerData && playerData.length > 0){
                    setSelectedPosition("All");
                }
                setError("");
            } catch(error){
            }
        } else if(searchCategory === "Favorite"){
            try{
                const playerResponse = await fetch(`${API_URL}/user/${sportName}Players/favorite/playername?playerName=${searchPlayer}`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization" : `Bearer ${access}`
                    }
                });
                if(!playerResponse.ok){
                    setError("Player could not be found");
                    setPlayers([]);
                    return;
                }
                const players = await playerResponse.json();
                setPlayers(players);
                if(players && players.length > 0){
                    setSelectedPosition(player[0].pos);
                }
                setError("");
            } catch(error){
            }
        }
    }

    document.title = `${sportName.toUpperCase()} Players`;

    return(
        <div className = "playerListPage">
            <Navbar/>
            <div className = "playerListPageContainer">
                <form onSubmit={(e) => {
                    e.preventDefault();
                    loadPlayerByName(searchPlayer);
                }}>
                    <div className="playerListSearchBar">
                        <input 
                            type="search"
                            value={searchPlayer}
                            onChange={(e) => setSearchPlayer(e.target.value)}
                            placeholder="Search Player"
                        />
                        <button onClick={(e) => {
                            e.preventDefault();
                            loadPlayerByName(searchPlayer);
                        }}>Search</button>
                    </div>
                </form>
                <div className="playerListPageFrame">
                    {error && error.length > 0 &&
                        <div className="errorMessage">
                            <h3>{error}</h3>
                        </div>
                    }
                    {sportName === "nfl" && 
                        <NFLNavBar selectedPosition={selectedPosition} setSelectedPosition={setSelectedPosition} 
                            filterText={filterText} setFilterText={setFilterText} 
                            searchCategory={searchCategory} setSearchCategory={setSearchCategory}
                            loadPlayers={loadPlayerBySelectedPosition}/>
                    }
                    {sportName === "nba" && 
                        <NBANavBar selectedPosition={selectedPosition} setSelectedPosition={setSelectedPosition} 
                        filterText={filterText} setFilterText={setFilterText} 
                        searchCategory={searchCategory} setSearchCategory={setSearchCategory}
                        loadPlayers={loadPlayerBySelectedPosition}/> 
                    }
                    {
                        players.map((player,index) => (
                            <div className ="playerListCol" key={index}>
                                <Link to={`/${sportName.toUpperCase()}/Players/${player.id}`}>
                                    <h3>
                                        {player.headshotUrl === "N/A" ? <User size={80}/> 
                                            : <img src={player.headshotUrl}/>}
                                        {player.name}
                                    </h3>
                                    <h4>{player.team} - {player.pos}</h4>
                                </Link>
                            </div>
                        ))
                    }
                </div>
            </div>
        </div>
    )

}

export default PlayerLists