import { useState} from "react";
import { useNavigate } from "react-router-dom";
import PasswordText from "../../Components/PasswordText";

function RegistrationPage(){
    const [username,setUsername] = useState("");
    const [email,setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showPassword,setShowPassword] = useState("");
    const [showConfirmPassword,setShowConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL; 

    async function registerUser(e){
        e.preventDefault();
        setError("");
        try{
            if(username.length == 0){
                setError("Username is empty");
                return;
            }
            else if(email.length < 3) {
                setError("Email must be at least 3 characters");
                return;
            }
            else if(password.length < 6) {
                setError("Password must be at least 6 characters");
                return;
            }
            else if (password !== confirmPassword) {
                setError("Passwords do not match");
                return;
            }
            const response = await fetch(`${API_URL}/api/register`, {
                method : "POST",
                headers : {"Content-Type" : "application/json"},
                body : JSON.stringify({
                    name : username,
                    email : email,
                    password : password
                })
            });
            if(!response.ok){
                throw new Error("Could not register user");
            }
            const message = await response.text();
            if(message === "User added successfully"){
                navigate("/login");
            } else {
                setError(message);
            }
        } catch(error){
        }
    }

    document.title = "Registration";

    return(
        <>  
            <div className="registrationPageContainer">
                <h1 style= {{display : 'flex', justifyContent : 'center', fontFamily : 
                    'Arial', fontWeight : 'normal', color : 'white'
                }}>Sign Up</h1>
                <h4 style ={{display : 'flex', justifyContent : 'center', fontFamily : 'Arial',
                    fontWeight : 'normal', fontStyle : 'italic', marginTop : '0px', color : 'rgb(180, 184, 184)'
                }}>Create a Free Account To Access StatSync!</h4>
                <div className = "registrationInputsContainer">
                    <div className="registrationInputs">
                        <form onSubmit={registerUser}>
                            <h4>Username</h4>
                            <input 
                                type="text"
                                value={username} 
                                onChange={(e) => setUsername(e.target.value)} 
                                placeholder="Enter Username"
                            />
                            <h4>Email</h4>
                            <h5>Email must be at least 3 characters</h5>
                            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} 
                            placeholder="Enter Email" style={{borderColor : email.length > 0 && email.length < 3 ? "red" : "black"}}/>
                            <PasswordText 
                                password={password}
                                registrationPage={true}
                                setPassword={setPassword}
                                showPassword={showPassword}
                                setShowPassword={setShowPassword}
                            />
                            <PasswordText 
                                password={confirmPassword}
                                setPassword={setConfirmPassword}
                                showPassword={showConfirmPassword}
                                setShowPassword={setShowConfirmPassword}
                                confirmPassword={true}
                            />
                            {error && (
                                <p style={{ 
                                    color: "red", fontSize: "16px", marginTop: "15px",marginBottom : "0px", fontWeight : 'bold'
                                }}>
                                {error}
                                </p>
                            )}
                            <button type="submit" id="signUp">Sign Up</button>
                        </form>
                        <div className="alreadyHaveAnAccountRegPage">
                                <h4 style={{fontWeight : 'normal'}}>
                                    Have an Account? Login
                                </h4>
                                <a href="/login">
                                    Here
                                </a>
                            </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default RegistrationPage