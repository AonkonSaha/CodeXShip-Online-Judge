import { useState, useContext, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { AuthContext } from "./AuthContext";
import NavBar from "../NavBar_Footer/NavBarCus";
import toast from "react-hot-toast";

const Login = () => {
  const [credentials, setCredentials] = useState({ mobile: "", password: "" });
  const { login, darkMode } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const googleClientId = process.env.REACT_APP_GOOGLE_CLIENT_ID;

  const toastStyle = {
    style: {
      background: darkMode ? "#1f2937" : "#fff",
      color: darkMode ? "#e5e7eb" : "#111827",
      border: darkMode ? "1px solid #3b82f6" : "1px solid #d1d5db",
      boxShadow: darkMode
        ? "0 4px 20px rgba(59,130,246,0.2)"
        : "0 4px 12px rgba(0,0,0,0.1)",
      borderRadius: "10px",
      padding: "14px 16px",
      fontWeight: 500,
    },
    iconTheme: {
      primary: darkMode ? "#60a5fa" : "#2563eb",
      secondary: darkMode ? "#1f2937" : "#fff",
    },
  };

  // 🔹 GOOGLE LOGIN: Initialize & Render Google Button
  useEffect(() => {
    if (window.google && googleClientId) {
        window.google.accounts.id.initialize({
        client_id: googleClientId,
        callback: handleGoogleResponse,
      });

      window.google.accounts.id.renderButton(
        document.getElementById("googleSignInDiv"),
        {
          theme: darkMode ? "filled_black" : "outline",
          size: "large",
          width: "100%",
          text: "signin_with",
          shape: "rectangular",
        }
      );
    }
  }, [darkMode, googleClientId]);

  // 🔹 GOOGLE LOGIN CALLBACK
  const handleGoogleResponse = async (response) => {
    toast.dismiss();
    const toastId = toast.loading("Signing in with Google...", toastStyle);
    try {
      const res = await fetch(`${baseURL}/api/auth/v2/login/google`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ credential: response.credential, }),
      });

      const data = await res.json();

      if (res.ok) {
        login(data.jwt || data.data?.token);
        toast.success("Google login successful! Redirecting...", { id: toastId, ...toastStyle });
        setTimeout(() => navigate("/"), 1000);
      } else {
        toast.error(data.message || "Google login failed.", { id: toastId, ...toastStyle });
      }
    } catch (err) {
      console.error(err);
      toast.error("Google login error. Please try again.", { id: toastId, ...toastStyle });
    }
  };

  // 🔹 PASSWORD LOGIN HANDLER
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    toast.dismiss();
    const toastId = toast.loading("Verifying credentials...", toastStyle);

    try {
      const response = await fetch(`${baseURL}/api/auth/v1/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });
      const data = await response.json();

      if (response.ok) {

        login(data.data.token);
        toast.success("Login successful! Redirecting...", { id: toastId, ...toastStyle });
        setTimeout(() => navigate("/"), 1000);
      } else {
        toast.error(data.message || "Invalid mobile number or password.", { id: toastId, ...toastStyle });
      }
    } catch {
      toast.error("Network error. Please try again.", { id: toastId, ...toastStyle });
    } finally {
      setLoading(false);
    }
  };

  const Loader = () => (
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
          ></div>
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
          ></div>
          <div
            className={`absolute w-10 h-10 rounded-full ${
              darkMode ? "bg-gray-900/80" : "bg-white/70"
            } backdrop-blur-md border border-gray-400/30`}
            style={{
              boxShadow: darkMode
                ? "inset 0 0 10px rgba(96,165,250,0.3)"
                : "inset 0 0 10px rgba(37,99,235,0.2)",
            }}
          ></div>
        </div>

        <p className={`text-lg font-semibold ${darkMode ? "text-gray-200" : "text-gray-800"}`}>
          Logging you in...
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

  return (
    <>
      <NavBar />
      {loading && <Loader />}
      <div
        className={`min-h-screen flex items-center justify-center px-4 ${
          darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"
        }`}
      >
        <div
          className={`max-w-md w-full p-8 rounded-2xl shadow-lg ${
            darkMode ? "bg-gray-800/80 backdrop-blur-lg" : "bg-white/90"
          }`}
          style={{
            boxShadow: darkMode
              ? "0 8px 40px rgba(59,130,246,0.2)"
              : "0 8px 30px rgba(0,0,0,0.1)",
          }}
        >
          <h2 className="text-center text-3xl font-extrabold mb-2">Welcome Back!</h2>
          <p className={`text-center mb-6 ${darkMode ? "text-gray-400" : "text-gray-600"}`}>
            Sign in to continue to your account
          </p>

          <form onSubmit={handleLogin} className="space-y-6">
            {["mobile", "password"].map((field) => (
              <div key={field}>
                <label htmlFor={field} className="block text-sm font-medium mb-1">
                  {field === "mobile" ? "" : ""}
                </label>
                <input
                  type={field === "mobile" ? "text" : "password"}
                  id={field}
                  value={credentials[field]}
                  onChange={(e) => setCredentials({ ...credentials, [field]: e.target.value })}
                  required
                  placeholder={field === "mobile" ? "Enter your email or mobile" : "Enter your password"}
                  className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 ${
                    darkMode
                      ? "bg-gray-700 border-gray-600 text-white focus:ring-blue-400"
                      : "bg-white border-gray-300 text-gray-900 focus:ring-blue-500"
                  }`}
                />
              </div>
            ))}

            <button
              type="submit"
              disabled={loading}
              className={`w-full py-3 text-white font-semibold rounded-lg transition duration-300 ${
                loading ? "bg-gray-500 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"
              }`}
            >
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>

          {/* 🔹 Google Sign-In Button */}
          <div className="my-6 flex items-center justify-center">
            <div className="w-full border-t border-gray-400"></div>
            <span className="px-3 text-sm text-gray-500">OR</span>
            <div className="w-full border-t border-gray-400"></div>
          </div>

          <div id="googleSignInDiv" className="flex justify-center"></div>

          <div className="text-center mt-6">
            <p className="text-sm">
              Don’t have an account?{" "}
              <NavLink to="/register" className="font-medium text-blue-500 hover:text-blue-600">
                Register here      
              </NavLink>
            </p>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
