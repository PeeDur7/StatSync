import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar";
import { User } from "lucide-react";

function NBAPlayerData() {
  const { playerId } = useParams();
  const navigate = useNavigate();
  const [player, setPlayer] = useState(null);
  const [accessToken, setAccess] = useState("");
  const [refreshToken, setRefresh] = useState("");
  const [gameLogs, setGameLogs] = useState({});
  const [seasonStats, setSeasonStats] = useState({});
  const [removeButton, setRemoveButton] = useState(false);
  const [addButton, setAddButton] = useState(true);
  const [selectedGameLogYear, setSelectedGameLogYear] = useState(null);

  const nbaTeams = [
    "Hawks", "Celtics", "Nets", "Hornets", "Bulls", "Cavaliers", "Mavericks",
    "Nuggets", "Pistons", "Warriors", "Rockets", "Pacers", "Clippers", "Lakers",
    "Grizzlies", "Heat", "Bucks", "Timberwolves", "Pelicans", "Knicks", "Thunder",
    "Magic", "76ers", "Suns", "Trail Blazers", "Kings", "Spurs", "Raptors", "Jazz",
    "Wizards"
  ];

  // Helper functions for formatting
  const formatStat = (value) => {
    if (value === undefined || value === null || value === "-" || value === "") return "-";
    const num = Number(value);
    if (isNaN(num)) return value;
    return num % 1 === 0 ? num.toString() : num.toFixed(1);
  };

  const formatStatCategory = (category) => {
    // Handle common NBA abbreviations for game logs only
    const abbreviations = {
      'PTS': 'PTS',
      'REB': 'REB',
      'AST': 'AST',
      'STL': 'STL',
      'BLK': 'BLK',
      'TO': 'TO',
      'PF': 'PF',
      'MIN': 'MIN',
      'FG': 'FG',
      'FG%': 'FG%',
      '3PT': '3PT',
      '3P%': '3P%',
      'FT': 'FT',
      'FT%': 'FT%'
    };
    
    if (abbreviations[category]) return abbreviations[category];
    
    // Special case for NBARating
    if (category === 'NBARating') return 'NBA Rating';
    
    // For long stat names, just return as-is (capitalizing first letter of each word)
    const words = category.replace(/([A-Z])/g, ' $1').trim().split(' ');
    return words.map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
  };

  // Sort stats with priority order
  const sortStatTitles = (statTitles) => {
    // Map of stat names to their priority order
    const priorityMap = {
      'gamesPlayed': 1,
      'gamesStarted': 2,
      'avgMinutes': 3,
      'avgPoints': 4,
      'avgAssists': 5,
      'avgRebounds': 6,
      'avgSteals': 7,
      'avgBlocks': 8,
      'avgFouls': 9,
      'avgDefensiveRebounds': 10,
      'avgOffensiveRebounds': 11,
      'avgTurnovers': 12,
      // Then totals
      'MIN': 100, 'minutes': 100,
      'PTS': 101, 'points': 101,
      'AST': 102, 'assists': 102,
      'REB': 103, 'rebounds': 103,
      'STL': 104, 'steals': 104,
      'BLK': 105,
      'totalTurnovers': 106,
      'fouls': 107,
      'offensiveRebounds': 108,
      'defensiveRebounds': 109
    };
    
    return statTitles.sort((a, b) => {
      const aPriority = priorityMap[a] || 999;
      const bPriority = priorityMap[b] || 999;
      
      if (aPriority !== bPriority) {
        return aPriority - bPriority;
      }
      
      // If same priority or both not in priority, maintain original order
      return 0;
    });
  };

  // User auth check
  useEffect(() => {
    async function checkUserAuth() {
      const accessToken = sessionStorage.getItem("accessToken");
      const refreshToken = localStorage.getItem("refreshToken");

      if (!accessToken && !refreshToken) {
        navigate("/");
        return;
      }

      let accessTokenToUse = accessToken;
      setAccess(accessTokenToUse);

      if (!accessToken && refreshToken) {
        try {
          const getRefreshToken = await fetch("http://localhost:8080/api/refresh", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken }),
          });
          if (!getRefreshToken.ok) {
            navigate("/");
            return;
          }
          const data = await getRefreshToken.json();
          localStorage.setItem("refreshToken", data.refreshToken);
          sessionStorage.setItem("accessToken", data.accessToken);
          accessTokenToUse = data.accessToken;
          setAccess(accessTokenToUse);
          setRefresh(data.refreshToken);
        } catch (error) {
        }
      }
    }
    checkUserAuth();
  }, [navigate]);

  // Fetch player data
  useEffect(() => {
    if (!accessToken) return;

    async function getPlayerData() {
      try {
        const res = await fetch(`http://localhost:8080/nba/player/data?id=${playerId}`, {
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

  // Process stats - Updated to match your data structure
  useEffect(() => {
    if (!player?.stats) return;

    document.title = `${player.name} Stats`;

    const logs = {};
    const stats = {};
    
    Object.entries(player.stats).forEach(([season, data]) => {
      if (data.seasonTotalsAndRank) {
        stats[season] = data.seasonTotalsAndRank;
      }
      if (data.gameLog) {
        logs[season] = data.gameLog;
      }
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
    async function checkNBAPlayerInFavorites(){
        try{
            if(!player || !accessToken) return;
            
            const respo = await fetch(`http://localhost:8080/user/nbaPlayers/favorite/player?player=${player.name}`,{
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
            const isInFavorites = favoriteList.some(favPlayer => 
              favPlayer.id === player.id || favPlayer.id === player._id
            );
            
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

    checkNBAPlayerInFavorites();
  }, [player, accessToken]);

  if (!player) {
    return (
      <div className="NBAPlayerDataPage">
        <Navbar />
        <div className="NBAPlayerDataContainer">
          <h2>Loading player data...</h2>
        </div>
      </div>
    );
  }

  const date = new Date();
  const currentYear = date.getFullYear();
  const currentMonth = date.getMonth() + 1;
  const calculatedSeasonYear = currentMonth < 10 ? currentYear - 1 : currentYear;

  // Get available game log years (last 5 years max)
  const availableGameLogYears = Object.keys(gameLogs)
    .map(Number)
    .sort((a, b) => b - a)
    .slice(0, 5);

  const displayYear = selectedGameLogYear || (availableGameLogYears.length > 0 ? availableGameLogYears[0] : calculatedSeasonYear);
  const nbaSeasonGameLogYear = gameLogs[displayYear] || {};
  
  // Get stat titles from first available game and sort them
  const firstGameKey = Object.keys(nbaSeasonGameLogYear)[0];
  const rawStatTitles = firstGameKey ? Object.keys(nbaSeasonGameLogYear[firstGameKey]?.stats || {}) : [];
  const statTitles = sortStatTitles(rawStatTitles);
  
  // Get previous season stat titles - collect all unique stat names from all seasons
  const allPrevStatNames = new Set();
  Object.values(seasonStats).forEach(yearStats => {
    Object.keys(yearStats).forEach(statName => {
      allPrevStatNames.add(statName);
    });
  });
  const prevStatTitles = sortStatTitles(Array.from(allPrevStatNames));

  const addToFavorites = async() => {
    try{
        const respo = await fetch(`http://localhost:8080/user/nbaPlayers/favorite/add`,{
            method : "POST",
            headers: { 
                "Content-Type": "application/json", 
                Authorization: `Bearer ${accessToken}`
            },
            body : JSON.stringify({
              playerId : (player.id || player._id).toString()
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
        const respo = await fetch(`http://localhost:8080/user/nbaPlayers/favorite/remove`,{
            method : "PUT",
            headers: { 
                "Content-Type": "application/json", 
                Authorization: `Bearer ${accessToken}`
            },
            body : JSON.stringify({
              playerId : (player.id || player._id).toString()
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

  // Helper function to adjust date by subtracting 1 day
  const adjustDate = (dateString) => {
    if (!dateString || dateString === "-") return "-";
    
    const parts = dateString.split('/');
    if (parts.length !== 2) return dateString;
    
    let month = parseInt(parts[0]);
    let day = parseInt(parts[1]);
    
    // Subtract 1 from day
    day = day - 1;
    
    // Handle month transitions
    if (day === 0) {
      month = month - 1;
      if (month === 0) {
        month = 12;
      }
      
      // Days in each month (non-leap year approximation)
      const daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
      day = daysInMonth[month - 1];
    }
    
    return `${month}/${day}`;
  };

  return (
    <div className="NBAPlayerDataPage">
      <Navbar />
      <div className="NBAPlayerDataContainer">
        {/* Headshot and profile */}
        <div className="NBAPlayerDataPageHeadshotRow">
          {!player.headshotUrl ? (
            <User size={100} color="white" />
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
          {(player.currentTeamLogo || player.currentTeamLogoUrl) && player.team && (
            <div className="NBAPlayerDataPageTeamStack">
              <img 
                className="team-logo" 
                src={player.currentTeamLogo || player.currentTeamLogoUrl} 
                alt={player.team}
              />
              <h4>{player.team}</h4>
            </div>
          )}
          <div className="profile-item-wrapper">
            <h3>POS</h3>
            <h3>
              {player.pos || "N/A"} {player.jersey && player.jersey !== "?" ? `#${player.jersey.trim()}` : ""}
            </h3>
          </div>
          <div className="profile-item-wrapper">
            <h3>AGE</h3>
            <h3>{player.age || "N/A"}</h3>
          </div>
          {player.height && (
            <div className="profile-item-wrapper">
              <h3>HEIGHT</h3>
              <h3>{player.height}</h3>
            </div>
          )}
          {player.weight && (
            <div className="profile-item-wrapper">
              <h3>WEIGHT</h3>
              <h3>{player.weight}</h3>
            </div>
          )}
          <div className="profile-item-wrapper">
            <h3>EXP</h3>
            <h3>{player.experience || "N/A"}</h3>
          </div>
        </div>

        {/* Game Logs - Current Season */}
        {statTitles.length > 0 && (
          <div className="NBAPlayerDataPageGameLogContainer">
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
            <div className="NBAPlayerDataPageGameLogHeader">
              <div className="game-col">GM</div>
              <div className="date-col">DATE</div>
              <div className="opp-col">OPP</div>
              <div className="score-col">SCORE</div>
              {statTitles.map(stat => (
                <div key={stat} className="stat-col">
                  <div className="stat-header">{formatStatCategory(stat)}</div>
                </div>
              ))}
            </div>

            {/* Table Rows */}
            {Object.entries(nbaSeasonGameLogYear)
              .sort(([keyA], [keyB]) => {
                // Sort by game ID numerically
                return Number(keyA) - Number(keyB);
              })
              .map(([gameId, gameData], index) => {
                const atVs = gameData.atVs === "@" ? "@" : "";
                const opponent = gameData.opponent || "-";
                const score = gameData.score || "-";
                const gameResult = gameData.gameResult && gameData.gameResult !== "N/A" ? ` ${gameData.gameResult}` : "";
                const gameDate = adjustDate(gameData.gameDate);
                const gameStats = gameData.stats || {};

                return (
                  <div key={gameId} className="NBAPlayerDataPageGameLogRow">
                    <div className="game-col">{index + 1}</div>
                    <div className="date-col">{gameDate}</div>
                    <div className="opp-col">{atVs} {opponent}</div>
                    <div className="score-col">{score}{gameResult}</div>
                    {statTitles.map(stat => {
                      const statValue = gameStats[stat];
                      const displayValue = !statValue || statValue === "N/A" || statValue === "" 
                        ? "-" 
                        : statValue;
                      
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
        )}

        {/* Previous Season Stats */}
        {prevStatTitles.length > 0 && (
          <div className="NBAPlayerDataPagePrevStats">
            <h3 className="section-title">Current And Previous Season Stats</h3>
            
            {/* Table Header */}
            <div className="NBAPlayerDataPagePrevSeasonStatsHeader">
              <div className="year-col">YEAR</div>
              {prevStatTitles.map(stat => (
                <div key={stat} className="prev-stat-col">{formatStatCategory(stat)}</div>
              ))}
            </div>

            {/* Table Rows */}
            {Object.entries(seasonStats)
              .filter(([year]) => Number(year) <= calculatedSeasonYear)
              .sort(([yearA], [yearB]) => Number(yearB) - Number(yearA))
              .map(([year, stats]) => (
                <div key={year} className="NBAPlayerDataPagePrevSeasonStatsRow">
                  <div className="year-col">{year}</div>
                  {prevStatTitles.map(statname => {
                    const statObj = stats[statname];
                    return (
                      <div key={statname} className="prev-stat-col">
                        {statObj && statObj.value !== undefined ? (
                          <>
                            <span className="stat-value">{formatStat(statObj.value)}</span>
                            {statObj.rank && <span className="stat-rank">(#{statObj.rank})</span>}
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
        )}
      </div>
    </div>
  );
}

export default NBAPlayerData;