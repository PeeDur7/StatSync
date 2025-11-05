import { useEffect, useState } from "react";
import Navbar from "./Navbar";

function NewsPages({sportName}){
    const [sportsNews, setSportsNews] = useState([]);
    const [searchText, setSearchText] = useState("");
    const [lastSearchedText, setLastSearchedText] = useState("");
    const[currentNews, setCurrentNews] = useState([]);
    const [loading, setLoading] = useState(true);

    const API_URL = import.meta.env.VITE_API_URL; 

    useEffect(() => {
        async function loadSportsNews(){
            const access = sessionStorage.getItem("accessToken");
            if(!access){
                return;
            }
            try{
                const newsResponse = await fetch(`${API_URL}/${sportName}/news`,{
                    method : "GET",
                    headers : {
                        "Content-Type" : "application/json",
                        "Authorization": `Bearer ${access}`
                    }
                });
    
                if(!newsResponse.ok){
                    return;
                }
                const newsResponseData = await newsResponse.json();
                setCurrentNews(newsResponseData);
                setSportsNews(newsResponseData);
            } catch(error){
            } finally {
                setLoading(false);
            }
        }
        loadSportsNews();
        document.title = `${sportName.toUpperCase()} News`
    },[]);

    async function getNewsOfSearchText(searchText){
        const accessToken = sessionStorage.getItem("accessToken");
        if(searchText.length === 0){
            setCurrentNews(sportsNews);
            setLastSearchedText("");
            return;
        }
        setLastSearchedText(searchText);
        try{
            const newsSearchResponse = await fetch(`${API_URL}/${sportName}/player/news?playerName=${searchText}`, {
                method : "GET",
                headers : {
                    "Content-Type" : "application/json",
                    "Authorization" : `Bearer ${access}`
                }
            });

            if(!newsSearchResponse.ok){
                return;
            }
            const newsSearchResponseData = await newsSearchResponse.json();
            setCurrentNews(newsSearchResponseData);
        } catch(error){
        }
    }

    if(loading){
        return(
            <div className="NewsPage">
                <Navbar/>
                <div className ="NewsPageContainer">
                    <h2>Loading news...</h2>
                </div>
            </div>
        )
    }

    return(
        <div className="NewsPage">
            <Navbar/>
            <div className ="NewsPageContainer">
                <form onSubmit={(e) => {
                    e.preventDefault();
                    getNewsOfSearchText(searchText);
                }}>
                    <div className="NewsPageSearch">
                        <input type="search" 
                            value = {searchText} 
                            onChange={(e) => setSearchText(e.target.value)}
                            placeholder="Search latest news"
                        />
                        <button onClick={
                            (e) => {
                                e.preventDefault();
                                getNewsOfSearchText(searchText)
                        }}>Search</button>
                    </div>
                    <div className="NewsPageColumn">
                        <div className="NewsPageFrame">
                            {currentNews.length === 0 && lastSearchedText.length > 0 && searchText.length > 0 &&
                                <h2>No latest news about {lastSearchedText}</h2>
                            }

                            {currentNews.length === 0 && searchText.length == 0 &&
                                sportsNews.map((article, index) => (
                                    <a key={index} href={article.espnLink} target="_blank">
                                        <img src={article.image !== "N/A" ? article.image : "/placeholder.jpg"}/>
                                        <div className="news-content">
                                            <h3>{article.headline}</h3>
                                            <p>{article.published}</p>
                                        </div>
                                    </a>
                                ))
                            }

                            {currentNews.map((article, index) => (
                                <a key={index} href={article.espnLink} target="_blank">
                                    <img src={article.image !== "N/A" ? article.image : "/placeholder.jpg"}/>
                                    <div className="news-content">
                                        <h3>{article.headline}</h3>
                                        <p>{article.published}</p>
                                    </div>
                                </a>
                            ))}
                        </div>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default NewsPages