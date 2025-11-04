import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";

function ConfirmVerificationCode(){
    const location = useLocation();
    const email = location.state?.email;
    const navigate = useNavigate();
    const [verificationCode,setVerificationCode] = useState("");
    const [error, setError] = useState("");

    const API_URL = import.meta.env.VITE_API_URL; 


    useEffect(() => {
        if (!email) {
            navigate("/forgotPassword");
        }
    }, [email,navigate]);

    async function confirmVerificationCode(e){
        e.preventDefault();
        setError("");
        try{
            const response = await fetch(`${API_URL}/api/verify/forgot/password`, {
                method : "POST",
                headers : {"Content-Type" : "application/json"},
                body : JSON.stringify({
                    email : email,
                    verificationCode : verificationCode
                })
            });
            if(!response.ok){
                setError("Error matching verification code", {state : {email}});
                return;
            }
            const message = await response.text();
            if(message === "Success verifying code"){
                setError("");
                navigate("/createNewPassword", {state : {email}});
                return;
            } else {
                setError(message);
            }
        } catch(error){
        }
    }

    useEffect(() => {
        document.title = "Confirmation Code";
    }, []);
    
    return(
        <>
            <div className="confirmationCodeContainer">
                <h1>Verify Verification Code</h1>
                <div className="confirmationCodeBoxPadder">
                    <div className="confirmationCode">
                        <form onSubmit={confirmVerificationCode}>
                            <h4>Verification Code</h4>
                            <input
                                value={verificationCode}
                                placeholder="Enter verification code"
                                onChange={(e) => setVerificationCode(e.target.value)}
                                required
                            />
                            {error && (
                                <p style={{ color: "red", fontSize: "16px", marginTop: "15px",marginBottom : "0px", fontWeight : 'bold'}}>
                                {error}
                                </p>
                            )}
                            <br/><br/><button type="submit">Verify Verification Code</button>
                        </form>
                    </div>
                </div>
            </div>
        </>
    )
}

export default ConfirmVerificationCode