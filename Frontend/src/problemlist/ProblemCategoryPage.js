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
    const [hasMore, setHasMore] = useState(false);

    const navigate = useNavigate();
    const observerRef = useRef(null);

    const token = localStorage.getItem("token");
    const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
    const { darkMode, user, isAdmin } = useContext(AuthContext);

    useEffect(() => {
        if (token) {
            const decoded = jwtDecode(token);
            setRole(decoded.role);
        }
    }, [token]);

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

            if (!newProblems || newProblems.length === 0) {
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

    useEffect(() => { fetchProblems(); }, [fetchProblems]);

    useEffect(() => {
        setProblems([]);
        setPage(0);
        setHasMore(true);
    }, [category, searchTerm, difficultyFilter]);

    useEffect(() => {
        if (loading || !hasMore) return;

        const observer = new IntersectionObserver(
            (entries) => { if (entries[0].isIntersecting) setPage((prev) => prev + 1); },
            { threshold: 1 }
        );

        if (observerRef.current) observer.observe(observerRef.current);
        return () => { if (observerRef.current) observer.unobserve(observerRef.current); };
    }, [loading, hasMore]);

    const handleDelete = async (handle) => {
        if (!window.confirm("Are you sure you want to delete this problem?")) return;
        try {
            await axios.delete(`${baseURL}/api/problem/v1/remove/${handle}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setProblems(problems.filter(problem => problem.handle !== handle));
        } catch {
            alert("Failed to delete problem. Please try again.");
        }
    };

    return (
        <div className={`min-h-screen ${darkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-800'} flex flex-col`}>
            <NavBar />

            <div className="container mx-auto px-4 sm:px-6 lg:px-10 py-6 flex-grow">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 md:mb-10 space-y-4 md:space-y-0">
                    <h1 className={`text-2xl sm:text-3xl md:text-4xl font-bold ${darkMode ? 'text-white' : 'text-gray-800'}`}>
                        Problem List
                    </h1>

                    <div className="flex flex-col sm:flex-row sm:space-x-4 space-y-2 sm:space-y-0 w-full sm:w-auto">
                        <input
                            type="text"
                            placeholder="Search problems..."
                            className={`p-2 border rounded-lg w-full sm:w-64 focus:ring-2 focus:ring-indigo-500 ${darkMode ? 'bg-gray-800 text-white border-gray-600' : 'bg-white text-gray-800 border-gray-300'}`}
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                        <select
                            className={`p-2 border rounded-lg w-full sm:w-40 focus:ring-2 focus:ring-indigo-500 ${darkMode ? 'bg-gray-800 text-white border-gray-600' : 'bg-white text-gray-800 border-gray-300'}`}
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

                <div className={`p-4 sm:p-6 border-2 ${darkMode ? 'bg-gray-800 border-gray-700' : 'bg-white border-gray-300'} rounded-lg shadow-md space-y-3 flex flex-col`}>
                    {problems.length === 0 && !loading && (
                        <div className="text-center text-gray-500 py-6">No problems found.</div>
                    )}

                    {problems.map((problem) => (
                        <div
                            key={problem.id}
                            className={`block p-4 border-t-4 ${darkMode ? 'bg-gray-700 border-gray-600' : 'bg-white border-gray-800'} shadow-md rounded-xl hover:shadow-lg transition-transform transform hover:scale-[1.02]`}
                        >
                            <Link to={`/problem/page/${problem.id}`}>
                                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center">
                                    <h3 className={`text-lg sm:text-xl font-semibold ${darkMode ? 'text-white' : 'text-gray-800'} hover:text-indigo-600`}>
                                        {problem.title}
                                    </h3>
                                </div>
                            </Link>

                            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center text-sm font-medium mt-2 space-y-2 sm:space-y-0">
                                <div className="flex flex-wrap items-center gap-4">
                                    <p className={
                                        problem.difficulty === 'Easy' ? 'text-green-600' :
                                            problem.difficulty === 'Medium' ? 'text-yellow-600' :
                                                'text-red-600'
                                    }>
                                        {problem.difficulty}
                                    </p>
                                    <p className={darkMode ? 'text-gray-400' : 'text-gray-600'}>{problem.type}</p>
                                </div>

                                <div className="flex items-center space-x-2">
                                    <div className="flex items-center gap-1 bg-yellow-400 text-gray-900 px-2 py-1 rounded-full font-semibold text-sm">
                                        <BadgeDollarSign size={16} />
                                        {problem.coins || 0}
                                    </div>

                                    <button
                                        className={`px-4 py-2 rounded-full flex justify-center items-center transition-colors duration-300 ${problem.solved ? 'bg-green-600 hover:bg-green-500' : 'bg-red-600 hover:bg-red-500'}`}
                                        aria-label={problem.solved ? "Problem Solved" : "Problem Not Solved"}
                                    >
                                        {problem.solved ? <FaCheckCircle className="text-white text-xl" /> : <FaTimesCircle className="text-white text-xl" />}
                                    </button>
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

                    {loading && <p className="text-center text-gray-500 py-4">Loading...</p>}
                    <div ref={observerRef} className="h-10"></div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default ProblemCategoryPage;
