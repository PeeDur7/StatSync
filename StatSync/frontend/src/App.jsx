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

function App() {

    return(
        <Router>
            <Routes>
                <Route path = "/registration" element={<RegistrationPage/>}/>
                <Route path = "/login" element={<LoginPage/>}/>
                <Route path = "/home" element={<HomePage/>}/>
                <Route path = "/" element={<WelcomePage/>}/>
                <Route path = "/forgotPassword" element={<ForgotPasswordPage/>}/>
                <Route path = "/verifyCode" element={<ConfirmVerificationCode/>}/>
                <Route path = "/createNewPassword" element={<SubmitForgotPassword/>}/>
                <Route path = "/NFL/News" element={<NFLNews/>}/>
                <Route path ="/NFL/Players" element={<NFLPlayerList/>}/>
                <Route path="/NFL/Players/:playerId" element={<NFLPlayerData/>}/>
                <Route path="/NBA/News" element={<NBANews/>}/>
                <Route path="/NBA/Players" element={<NBAPlayerList/>}/>
                <Route path="/NBA/Players/:playerId" element={<NBAPlayerData/>}/>
            </Routes>
        </Router>
    )
}

export default App
