import { useState, useContext } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import { AuthContext } from "../auth_component/AuthContext";
import Footer from "../NavBar_Footer/Footer";
import toast from "react-hot-toast";

const Register = () => {
  const [user, setUser] = useState({
    username: "",
    email: "",
    mobile: "",
    gender: "",
    password: "",
    confirm_pass: "",
  });

  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const { darkMode } = useContext(AuthContext);
  const navigate = useNavigate();
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

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
    position: "top-right",
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setFieldErrors({});

    if (user.password !== user.confirm_pass) {
      toast.error("Passwords do not match.", toastStyle);
      return;
    }

    setLoading(true);
    const toastId = toast.loading("Registering...", toastStyle);

    try {
      const response = await fetch(`${baseURL}/api/auth/v1/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user),
      });

      const data = await response.json();

      if (response.ok) {
        toast.success("Registration successful! Redirecting...", {
          id: toastId,
          ...toastStyle,
        });
        setTimeout(() => navigate("/login"), 1200);
      } else {
        if (Array.isArray(data)) {
          const errors = {};
          data.forEach((err) => {
            if (err.field) errors[err.field] = err.message;
          });
          setFieldErrors(errors);
        }
        toast.error(data.message || "Registration failed.", {
          id: toastId,
          ...toastStyle,
        });
      }
    } catch {
      toast.error("Network error. Please try again.", { id: toastId, ...toastStyle });
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Spinner Loader (same as login)
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
          Creating your account...
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
    <div
      className={`flex flex-col min-h-screen transition-all duration-500 ${
        darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"
      }`}
    >
      <NavBar />
      {loading && <Loader />}

      <main className="flex-grow flex items-center justify-center px-4 mt-4">
        <div
          className={`w-full max-w-md p-8 rounded-2xl shadow-2xl backdrop-blur-lg transform transition-all duration-300 ${
            darkMode
              ? "bg-gray-800/80 text-white"
              : "bg-white/90 text-gray-900"
          }`}
          style={{
            boxShadow: darkMode
              ? "0 8px 40px rgba(59,130,246,0.2)"
              : "0 8px 30px rgba(0,0,0,0.1)",
          }}
        >
          <h2 className="text-center text-3xl font-extrabold mb-2 tracking-wide">
            Create Your Account
          </h2>
          <p
            className={`text-center mb-6 ${
              darkMode ? "text-gray-400" : "text-gray-600"
            }`}
          >
            Join <span className="text-blue-500 font-semibold">CodeXShip</span> and start your coding journey!
          </p>

          <form onSubmit={handleRegister} className="space-y-6">
            {[
              { label: "Username", key: "username", type: "text" },
              { label: "Email", key: "email", type: "email" },
              { label: "Mobile Number", key: "mobile", type: "text" },
              { label: "Password", key: "password", type: "password" },
              { label: "Confirm Password", key: "confirm_pass", type: "password" },
            ].map(({ label, key, type }) => (
              <div key={key}>
                <label className="block text-sm font-medium mb-1">{label}</label>
                <input
                  type={type}
                  value={user[key]}
                  onChange={(e) => setUser({ ...user, [key]: e.target.value })}
                  required
                  placeholder={`Enter your ${label.toLowerCase()}`}
                  className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 transition-all ${
                    darkMode
                      ? "bg-gray-700 border-gray-600 text-white focus:ring-blue-400"
                      : "bg-white border-gray-300 text-gray-900 focus:ring-blue-500"
                  } ${fieldErrors[key] ? "border-red-500 focus:ring-red-400" : ""}`}
                />
                {fieldErrors[key] && (
                  <p className="text-red-500 text-sm mt-1">{fieldErrors[key]}</p>
                )}
              </div>
            ))}

            {/* Gender Buttons */}
            <div>
              <label className="block text-sm font-medium mb-2">Gender</label>
              <div className="flex justify-between space-x-2">
                {["male", "female", "other"].map((g) => (
                  <button
                    key={g}
                    type="button"
                    onClick={() => setUser({ ...user, gender: g })}
                    className={`flex-1 py-3 rounded-lg font-medium border transition-all duration-300 ${
                      user.gender === g
                        ? darkMode
                          ? "bg-blue-500 border-blue-500 text-white"
                          : "bg-blue-600 border-blue-600 text-white"
                        : darkMode
                        ? "bg-gray-700 border-gray-600 hover:bg-gray-600 text-white"
                        : "bg-white border-gray-300 hover:bg-gray-100 text-gray-900"
                    }`}
                  >
                    {g.charAt(0).toUpperCase() + g.slice(1)}
                  </button>
                ))}
              </div>
              {fieldErrors.gender && (
                <p className="text-red-500 text-sm mt-1">{fieldErrors.gender}</p>
              )}
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className={`w-full py-3 text-white font-semibold rounded-lg transition-all duration-300 shadow-md ${
                loading
                  ? "bg-gray-500 cursor-not-allowed"
                  : "bg-blue-600 hover:bg-blue-700"
              }`}
            >
              {loading ? "Registering..." : "Register"}
            </button>
          </form>

          <div className="text-center mt-6">
            <p className="text-sm">
              Already have an account?{" "}
              <NavLink
                to="/login"
                className="font-medium text-blue-500 hover:text-blue-600"
              >
                Login here
              </NavLink>
            </p>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default Register;
