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
          Solve programming challenges, participate in contests, and improve your skills.
        </motion.p>
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.8 }}
        >
          <Link
            to="/problem/all"
            className="bg-blue-600 text-white px-6 py-3 rounded-full hover:bg-blue-700 transition duration-300 shadow-lg"
          >
            Start Solving Problems
          </Link>
        </motion.div>
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

      {/* Features Section */}
      <section className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-12 px-4 md:px-20">
        {[ 
          { title: "Problems", desc: "Enhance your coding skills with real-world challenges.", link: "/problem", btnText: "View Problems" },
          { title: "Contests", desc: "Compete with developers worldwide and rank higher.", link: "/contests", btnText: "View Contests" },
          { title: "Leaderboard", desc: "Track your progress and compare with top coders.", link: "/leaderboard", btnText: "View Leaderboard" },
          { title: "User Profiles", desc: "Build your coding profile and showcase achievements.", link: "/profile", btnText: "View Profile" },
          { title: "Discussion Forum", desc: "Engage with the community and discuss coding topics.", link: "/forum", btnText: "Join Forum" },
          { title: "Tutorials", desc: "Learn new algorithms and techniques with our guides.", link: "/tutorials", btnText: "View Tutorials" },
        ].map((feature, index) => (
          <motion.div
            key={index}
            className={`text-center p-8 rounded-lg shadow-lg hover:shadow-2xl transition duration-300 ${darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-800"}`}
            whileHover={{ scale: 1.05 }}
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: index * 0.2 }}
          >
            <h3 className="text-xl md:text-2xl font-semibold mb-4">{feature.title}</h3>
            <p className="mb-6 text-sm md:text-base">{feature.desc}</p>
            <Link
              to={feature.link}
              className="bg-gray-800 text-white px-6 py-3 rounded-full hover:bg-gray-900 transition duration-300"
            >
              {feature.btnText}
            </Link>
          </motion.div>
        ))}
      </section>
    </main>
  );
};

export default MainBody;
