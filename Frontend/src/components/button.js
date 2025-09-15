import { useContext } from "react";
import { AuthContext } from "../auth_component/AuthContext";

const Button = ({ children, onClick, className = "", variant = "default" }) => {
  const { darkMode } = useContext(AuthContext); // Get dark mode state from context

  const baseStyle = "px-4 py-2 rounded-md transition duration-200";
  const variants = {
    default: darkMode
      ? "bg-blue-500 text-white hover:bg-blue-600"
      : "bg-blue-600 text-white hover:bg-blue-700",
    ghost: darkMode
      ? "bg-transparent text-gray-200 hover:bg-gray-700"
      : "bg-transparent text-gray-800 hover:bg-gray-200",
  };

  return (
    <button
      onClick={onClick}
      className={`${baseStyle} ${variants[variant]} ${className}`}
    >
      {children}
    </button>
  );
};

export default Button;
