function PasswordText({password, setPassword, showPassword, setShowPassword, confirmPassword, forgotPassword, registrationPage}){
    return(
        <>
            <div className="passwordText">
                <h4>{confirmPassword ? "Confirm Password" : "Password"}</h4>
                {registrationPage && (
                    <h5>Password must be at least 6 characters</h5>
                )}
                <input
                    type = {showPassword ? "text" : "password"}
                    value = {password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder={confirmPassword ? "Confirm Password" : "Enter Password"}
                    style = {{borderColor : password.length > 0 && password.length < 6 ? "red" : "black"}}
                />
                <div className = "showPasswordLogin">
                    <input
                        id={confirmPassword ? "showConfirmPasswordCheck" : "showPasswordCheck"}
                        type = "checkbox"
                        onChange={() => setShowPassword(!showPassword)}
                    />
                    <h4 style={{
                        paddingBottom : '10px',
                        fontWeight: 'normal',
                        paddingLeft: '8px',
                        marginTop: 0,
                    }}>
                        Show Password
                    </h4>
                    {forgotPassword && (
                        <a href="/forgotPassword" id="forgotPasswordLogin" style={{
                            paddingBottom : '0px',
                            fontWeight: 'normal',
                            paddingRight: '8px',
                            margin: 0
                        }}>
                            Forgot Password?
                        </a>
                    )}
                </div>
            </div>
        </>
    )
}

export default PasswordText