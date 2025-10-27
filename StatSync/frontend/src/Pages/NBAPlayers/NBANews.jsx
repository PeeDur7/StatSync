import { useEffect } from "react";
import NewsPages from "../../Components/NewsPages";
import { useNavigate } from "react-router-dom";


function NBANews(){
    const API_URL = import.meta.env.VITE_API_URL; 
    const navigate = useNavigate();
    useEffect(() => {
            async function checkUserAuth(){
                const accessToken = sessionStorage.getItem("accessToken");
                const refreshToken = localStorage.getItem("refreshToken");
    
                if(!accessToken && !refreshToken){
                    navigate("/");
                    return;
                } 
    
                if(!accessToken && refreshToken){
                    try{
                        const getRefreshToken = await fetch(`${API_URL}/api/refresh`, {
                            method : "POST",
                            headers : {"Content-Type" : "application/json"},
                            body : JSON.stringify({
                                refreshToken : refreshToken
                            })
                        });
                        if(!getRefreshToken.ok){
                            navigate("/");
                            return;
                        }
                        const data = await getRefreshToken.json();
                        localStorage.setItem("refreshToken",data.refreshToken);
                        sessionStorage.setItem("accessToken",data.accessToken);
                    } catch (error){
                    }
                }
            }
    
            checkUserAuth();
    }, []);

    return(
        <NewsPages sportName="nba"/>
    )
}

export default NBANews;