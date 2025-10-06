import React, { useEffect, useState, useContext, useRef, useCallback } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import Footer from '../NavBar_Footer/Footer';
import { Link, useNavigate, useParams } from "react-router-dom";
import { FaCheckCircle, FaTimesCircle, FaEdit, FaTrash } from 'react-icons/fa';
import { BadgeDollarSign } from 'lucide-react';
import NavBar from '../NavBar_Footer/NavBarCus';
import { AuthContext } from "../auth_component/AuthContext";

const ProblemCategoryPage = () => {
    const { category } = useParams();
    const [problems, setProblems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [difficultyFilter, setDifficultyFilter] = useState("");
    const [role, setRole] = useState("");
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    const navigate = useNavigate();
    const observerRef = useRef(null);

    const token = localStorage.getItem("token");
    const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
    const { darkMode } = useContext(AuthContext);

    useEffect(() => {
        if (token) {
            const decoded = jwtDecode(token);
            setRole(decoded.role);
        }
    }, [token]);

    // Fetch paginated problems from backend with filters
    const fetchProblems = useCallback(async () => {
        if (!hasMore) return;

        setLoading(true);
        try {
            const headers = token ? { Authorization: `Bearer ${token}` } : {};

            const response = await axios.get(
                `${baseURL}/api/problem/v1/category/${category}`, {
                    headers,
                    params: {
                        page,
                        size: 5,
                        search: searchTerm || "",
                        difficulty: difficultyFilter || ""
                    }
                }
            );
 
            const newProblems = response.data.data.content || response.data.data;

            if (!newProblems || newProblems==null || newProblems.length===0) {
                setHasMore(false);
                return;
            }

            setProblems((prev) => [...prev, ...newProblems]);
            setHasMore(!(response.data.data.last));
        } catch (err) {
            setHasMore(false);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [token, category, baseURL, page, hasMore, searchTerm, difficultyFilter]);

    // Fetch when filters or page changes
    useEffect(() => {
        fetchProblems();
    }, [fetchProblems]);

    // Reset problems when category, search, or difficulty changes
    useEffect(() => {
        setProblems([]);
        setPage(0);
        setHasMore(true);
    }, [category, searchTerm, difficultyFilter]);

    // Intersection Observer for infinite scroll
    useEffect(() => {
        if (loading || !hasMore) return;

        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    setPage((prevPage) => prevPage + 1);
                }
            },
            { threshold: 1 }
        );

        if (observerRef.current) observer.observe(observerRef.current);

        return () => {
            if (observerRef.current) observer.unobserve(observerRef.current);
        };
    }, [loading, hasMore]);

    const handleDelete = async (handle) => {
        if (!window.confirm("Are you sure you want to delete this problem?")) return;

        try {
            await axios.delete(`${baseURL}/api/problem/v1/remove/${handle}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            setProblems(problems.filter(problem => problem.handle !== handle));
        } catch (err) {
            alert("Failed to delete problem. Please try again.");
        }
    };

    return (
        <div className={`min-h-screen ${darkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-800'} flex flex-col`}>
            <NavBar />

            <div className="container mx-auto px-6 py-10 flex-grow">
                <div className="flex justify-between items-center mb-8">
                    <h1 className={`text-4xl font-bold ${darkMode ? 'text-white' : 'text-gray-800'} text-left`}>
                        Problem List
                    </h1>

                    <div className="flex items-center space-x-4">
                        <input
                            type="text"
                            placeholder="Search problems..."
                            className={`p-2 border ${darkMode ? 'bg-gray-800 text-white border-gray-600' : 'bg-white text-gray-800 border-gray-300'} rounded-lg w-full focus:ring-2 focus:ring-indigo-500`}
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                        <select
                            className={`p-2 border ${darkMode ? 'bg-gray-800 text-white border-gray-600' : 'bg-white text-gray-800 border-gray-300'} rounded-lg focus:ring-2 focus:ring-indigo-500`}
                            value={difficultyFilter}
                            onChange={(e) => setDifficultyFilter(e.target.value)}
                        >
                            <option value="">All Difficulties</option>
                            <option value="Easy">Easy</option>
                            <option value="Medium">Medium</option>
                            <option value="Hard">Hard</option>
                        </select>
                    </div>
                </div>

                {/* Problem List Box */}
                <div className={`p-6 border-2 ${darkMode ? 'bg-gray-800 border-gray-700' : 'bg-white border-gray-300'} rounded-lg shadow-md space-y-3 h-full flex flex-col`}>
                    {problems.length === 0 && !loading && (
                        <div className="text-center text-gray-500">No problems found.</div>
                    )}
                    {problems.map((problem) => (
                        <div
                            key={problem.id}
                            className={`block p-4 border-t-4 ${darkMode ? 'bg-gray-700 border-gray-600' : 'bg-white border-gray-800'} shadow-md rounded-xl hover:shadow-lg transition-transform transform hover:scale-[1.02]`}
                        >
                            <Link to={`/problem/page/${problem.id}`}>
                                <div className="flex justify-between items-center">
                                    <h3 className={`text-xl font-semibold ${darkMode ? 'text-white' : 'text-gray-800'} hover:text-indigo-600`}>
                                        {problem.title}
                                    </h3>
                                </div>
                            </Link>

                            <div className="flex justify-between items-center text-sm font-medium space-x-4 mt-2">
                                <div className="flex space-x-4">
                                    <p className={
                                        problem.difficulty === 'Easy'
                                            ? 'text-green-600'
                                            : problem.difficulty === 'Medium'
                                                ? 'text-yellow-600'
                                                : 'text-red-600'
                                    }>
                                        {problem.difficulty}
                                    </p>
                                    <p className={darkMode ? 'text-gray-400' : 'text-gray-600'}>{problem.type}</p>
                                </div>

                                {/* Coins + Solve Status */}
                                <div className="flex items-center space-x-2">
                                    <div className="flex items-center gap-1 bg-yellow-400 text-gray-900 px-2 py-1 rounded-full font-semibold text-sm">
                                        <BadgeDollarSign size={16} />
                                        {problem.coins || 0}
                                    </div>

                                    <button
                                        className={`px-4 py-2 rounded-full text-white flex justify-center items-center transition-colors duration-300 ${problem.solved ? 'bg-green-600 hover:bg-green-500' : 'bg-red-600 hover:bg-red-500'}`}
                                        aria-label={problem.solved ? "Problem Solved" : "Problem Not Solved"}
                                    >
                                        {problem.solved ? (
                                            <FaCheckCircle className="text-white text-xl" />
                                        ) : (
                                            <FaTimesCircle className="text-white text-xl" />
                                        )}
                                    </button>
                                </div>
                            </div>

                            {role === "ADMIN" && (
                                <div className="flex flex-col sm:flex-row sm:space-x-3 mt-3">
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

                    {/* Loader & Observer */}
                    {loading && <p className="text-center text-gray-500">Loading...</p>}
                    <div ref={observerRef} className="h-10"></div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default ProblemCategoryPage;
