import React, { useContext, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { FaCoins, FaTimes } from "react-icons/fa";
import { AuthContext } from "../auth_component/AuthContext";

const AUTO_CLOSE_TIME = 300000; // 5 minutes in ms

const DailyRewardModal = ({ isOpen, coins, onClose, streak }) => {
  const { setIsAddDailyCoin, darkMode } = useContext(AuthContext);
  const [progress, setProgress] = useState(100);

  useEffect(() => {
    if (!isOpen) return;

    setIsAddDailyCoin(true);
    setProgress(100);

    const startTime = Date.now();

    const interval = setInterval(() => {
      const elapsed = Date.now() - startTime;
      const percentage = Math.max(0, 100 - (elapsed / AUTO_CLOSE_TIME) * 100);
      setProgress(percentage);
    }, 1000);

    const timer = setTimeout(() => handleClose(), AUTO_CLOSE_TIME);

    return () => {
      clearTimeout(timer);
      clearInterval(interval);
      setIsAddDailyCoin(false);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isOpen]);

  const handleClose = () => {
    setIsAddDailyCoin(false);
    onClose();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          className={`fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm transition-colors duration-300 ${
            darkMode ? "bg-black/50" : "bg-gray-800/40"
          }`}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <motion.div
            className={`relative rounded-3xl p-8 w-11/12 max-w-md text-center shadow-2xl border transition-colors duration-300 ${
              darkMode
                ? "bg-gray-900 border-gray-700 text-white"
                : "bg-white border-blue-200 text-gray-900"
            }`}
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.9, opacity: 0 }}
            transition={{ type: "spring", stiffness: 250, damping: 20 }}
          >
            {/* Close Button */}
            <motion.button
              onClick={handleClose}
              className={`absolute top-4 right-4 transition-colors duration-200 ${
                darkMode
                  ? "text-gray-400 hover:text-red-400"
                  : "text-gray-500 hover:text-red-500"
              }`}
              whileHover={{ rotate: 90, scale: 1.2 }}
              whileTap={{ scale: 0.9 }}
              aria-label="Close"
            >
              <FaTimes size={20} />
            </motion.button>

            {/* Header */}
            <h2
              className={`text-3xl font-extrabold mb-4 ${
                darkMode ? "text-blue-400" : "text-blue-600"
              }`}
            >
              🎉 Daily Reward!
            </h2>

            {/* Reward info */}
            <p className={`text-lg mb-3 ${darkMode ? "text-gray-300" : "text-gray-700"}`}>
              You earned{" "}
              <span className="font-bold text-yellow-500 inline-flex items-center gap-1">
                {coins} <FaCoins className="inline text-yellow-500" />
              </span>{" "}
              for logging in today!
            </p>

            {streak >= 1 && (
              <p className="text-md mb-4 text-green-500 font-semibold">
                🔥 {streak}-day login streak! Keep it up!
              </p>
            )}

            {/* Progress Bar */}
            <div
              className={`w-full rounded-full h-2.5 mb-4 overflow-hidden ${
                darkMode ? "bg-gray-700" : "bg-gray-200"
              }`}
            >
              <motion.div
                className="h-2.5 bg-gradient-to-r from-yellow-400 to-orange-500"
                style={{ width: `${progress}%` }}
                transition={{ ease: "linear" }}
              />
            </div>

            {/* Buttons */}
            <div className="flex justify-center gap-4 mt-4">
              <motion.button
                onClick={handleClose}
                className={`px-6 py-2 text-white font-semibold rounded-full shadow-lg transition-colors duration-200 ${
                  darkMode
                    ? "bg-blue-500 hover:bg-blue-400"
                    : "bg-blue-600 hover:bg-blue-700"
                }`}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Claim Reward
              </motion.button>

              <motion.button
                onClick={handleClose}
                className={`px-6 py-2 font-semibold rounded-full shadow-lg transition-colors duration-200 ${
                  darkMode
                    ? "bg-gray-700 text-white hover:bg-gray-600"
                    : "bg-gray-300 text-gray-800 hover:bg-gray-400"
                }`}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Cancel
              </motion.button>
            </div>

            {/* Floating coins animation */}
            <div className="absolute inset-0 pointer-events-none overflow-hidden">
              {[...Array(6)].map((_, i) => (
                <motion.div
                  key={i}
                  className="absolute text-yellow-400 text-2xl"
                  style={{ left: `${Math.random() * 80 + 10}%`, bottom: 0 }}
                  animate={{
                    y: [-10, -150 - Math.random() * 50],
                    scale: [1, 1.4, 1],
                    rotate: [0, 360],
                    opacity: [1, 0.8, 0],
                  }}
                  transition={{
                    repeat: Infinity,
                    repeatType: "loop",
                    duration: 2 + Math.random(),
                    delay: i * 0.25,
                    ease: "easeInOut",
                  }}
                >
                  <FaCoins />
                </motion.div>
              ))}
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default DailyRewardModal;
