import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { FaCheckCircle, FaTimesCircle } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import NavBar from '../NavBar_Footer/NavBarCus';
import Footer from '../NavBar_Footer/Footer';

const SubmissionHistory = () => {
    const [submissions, setSubmissions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusFilter, setStatusFilter] = useState("");
    const [languageFilter, setLanguageFilter] = useState("");
    const [role,setRole]=useState("");
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
    

    useEffect(() => {
        if (token) {
            const decoded = jwtDecode(token);
            setRole(decoded.role);
        }
        const fetchSubmissions = async () => {
            try {
                
                const headers = token ? { Authorization: `Bearer ${token}` } : {};
                const response = await axios.get(`${baseURL}/code/submission/history/${role}`, { headers });
                setSubmissions(response.data.execution);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        if (role) fetchSubmissions();
    }, [token]);

    const filteredSubmissions = submissions.filter(submission =>
        (statusFilter ? submission.status === statusFilter : true) &&
        (languageFilter ? submission.language === languageFilter : true)
    );

    if (loading) return <p className="text-center mt-10">Loading submissions...</p>;
    if (error) return <p className="text-center text-red-600">Error: {error}</p>;

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            <NavBar />
            <div className="container mx-auto px-6 py-10 flex-grow">
                <h1 className="text-4xl font-bold text-gray-800 mb-6">Submission History</h1>
                <div className="flex space-x-4 mb-6">
                    <select
                        className="p-2 border border-gray-300 rounded-lg"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        <option value="">All Statuses</option>
                        <option value="Accepted">Accepted</option>
                        <option value="Rejected">Rejected</option>
                    </select>
                    <select
                        className="p-2 border border-gray-300 rounded-lg"
                        value={languageFilter}
                        onChange={(e) => setLanguageFilter(e.target.value)}
                    >
                        <option value="">All Languages</option>
                        <option value="Python">Python</option>
                        <option value="Java">Java</option>
                        <option value="C++">C++</option>
                    </select>
                </div>
                <div className="bg-white p-6 border-2 border-gray-300 rounded-lg shadow-md space-y-3">
                    {filteredSubmissions.length === 0 ? (
                        <p className="text-center text-gray-500">No submissions found.</p>
                    ) : (
                        filteredSubmissions.map((submission) => (
                            <div key={submission.id} className="p-4 border-t-4 rounded-xl shadow-md hover:shadow-lg transition-transform transform hover:scale-[1.02]">
                                <div className="flex justify-between items-center">
                                    <h3 className="text-xl font-semibold text-gray-800">{submission.problemTitle}</h3>
                                    <p className="text-gray-600">{submission.language}</p>
                                </div>
                                <div className="flex justify-between items-center mt-2">
                                    {/* <p className="text-gray-600">Execution Time: {submission.executionTime} ms</p> */}
                                    <p className="text-gray-600">Execution Time: 1ms</p>
                                    <span className={`px-3 py-1 text-white rounded-full ${submission.status === 'Accepted' ? 'bg-green-600' : 'bg-red-600'}`}>
                                        {submission.status}
                                    </span>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
            <Footer />
        </div>
    );
};

export default SubmissionHistory;
