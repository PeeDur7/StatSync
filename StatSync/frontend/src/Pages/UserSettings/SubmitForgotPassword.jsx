import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import PasswordText from "../../Components/PasswordText";

function SubmitForgotPassword(){
    const location = useLocation();
    const email = location.state?.email;
    const navigate = useNavigate();
    const [newPassword,setNewPassword] = useState("");
    const [confirmNewPassword,setConfirmNewPassword] = useState("");
    const [showPassword,setShowPassword] = useState("");
    const [showConfirmPassword,setShowConfirmPassword] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        if (!email) {
            navigate("/forgotPassword");
            return;
        }
    }, [email,navigate]);

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
    },[]);

    async function submitPasswords(e){
        e.preventDefault();
        setError("");
        try{
            if(newPassword.length < 6 || confirmNewPassword.length < 6){
                setError("Passwords must be at least 6 characters");
                return;
            }
            const response = await fetch("http://localhost:8080/api/create/new/password",{
                method : "POST",
                headers : {"Content-Type" : "application/json"},
                body : JSON.stringify({
                    email : email,
                    newPassword : newPassword,
                    confirmPassword : confirmNewPassword
                })
            });
            if(!response.ok){
                setError("Passwords do not match");
                return;
            }
            const message = await response.text();
            if(message === "Password has been reset"){
                setError("");
                navigate("/login");
                return;
            } else {    
                setError("Passwords do not match");
                return;
            }
        } catch (error){
        }
    }

    document.title = "Create New Password";

    return(
        <>
            <div className = "createNewPasswordContainer">
                <h1>Create New Password</h1>
                <div className = "createNewPasswordBoxPadder">
                    <div className="createNewPassword">
                        <form onSubmit={submitPasswords}>
                            <PasswordText
                                password={newPassword}
                                setPassword={setNewPassword}
                                showPassword={showPassword}
                                setShowPassword={setShowPassword}
                            />
                            <PasswordText
                                password={confirmNewPassword}
                                setPassword={setConfirmNewPassword}
                                showPassword={showConfirmPassword}
                                setShowPassword={setShowConfirmPassword}
                                confirmPassword={true}
                            />
                            {error && (
                                <p style={{ color: "red", fontSize: "14px", marginTop: "15px",marginBottom : "0px" }}>
                                {error}
                                </p>
                            )}
                            <button type="submit">Create New Password</button>
                        </form>
                    </div>
                </div>
            </div>
        </>
    )
}

export default SubmitForgotPassword