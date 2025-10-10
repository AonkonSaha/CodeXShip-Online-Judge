import { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const [user, setUser] = useState(null); // { username, role }
  const [coins, setCoins] = useState(null);
  const [darkMode, setDarkMode] = useState(false);

  // ================== Helpers ==================
  const isAuthenticated = !!user;
  const hasRole = (role) => user?.roles?.includes(role);
  const isAdmin = hasRole("ADMIN");
  const isContestUser = hasRole("CONTEST_USER");
  const isNormalUser = hasRole("NORMAL_USER");

  // ================== Coins API ==================
  const fetchUserCoins = async (token) => {
    try {
      const res = await axios.get(`${baseURL}/api/coin/get`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCoins(res.data.data);
    } catch (err) {
      console.error("Failed to fetch coins:", err);
      setCoins(null);
    }
  };

  // ================== Init from LocalStorage ==================
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        const userData = {
          username: decoded.sub,
          roles: Array.isArray(decoded.role) ? decoded.role : [decoded.role]};
        setUser(userData);
        fetchUserCoins(token);
      } catch (err) {
        console.error("Invalid token:", err);
        localStorage.removeItem("token");
      }
    }

    // Dark mode preference
    const storedDarkMode = localStorage.getItem("darkMode");
    if (storedDarkMode) {
      setDarkMode(storedDarkMode === "true");
    }
  }, []);

  // ================== Auth Actions ==================
  const login = (token) => {
    localStorage.setItem("token", token);
    try {
      const decoded = jwtDecode(token);
      setUser({ username: decoded.sub,  roles: Array.isArray(decoded.role) ? decoded.role : [decoded.role] });
      fetchUserCoins(token);
    } catch (err) {
      console.error("Failed to decode token:", err);
      logout();
    }
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
      setCoins(null);
    }
  };

  // ================== Dark Mode ==================
  const toggleDarkMode = () => {
    const newDarkMode = !darkMode;
    setDarkMode(newDarkMode);
    localStorage.setItem("darkMode", newDarkMode);
  };

  // ================== Context Value ==================
  return (
    <AuthContext.Provider
      value={{
        user,
        coins,
        darkMode,
        toggleDarkMode,
        login,
        logout,
        isAuthenticated,
        isAdmin,
        isContestUser,
        isNormalUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
