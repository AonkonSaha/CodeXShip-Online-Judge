import React, { useContext } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { AuthContext } from "../auth_component/AuthContext";

const MainBody = () => {
  const { darkMode } = useContext(AuthContext);

  return (
    <main className={darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"}>
      {/* Introduction Section */}
      <section className="text-center mb-12 py-8 md:py-16 px-4 md:px-20">
        <motion.h2
          className="text-4xl md:text-5xl font-bold mb-4"
          initial={{ opacity: 0, y: -50 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
        >
          Welcome to the CodeXShip Online Judge
        </motion.h2>
        <motion.p
          className="text-base md:text-lg mb-6"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1.2 }}
        >
      Set sail on your own ship into the digital seas, guided by your coding mastery — where coders explore, solve, and conquer challenges      </motion.p>
       
      </section>

      {/* Programming Information Section */}
      <section className="text-center mb-16">
        <motion.h3
          className="text-2xl md:text-3xl font-semibold mb-6"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
        >
          Why Learn Competitive Programming?
        </motion.h3>
        <motion.p
          className="max-w-2xl mx-auto text-base md:text-lg"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1.2 }}
        >
          Competitive programming helps you develop problem-solving skills, optimize algorithms, and prepare for coding interviews at top tech companies like Google, Facebook, and Microsoft.
        </motion.p>
      </section>

    
    </main>
  );
};

export default MainBody;
