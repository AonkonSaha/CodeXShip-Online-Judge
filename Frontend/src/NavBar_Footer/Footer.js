import React, { useContext } from "react";
import { AuthContext } from "../auth_component/AuthContext"; // Adjust the path based on your project structure

const Footer = () => {
  const { darkMode } = useContext(AuthContext); // Assuming darkMode is part of the AuthContext

  return (
    <footer
      className={`text-center py-0 mt-4 sticky  bottom-0 left-0 w-full z-50 ${
        darkMode ? "bg-gray-800   text-gray-300" : "bg-white text-gray-700"
      } shadow-md transition duration-300`}
    >
      <div className="container mx-auto px-4">
        <div className="flex justify-center items-center space-x-5 mb-1">
          {/* Social Icons */}
          <a
            href="https://www.facebook.com"
            className={`${
              darkMode ? "text-gray-300 hover:text-blue-500" : "text-gray-700 hover:text-blue-500"
            } transition duration-300`}
          >
            <i className="fab fa-facebook"></i> {/* Add proper icon */}
          </a>
          <a
            href="https://www.twitter.com"
            className={`${
              darkMode ? "text-gray-300 hover:text-blue-400" : "text-gray-700 hover:text-blue-400"
            } transition duration-300`}
          >
            <i className="fab fa-twitter"></i> {/* Add proper icon */}
          </a>
          <a
            href="https://www.instagram.com"
            className={`${
              darkMode ? "text-gray-300 hover:text-pink-500" : "text-gray-700 hover:text-pink-500"
            } transition duration-300`}
          >
            <i className="fab fa-instagram"></i> {/* Add proper icon */}
          </a>
        </div>
        <p className={`text-xs ${darkMode ? "text-gray-400" : "text-gray-700"}`}>
          &copy; 2024 YourCompany. All rights reserved. |{" "}
          <a
            href="https://www.linkedin.com/in/aonkonsaha/"
            className="text-blue-400  hover:text-blue-600 transition duration-300"
          >
            Developed By Aonkon Saha
          </a>
        </p>
      </div>
    </footer>
  );
};

export default Footer;
