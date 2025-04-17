import React, { useState, useContext } from "react";
import { AuthContext } from "../auth_component/AuthContext"; // Assuming darkMode state is in AuthContext

const DropdownMenu = ({ children }) => {
  const [open, setOpen] = useState(false);
  const { darkMode } = useContext(AuthContext);

  return (
    <div className="relative">
      <button onClick={() => setOpen(!open)} className="focus:outline-none">
        {children[0]}
      </button>
      {open && (
        <div
          className={`absolute right-0 mt-2 w-48 shadow-md rounded-lg transition-all duration-200 ${
            darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"
          }`}
        >
          {children[1]}
        </div>
      )}
    </div>
  );
};

const DropdownMenuTrigger = ({ children }) => children;

const DropdownMenuContent = ({ children }) => (
  <div className="p-2">{children}</div>
);

const DropdownMenuItem = ({ children, onClick }) => {
  const { darkMode } = useContext(AuthContext);

  return (
    <div
      onClick={onClick}
      className={`p-2 cursor-pointer transition-all duration-200 rounded-md ${
        darkMode ? "hover:bg-gray-700" : "hover:bg-gray-100"
      }`}
    >
      {children}
    </div>
  );
};

export { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem };
