import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../../Components/Navbar";

function HomePage(){
    const [username,setUsername] = useState("");
    const [nflNews, setNflNews] = useState([]);
    const [nbaNews, setNbaNews] = useState([]);
    const [nflPlayerFavorites,setNflPlayerFavorites] = useState([]);
    const [nbaPlayerFavorites,setNbaPlayerFavorites] = useState([]);
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL;
    const access = sessionStorage.getItem("accessToken"); 

    useEffect(() => {
        async function getUserName(){
            try{
                const usernameFetch = await fetch(`${API_URL}/api/home`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization": `Bearer ${access}`
                    }
                });
                if(!usernameFetch.ok){
                    navigate("/");
                    return;
                }
                const usernameData = await usernameFetch.text();
                setUsername(usernameData);
            } catch(error){
            }

        }

        getUserName();
    }, [navigate]);

    useEffect(() => {
        if(!access) return;
        async function loadNFLNews(){
            try{
                const nflNewsResponse = await fetch(`${API_URL}/nfl/news`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization": `Bearer ${access}`
                    }
                });

                if(!nflNewsResponse.ok){
                    return;
                }
                const nflNews = await nflNewsResponse.json();
                setNflNews(nflNews);
            } catch(error){
            }
        }

        async function loadNBANews(){
            try{
                const nbaNewsResponse = await fetch(`${API_URL}/nba/news`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization": `Bearer ${access}`
                    }
                });
                if(!nbaNewsResponse.ok){
                    return;
                }
                const nbaNews = await nbaNewsResponse.json();
                setNbaNews(nbaNews);
            } catch(error){
            }
        }

        async function loadNFLPlayerFavorites(){
            if(!access){
                return;
            }
            try{
                const nflFavoriteResponse = await 
                    fetch(`${API_URL}/user/nflPlayers/favorite`,{
                        method : "POST",
                        headers : {
                            "Content-Type" : "application/json",
                            "Authorization": `Bearer ${access}`
                        }
                });

                if(!nflFavoriteResponse.ok){
                    return;
                }

                const nflFavorites = await nflFavoriteResponse.json();
                setNflPlayerFavorites(nflFavorites);
            } catch(error){
            }
        }

        async function loadNBAPlayerFavorites(){
            if(!access){
                return;
            }
            try{
                const nbaFavoriteResponse = await 
                    fetch(`${API_URL}/user/nbaPlayers/favorite`,{
                        method : "POST",
                        headers : {
                            "Content-Type" : "application/json",
                            "Authorization": `Bearer ${access}`
                        }
                });

                if(!nbaFavoriteResponse.ok){
                    return;
                }

                const nbaFavorites = await nbaFavoriteResponse.json();
                setNbaPlayerFavorites(nbaFavorites);
            } catch(error){
            }
        }

        loadNFLNews();
        loadNBANews();
        loadNFLPlayerFavorites();
        loadNBAPlayerFavorites();
    },[access])

    document.title = "Home";

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