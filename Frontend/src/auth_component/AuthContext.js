// src/auth_component/AuthContext.js
import { createContext, useState, useEffect, useCallback } from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const [user, setUser] = useState(null); // { username, roles: [] }
  const [coins, setCoins] = useState(null);
  const [darkMode, setDarkMode] = useState(false);
  const [loading, setLoading] = useState(true);

  // ================== Helpers ==================
  const isAuthenticated = !!user;
  const hasRole = (role) => user?.roles?.includes(role);
  const isAdmin = hasRole("ADMIN");
  const isContestUser = hasRole("CONTEST_USER");
  const isNormalUser = hasRole("NORMAL_USER");

  // ================== Coins API ==================
  const fetchUserCoins = useCallback(async (token) => {
    try {
      const res = await axios.get(`${baseURL}/api/coin/get`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCoins(res.data.data);
    } catch (err) {
      console.error("Failed to fetch coins:", err);
      setCoins(null);
    }
  }, [baseURL]);

  // ================== Normalize Roles ==================
  const normalizeRoles = (decoded) => {
    if (Array.isArray(decoded.role)) return decoded.role.map(normalize);
    if (Array.isArray(decoded.roles)) return decoded.roles.map(normalize);
    if (Array.isArray(decoded.authorities))
      return decoded.authorities.map((a) =>
        typeof a === "string" ? normalize(a) : normalize(a.authority)
      );
    return [normalize(decoded.role || decoded.roles || decoded.authority)];
  };

  const normalize = (r) => String(r || "").toUpperCase().replace(/^ROLE_/, "");

  // ================== Init from LocalStorage ==================
  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem("token");
      if (token) {
        try {
          const decoded = jwtDecode(token);

          // Check if token expired
          if (decoded.exp * 1000 < Date.now()) {
            console.warn("Token expired — removing.");
            localStorage.removeItem("token");
            setUser(null);
          } else {
            const roles = normalizeRoles(decoded);
            const userData = { username: decoded.sub, roles };
            setUser(userData);
            await fetchUserCoins(token);
          }
        } catch (err) {
          console.error("Invalid token:", err);
          localStorage.removeItem("token");
        }
      }

      // Apply dark mode preference
      const storedDark = localStorage.getItem("darkMode");
      if (storedDark) setDarkMode(storedDark === "true");

      setLoading(false);
    };

    initAuth();
  }, [fetchUserCoins]);

  // ================== Auth Actions ==================
  const login = (token) => {
    localStorage.setItem("token", token);
    try {
      const decoded = jwtDecode(token);
      const roles = normalizeRoles(decoded);
      setUser({ username: decoded.sub, roles });
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
    const newMode = !darkMode;
    setDarkMode(newMode);
    localStorage.setItem("darkMode", newMode);
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
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
