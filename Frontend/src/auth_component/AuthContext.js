import { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";
import axios from "axios";
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const [user, setUser] = useState(null);
  const [coins,setCoins]=useState(null);
  const [darkMode, setDarkMode] = useState(false);

  const fetchUserCoins = async (token) => {
    try {
      const res = await axios.get(`${baseURL}/api/coin/get`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCoins(res.data.data); 
    } catch (err) {
      console.error("Failed to fetch user info:", err);
      setCoins(null);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      const decoded = jwtDecode(token);
      setUser({ username: decoded.sub, role: decoded.role });
      fetchUserCoins(token);
    }

    const storedDarkMode = localStorage.getItem("darkMode");
    if (storedDarkMode) {
      setDarkMode(storedDarkMode === "true");
    }
  }, []);

  const login = (token) => {
    localStorage.setItem("token", token);
    const decoded = jwtDecode(token);
    setUser({ username: decoded.sub, role: decoded.role });
  };

const logout = async () => {
    const token = localStorage.getItem("token");
    try {
      if (token) {
        await axios.post(
          `${baseURL}/api/auth/v1/logout`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        );
      }
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      localStorage.removeItem("token");
      setUser(null);
      // navigate("/login");
    }
  };

  // Toggle dark mode
  const toggleDarkMode = () => {
    const newDarkMode = !darkMode;
    setDarkMode(newDarkMode);
    localStorage.setItem("darkMode", newDarkMode); // Persist dark mode preference
  };

  return (
    <AuthContext.Provider value={{ user, login, coins, logout, darkMode, toggleDarkMode }}>
      {children}
    </AuthContext.Provider>
  );
};
