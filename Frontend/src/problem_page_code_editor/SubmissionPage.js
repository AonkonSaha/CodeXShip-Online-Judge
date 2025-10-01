import React, { useEffect, useState, useContext } from "react";
import { motion } from "framer-motion";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { FaSortUp, FaSortDown } from "react-icons/fa";

export default function SubmissionsPage() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [sortOrder, setSortOrder] = useState("desc"); 

  const { darkMode } = useContext(AuthContext);
  const token = localStorage.getItem("token");

  const fetchSubmissions = (pageNumber = 0, sort = sortOrder) => {
    setLoading(true);
    fetch(
      `http://localhost:8090/api/submission/v1/get/user/all?page=${pageNumber}&size=${size}&sort_field=createdAt&sortBy=${sort}`,
      { headers: { Authorization: `Bearer ${token}` } }
    )
      .then((res) => res.json())
      .then((data) => {
        const pageData = data.data;
        setSubmissions(pageData?.content || []);
        setTotalPages(pageData?.totalPages || 0);
        setPage(pageData?.number || 0);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  };

  useEffect(() => {
    fetchSubmissions();
    // eslint-disable-next-line
  }, [sortOrder]);

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleString("en-US", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  };

  const toggleSort = () => {
    setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  };

  if (loading) {
    return (
      <div className={`flex items-center justify-center h-screen ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"}`}>
        <motion.div
          className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"
          initial={{ rotate: 0 }}
          animate={{ rotate: 360 }}
          transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
        />
      </div>
    );
  }

  return (
    <>
      <NavBar />
      <div className={`p-6 min-h-screen ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"}`}>
        <h1 className="text-2xl font-bold mb-6">My Submissions</h1>

        {submissions.length === 0 ? (
          <p className="text-gray-500">No submissions yet.</p>
        ) : (
          <div>
            <div className={`overflow-x-auto shadow rounded-2xl mb-6 ${darkMode ? "bg-gray-800" : "bg-white"}`}>
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className={darkMode ? "bg-gray-700 text-gray-200" : "bg-gray-200 text-gray-700"}>
                    {/* Date column with sorting */}
                    <th
                      className="p-3 cursor-pointer flex items-center"
                      onClick={toggleSort}
                      title="Sort by Date"
                    >
                      Date
                      {sortOrder === "asc" ? (
                        <FaSortUp className="ml-1 text-gray-600" />
                      ) : (
                        <FaSortDown className="ml-1 text-gray-600" />
                      )}
                    </th>
                    <th className="p-3">Problem</th>
                    <th className="p-3">Verdict</th>
                    <th className="p-3">Passed</th>
                    <th className="p-3">Time (s)</th>
                    <th className="p-3">Memory (KB)</th>
                  </tr>
                </thead>
                <tbody>
                  {submissions.map((s) => (
                    <tr
                      key={s.id}
                      className={`border-b transition ${darkMode ? "border-gray-700 hover:bg-gray-700" : "border-gray-200 hover:bg-gray-50"}`}
                    >
                      <td className="p-3 font-medium">{formatDate(s.created_at)}</td>
                      <td className="p-3 font-medium">{s.problemName}</td>
                      <td className="p-3">
                        <span
                          className={`px-3 py-1 rounded-full text-sm font-semibold ${
                            s.verdict === "ACCEPTED"
                              ? "bg-green-100 text-green-700 dark:bg-green-800 dark:text-green-200"
                              : s.verdict === "WRONG_ANSWER"
                              ? "bg-red-100 text-red-700 dark:bg-red-800 dark:text-red-200"
                              : "bg-yellow-100 text-yellow-700 dark:bg-yellow-800 dark:text-yellow-200"
                          }`}
                        >
                          {s.verdict}
                        </span>
                      </td>
                      <td className="p-3">{s.passed}/{s.total}</td>
                      <td className="p-3">{s.time?.toFixed(4)}</td>
                      <td className="p-3">{s.memory}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination Controls */}
            <div className="flex justify-center items-center space-x-4">
              <button
                onClick={() => fetchSubmissions(page - 1)}
                disabled={page === 0}
                className="px-4 py-2 rounded-lg bg-blue-500 text-white disabled:bg-gray-400"
              >
                Previous
              </button>

              <span>
                Page {page + 1} of {totalPages}
              </span>

              <button
                onClick={() => fetchSubmissions(page + 1)}
                disabled={page + 1 >= totalPages}
                className="px-4 py-2 rounded-lg bg-blue-500 text-white disabled:bg-gray-400"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
      <Footer />
    </>
  );
}
