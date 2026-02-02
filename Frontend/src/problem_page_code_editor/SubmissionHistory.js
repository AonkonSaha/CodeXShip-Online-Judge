import React, { useEffect, useState, useContext } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { FaSortUp, FaSortDown, FaSearch } from "react-icons/fa";

export default function SubmissionHistory() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [sortOrder, setSortOrder] = useState("desc");
  const [searchQuery, setSearchQuery] = useState("");

  const { darkMode } = useContext(AuthContext);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const fetchSubmissions = (pageNumber = 0, sort = sortOrder, search = searchQuery) => {
    setLoading(true);
    fetch(
      `${baseURL}/api/v1/submissions?page=${pageNumber}&size=${size}&sort_field=createdAt&sortBy=${sort}&search=${search}`,
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

  const pointLanguage = (language) => {
    return language.includes("cpp-clang-9.0.1-14")
      ? "C++14"
      : language.includes("cpp-clang-10.0.1-17")
      ? "C++17"
      : language.includes("csharp-sdk-3.1.406")
      ? "C# (.NET Core SDK 3.1.406)"
      : language.includes("csharp-sdk-8.0.302")
      ? "C# (.NET Core SDK 8.0.302)"
      : language.includes("python-pypy-7.3.12-3.10")
      ? "Python 3.10"
      : language.includes("python-pypy-7.3.12-3.9")
      ? "Python 3.9"
      : language.includes("java-jdk-14.0.1")
      ? "Java (OpenJDK 14.0.1)"
      : "C";
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

  const toggleSort = () => setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  const handleSearch = (e) => {
    e.preventDefault();
    fetchSubmissions(0, sortOrder, searchQuery);
  };

  if (loading) {
    return (
      <div
        className={`flex items-center justify-center h-screen ${
          darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"
        }`}
      >
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
    <div className={`flex flex-col min-h-screen ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"}`}>
      <NavBar />

      <main className="flex-grow p-4 sm:p-6">
        {/* Title + Search */}
        <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 sm:mb-6 gap-4">
          <h1 className="text-2xl sm:text-3xl font-bold">My Submissions</h1>
          <form
            onSubmit={handleSearch}
            className="flex w-full sm:w-80 bg-white dark:bg-gray-800 rounded-full shadow-md overflow-hidden border focus-within:ring-2 focus-within:ring-blue-400 transition"
          >
            <input
              type="text"
              placeholder="Search submissions..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="px-4 py-2 w-full text-gray-700 dark:text-gray-200 bg-transparent focus:outline-none"
            />
            <button
              type="submit"
              className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white flex items-center justify-center transition"
            >
              <FaSearch className="text-lg" />
            </button>
          </form>
        </div>

        {/* Submissions Table */}
        {submissions.length === 0 ? (
          <p className="text-gray-500 text-center mt-6">No submissions found.</p>
        ) : (
          <div
            className={`overflow-x-auto mb-6 rounded-2xl shadow-lg transition-shadow ${
              darkMode ? "bg-gray-800 hover:shadow-gray-700" : "bg-white hover:shadow-gray-300"
            }`}
          >
            <table className="w-full min-w-[600px] text-left border-collapse">
              <thead>
                <tr className={darkMode ? "bg-gray-700 text-gray-200" : "bg-gray-100 text-gray-700"}>
                  <th
                    className="p-3 cursor-pointer flex items-center"
                    onClick={toggleSort}
                    title="Sort by Date"
                  >
                    Date{" "}
                    {sortOrder === "asc" ? <FaSortUp className="ml-1 text-gray-400" /> : <FaSortDown className="ml-1 text-gray-400" />}
                  </th>
                  <th className="p-3">Submission ID</th>
                  <th className="p-3">Problem</th>
                  <th className="p-3">Language</th>
                  <th className="p-3">Verdict</th>
                  <th className="p-3">Passed</th>
                  <th className="p-3">Time (s)</th>
                  <th className="p-3">Memory (KB)</th>
                </tr>
              </thead>
              <tbody>
                {submissions.map((s, i) => {
                  const isEven = i % 2 === 0;
                  return (
                    <tr
                      key={s.id}
                      className={`transition-all border-b ${
                        darkMode
                          ? isEven
                            ? "bg-gray-800 hover:bg-gray-700/60"
                            : "bg-gray-900 hover:bg-gray-700/60"
                          : isEven
                          ? "bg-white hover:bg-gray-50"
                          : "bg-gray-50 hover:bg-gray-100"
                      }`}
                    >
                      {/* Date */}
                      <td className="p-3 font-medium">{formatDate(s.created_at)}</td>

                      {/* Submission ID clickable */}
                      <td className="p-3 font-medium">
                        <span
                          onClick={() => navigate(`/submission/${s.id}`)}
                          className="text-blue-500 hover:underline cursor-pointer"
                        >
                          #{s.id}
                        </span>
                      </td>

                      {/* Problem Name clickable */}
                      <td className="p-3 font-medium">
                        <span
                          onClick={() => navigate(`/problem/page/${s.problem_id}`)}
                          className="text-blue-500 hover:underline cursor-pointer"
                        >
                          {s.problemName}
                        </span>
                      </td>

                      {/* Language */}
                      <td className="p-3 font-medium">{pointLanguage(s.language ?? "cpp-clang-9.0.1-14")}</td>

                      {/* Verdict */}
                      <td className="p-3">
                        <span
                          className={`px-3 py-1 rounded-full text-sm font-semibold transition-colors ${
                            s?.verdict === "Accepted"
                              ? "bg-green-100 text-green-700 dark:bg-green-800 dark:text-green-200"
                              : s?.verdict === "Wrong Answer"
                              ? "bg-red-100 text-red-700 dark:bg-red-800 dark:text-red-200"
                              : "bg-yellow-100 text-yellow-700 dark:bg-yellow-800 dark:text-yellow-200"
                          }`}
                        >
                          {s?.verdict}
                        </span>
                      </td>

                      {/* Passed */}
                      <td className="p-3">{s?.passed}/{s?.total}</td>

                      {/* Time */}
                      <td className="p-3">{s.time?.toFixed(4)}</td>

                      {/* Memory */}
                      <td className="p-3">{s?.memory}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        <div className="flex flex-col sm:flex-row justify-center items-center gap-3 sm:gap-4">
          <button
            onClick={() => fetchSubmissions(page - 1)}
            disabled={page === 0}
            className="px-4 py-2 rounded-full bg-blue-500 text-white disabled:bg-gray-400 transition"
          >
            Previous
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => fetchSubmissions(page + 1)}
            disabled={page + 1 >= totalPages}
            className="px-4 py-2 rounded-full bg-blue-500 text-white disabled:bg-gray-400 transition"
          >
            Next
          </button>
        </div>
      </main>

      <Footer />
    </div>
  );
}
