import { useNavigate } from "react-router-dom";
import statPicture from "../../assets/StatPicture.png";
import newsPicture from "../../assets/news.png";
import writingPicture from "../../assets/writing.png";
import { useEffect } from "react";


function WelcomePage(){
    const navigate = useNavigate();

    document.title = "Welcome";

    useEffect(() => {
        async function checkUserAuth(){
            const accessToken = sessionStorage.getItem("accessToken");
            const refreshToken = localStorage.getItem("refreshToken");

            if(accessToken && refreshToken){
                navigate("/home");
                return;
            }

            else if(!accessToken && !refreshToken){
                return;
            } 
            
            else{
                try{
                    const getRefreshToken = await fetch("http://localhost:8080/api/refresh", {
                        method : "POST",
                        headers : {"Content-Type" : "application/json"},
                        body : JSON.stringify({
                            refreshToken : refreshToken
                        })
                    });
                    if(!getRefreshToken.ok){
                        return;
                    }
                    const data = await getRefreshToken.json();
                    navigate("/home");
                    return;
                } catch (error){
                }
            }
        }

        checkUserAuth();
    }, []);

    return(
        <>
            <div className = "HomePageContainer">
                <div className="HomePageIfNotLoggedIn">
                    <h1 style={{fontSize : '45px', marginBottom : '0px'}}>Welcome to StatSync!</h1>
                    <h3 style={{fontWeight: 'normal', fontStyle: 'italic',marginBottom : '30px'}}>
                        All your NBA and NFL stats, news, and player tracking in one place.
                    </h3>
                    <h3 style={{fontWeight : 'normal', fontStyle : 'italic', marginTop : '0px'}}>
                        NFL or NBA fanatics? Join StatSync to access 
                    </h3>
                    <div className = "HomePageReasons">
                        <p> <img src={statPicture}/> NBA and NFL players stats</p>
                        <p> <img src={newsPicture}/> NBA and NFL latest news</p>
                        <p> <img src={writingPicture}/> Track your favorite players</p>
                    </div>
                    <h3 className="goalText" style={{fontWeight : 'normal'}}>
                        Instead of jumping between multiple sites for stats, news, and updates, 
                        StatSync brings everything into one easy dashboard. Whether you're deep into fantasy, tracking your parlays,
                        or just love analyzing player trends, StatSync gives you tools to stay engaged. Our goal is to let you easily 
                        follow the players you care about most.
                    </h3>
                    <button onClick={() => navigate("/registration")}>
                        Get Started
                    </button>
                </div>
            </div>
        </>
    )
}

export default WelcomePage