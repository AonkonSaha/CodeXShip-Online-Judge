import { createContext, useState, useEffect, useCallback, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import toast from "react-hot-toast";


export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const [user, setUser] = useState(null); // { username, roles: [] }
  const [coins, setCoins] = useState(null);
  const [darkMode, setDarkMode] = useState(true);
  const [loading, setLoading] = useState(true);
  const logoutTimerRef = useRef(null);
  const [dailyCoins,setDailyCoins] = useState(0);
  const [isAddDailyCoin,setIsAddDailyCoin] = useState(false);
  const [streak,setStreak] = useState(0);


  // ================== Helpers ==================
  const isAuthenticated = !!user;
  const hasRole = (role) => user?.roles?.includes(role);
  const isAdmin = hasRole("ADMIN");
  const isContestUser = hasRole("CONTEST_USER");
  const isNormalUser = hasRole("NORMAL_USER");
  const isProblemEditor = hasRole("PROBLEM_EDITOR");

  // ================== Coins API ==================
  const fetchUserCoins = useCallback(
    async (token) => {
      try {
        const res = await axios.get(`${baseURL}/api/coin/get`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setCoins(res.data.data.total_present_coins);
        setUser((prev) => ({
        ...prev,
        image_url: res.data.data.image_url }));
      } catch (err) {
        console.error("Failed to fetch coins:", err);
        setCoins(null);
      }
    },
    [baseURL]
  );

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

  // ================== Logout ==================
  const logout = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const decoded = jwtDecode(token);
  
      if (token) {
        toast.error("You are logout by System")
        await axios.post(
          `${baseURL}/api/auth/v1/logout`,
          {},
          { headers: { Authorization: `Bearer ${token}`}}
          
        );
      }
    } catch (err) {
      console.error("Logout API call failed:", err);
    } finally {
      localStorage.removeItem("token");
      setUser(null);
      setCoins(null);
      clearTimeout(logoutTimerRef.current);
    }
  }, [baseURL]);
  const clearUserInfo= ()=>{
    localStorage.removeItem("token");
    setUser(null);
    setCoins(null);
  }

  // ================== Auto Logout Scheduler ==================
  const scheduleAutoLogout = (expTime) => {
    clearTimeout(logoutTimerRef.current);
    const timeLeft = expTime * 1000 - Date.now();
    if (timeLeft > 0) {
      logoutTimerRef.current = setTimeout(() => {
        console.warn("JWT expired — auto logging out");
        logout();
      }, timeLeft);
    }
  };

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
            const userData = { username: decoded.sub, roles, userId:decoded.userId};
            setUser(userData);
            await fetchUserCoins(token);
            scheduleAutoLogout(decoded.exp);
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
  }, [fetchUserCoins, logout]);

  // ================== Login ==================
  const login = (token) => {
    localStorage.setItem("token", token);
    try {
      const decoded = jwtDecode(token);
      const roles = normalizeRoles(decoded);
      const userData = {
        username: decoded.sub,
        roles,
        image_url: decoded.image_url,
        userId:decoded.user_id,
      };

      setIsAddDailyCoin(decoded.is_add_daily_coin || false);
      setDailyCoins(decoded.daily_reward_coin || 0);
      setStreak(decoded.num_of_days_login || 0 );
      setUser(userData);
      console.log(decoded.is_add_daily_coin)
      fetchUserCoins(token);
      scheduleAutoLogout(decoded.exp);
    } catch (err) {
      console.error("Failed to decode token:", err);
      logout();
    }
  };
   
  const updateUserImage = (newImageUrl) => {
    setUser((prev) => ({
      ...prev,
      image_url: newImageUrl,
    }));
  };
  const updateUserCoins = (uCoins) =>{
      setCoins(coins+uCoins);
  }
  const minusUserCoins = (uCoins) =>{
      setCoins(coins-uCoins);
  }
  // ================== Dark Mode ==================
  const toggleDarkMode = () => {
    const newMode = !darkMode;
    setDarkMode(newMode);
    localStorage.setItem("darkMode", newMode);
  };

  // ================== Cleanup ==================
  useEffect(() => {
    return () => clearTimeout(logoutTimerRef.current);
  }, []);

  // ================== Context Value ==================
  return (
    <AuthContext.Provider
      value={{
        user,
        updateUserImage,
        updateUserCoins,
        minusUserCoins,
        coins,
        darkMode,
        toggleDarkMode,
        login,
        logout,
        isAddDailyCoin,
        setIsAddDailyCoin,
        streak,
        dailyCoins,
        clearUserInfo,
        isAuthenticated,
        isAdmin,
        isContestUser,
        isNormalUser,
        isProblemEditor,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
