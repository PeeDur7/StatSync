import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar";

function HomePage(){
    const [username,setUsername] = useState("");
    const [nflNews, setNflNews] = useState([]);
    const [nbaNews, setNbaNews] = useState([]);
    const [nflPlayerFavorites,setNflPlayerFavorites] = useState([]);
    const [nbaPlayerFavorites,setNbaPlayerFavorites] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL;

    useEffect(() => {
        const access = sessionStorage.getItem("accessToken");
        if(!access) return;
    
        const loadData = async () => {
            try {
                await Promise.all([
                    (async () => {
                        try {
                            const response = await fetch(`${API_URL}/nfl/news/home?limit=4`, {
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${access}`
                                }
                            });
                            if(response.ok) {
                                const data = await response.json();
                                setNflNews(data);
                            }
                        } catch {}
                    })(),
                    (async () => {
                        try {
                            const response = await fetch(`${API_URL}/nba/news/home?limit=4`, {
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${access}`
                                }
                            });
                            if(response.ok) {
                                const data = await response.json();
                                setNbaNews(data);
                            }
                        } catch {}
                    })(),
                    (async () => {
                        try {
                            const response = await fetch(`${API_URL}/user/nflPlayers/favorite`, {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${access}`
                                }
                            });
                            if(response.ok) {
                                const data = await response.json();
                                setNflPlayerFavorites(data);
                            }
                        } catch {}
                    })(),
                    (async () => {
                        try {
                            const response = await fetch(`${API_URL}/user/nbaPlayers/favorite`, {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${access}`
                                }
                            });
                            if(response.ok) {
                                const data = await response.json();
                                setNbaPlayerFavorites(data);
                            }
                        } catch {}
                    })(),
                    (async () => {
                        try {
                            const response = await fetch(`${API_URL}/api/home`, {
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${access}`
                                }
                            });
                            if(!response.ok) {
                                navigate("/");
                                return;
                            }
                            const username = await response.text();
                            setUsername(username);
                        } catch {}
                    })()
                ]);
            } finally {
                setLoading(false); 
            }
        };
    
        loadData();
    }, [navigate, API_URL]);
    

    useEffect(() => {
        document.title = "Home";
    },[]);

    if(loading){
        return(
            <div className="HomePageIfLoggedInNav">
                <Navbar/>
                <div className="HomePageIfLoggedInContainer">
                    <h2>Loading Home Page...</h2>
                </div>
            </div>
        )
    }

    return(
        <div className="HomePageIfLoggedInNav">
            <Navbar/>
            <div className="HomePageIfLoggedInContainer">
                <h1 className="welcome-header">Welcome {username}!</h1>
                
                <div className="home-grid">
                    {/* NFL News Section */}
                    <div className="home-section">
                        <h2 className="section-title">Latest NFL News</h2>
                        <div className="news-grid">
                            {nflNews.length > 0 && nflNews.slice(0, 4).map((article, index) => (
                                <a key={index} href={article.espnLink} target="_blank" rel="noopener noreferrer" className="news-card">
                                    <img src={article.image !== "N/A" ? article.image : "/placeholder.jpg"} alt={article.headline}/>
                                    <div className="news-content">
                                        <h3>{article.headline}</h3>
                                        <p>{article.published}</p>
                                    </div>
                                </a>
                            ))}
                        </div>
                    </div>

                    {/* NBA News Section */}
                    <div className="home-section">
                        <h2 className="section-title">Latest NBA News</h2>
                        <div className="news-grid">
                            {nbaNews.length > 0 && nbaNews.slice(0, 4).map((article, index) => (
                                <a key={index} href={article.espnLink} target="_blank" rel="noopener noreferrer" className="news-card">
                                    <img src={article.image !== "N/A" ? article.image : "/placeholder.jpg"} alt={article.headline}/>
                                    <div className="news-content">
                                        <h3>{article.headline}</h3>
                                        <p>{article.published}</p>
                                    </div>
                                </a>
                            ))}
                        </div>
                    </div>

                    {/* NFL Favorites Section */}
                    <div className="home-section">
                        <h2 className="section-title">Your NFL Favorites</h2>
                        <div className="favorites-grid">
                            {nflPlayerFavorites.length > 0 ? (
                                nflPlayerFavorites.slice(0, 6).map((player, index) => (
                                    <div key={index} className="player-card">
                                        <Link to={`/NFL/Players/${player.id}`}>
                                            <div className="player-images">
                                                <img src={player.headshotUrl} alt={player.name} className="player-headshot"/>
                                                <img src={player.currentTeamLogoUrl} alt={player.team} className="team-logo-small"/>
                                            </div>
                                            <h4>{player.name}</h4>
                                            <p className="player-pos">{player.pos}</p>
                                            <p className="player-team">{player.team}</p>
                                            <p className="player-age">Age: {player.age}</p>
                                        </Link>
                                    </div>
                                ))
                            ) : (
                                <p className="no-favorites">No favorites yet</p>
                            )}
                        </div>
                    </div>

                    {/* NBA Favorites Section */}
                    <div className="home-section">
                        <h2 className="section-title">Your NBA Favorites</h2>
                        <div className="favorites-grid">
                            {nbaPlayerFavorites.length > 0 ? (
                                nbaPlayerFavorites.slice(0, 6).map((player, index) => (
                                    <div key={index} className="player-card">
                                        <Link to={`/NBA/Players/${player.id}`}>
                                            <div className="player-images">
                                                <img src={player.headshotUrl} alt={player.name} className="player-headshot"/>
                                                <img src={player.currentTeamLogo} alt={player.team} className="team-logo-small"/>
                                            </div>
                                            <h4>{player.name}</h4>
                                            <p className="player-pos">{player.pos}</p>
                                            <p className="player-team">{player.team}</p>
                                            <p className="player-age">Age: {player.age}</p>
                                        </Link>
                                    </div>
                                ))
                            ) : (
                                <p className="no-favorites">No favorites yet</p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default HomePage