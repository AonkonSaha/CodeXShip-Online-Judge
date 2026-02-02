import React, { useEffect, useState, useContext, useRef, useCallback } from "react";
import axios from "axios";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { Toaster, toast } from "react-hot-toast";
import { GiTrophyCup } from "react-icons/gi";
import { useNavigate } from "react-router-dom";


const RankPage = () => {
  const { darkMode,user } = useContext(AuthContext);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const observerRef = useRef(null);
  const searchTimeout = useRef(null);
  const navigate = useNavigate();


  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const fetchRanking = useCallback(async () => {
    if (!hasMore) return;
    setLoading(true);

    try {
      const response = await axios.get(`${baseURL}/api/v1/ranking`, {
        params: { page, size: 10, search: searchTerm || "" },
      });

      const data = response.data.data?.content || response.data.data || [];

      if (!data || data.length === 0) {
        setHasMore(false);
        return;
      }

      setUsers((prev) => [...prev, ...data]);
      setHasMore(!(response.data.data?.last));
    } catch {
      toast.error("Failed to fetch ranking");
      setHasMore(false);
    } finally {
      setLoading(false);
    }
  }, [baseURL, page, hasMore, searchTerm]);

  useEffect(() => {
    fetchRanking();
  }, [fetchRanking]);

  useEffect(() => {
    clearTimeout(searchTimeout.current);
    searchTimeout.current = setTimeout(() => {
      setUsers([]);
      setPage(0);
      setHasMore(true);
    }, 500);
    return () => clearTimeout(searchTimeout.current);
  }, [searchTerm]);

  useEffect(() => {
    if (loading || !hasMore) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) setPage((prev) => prev + 1);
      },
      { threshold: 1 }
    );

    if (observerRef.current) observer.observe(observerRef.current);
    return () => {
      if (observerRef.current) observer.unobserve(observerRef.current);
    };
  }, [loading, hasMore]);

  // Navigate to user profile
  const handleUserClick = (user) => {
     if (user?.username) 
      {
        navigate(`/profile/${user.username}?email=${encodeURIComponent(user.email)}`);
      }

  };
  return (
    <div
      className={`min-h-screen flex flex-col ${
        darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"
      }`}
    >
      <Toaster position="top-right" />
      <NavBar />

      <div className="w-full px-6 sm:px-10 py-10 flex-grow">
        {/* Header Section */}
        <div className="flex flex-col sm:flex-row justify-between items-center mb-6">
          <h1
            className={`text-3xl sm:text-4xl font-bold mb-4 sm:mb-0 ${
              darkMode ? "text-white" : "text-gray-800"
            }`}
          >
            🏆 User Leaderboard
          </h1>

          <div className="w-full sm:w-auto">
            <input
              type="text"
              placeholder="Search user..."
              className={`w-full sm:w-72 p-2 rounded-lg border focus:ring-2 focus:ring-indigo-500 ${
                darkMode
                  ? "bg-gray-800 text-white border-gray-600"
                  : "bg-white text-gray-800 border-gray-300"
              }`}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        {/* Leaderboard Table */}
        <div
          className={`overflow-x-auto shadow-lg rounded-lg border ${
            darkMode ? "border-gray-700 bg-gray-800" : "border-gray-200 bg-white"
          } w-full`}
        >
          <table
            className={`min-w-full border-collapse ${
              darkMode ? "text-white" : "text-gray-800"
            }`}
          >
            <thead
              className={`${
                darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"
              }`}
            >
              <tr>
                <th className="px-4 py-2 text-left w-20">Rank</th>
                <th className="px-4 py-2 text-center w-20">Solved</th>
                <th className="px-4 py-2 text-center w-20">Coins</th>
                <th className="px-4 py-2 text-center w-24">Score</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user, index) => {
                const rank = index + 1;
                const score =
                  (user.total_problems_solved || 0) +
                  (user.total_present_coins || 0);

                const trophyColor =
                  rank === 1
                    ? "text-yellow-400"
                    : rank === 2
                    ? "text-gray-400"
                    : rank === 3
                    ? "text-orange-500"
                    : "text-blue-500";

                return (
                  <tr
                    key={user.mobile || user.email || index}
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
                    <td className="px-4 py-2 flex items-center gap-2 font-semibold">
                      {rank <= 3 && (
                        <GiTrophyCup className={`${trophyColor} text-lg`} />
                      )}
                      <span>{rank}</span>
                    </td>

                      {/* User (clickable) */}
                    <td
                      className="px-4 py-2 flex items-center gap-3 cursor-pointer hover:underline"
                      onClick={() => handleUserClick(user)}
                    >

                      <img
                        src={user.image_url || `https://res.cloudinary.com/dagkiubxf/image/upload/v1760908494/Default_Men_ujdzoj.png`}
                        alt={user.username}
                        className="w-8 h-8 rounded-full object-cover border-2 border-gray-300"
                      />
                      <span className="truncate font-medium">
                        {user.username}
                      </span>
                      {/* Status dot */}
                      <span
                        className={`inline-block w-2 h-2 rounded-full ${
                          user.activity_status ? "bg-green-500" : "bg-gray-400"
                        }`}
                        title={user.activity_status ? "Active" : "Inactive"}
                      ></span>
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

          {loading && (
            <p className="text-center text-gray-500 py-4">Loading...</p>
          )}
          {!loading && users.length === 0 && (
            <p className="text-center text-gray-500 py-6">No users found.</p>
          )}
          <div ref={observerRef} className="h-10"></div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default RankPage;
