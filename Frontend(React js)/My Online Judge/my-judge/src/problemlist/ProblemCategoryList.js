import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import NavBar from '../NavBar_Footer/NavBarCus';
import Footer from '../NavBar_Footer/Footer';
import { AuthContext } from "../auth_component/AuthContext";

const ProblemCategoryList = () => {
    const navigate = useNavigate();
    const { darkMode } = useContext(AuthContext);
    const [search, setSearch] = useState("");

    // Categories List
    const categories = [
        { name: 'Graph', description: 'Problems related to graphs, traversal, etc.', path: '/category/graph', icon: '📊' },
        { name: 'Tree', description: 'Problems on trees, binary trees, etc.', path: '/category/tree', icon: '🌳' },
        { name: 'Math', description: 'Math-related problems, combinatorics, number theory.', path: '/category/math', icon: '➗' },
        { name: 'Binary Search', description: 'Efficient searching problems using binary search.', path: '/category/binary-search', icon: '🔍' },
        { name: 'String', description: 'String manipulation problems, pattern matching, etc.', path: '/category/string', icon: '🔤' },
        { name: 'Dynamic Programming', description: 'Problems involving dynamic programming concepts.', path: '/category/dp', icon: '💡' },
        { name: 'Greedy', description: 'Problems using greedy algorithms for optimization.', path: '/category/greedy', icon: '💰' },
        { name: 'Two Pointer', description: 'Problems solving with two-pointer technique.', path: '/category/two-pointer', icon: '👯‍♂️' },
    ];

    // Filter categories based on search input
    const filteredCategories = categories.filter(category =>
        category.name.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <div className={`min-h-screen flex flex-col ${darkMode ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-800'}`}>
            <NavBar />

            <div className="container mx-auto px-6 py-12 flex-grow max-w-7xl">
                {/* Header Section with Flexbox Layout */}
                <div className="flex flex-col sm:flex-row justify-between items-center mb-6">
                    {/* Left-aligned Heading */}
                    <h1 className="text-3xl sm:text-4xl font-bold mb-4 sm:mb-0">
                        Problem Categories
                    </h1>

                    {/* Right-aligned Search Filter Box */}
                    <input
                        type="text"
                        placeholder="Search categories..."
                        className={`w-full sm:max-w-md px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${darkMode ? 'bg-gray-800 border-gray-600 text-white focus:ring-indigo-400' : 'bg-white border-gray-300 focus:ring-indigo-400'}`}
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                </div>

                {/* Page Border Box with Categories */}
                <div className={`p-6 rounded-lg shadow-md border ${darkMode ? 'bg-gray-900 border-gray-700' : 'bg-white border-gray-300'}`}>
                    {/* Grid Layout for Categories */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                        {filteredCategories.map((category, index) => (
                            <div
                                key={index}
                                className={`rounded-xl p-12 flex flex-col items-center border cursor-pointer transition-all duration-300 hover:shadow-lg ${darkMode ? 'bg-gray-900 border-gray-600 hover:bg-gray-600' : 'bg-gray-50 border-gray-200 hover:bg-gray-200'}`}
                                onClick={() => navigate(category.path)}
                            >
                                {/* Icon */}
                                <div className="text-4xl mb-4">
                                    {category.icon}
                                </div>

                                {/* Title */}
                                <h2 className="text-xl font-semibold">{category.name}</h2>

                                {/* Description */}
                                <p className="text-center mt-2">{category.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default ProblemCategoryList;
