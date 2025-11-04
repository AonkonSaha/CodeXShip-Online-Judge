import React, {
  useEffect,
  useState,
  useContext,
  useRef,
  useCallback,
} from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { Link, useNavigate, useParams } from "react-router-dom";
import { FaEdit, FaTrash, FaMedal } from "react-icons/fa";
import { CheckCircle, XCircle } from "lucide-react";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";

const ProblemCategoryPage = () => {
  const { category } = useParams();
  const navigate = useNavigate();
  const observerRef = useRef(null);
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const { darkMode, isAdmin } = useContext(AuthContext);

  // States
  const [problems, setProblems] = useState([]);
  const [solvedProblems, setSolvedProblems] = useState(new Set());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [difficultyFilter, setDifficultyFilter] = useState("");
  const [solvedFilter, setSolvedFilter] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  // Refs
  const isFetchingRef = useRef(false);
  const observerInstance = useRef(null);

  // Decode token once
  useEffect(() => {
    if (token) {
      try {
        jwtDecode(token);
      } catch {
        console.warn("Invalid token");
      }
    }
  }, [token]);

  /** ✅ Fetch solved problems once per user **/
  const fetchSolvedProblems = useCallback(async () => {
    if (!token) return;
    try {
      const res = await axios.get(`${baseURL}/api/auth/v1/profile`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const solvedIds = new Set(res.data.data.userSolvedProblems || []);
      setSolvedProblems(solvedIds);
    } catch (e) {
      console.warn("Could not fetch solved problems:", e.message);
    }
  }, [token, baseURL]);

  /** ✅ Fetch problems with smart pagination **/
  const fetchProblems = useCallback(
    async (nextPage = page) => {
      if (!hasMore || isFetchingRef.current) return;

      isFetchingRef.current = true;
      setLoading(true);

      try {
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        const res = await axios.get(
          `${baseURL}/api/problem/v1/category/${category}`,
          {
            headers,
            params: {
              page: nextPage,
              size: 6, // load 10 per request for smoother pagination
              search: searchTerm.trim(),
              difficulty: difficultyFilter,
              solved_filter: solvedFilter
            },
          }
        );

        const data = res.data.data;
        const newProblems = data?.content || [];

        if (newProblems.length === 0) {
          setHasMore(false);
          return;
        }

        const filteredProblems = newProblems.filter((p) => {
          if (solvedFilter === "solved") return solvedProblems.has(p.id);
          if (solvedFilter === "unsolved") return !solvedProblems.has(p.id);
          return true;
        });

        setProblems((prev) =>
          nextPage === 0 ? filteredProblems : [...prev, ...filteredProblems]
        );

        setHasMore(!data?.last);
      } catch (err) {
        console.error("Error fetching problems:", err.message);
        setError("Failed to load problems. Please try again later.");
        setHasMore(false);
      } finally {
        setLoading(false);
        isFetchingRef.current = false;
      }
    },
    [
      token,
      baseURL,
      category,
      page,
      searchTerm,
      difficultyFilter,
      solvedFilter,
      solvedProblems,
      hasMore,
    ]
  );

  /** Reset when filters change **/
  useEffect(() => {
    setProblems([]);
    setPage(0);
    setHasMore(true);
    fetchSolvedProblems();
  }, [category, searchTerm, difficultyFilter, solvedFilter, fetchSolvedProblems]);

  /** Intersection Observer for infinite scroll **/
  useEffect(() => {
    if (observerInstance.current) observerInstance.current.disconnect();

    observerInstance.current = new IntersectionObserver(
      (entries) => {
        const first = entries[0];
        if (first.isIntersecting && hasMore && !isFetchingRef.current) {
          setPage((prev) => prev + 1);
        }
      },
      { threshold: 1 }
    );

    const current = observerRef.current;
    if (current) observerInstance.current.observe(current);

    return () => observerInstance.current?.disconnect();
  }, [hasMore]);

  /** Trigger data load when page changes **/
  useEffect(() => {
    fetchProblems(page);
  }, [page, fetchProblems]);

  /** Delete Problem (Admin Only) **/
  const handleDelete = async (handle) => {
    if (!window.confirm("Are you sure you want to delete this problem?")) return;
    try {
      await axios.delete(`${baseURL}/api/problem/v1/remove/${handle}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProblems((prev) => prev.filter((p) => p.handle !== handle));
    } catch {
      alert("Failed to delete problem. Please try again.");
    }
  };

  const isSolved = (id) => solvedProblems.has(id);

  return (
    <div
      className={`min-h-screen flex flex-col transition-colors duration-300 ${
        darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-800"
      }`}
    >
      <NavBar />
      <div className="container mx-auto px-4 sm:px-6 lg:px-10 py-6 flex-grow">
        {/* Header and Filters */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 md:mb-10 space-y-4 md:space-y-0">
          <h1
            className={`text-3xl sm:text-4xl font-extrabold tracking-tight ${
              darkMode ? "text-white" : "text-gray-800"
            }`}
          >
            {category?.toUpperCase()} Problems
          </h1>

          <div className="flex flex-col sm:flex-row sm:space-x-4 space-y-2 sm:space-y-0 w-full sm:w-auto">
            <input
              type="text"
              placeholder="Search problems..."
              className={`p-2 border rounded-lg w-full sm:w-64 focus:ring-2 focus:ring-indigo-500 ${
                darkMode
                  ? "bg-gray-800 text-white border-gray-600"
                  : "bg-white text-gray-800 border-gray-300"
              }`}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <select
              className={`p-2 border rounded-lg w-full sm:w-40 focus:ring-2 focus:ring-indigo-500 ${
                darkMode
                  ? "bg-gray-800 text-white border-gray-600"
                  : "bg-white text-gray-800 border-gray-300"
              }`}
              value={difficultyFilter}
              onChange={(e) => setDifficultyFilter(e.target.value)}
            >
              <option value="">All Difficulties</option>
              <option value="Easy">Easy</option>
              <option value="Medium">Medium</option>
              <option value="Hard">Hard</option>
            </select>
            <select
              className={`p-2 border rounded-lg w-full sm:w-40 focus:ring-2 focus:ring-indigo-500 ${
                darkMode
                  ? "bg-gray-800 text-white border-gray-600"
                  : "bg-white text-gray-800 border-gray-300"
              }`}
              value={solvedFilter}
              onChange={(e) => setSolvedFilter(e.target.value)}
            >
              <option value="">All Problems</option>
              <option value="solved">Solved</option>
              <option value="unsolved">Unsolved</option>
            </select>
          </div>
        </div>

        {/* Problems List */}
        <div
          className={`p-4 sm:p-6 border-2 rounded-lg shadow-md space-y-3 ${
            darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"
          }`}
        >
          {error && (
            <div className="text-center text-red-500 py-4 font-medium">{error}</div>
          )}

          {!loading && problems.length === 0 && !error && (
            <div className="text-center text-gray-500 py-6">No problems found.</div>
          )}

          {problems.map((problem) => (
            <div
              key={problem.id}
              className={`p-4 border-l-4 shadow-md rounded-xl hover:shadow-lg transition-transform transform hover:scale-[1.02] ${
                darkMode ? "bg-gray-700 border-gray-600" : "bg-white border-gray-800"
              }`}
            >
              <Link to={`/problem/page/${problem.id}`}>
                <h3
                  className={`text-lg sm:text-xl font-semibold hover:text-indigo-500 ${
                    darkMode ? "text-white" : "text-gray-800"
                  }`}
                >
                  {problem.title}
                </h3>
              </Link>

              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center text-sm font-medium mt-2 space-y-2 sm:space-y-0">
                <div className="flex flex-wrap items-center gap-4">
                  <p
                    className={`${
                      problem.difficulty === "Easy"
                        ? "text-green-500"
                        : problem.difficulty === "Medium"
                        ? "text-yellow-500"
                        : "text-red-500"
                    }`}
                  >
                    {problem.difficulty}
                  </p>
                  <p className={darkMode ? "text-gray-400" : "text-gray-600"}>
                    {problem.type}
                  </p>
                </div>

                <div className="flex items-center space-x-3">
                  {/* New Coin Icon */}
                  <div className="flex items-center gap-1 bg-gradient-to-r from-yellow-400 to-yellow-300 text-gray-900 px-2 py-1 rounded-full font-semibold text-sm shadow-sm">
                    <FaMedal size={16} className="text-amber-700" />
                    {problem.coins || 0}
                  </div>

                  {/* New Solved Status */}
                  {isSolved(problem.id) ? (
                    <div className="flex items-center gap-1 bg-green-100 text-green-700 border border-green-300 px-3 py-1 rounded-full text-sm font-medium">
                      <CheckCircle size={16} />
                      Solved
                    </div>
                  ) : (
                    <div className="flex items-center gap-1 bg-red-100 text-red-700 border border-red-300 px-3 py-1 rounded-full text-sm font-medium">
                      <XCircle size={16} />
                      Unsolved
                    </div>
                  )}
                </div>
              </div>

              {isAdmin && (
                <div className="flex flex-col sm:flex-row sm:space-x-3 mt-3 space-y-2 sm:space-y-0">
                  <button
                    className="flex items-center justify-center px-2 py-1 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition transform hover:scale-105"
                    onClick={() => navigate(`/editproblem/${problem.id}`)}
                  >
                    <FaEdit className="text-white text-lg" />
                  </button>
                  <button
                    className="flex items-center justify-center px-2 py-1 bg-red-600 text-white rounded-lg hover:bg-red-500 transition transform hover:scale-105"
                    onClick={() => handleDelete(problem.handle)}
                  >
                    <FaTrash className="text-white text-lg" />
                  </button>
                </div>
              )}
            </div>
          ))}

          {loading && (
            <p className="text-center text-gray-500 py-4 animate-pulse">
              Loading more problems...
            </p>
          )}
          <div ref={observerRef} className="h-10"></div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ProblemCategoryPage;
