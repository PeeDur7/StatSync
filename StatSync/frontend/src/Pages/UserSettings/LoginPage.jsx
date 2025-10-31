import {useState} from "react";
import { useNavigate } from "react-router-dom";
import PasswordText from "../../Components/PasswordText";

function LoginPage(){
    const [user,setUser] = useState("");
    const [password,setPassword] = useState("");
    const navigate = useNavigate();
    const [showPassword,setShowPassword] = useState(false);
    const [error,setError] = useState("");
    const API_URL = import.meta.env.VITE_API_URL; 


    const setUserInfo = (e) => {
        setUser(e.target.value);
        setError("");
    }
    
    const setPasswordInfo = (e) => {
        setPassword(e.target.value);
    }

    document.title = "Login";

    //checks if idenfitifer and password matches the database info
    async function checkAuthenticationAndSaveToken(e){
        e.preventDefault();
        setError("");
        try {
            const response = await fetch(`${API_URL}/api/authenticate`, {
                method : "POST",
                headers : {"Content-Type" : "application/json"},
                body : JSON.stringify({
                    identifier : user,
                    password : password
                })
            });
            if(!response.ok){
                setError("Invalid Credientials");
                return;
            }
            const data = await response.json();
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);

            navigate("/home");
            return;
        } catch (error){
        }
    }
    const passwordType = showPassword ? "text" : "password";
    return(
        <>
            <div className="loginPageContainer">
                <h1 style= {{display : 'flex', justifyContent : 'center', fontFamily : 'Arial', fontWeight : 'normal',
                    marginBottom : '15px'
                }}>
                    Login
                </h1>
                <h4 style ={{display : 'flex', justifyContent : 'center', fontFamily : 'Arial',
                    fontWeight : 'normal', fontStyle : 'italic', marginTop : '0px', color : 'rgb(180, 184, 184)'
                }}>Please Login to Continue</h4>
                <div className = "loginInputTextsContainer">
                    <div className = "loginInputTexts">
                        <form onSubmit = {checkAuthenticationAndSaveToken}>
                            <h4>Username/Email</h4>
                            <input type="text" value = {user} onChange={(setUserInfo)} placeholder="Enter Username or Email"/>
                            <br></br>
                            <PasswordText
                                password={password}
                                setPassword={setPassword}
                                showPassword={showPassword}
                                setShowPassword={setShowPassword}
                                forgotPassword={true}
                            />
                            {error && (
                                <p style={{ 
                                    color: "red", fontSize: "16px", marginTop: "15px",marginBottom : "0px", fontWeight : 'bold'
                                }}>
                                {error}
                                </p>
                            )}
                            <br/><br/><button type = "submit" id="loginButton">Login</button>
                        </form>
                        <div className="createAccountOnLoginPage">
                            <h4 style={{fontWeight : 'normal'}}>
                                Don't Have an Account? Sign Up
                            </h4>
                            <a href="/registration">
                                Here
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default LoginPage