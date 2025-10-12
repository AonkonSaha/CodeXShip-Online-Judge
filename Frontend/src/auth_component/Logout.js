import { useEffect, useContext, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "./AuthContext";
import toast from "react-hot-toast";

const Logout = () => {
  const navigate = useNavigate();
  const { logout, darkMode } = useContext(AuthContext);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const didLogout=useRef(false);
  
  const toastStyle = {
    style: {
      background: darkMode ? "#1f2937" : "#fff",
      color: darkMode ? "#e5e7eb" : "#111827",
      border: darkMode ? "1px solid #3b82f6" : "1px solid #d1d5db",
      borderRadius: "10px",
      padding: "14px 16px",
      fontWeight: 500,
      boxShadow: darkMode
        ? "0 4px 20px rgba(59,130,246,0.2)"
        : "0 4px 12px rgba(0,0,0,0.1)",
    },
    iconTheme: {
      primary: darkMode ? "#60a5fa" : "#2563eb",
      secondary: darkMode ? "#1f2937" : "#fff",
    },
    position: "top-right", // ✅ Toast appears in the top-right corner
  };

  useEffect(() => {
    if(didLogout.current===true)return;
    didLogout.current=true;
    const handleLogout = async () => {
      if (!token) {
        logout();
        navigate("/login");
        return;
      }

      const toastId = ("Logging out...", toastStyle);
      setLoading(true);

      try {
        const { status } = await axios.post(
          `${baseURL}/api/auth/v1/logout`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        );

        if (status === 204) {
          toast.success("Logout successful!", { id: toastId, ...toastStyle });
        } else {
          toast.error("Logout failed. Clearing session locally.", { id: toastId, ...toastStyle });
        }
      } catch (error) {
        console.error("Logout failed:", error);
        toast.error("Network error. Clearing session locally.", { id: toastId, ...toastStyle });
      } finally {
        logout();
        setLoading(false);
        navigate("/login");
      }
    };

    handleLogout();
  }, [logout, navigate, token, baseURL, darkMode]);

  if (!loading) return null;

  // Spinner while logging out
  return (
    <div
      className={`fixed inset-0 flex items-center justify-center z-50 ${
        darkMode ? "bg-gray-900/90" : "bg-white/80"
      } backdrop-blur-md`}
    >
      <div className="flex flex-col items-center space-y-6">
        <div className="relative flex items-center justify-center">
          <div
            className="absolute inset-0 blur-xl opacity-70 rounded-full"
            style={{
              background: darkMode
                ? "conic-gradient(from 180deg, #2563eb, #60a5fa, #93c5fd, #2563eb)"
                : "conic-gradient(from 180deg, #3b82f6, #60a5fa, #93c5fd, #2563eb)",
              filter: "drop-shadow(0 0 20px rgba(59,130,246,0.4))",
            }}
          />
          <div
            className="relative w-20 h-20 rounded-full border-4 border-t-transparent animate-spin"
            style={{
              borderImage: darkMode
                ? "linear-gradient(45deg, #60a5fa, #2563eb) 1"
                : "linear-gradient(45deg, #3b82f6, #60a5fa) 1",
              boxShadow: darkMode
                ? "0 0 25px rgba(96,165,250,0.4)"
                : "0 0 25px rgba(37,99,235,0.4)",
            }}
          />
          <div
            className={`absolute w-10 h-10 rounded-full ${
              darkMode ? "bg-gray-900/80" : "bg-white/70"
            } backdrop-blur-md border border-gray-400/30`}
            style={{
              boxShadow: darkMode
                ? "inset 0 0 10px rgba(96,165,250,0.3)"
                : "inset 0 0 10px rgba(37,99,235,0.2)",
            }}
          />
        </div>

        <p className={`text-lg font-semibold ${darkMode ? "text-gray-200" : "text-gray-800"}`}>
          Logging out...
        </p>

        <div className="flex space-x-1 mt-1">
          {[0, 1, 2].map((i) => (
            <div
              key={i}
              className={`w-2.5 h-2.5 rounded-full ${darkMode ? "bg-blue-400" : "bg-blue-600"} animate-bounce`}
              style={{ animationDelay: `${i * 0.15}s` }}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default Logout;
