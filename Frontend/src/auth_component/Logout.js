import { useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "./AuthContext";

const Logout = () => {
  const navigate = useNavigate();
  const { logout } = useContext(AuthContext);
  const token=localStorage.getItem("token");
  const baseURL=process.env.REACT_APP_BACK_END_BASE_URL;
  useEffect(() => {
     
    const handleLogout = async () => {
      try {
        alert("Logout Button");
        const response = await axios.post(
            `${baseURL}/api/auth/v1/logout`,
            {}, // Empty body
            {
                headers: { Authorization: `Bearer ${token}` },
            }
          );
        
      } catch (error) {
        console.error("Logout failed:", error);
      }

      // Perform frontend logout (clear token from localStorage)
      logout();
      navigate("/");
    };

    handleLogout();
  }, [logout, navigate, token]);

  return null; // No UI needed for logout
};

export default Logout;
