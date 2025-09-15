import React, { useContext } from "react";
import { AuthContext } from "../auth_component/AuthContext"; // Assuming darkMode state is in AuthContext

const Avatar = ({ children }) => {
  const { darkMode } = useContext(AuthContext);

  return (
    <div className="relative inline-block w-10 h-10">
      {children}
    </div>
  );
};

const AvatarImage = ({ src, alt = "User Avatar" }) => {
  const { darkMode } = useContext(AuthContext);

  return (
    <img
      src={src || "https://via.placeholder.com/40"}
      alt={alt}
      className={`rounded-full w-10 h-10 border-2 ${
        darkMode ? "border-gray-400" : "border-gray-500"
      }`}
    />
  );
};

const AvatarFallback = ({ children }) => {
  const { darkMode } = useContext(AuthContext);

  return (
    <div
      className={`rounded-full w-10 h-10 flex items-center justify-center font-semibold ${
        darkMode ? "bg-gray-700 text-gray-200" : "bg-gray-300 text-gray-800"
      }`}
    >
      {children}
    </div>
  );
};

export { Avatar, AvatarImage, AvatarFallback };
