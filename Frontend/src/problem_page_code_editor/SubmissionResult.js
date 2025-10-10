import React, { useContext } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { FiClipboard } from "react-icons/fi";
import { AuthContext } from "../auth_component/AuthContext";

const SubmissionResult = () => {
  const { darkMode } = useContext(AuthContext);
  const location = useLocation();
  const navigate = useNavigate();
  const { submissionStatus, executionResult } = location.state || {};

  if (!submissionStatus || !executionResult) {
    return (
      <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"} min-h-screen flex flex-col`}>
        <NavBar />
        <div className="flex-grow p-6 text-center">
          <h2 className="text-2xl font-semibold mb-4">No submission data found!</h2>
          <button
            onClick={() => navigate(-1)}
            className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Back to Problem
          </button>
        </div>
        <Footer />
      </div>
    );
  }

  const { total, passed, results } = executionResult;

  const summary = results.reduce(
    (acc, result) => {
      const s = result.status?.toLowerCase() || "";
      if (s.includes("passed") || s.includes("ok") || result.passed) acc.passed += 1;
      else if (s.includes("wrong") || s.includes("fail")) acc.failed += 1;
      else if (s.includes("error") || s.includes("tle") || s.includes("ce") || s.includes("runtime")) acc.error += 1;
      else acc.unknown += 1;
      return acc;
    },
    { passed: 0, failed: 0, error: 0, unknown: 0 }
  );

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(() => alert("Copied to clipboard!"));
  };

  const getStatusColor = (status) => {
    if (!status) return darkMode ? "text-gray-300" : "text-gray-600";
    const s = status.toLowerCase();
    if (s.includes("accepted") || s.includes("passed") || s.includes("ok")) return "text-green-500 font-semibold";
    if (s.includes("wrong") || s.includes("fail")) return "text-red-500 font-semibold";
    if (s.includes("error") || s.includes("tle") || s.includes("ce") || s.includes("runtime")) return "text-orange-400 font-semibold";
    if (s.includes("processing") || s.includes("pending")) return "text-blue-500 font-semibold";
    return darkMode ? "text-gray-300 font-semibold" : "text-gray-600 font-semibold";
  };

  const headerStatus = results.every(r => r.passed) ? "Accepted" : submissionStatus;

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"} min-h-screen flex flex-col`}>
      <NavBar />

      <div className="flex-grow p-4 md:p-10 w-full max-w-[95%] mx-auto">
        {/* Header */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4 md:gap-0">
          <h1 className="text-2xl md:text-3xl font-bold">Submission Result</h1>
          <div className="flex flex-col sm:flex-row gap-2 sm:gap-6 text-sm md:text-base">
            <p>Status: <span className={getStatusColor(headerStatus)}>{headerStatus}</span></p>
            <p>Passed {passed} / {total} Testcases</p>
          </div>
        </div>

        {/* Summary Badges */}
        <div className="flex flex-wrap gap-2 md:gap-4 mb-6">
          <div className={`px-3 py-1 rounded font-semibold ${darkMode ? "bg-green-800 text-green-200" : "bg-green-100 text-green-700"}`}>✅ Passed: {summary.passed}</div>
          <div className={`px-3 py-1 rounded font-semibold ${darkMode ? "bg-red-800 text-red-200" : "bg-red-100 text-red-700"}`}>❌ Failed: {summary.failed}</div>
          <div className={`px-3 py-1 rounded font-semibold ${darkMode ? "bg-orange-800 text-orange-200" : "bg-orange-100 text-orange-700"}`}>⚠️ Error: {summary.error}</div>
          {summary.unknown > 0 && (
            <div className={`px-3 py-1 rounded font-semibold ${darkMode ? "bg-gray-700 text-gray-200" : "bg-gray-100 text-gray-700"}`}>❓ Unknown: {summary.unknown}</div>
          )}
        </div>

        {/* Test Cases Table */}
        <div className="overflow-x-auto rounded-md border border-gray-300">
          <table className={`w-full table-auto min-w-[600px] divide-y ${darkMode ? "divide-gray-700" : "divide-gray-300"}`}>
            <thead className={`${darkMode ? "bg-gray-800 text-gray-200" : "bg-gray-100 text-gray-800"}`}>
              <tr className="text-sm md:text-base">
                <th className="px-3 md:px-4 py-2 text-left">#</th>
                <th className="px-3 md:px-4 py-2 text-left">Status</th>
                <th className="px-3 md:px-4 py-2 text-left">Expected</th>
                <th className="px-3 md:px-4 py-2 text-left">Time (s)</th>
                <th className="px-3 md:px-4 py-2 text-left">Memory (KB)</th>
              </tr>
            </thead>
            <tbody className={`divide-y ${darkMode ? "divide-gray-700" : "divide-gray-200"}`}>
              {results.map((result, index) => (
                <tr
                  key={index}
                  className={result.passed ? (darkMode ? "bg-green-900/20" : "bg-green-50") : (darkMode ? "bg-red-900/20" : "bg-red-50")}
                >
                  <td className="px-3 md:px-4 py-1 text-sm md:text-base">{index + 1}</td>
                  <td className={`px-3 md:px-4 py-1 text-sm md:text-base ${getStatusColor(result.status)}`}>{result.status || "-"}</td>
                  <td className="px-3 md:px-4 py-1 font-mono text-sm md:text-base relative max-w-xs">
                    <div className="overflow-y-auto">{result.expectedOutput || "-"}</div>
                    {result.expectedOutput && (
                      <FiClipboard
                        className="absolute top-1 right-1 cursor-pointer text-gray-400 hover:text-gray-200"
                        onClick={() => copyToClipboard(result.expectedOutput)}
                        title="Copy Expected"
                      />
                    )}
                  </td>
                  <td className="px-3 md:px-4 py-1 text-sm md:text-base">{result.time || "-"}</td>
                  <td className="px-3 md:px-4 py-1 text-sm md:text-base">{result.memory || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default SubmissionResult;
