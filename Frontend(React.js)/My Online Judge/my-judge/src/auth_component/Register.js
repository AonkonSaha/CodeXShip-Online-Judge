import { useState, useContext } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import { AuthContext } from "../auth_component/AuthContext";
import Footer from "../NavBar_Footer/Footer";

const Register = () => {
  const [user, setUser] = useState({ username: "", email: "", password: "" });
  const [confirmPass, setConfirmPass] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { darkMode } = useContext(AuthContext);
  const navigate = useNavigate();
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (user.password !== confirmPass) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${baseURL}/api/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user),
      });

      if (response.ok) {
        alert("Registration successful! Please log in.");
        navigate("/login");
      } else {
        setError("Registration failed.");
      }
    } catch (error) {
      setError("Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <NavBar />
      <div className={`min-h-screen flex items-center justify-center px-4 transition-all duration-300 ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"}`}>
        <div className={`max-w-md w-full mt-3  p-8 rounded-2xl shadow-xl transition-all duration-300 ${darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"}`}>
          <h2 className="text-center text-3xl font-extrabold">Create Your Account</h2>
          <p className="text-center mt-2">Join us and start coding!</p>

          {error && <p className="text-center text-red-600 mt-4">{error}</p>}

          <form onSubmit={handleRegister} className="mt-6 space-y-6">
            <div>
              <label className="block text-sm font-medium">Username</label>
              <input
                type="text"
                value={user.username}
                onChange={(e) => setUser({ ...user, username: e.target.value })}
                required
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 ${darkMode ? "bg-gray-700 border-gray-600 text-white" : "bg-white border-gray-300 text-gray-900"}`}
                placeholder="Enter your username"
              />
            </div>
            <div>
              <label className="block text-sm font-medium">Email</label>
              <input
                type="email"
                value={user.email}
                onChange={(e) => setUser({ ...user, email: e.target.value })}
                required
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 ${darkMode ? "bg-gray-700 border-gray-600 text-white" : "bg-white border-gray-300 text-gray-900"}`}
                placeholder="Enter your email"
              />
            </div>
            <div>
              <label className="block text-sm font-medium">Password</label>
              <input
                type="password"
                value={user.password}
                onChange={(e) => setUser({ ...user, password: e.target.value })}
                required
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 ${darkMode ? "bg-gray-700 border-gray-600 text-white" : "bg-white border-gray-300 text-gray-900"}`}
                placeholder="Create a password"
              />
            </div>
            <div>
              <label className="block text-sm font-medium">Confirm Password</label>
              <input
                type="password"
                value={confirmPass}
                onChange={(e) => setConfirmPass(e.target.value)}
                required
                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 ${darkMode ? "bg-gray-700 border-gray-600 text-white" : "bg-white border-gray-300 text-gray-900"}`}
                placeholder="Confirm your password"
              />
            </div>
            <div>
              <button
                type="submit"
                className={`w-full py-3 font-semibold rounded-lg transition duration-300 ${loading ? "bg-gray-400" : darkMode ? "bg-blue-500 hover:bg-blue-600 text-white" : "bg-blue-600 hover:bg-blue-700 text-white"}`}
                disabled={loading}
              >
                {loading ? "Registering..." : "Register"}
              </button>
            </div>
          </form>

          <div className="text-center mt-6">
            <p className="text-sm">
              Already have an account?{' '}
              <NavLink to="/login" className="font-medium text-blue-500 hover:text-blue-600">
                Login here
              </NavLink>
            </p>
          </div>
        </div>
      </div>

    </>
  );
};

export default Register;
