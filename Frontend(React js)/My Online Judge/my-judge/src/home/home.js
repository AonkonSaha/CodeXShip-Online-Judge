import React, { useContext } from "react";
import Footer from "../NavBar_Footer/Footer";
import MainBody from "./mainbody";
import NavBar from "../NavBar_Footer/NavBarCus";
import { AuthContext } from "../auth_component/AuthContext";

const HomePage = () => {
  const { darkMode } = useContext(AuthContext);

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"} min-h-screen`}>
      <NavBar />
      <MainBody />
      <Footer />
    </div>
  );
};

export default HomePage;
