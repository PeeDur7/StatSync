import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function ForgotPasswordPage(){
    const [email,setEmail] = useState("");
    const [error, setError] = useState("");
    const [buttonMessage,setButtonMessage] = useState("Send Verification Code");
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL; 

    async function sendVerificationCode(e){
        e.preventDefault();
        setError("");
        setButtonMessage("Loading...");
        try{
            const response = await fetch(
                `${API_URL}/api/send/password/recovery?email=${encodeURIComponent(email)}`, {
                method : "GET",
                headers : {"Content-Type" : "application/json"}
            });
            if(response.ok){
                setError("");
                navigate("/verifyCode", {state : {email}});
                return;
            }
            if(response.status === 404){
                setError("User not found");
            } else if (response.status === 429){
                setError("Email has already been sent. Please wait 15 minutes.");
            } else {
                setError("Something went wrong. Try again later.");
            }
            setButtonMessage("Send Verification Code");
        } catch(error){
        }
    }

    useEffect(() => {
        document.title = "Forgot Password";
    }, []);
    return(
        <div className="forgotPasswordContainer">
            <h1>Forgot Password</h1>
            <div className="forgotPasswordBoxPadder">
                <div className="forgotPassword">
                    <form onSubmit={sendVerificationCode}>
                        <h4>Email</h4>
                        <input
                            type="email"
                            value={email}
                            placeholder="Enter your email"
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        {error && (
                            <p style={{ 
                                color: "red", fontSize: "16px", marginTop: "15px",marginBottom : "0px", fontWeight : 'bold'
                            }}>
                            {error}
                            </p>
                        )}
                        <br/><br/><button type="submit">{buttonMessage}</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default ForgotPasswordPage