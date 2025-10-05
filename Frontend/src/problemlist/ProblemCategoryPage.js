import React, { useEffect, useState, useContext } from 'react';
import axios from 'axios';
import {jwtDecode} from 'jwt-decode'; // Correct import
import Footer from '../NavBar_Footer/Footer';
import { Link, useNavigate, useParams } from "react-router-dom";
import { FaCheckCircle, FaTimesCircle, FaEdit, FaTrash } from 'react-icons/fa';
import { BadgeDollarSign } from 'lucide-react'; // Coin icon
import NavBar from '../NavBar_Footer/NavBarCus';
import { AuthContext } from "../auth_component/AuthContext"; // Import the AuthContext

const ProblemCategoryPage = () => {
    const { category } = useParams();  // Get category from URL params
    const [problems, setProblems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [difficultyFilter, setDifficultyFilter] = useState("");
    const [role, setRole] = useState("");
    const navigate = useNavigate();

    const token = localStorage.getItem("token");
    const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
    const { darkMode } = useContext(AuthContext); // Get dark mode from AuthContext

    useEffect(() => {
        if (token) {
            const decoded = jwtDecode(token);
            setRole(decoded.role);
        }

        const fetchProblems = async () => {
            try {
                const headers = token ? { Authorization: `Bearer ${token}` } : {};
                const response = await axios.get(`${baseURL}/api/problem/v1/category/${category}`, { headers });
                setProblems(response.data.data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchProblems();
    }, [token, category, baseURL]);

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

    const filteredProblems = problems.filter(problem =>
        problem.title.toLowerCase().includes(searchTerm.toLowerCase()) &&
        (difficultyFilter ? problem.difficulty === difficultyFilter : true)
    );

    if (loading) return (
        <div className="flex justify-center items-center h-screen">
            <div className="spinner"></div>
            <p>Loading...</p>
        </div>
    );

    if (error) return <p>Error: {error}</p>;

    return (
        <div className={`min-h-screen ${darkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-800'} flex flex-col`}>
            <NavBar />

            <div className="container mx-auto px-6 py-10 flex-grow">
                <div className="flex justify-between items-center mb-8">
                    {/* Page Heading */}
                    <h1 className={`text-4xl font-bold ${darkMode ? 'text-white' : 'text-gray-800'} text-left`}>Problem List</h1>

                    {/* Search and Filter */}
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
                    {filteredProblems.length === 0 && (
                        <div className="text-center text-gray-500">
                            No problems found.
                        </div>
                    )}
                    {filteredProblems.map((problem) => (
                        <div
                            key={problem.id}
                            className={`block p-4 border-t-4 ${darkMode ? 'bg-gray-700 border-gray-600' : 'bg-white border-gray-800'} shadow-md rounded-xl hover:shadow-lg transition-transform transform hover:scale-[1.02]`}
                        >
                            <Link to={`/problem/page/${problem.id}`}>
                                <div className="flex justify-between items-center">
                                    <h3 className={`text-xl font-semibold ${darkMode ? 'text-white' : 'text-gray-800'} hover:text-indigo-600`}>{problem.title}</h3>
                                </div>
                            </Link>

                            {/* Problem Details */}
                            <div className="flex justify-between items-center text-sm font-medium space-x-4 mt-2">
                                <div className="flex space-x-4">
                                    <p className={problem.difficulty === 'Easy' ? 'text-green-600' : problem.difficulty === 'Medium' ? 'text-yellow-600' : 'text-red-600'}>
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
                                        {problem.solved ? <FaCheckCircle className="text-white text-xl" /> : <FaTimesCircle className="text-white text-xl" />}
                                    </button>
                                </div>
                            </div>

                            {/* Admin Controls */}
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
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default ProblemCategoryPage;
