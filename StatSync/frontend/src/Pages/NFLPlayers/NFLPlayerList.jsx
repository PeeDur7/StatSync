import { useEffect } from "react";
import PlayerLists from "../../Components/PlayerLists";
import { useNavigate } from "react-router-dom";

function NFLPlayerList(){

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
                    const getRefreshToken = await fetch("http://localhost:8080/api/refresh", {
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
        <PlayerLists sportName="nfl"/>
    )
}

export default NFLPlayerList