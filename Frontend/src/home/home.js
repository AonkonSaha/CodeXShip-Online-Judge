import React, { useContext } from "react";
import { motion } from "framer-motion";
import { useInView } from "react-intersection-observer";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { 
  FaTrophy, 
  FaUserAlt, 
  FaComments, 
  FaBookOpen, 
  FaCode, 
  FaRocket, 
  FaCoins, 
  FaBullhorn, 
  FaGavel 
} from "react-icons/fa";

const HomePage = () => {
  const { darkMode } = useContext(AuthContext);

  const features = [
    { title: "Problems", desc: "Sharpen your skills with diverse algorithmic challenges.", icon: <FaCode className="text-4xl text-blue-500 mb-4 " /> },
    { title: "Contests", desc: "Compete globally and climb the leaderboard in real-time.", icon: <FaRocket className="text-4xl text-teal-500 mb-4" /> },
    { title: "Leaderboard", desc: "Track your progress and compare with top coders worldwide.", icon: <FaTrophy className="text-4xl text-yellow-500 mb-4" /> },
    { title: "User Profiles", desc: "Showcase your achievements and grow your coding identity.", icon: <FaUserAlt className="text-4xl text-purple-500 mb-4" /> },
    { title: "Discussion Forum", desc: "Collaborate and discuss strategies with the coding community.", icon: <FaComments className="text-4xl text-pink-500 mb-4" /> },
    { title: "Tutorials", desc: "Learn algorithms and data structures from expert tutorials.", icon: <FaBookOpen className="text-4xl text-green-500 mb-4" /> },
  ];

  const extraFeatures = [
    { title: "Online Judge System", desc: "Submit your code and get instant feedback with multiple language support.", icon: <FaGavel className="text-4xl text-red-500 mb-4" /> },
    { title: "Coin Reward System", desc: "Collect coins by solving problems, winning contests, and contributing to the community — then redeem them for exclusive T-shirts and hoodies.", icon: <FaCoins className="text-3xl text-orange-500 mb-4" /> },
    { title: "Host Contests", desc: "Create and manage coding contests, invite participants, and track results easily.", icon: <FaBullhorn className="text-4xl text-indigo-500 mb-4" /> },
    { title: "Skill Growth & Recognition", desc: "Level up your profile, earn badges, and get recognized for your achievements.", icon: <FaTrophy className="text-4xl text-yellow-500 mb-4" /> },
  ];

  const stats = [
    { title: "Problems Solved", count: 12500, icon: <FaTrophy className="text-4xl text-yellow-500 mb-2" /> },
    { title: "Coins Earned", count: 8600, icon: <FaCoins className="text-4xl text-orange-500 mb-2" />, coinsEffect: true },
    { title: "Contests Hosted", count: 320, icon: <FaRocket className="text-4xl text-teal-500 mb-2" /> },
    { title: "Active Users", count: 5400, icon: <FaUserAlt className="text-4xl text-purple-500 mb-2" /> },
  ];

  // Safe Counter
  const Counter = ({ target }) => {
    const [ref, inView] = useInView({ triggerOnce: true });
    const [value, setValue] = React.useState(0);

    React.useEffect(() => {
      if (inView) {
        let start = 0;
        const duration = 2000;
        const increment = target / (duration / 16);
        const interval = setInterval(() => {
          start += increment;
          if (start >= target) {
            start = target;
            clearInterval(interval);
          }
          setValue(Math.floor(start));
        }, 16);
        return () => clearInterval(interval);
      }
    }, [inView, target]);

    return <span ref={ref} className="text-4xl md:text-5xl font-extrabold">{value}</span>;
  };

  // Floating Coins Component
  const FloatingCoins = () => {
    const coins = Array.from({ length: 6 });
    return (
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full h-full pointer-events-none">
        {coins.map((_, i) => (
          <motion.div
            key={i}
            className="absolute text-orange-400 text-xl"
            style={{ left: `${Math.random() * 80 + 10}%`, bottom: 0 }}
            initial={{ y: 0, scale: 1, rotate: 0, opacity: 1 }}
            animate={{
              y: [-10, -150 - Math.random() * 50],
              scale: [1, 1.5, 1],
              rotate: [0, 360],
              opacity: [1, 0.7, 0]
            }}
            transition={{
              repeat: Infinity,
              repeatType: "loop",
              duration: 2 + Math.random(),
              delay: i * 0.2,
              ease: "easeInOut"
            }}
          >
            <FaCoins />
          </motion.div>
        ))}
      </div>
    );
  };

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"} min-h-screen transition-colors duration-500`}>
      <NavBar />

      {/* Hero Section */}
      <main className="relative z-10">
        <section className="text-center px-6 md:px-20 py-20 md:py-15">
          <motion.h1
            className={`text-5xl md:text-7xl font-extrabold mb-6 ${
              darkMode 
                ? "bg-gradient-to-r from-blue-500  to-teal-400 bg-clip-text text-transparent" 
                : "bg-gradient-to-r  from-gray-900 to-gray-700 bg-clip-text text-transparent"
            }`}
            initial={{ opacity: 0, y: -50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 1 }}
          >
            Welcome to CodeXShip
          </motion.h1>

          <motion.p
            className={`max-w-2xl mx-auto text-lg md:text-xl leading-relaxed mb-10 ${
              darkMode ? "text-gray-400" : "text-gray-700"
            }`}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1.5 }}
          >
          Set sail on your own ship into the digital seas, guided by your coding mastery — where coders explore, solve problems, and conquer challenges.
          </motion.p>
        </section>

        {/* Stats Section */}
        <section className="relative px-6 md:px-20 py-12 md:py-20 grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          {stats.map((stat, index) => (
            <motion.div
              key={index}
              className={`p-6 rounded-xl shadow-md flex flex-col items-center justify-center relative ${
                darkMode ? "bg-gray-800" : "bg-white"
              }`}
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: index * 0.2 }}
              whileHover={{ scale: 1.05 }}
            >
              <motion.div
                animate={{ rotate: [0, 15, -15, 0] }}
                transition={{ repeat: Infinity, duration: 2, ease: "easeInOut" }}
              >
                {stat.icon}
              </motion.div>

              <Counter target={stat.count} />
              <p className="mt-2 text-lg font-medium">{stat.title}</p>

              {stat.coinsEffect && <FloatingCoins />}
            </motion.div>
          ))}
        </section>

        {/* Main Features Section */}
        <section className="px-6 md:px-20 py-12 md:py-20 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
          {features.map((feature, index) => (
            <motion.div
              key={index}
              className={`p-8 rounded-2xl shadow-lg hover:shadow-2xl transform transition-all duration-300 ${
                darkMode ? "bg-gray-800" : "bg-white"
              }`}
              whileHover={{ scale: 1.05, y: -5 }}
              initial={{ opacity: 0, y: 40 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: index * 0.15 }}
            >
              <div className="flex flex-col items-center text-center">
                {feature.icon}
                <h3 className="text-2xl font-bold mb-3">{feature.title}</h3>
                <p className={`mb-6 text-base leading-relaxed ${
                  darkMode ? "text-gray-300" : "text-gray-600"
                }`}>
                  {feature.desc}
                </p>
              </div>
            </motion.div>
          ))}
        </section>

        {/* Extra Features Section */}
        <section className="px-6 md:px-20 py-12 md:py-20 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-10">
          {extraFeatures.map((feature, index) => (
            <motion.div
              key={index}
              className={`p-8 rounded-2xl shadow-lg hover:shadow-2xl transform transition-all duration-300 ${
                darkMode ? "bg-gray-800" : "bg-white"
              }`}
              whileHover={{ scale: 1.05, y: -5 }}
              initial={{ opacity: 0, y: 40 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: index * 0.15 }}
            >
              <div className="flex flex-col items-center text-center">
                {feature.icon}
                <h3 className="text-2xl font-bold mb-3">{feature.title}</h3>
                <p className={`mb-6 text-base leading-relaxed ${
                  darkMode ? "text-gray-300" : "text-gray-600"
                }`}>
                  {feature.desc}
                </p>
              </div>
            </motion.div>
          ))}
        </section>
      </main>

      <Footer />
    </div>
  );
};

export default HomePage;
