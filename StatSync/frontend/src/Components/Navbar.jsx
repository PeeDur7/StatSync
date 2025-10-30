import { useEffect, useState } from "react";
import userProfileIcon from "../assets/user-128.png";
import { useNavigate } from "react-router-dom";

function Navbar(){
    const [nflDrop, setNFLDrop] = useState(false);
    const [nbaDrop, setNBADrop] = useState(false);
    const [userDrop, setUserDrop] = useState(false);
    const navigate = useNavigate();

    const API_URL = import.meta.env.VITE_API_URL; 

    const changeNFLDrop = () => {
        setNFLDrop(prev => !prev);
        setNBADrop(false);
    }

    const changeNBADrop = () => {
        setNBADrop(prev => !prev);
        setNFLDrop(false);
    }

    const userDropDown = () => {
        setUserDrop(prev => !prev);
    }

    const logOut = async () => {
        try{
            const token = sessionStorage.getItem("accessToken");
            const respo = await fetch(`http://localhost:8080/api/logout`,{
                method : "POST",
                headers : {
                    "Content-Type": "application/json", 
                    Authorization: `Bearer ${token}`
                },
            });
            if(respo.ok){
                sessionStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");

                navigate("/");
            }
        } catch(e){
        }
    }

    return(
        <div className="navbar">
            <nav>
                <a href="/">Home</a>
                <div className="NFLNav">
                    <button onClick={changeNFLDrop} className={`NFLDrop ${nflDrop ? "active" : ""}`}>NFL</button>
                    {nflDrop && 
                        <div className="NFLDropList" id="NFLDropListId">
                            <a href="/NFL/News">News</a>
                            <a href="/NFL/Players">Players</a>
                        </div>
                    }
                </div>
                <div className="NBANav">
                    <button onClick={changeNBADrop} className={`NBADrop ${nbaDrop ? "active" : ""}`}>NBA</button>
                    {nbaDrop && 
                        <div className="NBADropList" id="NBADropListId">
                            <a href="/NBA/News">News</a>
                            <a href="/NBA/Players">Players</a>
                        </div>
                    }
                </div>
                <div className="UserNav">
                    <button onClick={userDropDown} className={`UserDrop ${userDrop ? "active" : ""}`}>
                        <img src={userProfileIcon}/>
                    </button>
                    {userDrop && 
                        <div className="UserDropList" id="UserDropListId">
                            <button onClick={logOut} style={{'background' : 'red'}}>Log Out</button>
                        </div>
                    }
                </div>
            </nav>
        </div>
    )
}

export default Navbar