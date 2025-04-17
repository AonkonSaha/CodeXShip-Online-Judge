import React, { useContext, useState } from "react";
import { NavLink } from "react-router-dom";
import { Menu, X, Moon, Sun } from "lucide-react"; // Icons
import { AuthContext } from "../auth_component/AuthContext";
import Button from "../components/button";
import { Avatar, AvatarImage, AvatarFallback } from "../components/avatar";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "../components/dropdown-menu";

const NavBar = () => {
  const { user, logout, darkMode, toggleDarkMode } = useContext(AuthContext);
  const [isOpen, setIsOpen] = useState(false);

  const toggleMenu = () => setIsOpen(!isOpen);

  return (
    <header className={`sticky top-0 z-50 border-b transition-all duration-300 ${darkMode ? "bg-gray-900 text-white" : "bg-white text-gray-800"}`}>
      <nav className="container mx-auto flex justify-between items-center py-2 px-4">
        {/* Logo */}
        <NavLink to="/" className="text-lg font-bold">CodeXShip</NavLink>

        {/* Desktop Navigation */}
        <div className="hidden md:flex flex-grow justify-center space-x-4 text-sm">
          <NavLink to="/" className="hover:text-blue-500">Home</NavLink>
          <NavLink to="/problem/category" className="hover:text-blue-500">Problems</NavLink>
          <NavLink to="/contests" className="hover:text-blue-500">Contests</NavLink>
          <NavLink to="/leaderboard" className="hover:text-blue-500">Leaderboard</NavLink>
        </div>

        {/* Right Section (Auth & Theme Toggle) */}
        <div className="hidden md:flex items-center space-x-4">
          {/* Dark Mode Toggle */}
          <Button variant="ghost" onClick={toggleDarkMode} className="p-1">
            {darkMode ? <Sun size={18} className="text-yellow-400" /> : <Moon size={18} />}
          </Button>

          {!user ? (
            <>
              <NavLink to="/login" className="text-sm hover:text-blue-500">Login</NavLink>
              <NavLink to="/register" className="text-sm hover:text-blue-500">Register</NavLink>
            </>
          ) : (
            <DropdownMenu>
              <DropdownMenuTrigger>
                <Avatar className="w-8 h-8">
                  <AvatarImage src={user.profilePic || "https://via.placeholder.com/40"} />
                  <AvatarFallback>{user?.username?.charAt(0) || "U"}</AvatarFallback>
                </Avatar>
              </DropdownMenuTrigger>
              <DropdownMenuContent className={`bg-gray-800 ${darkMode ? 'text-white' : 'text-gray-800'}`}>
                <DropdownMenuItem asChild>
                  <NavLink to="/profile">Profile</NavLink>
                </DropdownMenuItem>
                {user.role === "ADMIN" && (
                  <>
                    <DropdownMenuItem asChild>
                      <NavLink to={`/editproblem/${null}`}>Create Problem</NavLink>
                    </DropdownMenuItem>
                    <DropdownMenuItem asChild>
                      <NavLink to="/deleteproblem">Delete Problem</NavLink>
                    </DropdownMenuItem>
                  </>
                )}
                <DropdownMenuItem onClick={logout}>Logout</DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}
        </div>

        {/* Mobile Menu Button */}
        <button className="md:hidden" onClick={toggleMenu}>
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </nav>

      {/* Mobile Menu */}
      <div className={`absolute top-14 left-0 w-full shadow-md md:hidden transition-all duration-300 ${isOpen ? "block" : "hidden"}`}>
        <div className={`flex flex-col space-y-2 p-3 ${darkMode ? "bg-gray-900 text-white" : "bg-white text-gray-800"}`}>
          <NavLink to="/" className="hover:text-blue-500" onClick={toggleMenu}>Home</NavLink>
          <NavLink to="/problem/category" className="hover:text-blue-500" onClick={toggleMenu}>Problems</NavLink>
          <NavLink to="/contests" className="hover:text-blue-500" onClick={toggleMenu}>Contests</NavLink>
          <NavLink to="/leaderboard" className="hover:text-blue-500" onClick={toggleMenu}>Leaderboard</NavLink>
          <Button variant="ghost" onClick={toggleDarkMode} className="text-sm">
            {darkMode ? <Sun size={18} className="text-yellow-400" /> : <Moon size={18} />} Toggle Theme
          </Button>
          {!user ? (
            <>
              <NavLink to="/login" className="hover:text-blue-500" onClick={toggleMenu}>Login</NavLink>
              <NavLink to="/register" className="hover:text-blue-500" onClick={toggleMenu}>Register</NavLink>
            </>
          ) : (
            <>
              <NavLink to="/profile" className="hover:text-blue-500" onClick={toggleMenu}>Profile</NavLink>
              {user.role === "ADMIN" && (
                <>
                  <NavLink to={`/editproblem/${null}`} className="hover:text-blue-500" onClick={toggleMenu}>Create Problem</NavLink>
                  <NavLink to="/deleteproblem" className="hover:text-blue-500" onClick={toggleMenu}>Delete Problem</NavLink>
                </>
              )}
              <button onClick={() => { logout(); toggleMenu(); }} className="hover:text-blue-500">Logout</button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default NavBar;
