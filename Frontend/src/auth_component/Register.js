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
        toast.success(
          "Registration successful! Redirecting to login...",
          { id: toastId, ...toastStyle }
        );
        setTimeout(() => navigate("/login"), 1200);
      } else {
        // Handle backend validation errors
        if (Array.isArray(data)) {
          const errors = {};
          data.forEach((err) => {
            if (err.field) errors[err.field] = err.message;
          });
          setFieldErrors(errors);
          toast.error("Registration failed.", { id: toastId, ...toastStyle });
        } else {
          toast.error("Registration failed.", { id: toastId, ...toastStyle });
        }
      }
    } catch (error) {
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
        <p className={`text-lg font-semibold ${darkMode ? "text-gray-200" : "text-gray-800"}`}>
          Registering...
        </p>
        <div className="flex space-x-2 mt-2">
          {[0, 1, 2].map((i) => (
            <div
              key={i}
              className={`w-3 h-3 rounded-full ${darkMode ? "bg-blue-400" : "bg-blue-600"} animate-bounce`}
              style={{ animationDelay: `${i * 0.2}s` }}
            />
          ))}
        </div>
      </div>
    </div>
  );

  return (
    <div className={`flex flex-col min-h-screen ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"}`}>
      <NavBar />
      
      {loading && <Loader />}

      <main className="flex-grow flex items-center justify-center px-4 sm:px-6 lg:px-8">
        <div
          className={`w-full max-w-md p-8 rounded-2xl shadow-xl transition-all duration-300 ${
            darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"
          }`}
        >
          <h2 className="text-center text-3xl font-extrabold">Create Your Account</h2>
          <p className="text-center mt-2">Join us and start coding!</p>

          <form onSubmit={handleRegister} className="mt-6 space-y-6">
            {[
              { label: "Username", type: "text", key: "username" },
              { label: "Email", type: "email", key: "email" },
              { label: "Mobile Number", type: "text", key: "mobile" },
              { label: "Password", type: "password", key: "password" },
              { label: "Confirm Password", type: "password", key: "confirm_pass" },
            ].map((field) => (
              <div key={field.key}>
                <label className="block text-sm font-medium">{field.label}</label>
                <input
                  type={field.type}
                  value={user[field.key]}
                  onChange={(e) => setUser({ ...user, [field.key]: e.target.value })}
                  required
                  placeholder={`Enter your ${field.label.toLowerCase()}`}
                  className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 ${
                    darkMode
                      ? "bg-gray-700 border-gray-600 text-white"
                      : "bg-white border-gray-300 text-gray-900"
                  } ${fieldErrors[field.key] ? "border-red-500 focus:ring-red-400" : ""}`}
                />
                {fieldErrors[field.key] && (
                  <p className="text-red-500 text-sm mt-1">{fieldErrors[field.key]}</p>
                )}
              </div>
            ))}

            {/* Gender Selection */}
            <div>
              <label className="block text-sm font-medium mb-2">Gender</label>
              <div className="flex justify-between space-x-2">
                {["male", "female", "other"].map((g) => (
                  <button
                    key={g}
                    type="button"
                    onClick={() => setUser({ ...user, gender: g })}
                    className={`flex-1 py-3 rounded-lg font-medium border transition-all duration-300
                      ${
                        user.gender === g
                          ? darkMode
                            ? "bg-blue-500 border-blue-500 text-white"
                            : "bg-blue-600 border-blue-600 text-white"
                          : darkMode
                          ? "bg-gray-700 border-gray-600 text-white hover:bg-gray-600"
                          : "bg-white border-gray-300 text-gray-900 hover:bg-gray-100"
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

            <button
              type="submit"
              disabled={loading}
              className={`w-full py-3 font-semibold rounded-lg transition duration-300 ${
                loading
                  ? "bg-gray-400 cursor-not-allowed"
                  : darkMode
                  ? "bg-blue-500 hover:bg-blue-600 text-white"
                  : "bg-blue-600 hover:bg-blue-700 text-white"
              }`}
            >
              {loading ? "Registering..." : "Register"}
            </button>
          </form>

          <div className="text-center mt-6">
            <p className="text-sm">
              Already have an account?{" "}
              <NavLink to="/login" className="font-medium text-blue-500 hover:text-blue-600">
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
