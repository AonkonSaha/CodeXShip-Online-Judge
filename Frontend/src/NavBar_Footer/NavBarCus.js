import React, { useContext, useState, useRef, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { Menu, X, Moon, Sun } from "lucide-react"; 
import { AuthContext } from "../auth_component/AuthContext";
import Button from "../components/button";
import { Avatar, AvatarImage, AvatarFallback } from "../components/avatar";
import {
  DropdownMenuContent,
  DropdownMenuItem,
} from "../components/dropdown-menu";
import { Coins } from "lucide-react"; // Coin icon

const NavBar = () => {
  const { user, logout, darkMode, toggleDarkMode, coins, isAdmin, isContestUser, isNormalUser} = useContext(AuthContext);
  const [isOpen, setIsOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const profileRef = useRef(null);
  const navigate = useNavigate();

  const toggleMenu = () => setIsOpen(!isOpen);
  const toggleProfile = () => setIsProfileOpen(!isProfileOpen);

  const handleProfileClick = () => {
    navigate("/profile");
    setIsProfileOpen(false);
  };

  // Close profile dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setIsProfileOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <header
      className={`sticky top-0 z-50 border-b transition-all duration-300 ${
        darkMode ? "bg-gray-900 text-white" : "bg-white text-gray-800"
      }`}
    >
      <nav className="container mx-auto flex justify-between items-center py-2 px-4">
        {/* Logo */}
        <NavLink to="/" className="text-lg font-bold">CodeXShip</NavLink>

        {/* Desktop Navigation */}
        <div className="hidden md:flex flex-grow justify-center space-x-4 text-sm">
          <NavLink to="/" className="hover:text-blue-500">Home</NavLink>
          <NavLink to="/problem/category" className="hover:text-blue-500">Problems</NavLink>
          {(isNormalUser || isContestUser || isAdmin) && (<NavLink to="/submission" className="hover:text-blue-500">Submissions</NavLink>)}
          <NavLink to="/contests" className="hover:text-blue-500">Contests</NavLink>
          <NavLink to="/leaderboard" className="hover:text-blue-500">Leaderboard</NavLink>
        </div>

        {/* Right Section */}
        <div className="hidden md:flex items-center space-x-4">
          <Button variant="ghost" onClick={toggleDarkMode} className="p-1">
            {darkMode ? <Sun size={18} className="text-yellow-400" /> : <Moon size={18} />}
          </Button>

          {/* Show Coins if user is logged in */}
          {user && (
            <div className="flex items-center gap-1 text-sm font-medium">
              <Coins size={18} className="text-yellow-500" />
              <span>{coins ?? 0}</span>
            </div>
          )}

          {!user ? (
            <>
              <NavLink to="/login" className="text-sm hover:text-blue-500">Login</NavLink>
              <NavLink to="/register" className="text-sm hover:text-blue-500">Register</NavLink>
            </>
          ) : (
            <div ref={profileRef} className="relative">
              <button onClick={toggleProfile}>
                <Avatar className="w-8 h-8">
                  {user?.profilePic ? (
                    <AvatarImage src={user.profilePic} alt={user.username} />
                  ) : (
                    <AvatarFallback>{user?.username?.charAt(0).toUpperCase() || "U"}</AvatarFallback>
                  )}
                </Avatar>
              </button>

              {isProfileOpen && (
                <div
                  className={`absolute right-0 mt-2 w-48 rounded-md shadow-lg z-50 ${
                    darkMode ? "bg-gray-900 text-white" : "bg-white text-gray-800"
                  }`}
                >
                  <DropdownMenuContent>
                    <DropdownMenuItem onClick={handleProfileClick}>
                      Profile
                    </DropdownMenuItem>
                    {isAdmin && (
                      <>
                        <DropdownMenuItem asChild>
                          <NavLink to={`/editproblem/${null}`} onClick={() => setIsProfileOpen(false)}>
                            Create Problem
                          </NavLink>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                          <NavLink to="/deleteproblem" onClick={() => setIsProfileOpen(false)}>
                            Delete Problem
                          </NavLink>
                        </DropdownMenuItem>
                      </>
                    )}
                    <DropdownMenuItem
                      onClick={() => {
                        logout();
                        setIsProfileOpen(false);
                      }}
                    >
                      Logout
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Mobile Menu Button */}
        <button className="md:hidden" onClick={toggleMenu}>
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </nav>

      {/* Mobile Menu */}
      <div
        className={`absolute top-14 left-0 w-full shadow-md md:hidden transition-all duration-300 ${
          isOpen ? "block" : "hidden"
        }`}
      >
        <div
          className={`flex flex-col space-y-2 p-3 ${
            darkMode ? "bg-gray-900 text-white" : "bg-white text-gray-800"
          }`}
        >
          <NavLink to="/" className="hover:text-blue-500" onClick={toggleMenu}>
            Home
          </NavLink>
          <NavLink to="/problem/category" className="hover:text-blue-500" onClick={toggleMenu}>
            Problems
          </NavLink>
          {(isNormalUser || isContestUser || isAdmin) && (<NavLink to="/submission" className="hover:text-blue-500" onClick={toggleMenu}>Submissions</NavLink>)}

          <NavLink to="/contests" className="hover:text-blue-500" onClick={toggleMenu}>
            Contests
          </NavLink>
          <NavLink to="/leaderboard" className="hover:text-blue-500" onClick={toggleMenu}>
            Leaderboard
          </NavLink>

          {/* Coins in Mobile */}
          {user && (
            <div className="flex items-center gap-1 text-sm font-medium">
              <Coins size={18} className="text-yellow-500" />
              <span>{coins ?? 0}</span>
            </div>
          )}

          <Button variant="ghost" onClick={toggleDarkMode} className="text-sm">
            {darkMode ? (
              <Sun size={18} className="text-yellow-400" />
            ) : (
              <Moon size={18} />
            )}{" "}
            Toggle Theme
          </Button>

          {!user ? (
            <>
              <NavLink to="/login" className="hover:text-blue-500" onClick={toggleMenu}>
                Login
              </NavLink>
              <NavLink to="/register" className="hover:text-blue-500" onClick={toggleMenu}>
                Register
              </NavLink>
            </>
          ) : (
            <>
              <NavLink to="/profile" className="hover:text-blue-500" onClick={toggleMenu}>
                Profile
              </NavLink>
              {isAdmin && (
                <>
                  <NavLink to={`/editproblem/${null}`} className="hover:text-blue-500" onClick={toggleMenu}>
                    Create Problem
                  </NavLink>
                  <NavLink to="/deleteproblem" className="hover:text-blue-500" onClick={toggleMenu}>
                    Delete Problem
                  </NavLink>
                </>
              )}
              <button
                onClick={() => {
                  logout();
                  toggleMenu();
                }}
                className="hover:text-blue-500"
              >
                Logout
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default NavBar;
