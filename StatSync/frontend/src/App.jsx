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
import PublicRoute from "./Pages/UserSettings/PublicRoute";

function App() {

    return(
        <Router>
            <Routes>
            <Route path="/" element={
                    <PublicRoute>
                        <WelcomePage/>
                    </PublicRoute>
                }/>
                <Route path="/login" element={
                    <PublicRoute>
                        <LoginPage/>
                    </PublicRoute>
                }/>
                <Route path="/registration" element={
                    <PublicRoute>
                        <RegistrationPage/>
                    </PublicRoute>
                }/>
                <Route path="/forgotPassword" element={
                    <PublicRoute>
                        <ForgotPasswordPage/>
                    </PublicRoute>
                }/>
                <Route path="/verifyCode" element={
                    <PublicRoute>
                        <ConfirmVerificationCode/>
                    </PublicRoute>
                }/>
                <Route path="/createNewPassword" element={
                    <PublicRoute>
                        <SubmitForgotPassword/>
                    </PublicRoute>
                }/>
                
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