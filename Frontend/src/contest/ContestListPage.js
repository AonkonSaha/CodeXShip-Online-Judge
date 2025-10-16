import React, { useState, useEffect, useContext, useCallback, useRef } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../auth_component/AuthContext';
import NavBar from '../NavBar_Footer/NavBarCus';
import Footer from '../NavBar_Footer/Footer';
import { FaTrophy, FaRegClock, FaCheckCircle, FaTimesCircle } from 'react-icons/fa';

const ContestListPage = () => {
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const { user, darkMode, isAuthenticated } = useContext(AuthContext);
  const [contests, setContests] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filter, setFilter] = useState('all');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(true);
  const observerRef = useRef(null);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  // ================= Fetch Contests =================
  const fetchContests = useCallback(async () => {
    if (!hasMore) return;
    setLoading(true);
    try {
      const response = await axios.get(`${baseURL}/api/contest/v1/all`, {
        params: { page, size: 6, search: searchTerm || '', filter },
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });

      const newData = response.data.data?.content || response.data.data || [];
      setContests((prev) => [...prev, ...newData]);
      setHasMore(!(response.data.data?.last));
    } catch (err) {
      console.error('Failed to load contests:', err);
      setHasMore(false);
    } finally {
      setLoading(false);
    }
  }, [baseURL, page, hasMore, searchTerm, filter, token]);

  useEffect(() => { fetchContests(); }, [fetchContests]);

  useEffect(() => {
    setContests([]);
    setPage(0);
    setHasMore(true);
  }, [searchTerm, filter]);

  useEffect(() => {
    if (loading || !hasMore) return;
    const observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting) setPage((p) => p + 1);
    });
    if (observerRef.current) observer.observe(observerRef.current);
    return () => observer.disconnect();
  }, [loading, hasMore]);

  const handleJoin = async (contestId) => {
    if (!isAuthenticated) return navigate('/login');
    try {
      await axios.post(`${baseURL}/api/contest/v1/join/${contestId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert('Successfully joined the contest!');
    } catch {
      alert('Failed to join contest. Try again.');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'UPCOMING': return 'text-yellow-500';
      case 'RUNNING': return 'text-green-500';
      case 'ENDED': return 'text-red-500';
      default: return 'text-gray-500';
    }
  };

  return (
    <div className={`min-h-screen flex flex-col ${darkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-900'}`}>
      <NavBar />

      <div className="container mx-auto px-4 sm:px-8 py-8 flex-grow">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-8 gap-4">
          <h1 className="text-3xl font-bold flex items-center gap-2">
            <FaTrophy className="text-yellow-400" /> Contests
          </h1>

          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              placeholder="Search contests..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className={`px-4 py-2 rounded-lg border focus:ring-2 w-full sm:w-64 ${darkMode ? 'bg-gray-800 border-gray-700 text-white focus:ring-blue-400' : 'bg-white border-gray-300 text-gray-900 focus:ring-blue-500'}`}
            />
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              className={`px-4 py-2 rounded-lg border focus:ring-2 ${darkMode ? 'bg-gray-800 border-gray-700 text-white focus:ring-blue-400' : 'bg-white border-gray-300 text-gray-900 focus:ring-blue-500'}`}
            >
              <option value="all">All</option>
              <option value="upcoming">Upcoming</option>
              <option value="running">Running</option>
              <option value="ended">Ended</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {contests.map((contest) => (
            <div
              key={contest.id}
              className={`p-6 rounded-2xl shadow-md border-l-4 transition-transform hover:scale-[1.02] ${darkMode ? 'bg-gray-800 border-blue-400' : 'bg-white border-blue-600'}`}
            >
              <div className="flex justify-between items-start mb-2">
                <h2 className="text-xl font-semibold line-clamp-1">{contest.title}</h2>
                <span className={`font-medium ${getStatusColor(contest.status)}`}>
                  {contest.status}
                </span>
              </div>

              <p className={`text-sm mb-3 ${darkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                {contest.description?.slice(0, 120) || 'No description provided.'}
              </p>

              <div className="flex justify-between text-sm mb-4">
                <div className="flex items-center gap-2">
                  <FaRegClock />
                  <span>{contest.duration || 'N/A'} mins</span>
                </div>
                <span>{new Date(contest.startTime).toLocaleString()}</span>
              </div>

              <div className="flex justify-between items-center">
                {contest.joined ? (
                  <button
                    disabled
                    className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-lg cursor-not-allowed"
                  >
                    <FaCheckCircle /> Joined
                  </button>
                ) : contest.status === 'RUNNING' ? (
                  <button
                    onClick={() => navigate(`/contest/${contest.id}`)}
                    className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition"
                  >
                    <FaTrophy /> Enter
                  </button>
                ) : contest.status === 'UPCOMING' ? (
                  <button
                    onClick={() => handleJoin(contest.id)}
                    className="flex items-center gap-2 bg-yellow-500 text-gray-900 px-4 py-2 rounded-lg hover:bg-yellow-400 transition"
                  >
                    Join Now
                  </button>
                ) : (
                  <button
                    onClick={() => navigate(`/contest/${contest.id}`)}
                    className="flex items-center gap-2 bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition"
                  >
                    <FaTimesCircle /> View Results
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>

        {loading && <p className="text-center text-gray-500 mt-6">Loading...</p>}
        <div ref={observerRef} className="h-10"></div>

        {!loading && contests.length === 0 && (
          <div className="text-center text-gray-500 py-16 text-lg">
            No contests found.
          </div>
        )}
      </div>

      <Footer />
    </div>
  );
};

export default ContestListPage;