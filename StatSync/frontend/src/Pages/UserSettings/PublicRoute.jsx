import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function PublicRoute({ children }) {
    const [isChecking, setIsChecking] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL;

    useEffect(() => {
        async function verifyAuth() {
            const accessToken = sessionStorage.getItem("accessToken");
            const refreshToken = localStorage.getItem("refreshToken");

            // Both tokens present - user is authenticated, redirect to home
            if (accessToken && refreshToken) {
                navigate("/home");
                return;
            }

            // No tokens at all - user is not authenticated, allow access
            if (!accessToken && !refreshToken) {
                setIsAuthenticated(false);
                setIsChecking(false);
                return;
            }

            // Access token expired but refresh token exists - try to refresh
            if (!accessToken && refreshToken) {
                console.log("Attempting refresh with token:", refreshToken);
                try {
                    const response = await fetch(`${API_URL}/api/refresh`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ refreshToken })
                    });
                    
                    if (response.ok) {
                        console.log("✅ Refresh successful!");
                        const data = await response.json();
                        sessionStorage.setItem("accessToken", data.accessToken);
                        localStorage.setItem("refreshToken", data.refreshToken);
                        // User is authenticated, redirect to home
                        navigate("/home");
                        return;
                    } else {
                        // Refresh token also invalid - clear and allow access
                        localStorage.removeItem("refreshToken");
                        sessionStorage.removeItem("accessToken");
                        setIsAuthenticated(false);
                    }
                } catch (error) {
                    localStorage.removeItem("refreshToken");
                    sessionStorage.removeItem("accessToken");
                    setIsAuthenticated(false);
                }
            }
            
            setIsChecking(false);
        }
        
        verifyAuth();
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
    
    return !isAuthenticated ? children : null;
}

export default PublicRoute;