import { useState, useContext } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { AuthContext } from "./AuthContext";
import NavBar from "../NavBar_Footer/NavBarCus";

const Login = () => {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const { login, darkMode } = useContext(AuthContext);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await fetch(`${baseURL}/api/auth/v1/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      const rawData = await response.json();
      if (response.ok) {
        login(rawData.data.token);
        navigate("/");
      } else {
        setError("Invalid username or password.");
      }
    } catch (error) {
      setError("Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <NavBar />
      <div className={`min-h-screen flex items-center justify-center px-4 transition-all duration-300 ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"}`}>
        <div className={`max-w-md w-full p-8 rounded-2xl shadow-lg transition-all duration-300 ${darkMode ? "bg-gray-800" : "bg-white"}`}>
          <h2 className="text-center text-3xl font-extrabold">Welcome Back!</h2>
          <p className="text-center mt-2">Sign in to continue</p>

          {error && <p className="text-center text-red-500 mt-4">{error}</p>}

          <form onSubmit={handleLogin} className="mt-6 space-y-6">
            <div>
              <label htmlFor="mobile" className="block text-sm font-medium">Mobile Number</label>
              <input
                type="text"
                id="mobile"
                value={credentials.mobile}
                onChange={(e) => setCredentials({ ...credentials, mobile: e.target.value })}
                required
                className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 ${darkMode ? "bg-gray-700 border-gray-600 text-white focus:ring-blue-400" : "bg-white border-gray-300 text-gray-900 focus:ring-blue-500"}`}
                placeholder="Enter your mobile number"
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium">Password</label>
              <input
                type="password"
                id="password"
                value={credentials.password}
                onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                required
                className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 ${darkMode ? "bg-gray-700 border-gray-600 text-white focus:ring-blue-400" : "bg-white border-gray-300 text-gray-900 focus:ring-blue-500"}`}
                placeholder="Enter your password"
              />
            </div>

            <div>
              <button
                type="submit"
                className={`w-full py-3 text-white font-semibold rounded-lg transition duration-300 ${loading ? "bg-gray-500" : "bg-blue-600 hover:bg-blue-700"}`}
                disabled={loading}
              >
                {loading ? "Logging in..." : "Login"}
              </button>
            </div>
          </form>

          <div className="text-center mt-6">
            <p className="text-sm">
              Don't have an account? {" "}
              <NavLink to="/register" className="font-medium text-blue-500 hover:text-blue-600">Register here</NavLink>
            </p>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;