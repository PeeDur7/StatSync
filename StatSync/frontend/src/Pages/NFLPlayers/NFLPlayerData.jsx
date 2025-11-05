import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Navbar from "../../Components/Navbar";
import { User } from "lucide-react";

function NFLPlayerData() {
  const { playerId } = useParams();
  const [player, setPlayer] = useState(null);
  const [gameLogs, setGameLogs] = useState({});
  const [seasonStats, setSeasonStats] = useState({});
  const [removeButton, setRemoveButton] = useState(false);
  const [addButton, setAddButton] = useState(true);
  const [selectedGameLogYear, setSelectedGameLogYear] = useState(null);
  const accessToken = sessionStorage.getItem("accessToken");

  const API_URL = import.meta.env.VITE_API_URL; 

  // Helper functions for formatting
  const formatStat = (value) => {
    if (value === undefined || value === null || value === "-" || value === "") return "-";
    const num = Number(value);
    if (isNaN(num)) return value;
    return num % 1 === 0 ? num.toString() : num.toFixed(2);
  };

  const formatStatCategory = (category) => {
    const separated = category.replace(/([A-Z])/g, ' $1').trim();
    return separated.toUpperCase();
  };

  // Sort stats based on position
  const sortStatsByPosition = (statTitles, position) => {
    if (!position) return statTitles;

    const posUpper = position.toUpperCase();
    let orderedStats = [];

    if (posUpper === "QB") {
      orderedStats = [
        "gamesPlayed",
        "completionPct",
        "passingAttempts",
        "completions",
        "passingYards",
        "passingYardsPerGame",
        "passingTouchdowns",
        "passingTouchdownPct",
        "yardsPerPassAttempt",
        "yardsPerCompletion",
        "QBRating",
        "rushingYards",
        "rushingYardsPerGame",
        "rushingTouchdowns",
        "totalTouchdowns",
        "yardsPerGame",
        "interceptions",
        "interceptionPct"
      ];
    } else if (posUpper === "RB") {
      orderedStats = [
        "gamesPlayed",
        "rushingAttempts",
        "rushingYards",
        "rushingYardsPerGame",
        "rushingTouchdowns",
        "rushingBigPlays",
        "receptions",
        "receivingYards",
        "yardsPerReception",
        "receivingTouchdowns",
        "totalTouchdowns",
        "rushingFumbles",
        "rushingFumblesLost"
      ];
    } else if (posUpper === "WR" || posUpper === "TE") {
      orderedStats = [
        "gamesPlayed",
        "receptions",
        "receivingTargets",
        "receivingYards",
        "receivingYardsPerGame",
        "yardsPerReception",
        "receivingTouchdowns",
        "receivingBigPlays",
        "rushingYards",
        "rushingTouchdowns",
        "totalTouchdowns",
        "receivingFumbles",
        "receivingFumblesLost"
      ];
    } else if (posUpper === "PK" || posUpper === "K") {
      orderedStats = [
        "gamesPlayed",
        "extraPointPct",
        "extraPointAttempts",
        "extraPointsMade",
        "fieldGoalPct",
        "fieldGoalAttempts",
        "fieldGoalsMade",
        "fieldGoalAttempts1_19",
        "fieldGoalsMade1_19",
        "fieldGoalAttempts20_29",
        "fieldGoalsMade20_29",
        "fieldGoalAttempts30_39",
        "fieldGoalsMade30_39",
        "fieldGoalAttempts40_49",
        "fieldGoalsMade40_49",
        "fieldGoalAttempts50",
        "fieldGoalsMade50"
      ];
    }

    // Filter to only include stats that exist in statTitles, maintaining order
    const sortedStats = orderedStats.filter(stat => statTitles.includes(stat));
    
    // Add any remaining stats not in the ordered list
    const remainingStats = statTitles.filter(stat => !orderedStats.includes(stat));
    
    return [...sortedStats, ...remainingStats];
  };

  // Fetch player data
  useEffect(() => {
    if (!accessToken) return;

    async function getPlayerData() {
      try {
        const res = await fetch(`${API_URL}/nfl/player/data?id=${playerId}`, {
          method: "GET",
          headers: { "Content-Type": "application/json", Authorization: `Bearer ${accessToken}` },
        });
        if (!res.ok) return;
        const data = await res.json();
        setPlayer(data);
      } catch (err) {
      }
    }
    getPlayerData();
  }, [accessToken, playerId]);

  // Process stats
  useEffect(() => {
    if (!player?.seasonStats) return;

    document.title = `${player.name} Stats`;

    const logs = {};
    const stats = {};
    Object.entries(player.seasonStats).forEach(([season, data]) => {
      if (data.seasonTotalsAndRank) stats[season] = data.seasonTotalsAndRank;
      if (data.gameLog) logs[season] = data.gameLog;
    });

    setGameLogs(logs);
    setSeasonStats(stats);

    // Set default selected year to most recent game log year
    const gameLogYears = Object.keys(logs).map(Number).sort((a, b) => b - a);
    if (gameLogYears.length > 0 && !selectedGameLogYear) {
      setSelectedGameLogYear(gameLogYears[0]);
    }
  }, [player, selectedGameLogYear]);

  useEffect(() => {
    async function checkNFLPlayerInFavorites(){
        try{
            if(!player || !accessToken) return;
            
            const respo = await fetch(`${API_URL}/user/nflPlayers/favorite/playername?playerName=${player.name}`,{
                method : "POST",
                headers : {
                    "Content-Type": "application/json", 
                    Authorization: `Bearer ${accessToken}`
                },
            });

            if(!respo.ok){
                return;
            }
            const favoriteList = await respo.json();
            
            // Check if any player in the list matches the current player's ID
            const isInFavorites = favoriteList.some(favPlayer => favPlayer.id === player.id || favPlayer.id === player._id);
            
            if(isInFavorites){
                setRemoveButton(true);
                setAddButton(false);
            } else {
                setAddButton(true);
                setRemoveButton(false);
            }
        } catch(e){
        }
    }

    checkNFLPlayerInFavorites();
  }, [player, accessToken]);

  if (!player) {
    return (
      <div className="NFLPlayerDataPage">
        <Navbar />
        <div className="NFLPlayerDataContainer">
          <h2>Loading player data...</h2>
        </div>
      </div>
    );
  }

  const date = new Date();
  const currentYear = date.getFullYear();
  const currentMonth = date.getMonth() + 1;
  const nflSeasonYear = currentMonth < 9 ? currentYear - 1 : currentYear;

  // Get available game log years (last 5 years max)
  const availableGameLogYears = Object.keys(gameLogs)
    .map(Number)
    .sort((a, b) => b - a)
    .slice(0, 5);

  const displayYear = selectedGameLogYear || nflSeasonYear;
  const nflSeasonGameLogYear = gameLogs[displayYear] || {};
  
  // Get stat titles and sort by position
  const rawStatTitles = Object.keys(nflSeasonGameLogYear?.["Week 1"]?.stats || {});
  const statTitles = sortStatsByPosition(rawStatTitles, player.pos);
  
  // Get previous season stat titles and sort by position
  const rawPrevStatTitles = Object.keys(seasonStats[`${nflSeasonYear - 1}`] || {});
  const prevStatTitles = sortStatsByPosition(rawPrevStatTitles, player.pos);

  const addToFavorites = async() => {
    try{
        const respo = await fetch(`${API_URL}/user/nflPlayers/favorite/add`,{
            method : "POST",
            headers: { 
                "Content-Type": "application/json", 
                Authorization: `Bearer ${accessToken}`
            },
            body : JSON.stringify({
              playerId : player.id.toString()
            })
        });
        if(!respo.ok) {
            return;
        }
        setAddButton(false);
        setRemoveButton(true);
    } catch(e){
    }
  }

  const removeFromFavorites = async() => {
    try {
        const respo = await fetch(`${API_URL}/user/nflPlayers/favorite/remove`,{
            method : "PUT",
            headers: { 
                "Content-Type": "application/json", 
                Authorization: `Bearer ${accessToken}`
            },
            body : JSON.stringify({
              playerId : player.id.toString()
            })
        })
        if(!respo.ok){
            return;
        }
        setRemoveButton(false);
        setAddButton(true);
    } catch(e){
    }
  }

  return (
    <div className="NFLPlayerDataPage">
      <Navbar />
      <div className="NFLPlayerDataContainer">
        {/* Headshot and profile */}
        <div className="NFLPlayerDataPageHeadshotRow">
          {player.headshotUrl === "N/A" ? (
            <User size={100} />
          ) : (
            <img className="player-headshot" src={player.headshotUrl} alt={player.name} />
          )}
          <div className="player-name-section">
            <h2>{player.name}</h2>
            <div className="favorite-button-container">
              {addButton && (
                <button onClick={() => addToFavorites()} className="add-favorite-btn">
                  Add to Favorites
                </button>
              )}
              {removeButton && (
                <button onClick={() => removeFromFavorites()} className="remove-favorite-btn">
                  Remove from Favorites
                </button>
              )}
            </div>
          </div>
          {player.currentTeamLogoUrl !== "N/A" && player.team !== "N/A" && (
            <div className="NFLPlayerDataPageTeamStack">
              <img 
                className="team-logo" 
                src={player.currentTeamLogoUrl} 
                alt={player.team}
              />
              <h4>{player.team}</h4>
            </div>
          )}
          <div className="profile-item-wrapper">
            <h3>POS</h3>
            <h3>
              {player.pos} #{player.jersey?.trim()}
            </h3>
          </div>
          <div className="profile-item-wrapper">
            <h3>AGE</h3>
            <h3>{player.age}</h3>
          </div>
          <div className="profile-item-wrapper">
            <h3>HEIGHT</h3>
            <h3>{player.height}</h3>
          </div>
          <div className="profile-item-wrapper">
            <h3>WEIGHT</h3>
            <h3>{player.weight}</h3>
          </div>
          <div className="profile-item-wrapper">
            <h3>EXP</h3>
            <h3>{player.experience}</h3>
          </div>
        </div>

        {/* Game Logs - Current Season */}
        <div className="NFLPlayerDataPageGameLogContainer">
          <div className="game-log-header-row">
            <h3 className="section-title">GAME LOGS</h3>
            {availableGameLogYears.length > 0 && (
              <div className="year-selector">
                {availableGameLogYears.map(year => (
                  <button
                    key={year}
                    className={`year-button ${selectedGameLogYear === year ? 'active' : ''}`}
                    onClick={() => setSelectedGameLogYear(year)}
                  >
                    {year}
                  </button>
                ))}
              </div>
            )}
          </div>
          
          {/* Table Header */}
          <div className="NFLPlayerDataPageGameLogHeader">
            <div className="week-col">WK</div>
            <div className="opp-col">OPP</div>
            <div className="score-col">SCORE</div>
            {statTitles.map(stat => (
              <div key={stat} className="stat-col">
                <div className="stat-header">{formatStatCategory(stat)}</div>
              </div>
            ))}
          </div>

          {/* Table Rows */}
          {Array.from({ length: 18 }, (_, i) => {
            const weekData = nflSeasonGameLogYear[`Week ${i + 1}`] || {};
            const atVs = weekData.atVs === "@" ? "@" : "";
            const opponent = weekData.opponent === "N/A" ? "-" : weekData.opponent;
            const score = weekData.score ==="N/A" ? "-" : weekData.score;
            const gameResult = weekData.gameResult === "N/A" ? "" : weekData.gameResult;
            const weekStats = weekData.stats || {};

            return (
              <div key={i} className="NFLPlayerDataPageGameLogRow">
                <div className="week-col">{i + 1}</div>
                <div className="opp-col">{atVs}{opponent}</div>
                <div className="score-col">{score}{gameResult}</div>
                {statTitles.map(stat => {
                  const statValue = weekStats[stat];
                  const displayValue = statValue === "N/A" || statValue === undefined || statValue === null || statValue === "" 
                    ? "-" 
                    : formatStat(statValue);
                  
                  return (
                    <div key={stat} className="stat-col">
                      <div className="stat-value-cell">{displayValue}</div>
                    </div>
                  );
                })}
              </div>
            );
          })}
        </div>

        {/* Previous Season Stats */}
        <div className="NFLPlayerDataPagePrevStats">
          <h3 className="section-title">Current And Previous Season Stats</h3>
          
          {/* Table Header */}
          <div className="NFLPlayerDataPagePrevSeasonStatsHeader">
            <div className="year-col">YEAR</div>
            {prevStatTitles.map(stat => (
              <div key={stat} className="prev-stat-col">{formatStatCategory(stat)}</div>
            ))}
          </div>

          {/* Table Rows */}
          {Object.entries(seasonStats)
            .filter(([year]) => Number(year) <= nflSeasonYear)
            .sort(([yearA], [yearB]) => Number(yearB) - Number(yearA))
            .map(([year, stats]) => (
              <div key={year} className="NFLPlayerDataPagePrevSeasonStatsRow">
                <div className="year-col">{year}</div>
                {prevStatTitles.map(statname => {
                  const statObj = stats[statname];
                  return (
                    <div key={statname} className="prev-stat-col">
                      {statObj ? (
                        <>
                          <span className="stat-value">{formatStat(statObj.value)}</span>
                          <span className="stat-rank">(#{statObj.rank})</span>
                        </>
                      ) : (
                        "-"
                      )}
                    </div>
                  );
                })}
              </div>
            ))}
        </div>
      </div>
    </div>
  );
}

export default NFLPlayerData;