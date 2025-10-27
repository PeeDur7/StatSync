import { useEffect, } from "react";
import { useNavigate } from "react-router-dom";
import PlayerLists from "../../Components/PlayerLists";

function NBAPlayerList(){
        const navigate = useNavigate();
        const API_URL = import.meta.env.VITE_API_URL; 

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
            <PlayerLists sportName="nba"/>
        )
}

export default NBAPlayerList