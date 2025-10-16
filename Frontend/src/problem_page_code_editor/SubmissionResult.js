import React, { useContext, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { FiClipboard } from "react-icons/fi";
import { AuthContext } from "../auth_component/AuthContext";
import toast, { Toaster } from "react-hot-toast";

const SubmissionResult = () => {
  const { darkMode } = useContext(AuthContext);
  const location = useLocation();
  const navigate = useNavigate();

  const { submission_code, language, problem_id } = location.state || {};
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const [executionResult, setExecutionResult] = useState(null);
  const [loading, setLoading] = useState(true);
  let didSubmit=false;

  useEffect(() => {
    if (!problem_id || !submission_code || !language) return;

    const submitCode = async () => {
      if(didSubmit)return;
      didSubmit=true;
      try {
        const token = localStorage.getItem("token");
        const res = await fetch(`${baseURL}/api/submission/v1/submit`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ problem_id, submission_code, language }),
        });
        const data = await res.json();
        if (data?.data) {
          setExecutionResult(data.data);
          toast.success("Submission completed!");
        } else {
          toast.error("Submission failed: Invalid response");
        }
      } catch (err) {
        console.error(err);
        toast.error("Submission failed. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    submitCode();
  }, [problem_id, submission_code, language, baseURL]);

  if (!problem_id || !submission_code) {
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
        <Toaster position="top-right" />
      </div>
    );
  }

  const submissionId = executionResult?.id || "-";
  const results = executionResult?.results || [];
  const total = executionResult?.total || results.length || 0;
  const passed = executionResult?.passed || results.filter(r => r.passed).length;
  const problemName = executionResult?.problemName || location.state?.problemName || "N/A";
  const createdAt = executionResult?.createdAt || location.state?.createdAt;
  const verdict = executionResult?.verdict || (loading ? "Processing..." : "Completed");
  const time = executionResult?.time || "-";
  const memory = executionResult?.memory || "-";

  // Summary counts
  const summary = results.reduce(
    (acc, result) => {
      if (!result) return acc;
      const s = result.status?.toLowerCase() || "";
      if (s.includes("passed") || s.includes("ok") || result.passed) acc.passed += 1;
      else if (s.includes("wrong") || s.includes("fail")) acc.failed += 1;
      else if (s.includes("error") || s.includes("tle") || s.includes("ce") || s.includes("runtime")) acc.error += 1;
      else acc.unknown += 1;
      return acc;
    },
    { passed: 0, failed: 0, error: 0, unknown: 0 }
  );

  const getStatusColor = (status) => {
    if (!status) return darkMode ? "text-gray-300" : "text-gray-600";
    const s = status.toLowerCase();
    if (s.includes("accepted") || s.includes("passed") || s.includes("ok")) return "text-green-500 font-semibold";
    if (s.includes("wrong") || s.includes("fail")) return "text-red-500 font-semibold";
    if (s.includes("error") || s.includes("tle") || s.includes("ce") || s.includes("runtime")) return "text-orange-400 font-semibold";
    if (s.includes("processing") || s.includes("pending")) return "text-blue-500 font-semibold";
    return darkMode ? "text-gray-300 font-semibold" : "text-gray-600 font-semibold";
  };

  const copyToClipboard = (text) => {
    if (!text) return;
    navigator.clipboard.writeText(text).then(() => toast.success("Copied to clipboard!"));
  };

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"} min-h-screen flex flex-col`}>
      <NavBar />
      <div className="flex-grow p-4 md:p-10 w-full max-w-[95%] mx-auto space-y-8">
        <h1 className="text-2xl md:text-3xl font-bold">Submission Result</h1>
        {/* Overall Result Table */}
        <div className="overflow-x-auto rounded-md border border-gray-300">
          <table className={`w-full table-auto divide-y ${darkMode ? "divide-gray-700" : "divide-gray-300"}`}>
            <thead className={`${darkMode ? "bg-gray-800 text-gray-200" : "bg-gray-100 text-gray-800"}`}>
              <tr className="text-sm md:text-base">
                <th className="px-3 md:px-4 py-2 text-left">#SubmissionID</th>
                <th className="px-3 md:px-4 py-2 text-left">Problem</th>
                <th className="px-3 md:px-4 py-2 text-left">Language</th>
                <th className="px-3 md:px-4 py-2 text-left">Date</th>
                <th className="px-3 md:px-4 py-2 text-left">Status</th>
                <th className="px-3 md:px-4 py-2 text-left">Summary</th>
                <th className="px-3 md:px-4 py-2 text-left">Time (s)</th>
                <th className="px-3 md:px-4 py-2 text-left">Memory (KB)</th>
              </tr>
            </thead>
            <tbody className={`divide-y ${darkMode ? "divide-gray-700" : "divide-gray-200"}`}>
              <tr>
                 <td className="px-3 md:px-4 py-2">{submissionId}</td>
                <td className="px-3 md:px-4 py-2">{problemName}</td>
                <td className="px-3 md:px-4 py-2">{language}</td>
                <td className="px-3 md:px-4 py-2">{createdAt ? new Date(createdAt).toLocaleString() : "-"}</td>
                <td className={`px-3 md:px-4 py-2 ${getStatusColor(verdict)}`}>{loading ? <span className="text-blue-500 animate-pulse">Processing...</span> : verdict}</td>
                <td className="px-3 md:px-4 py-2 flex flex-wrap gap-2">
                  <span className={`px-2 py-1 rounded ${darkMode ? "bg-green-800 text-green-200" : "bg-green-100 text-green-700"}`}>✅ {summary.passed}</span>
                  <span className={`px-2 py-1 rounded ${darkMode ? "bg-red-800 text-red-200" : "bg-red-100 text-red-700"}`}>❌ {summary.failed}</span>
                  <span className={`px-2 py-1 rounded ${darkMode ? "bg-orange-800 text-orange-200" : "bg-orange-100 text-orange-700"}`}>⚠️ {summary.error}</span>
                  {summary.unknown > 0 && (
                    <span className={`px-2 py-1 rounded ${darkMode ? "bg-gray-700 text-gray-200" : "bg-gray-100 text-gray-700"}`}>❓ {summary.unknown}</span>
                  )}
                </td>
                <td className="px-3 md:px-4 py-2">{time}</td>
                <td className="px-3 md:px-4 py-2">{memory}</td>
              </tr>
            </tbody>
          </table>
        </div>

        {/* Per-Testcase Table */}
        <div className="overflow-x-auto rounded-md border border-gray-300">
          <table className={`w-full table-auto min-w-[600px] divide-y ${darkMode ? "divide-gray-700" : "divide-gray-300"}`}>
            <thead className={`${darkMode ? "bg-gray-800 text-gray-200" : "bg-gray-100 text-gray-800"}`}>
              <tr className="text-sm md:text-base">
                <th className="px-3 md:px-3 py-2 text-left">#Testcase</th>
                
                <th className="px-3 md:px-4 py-2 text-left">Status</th>
                <th className="px-3 md:px-4 py-2 text-left">Expected</th>
                <th className="px-3 md:px-4 py-2 text-left">Time (s)</th>
                <th className="px-3 md:px-4 py-2 text-left">Memory (KB)</th>
              </tr>
            </thead>
            <tbody className={`divide-y ${darkMode ? "divide-gray-700" : "divide-gray-200"}`}>
              {results.length > 0 ? results.map((result, index) => (
                <tr key={index} className={result.passed ? (darkMode ? "bg-green-900/20" : "bg-green-50") : (darkMode ? "bg-red-900/20" : "bg-red-50")}>
                  <td className="px-3 md:px-4 py-1">{index + 1}</td>
                  <td className={`px-3 md:px-4 py-1 ${getStatusColor(result.status)}`}>
                    {result.status || (loading ? <span className="text-blue-500 animate-pulse">Processing...</span> : "-")}
                  </td>
                  <td className="px-3 md:px-4 py-1 font-mono relative max-w-xs">
                    <div className="overflow-y-auto">{result.expectedOutput || "-"}</div>
                    {result.expectedOutput && (
                      <FiClipboard
                        className="absolute top-1 right-1 cursor-pointer text-gray-400 hover:text-gray-200"
                        onClick={() => copyToClipboard(result.expectedOutput)}
                        title="Copy Expected"
                      />
                    )}
                  </td>
                  <td className="px-3 md:px-4 py-1">{result.time || (loading ? "…" : "-")}</td>
                  <td className="px-3 md:px-4 py-1">{result.memory || (loading ? "…" : "-")}</td>
                </tr>
              )) : (
                <tr>
                  <td colSpan={5} className="text-center py-4 text-gray-500">
                    {loading ? "Running testcases..." : "No test case results available"}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
      <Footer />
      <Toaster position="top-right" />
    </div>
  );
};

export default SubmissionResult;
