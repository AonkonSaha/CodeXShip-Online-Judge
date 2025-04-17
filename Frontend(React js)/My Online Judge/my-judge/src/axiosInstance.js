// import axios from "axios";
// import { jwtDecode } from "jwt-decode";
// import { logout } from "./AuthContext";

// const axiosInstance = axios.create({
//   baseURL: "http://localhost:8090",
// });

// axiosInstance.interceptors.request.use(
//   async (config) => {
//     const token = localStorage.getItem("token");
//     if (token) {
//       try {
//         const decoded = jwtDecode(token);
//         const currentTime = Date.now() / 1000;
//         if (decoded.exp < currentTime) {
//             alert("Token Expired!");
//             const handleLogout = async () => {
//               try {
                
//                 const response = await axios.post(
//                     "http://localhost:8090/api/logout",
//                     {},
//                     {
//                         headers: { Authorization: `Bearer ${token}` },
//                     }
//                   );
                
//               } catch (error) {
//                 console.error("Logout failed:", error);
//               }
//               localStorage.removeItem("token");
//               setUser(null);
//             };
        
//             handleLogout();
//           window.location.href = "/login"; // Redirect to login
//           return Promise.reject("Token expired");
//         }
//       } catch (error) {
//         alert("Token Expired!");
//         const handleLogout = async () => {
//           try {
            
//             const response = await axios.post(
//                 "http://localhost:8090/api/logout",
//                 {},
//                 {
//                     headers: { Authorization: `Bearer ${token}` },
//                 }
//               );
            
//           } catch (error) {
//             console.error("Logout failed:", error);
//           }
//           localStorage.removeItem("token");
//           setUser(null);
//         };
    
//         handleLogout();
//         window.location.href = "/login";
//         return Promise.reject("Invalid token");
//       }
//       config.headers["Authorization"] = `Bearer ${token}`;
//     }
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

// export default axiosInstance;
