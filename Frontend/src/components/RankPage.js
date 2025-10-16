import React, { useEffect, useState, useContext } from "react";
import axios from "axios";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { Toaster, toast } from "react-hot-toast";
import { GiTrophyCup } from "react-icons/gi";

const RankPage = () => {
  const { darkMode } = useContext(AuthContext);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const fetchRanking = async () => {
    try {
      const response = await axios.get(`${baseURL}/api/ranking/v1/get`);
      const data = response.data.data || [];
      setUsers(
        data.map(u => ({
          ...u,
          total_problems_solved: u.total_problems_solved || 0,
          total_present_coins: u.total_present_coins || 0
        }))
      );
    } catch (error) {
      console.error(error);
      toast.error("Failed to fetch ranking");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRanking();
  }, []);

  if (loading) {
    return (
      <div
        className={`flex items-center justify-center h-screen ${
          darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"
        }`}
      >
        <p className="text-lg font-medium">Loading Rankings...</p>
      </div>
    );
  }

  return (
    <div
      className={`min-h-screen flex flex-col ${
        darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"
      }`}
    >
      <Toaster position="top-right" />
      <NavBar />

      <div className="container mx-auto px-4 py-10 flex-grow max-w-4xl">
        <h1 className="text-3xl sm:text-4xl font-bold mb-6 text-center">
          🏆 User Leaderboard
        </h1>

        <div className="overflow-x-auto shadow-lg rounded-lg">
          <table
            className={`min-w-full border-collapse border ${
              darkMode ? "border-gray-700" : "border-gray-200"
            }`}
          >
            <thead
              className={`${
                darkMode ? "bg-gray-800 text-white" : "bg-gray-100 text-gray-800"
              }`}
            >
              <tr>
                <th className="px-4 py-2 text-left w-16">Rank</th>
                <th className="px-4 py-2 text-center w-20">Problems</th>
                <th className="px-4 py-2 text-center w-20">Coins</th>
                <th className="px-4 py-2 text-center w-24">Score</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user, index) => {
                const score =
                  (user.total_problems_solved || 0) +
                  (user.total_present_coins || 0);

                const trophyColor =
                  index === 0
                    ? "text-yellow-400"
                    : index === 1
                    ? "text-gray-400"
                    : index === 2
                    ? "text-orange-500"
                    : "text-blue-500";

                return (
                  <tr
                    key={user.mobile_number || user.email || index}
                    className={`border-b transition-colors ${
                      index % 2 === 0
                        ? darkMode
                          ? "bg-gray-800"
                          : "bg-white"
                        : darkMode
                        ? "bg-gray-700"
                        : "bg-gray-50"
                    } hover:bg-indigo-100 hover:text-black`}
                  >
                    {/* Rank */}
                    <td className="px-4 py-2 text-left font-semibold flex items-center gap-1">
                      {index < 3 && (
                        <GiTrophyCup className={`${trophyColor} text-lg`} />
                      )}
                      <span>{index + 1}</span>
                    </td>

                    {/* User */}
                    <td className="px-4 py-2 flex items-center gap-3">
                      <img
                        src={user.image_url || "/default-avatar.png"}
                        alt={user.username}
                        className="w-8 h-8 rounded-full object-cover border-2 border-gray-300"
                      />
                      <span className="truncate">{user.username}</span>
                    </td>

                    {/* Problems */}
                    <td className="px-4 py-2 text-center font-medium">
                      {user.total_problems_solved || 0}
                    </td>

                    {/* Coins */}
                    <td className="px-4 py-2 text-center font-medium">
                      {user.total_present_coins || 0}
                    </td>

                    {/* Score */}
                    <td className="px-4 py-2 text-center font-semibold">
                      {score}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default RankPage;
