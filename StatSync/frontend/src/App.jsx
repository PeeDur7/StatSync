import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./Pages/UserSettings/LoginPage";
import RegistrationPage from "./Pages/UserSettings/RegistrationPage";
import ForgotPasswordPage from "./Pages/UserSettings/ForgotPasswordPage";
import WelcomePage from "./Pages/UserSettings/WelcomePage";
import ConfirmVerificationCode from "./Pages/UserSettings/ConfirmVerificationCode";
import SubmitForgotPassword from "./Pages/UserSettings/SubmitForgotPassword";
import HomePage from "./Pages/UserSettings/HomePage";
import NFLNews from "./Pages/NFLPlayers/NFLNews";
import NFLPlayerList from "./Pages/NFLPlayers/NFLPlayerList";
import NFLPlayerData from "./Pages/NFLPlayers/NFLPlayerData";
import NBANews from "./Pages/NBAPlayers/NBANews";
import NBAPlayerList from "./Pages/NBAPlayers/NBAPlayerList";
import NBAPlayerData from "./Pages/NBAPlayers/NBAPlayerData";
import ProtectedRoute from "./Pages/UserSettings/ProtectedRoute";

function App() {

    return(
        <Router>
            <Routes>
                <Route path="/" element={<WelcomePage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/registration" element={<RegistrationPage/>}/>
                <Route path="/forgotPassword" element={<ForgotPasswordPage/>}/>
                <Route path="/verifyCode" element={<ConfirmVerificationCode/>}/>
                <Route path="/createNewPassword" element={<SubmitForgotPassword/>}/>
                
                <Route path="/home" element={
                    <ProtectedRoute>
                        <HomePage/>
                    </ProtectedRoute>
                }/>
                <Route path="/NFL/News" element={
                    <ProtectedRoute>
                        <NFLNews/>
                    </ProtectedRoute>
                }/>
                <Route path="/NFL/Players" element={
                    <ProtectedRoute>
                        <NFLPlayerList/>
                    </ProtectedRoute>
                }/>
                <Route path="/NFL/Players/:playerId" element={
                    <ProtectedRoute>
                        <NFLPlayerData/>
                    </ProtectedRoute>
                }/>
                <Route path="/NBA/News" element={
                    <ProtectedRoute>
                        <NBANews/>
                    </ProtectedRoute>
                }/>
                <Route path="/NBA/Players" element={
                    <ProtectedRoute>
                        <NBAPlayerList/>
                    </ProtectedRoute>
                }/>
                <Route path="/NBA/Players/:playerId" element={
                    <ProtectedRoute>
                        <NBAPlayerData/>
                    </ProtectedRoute>
                }/>
            </Routes>
        </Router>
    )
}

export default App