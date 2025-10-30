import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function ProtectedRoute({ children }) {
    const [isChecking, setIsChecking] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL;

    useEffect(() => {
        async function verifyAuth() {
            const accessToken = sessionStorage.getItem("accessToken");
            const refreshToken = localStorage.getItem("refreshToken");

            // Both tokens present - user is good
            if (accessToken && refreshToken) {
                setIsAuthenticated(true);
                setIsChecking(false);
                return;
            }

            // No tokens at all - redirect to welcome
            if (!accessToken && !refreshToken) {
                navigate("/");
                return;
            }

            // Access token expired but refresh token exists - try to refresh
            if (!accessToken && refreshToken) {
                try {
                    const response = await fetch(`${API_URL}/api/refresh`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ refreshToken })
                    });
                    
                    if (response.ok) {
                        const data = await response.json();
                        sessionStorage.setItem("accessToken", data.accessToken);
                        setIsAuthenticated(true);
                    } else {
                        // Refresh token also invalid - clear and redirect
                        localStorage.removeItem("refreshToken");
                        sessionStorage.removeItem("accessToken");
                        navigate("/");
                    }
                } catch (error) {
                    localStorage.removeItem("refreshToken");
                    sessionStorage.removeItem("accessToken");
                    navigate("/");
                }
            }
            
            setIsChecking(false);
        }
        
        verifyAuth();
        
        // Check auth every 5 minutes (300000ms) while user is on the page
        const interval = setInterval(verifyAuth, 300000);
        
        // Cleanup interval on unmount
        return () => clearInterval(interval);
        
    }, [navigate, API_URL]);

    if (isChecking) {
        return (
            <div style={{
                display: 'flex', 
                justifyContent: 'center', 
                alignItems: 'center', 
                height: '100vh'
            }}>
                Loading...
            </div>
        );
    }
    
    return isAuthenticated ? children : null;
}

export default ProtectedRoute;