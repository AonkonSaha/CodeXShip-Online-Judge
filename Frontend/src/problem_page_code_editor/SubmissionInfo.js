import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import MonacoEditor from "@monaco-editor/react";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";

const SubmissionInfo = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { darkMode } = useContext(AuthContext);

  const [loading, setLoading] = useState(true);
  const [submission, setSubmission] = useState(null);

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const token = localStorage.getItem("token");

  const formatLanguage = (language = "") => {
    return language.includes("cpp-clang-9.0.1-14")
      ? "C++14"
      : language.includes("cpp-clang-10.0.1-17")
        ? "C++17"
        : language.includes("csharp-sdk-3.1.406")
          ? "C# (.NET Core 3.1)"
          : language.includes("csharp-sdk-8.0.302")
            ? "C# (.NET 8)"
            : language.includes("python-pypy-7.3.12-3.10")
              ? "Python 3.10"
              : language.includes("python-pypy-7.3.12-3.9")
                ? "Python 3.9"
                : language.includes("java-jdk-14.0.1")
                  ? "Java 14"
                  : language.includes("c-clang")
                    ? "C"
                    : "Unknown";
  };

  useEffect(() => {
    const fetchSubmission = async () => {
      try {
        const res = await fetch(`${baseURL}/api/v1/submissions/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const json = await res.json();
        setSubmission(json.data);
      } catch (err) {
        console.error("Failed to load submission", err);
      } finally {
        setLoading(false);
      }
    };
    fetchSubmission();
  }, [id, baseURL, token]);

  /* ================= Loading ================= */
  if (loading) {
    return (
      <div
        className={`min-h-screen flex flex-col ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100"}`}
      >
        <NavBar />
        <div className="flex-grow flex items-center justify-center">
          <div className="flex flex-col items-center gap-4">
            <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
            <span className="text-sm text-gray-400">
              Loading submission details…
            </span>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  if (!submission) {
    return <div className="p-10 text-center">Submission not found</div>;
  }

  /* ================= Safe Values ================= */
  const safeDate =
    submission.created_at && !isNaN(new Date(submission.created_at).getTime())
      ? new Date(submission.created_at).toLocaleString()
      : "-";

  const results = Array.isArray(submission.results) ? submission.results : [];

  const verdictBadge =
    submission.verdict === "Accepted"
      ? "bg-green-100 text-green-700 dark:bg-green-500/20 dark:text-green-400"
      : "bg-red-100 text-red-700 dark:bg-red-500/20 dark:text-red-400";

  const Chip = ({ label, value, darkMode }) => (
    <div
      className={`text-xs font-medium px-2 py-0.5 rounded-full shadow-sm flex items-center gap-1 transition-colors duration-300
      ${darkMode ? "bg-gray-800 text-gray-300" : "bg-gray-100 text-gray-700"}
    `}
    >
      <span className="font-semibold">{label}:</span> {value}
    </div>
  );

  return (
    <div
      className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-100"} min-h-screen`}
    >
      <NavBar />

      <main className="max-w-6xl mx-auto p-6 space-y-6">
        {/* ================= Premium Submission Header ================= */}
        <div
          className={`rounded-xl border shadow-sm p-6 transition-colors duration-300
    ${darkMode ? "bg-gray-900 border-gray-700" : "bg-white border-gray-300"}
  `}
        >
          <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
            {/* Left: Submission ID + Problem */}
            <div className="flex flex-col gap-2">
              <h2
                className={`text-2xl font-bold tracking-tight ${
                  darkMode ? "text-white" : "text-gray-900"
                }`}
              >
                Submission #{submission.id}
              </h2>

              <div
                className={`flex items-center gap-2 text-sm ${darkMode ? "text-gray-400" : "text-gray-500"}`}
              >
                Problem:
                <span
                  onClick={() =>
                    navigate(`/problem/page/${submission.problem_id}`)
                  }
                  className="text-blue-500 hover:underline cursor-pointer font-medium"
                >
                  {submission.problemName}
                </span>
              </div>
            </div>

            {/* Right: Verdict + Chips */}
            <div className="flex flex-col md:items-end gap-2">
              {/* Verdict badge */}
              <span
                className={`px-4 py-1.5 rounded-full font-semibold text-sm shadow-sm transition-colors duration-300 ${
                  submission.verdict === "Accepted"
                    ? darkMode
                      ? "bg-green-600 text-white"
                      : "bg-green-400 text-white"
                    : darkMode
                      ? "bg-red-600 text-white"
                      : "bg-red-400 text-white"
                }`}
              >
                {submission.verdict}
              </span>

              {/* Meta chips */}
              <div className="flex flex-wrap gap-2 mt-2 md:mt-0">
                <Chip
                  label="Language"
                  value={formatLanguage(submission.language)}
                  darkMode={darkMode}
                />
                <Chip
                  label="Time"
                  value={`${submission.time ?? "-"} ms`}
                  darkMode={darkMode}
                />
                <Chip
                  label="Memory"
                  value={`${submission.memory ?? "-"} KB`}
                  darkMode={darkMode}
                />
                <Chip label="Submitted" value={safeDate} darkMode={darkMode} />
              </div>
            </div>
          </div>
        </div>

        {/* ================= Premium Testcase Results ================= */}
        <div
          className={`rounded-xl border overflow-hidden shadow-sm ${
            darkMode
              ? "bg-gray-800 border-gray-700"
              : "bg-white border-gray-300"
          }`}
        >
          <div className="px-5 py-3 font-semibold border-b flex items-center justify-between">
            <span>Testcase Results</span>
            <span className="text-xs text-gray-400">
              {results.length} cases
            </span>
          </div>

          {results.length === 0 ? (
            <div className="p-6 text-center text-sm text-gray-400">
              No testcase results available
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead
                className={`text-xs uppercase tracking-wide ${
                  darkMode
                    ? "bg-gray-700 text-gray-300"
                    : "bg-gray-100 text-gray-600"
                }`}
              >
                <tr>
                  <th className="p-3 text-left">#</th>
                  <th className="text-left">Verdict</th>
                  <th className="text-right">Time</th>
                  <th className="text-right pr-4">Memory</th>
                </tr>
              </thead>

              <tbody>
                {results.map((t, i) => {
                  const failed = !t.passed;

                  return (
                    <tr
                      key={i}
                      className={`
                border-t transition
                ${
                  failed
                    ? "bg-red-50 dark:bg-red-500/10 border-l-4 border-l-red-500"
                    : "border-l-4 border-l-green-500"
                }
                ${darkMode ? "hover:bg-gray-700/60" : "hover:bg-gray-50"}
              `}
                    >
                      {/* Index */}
                      <td className="p-3 font-medium">{i + 1}</td>

                      {/* Status */}
                      <td>
                        <span
                          className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold
                    ${
                      failed
                        ? "bg-red-100 text-red-700 dark:bg-red-500/20 dark:text-red-400"
                        : "bg-green-100 text-green-700 dark:bg-green-500/20 dark:text-green-400"
                    }`}
                        >
                          {t.status ?? (failed ? "FAILED" : "PASSED")}
                        </span>
                      </td>

                      {/* Time */}
                      <td className="text-right font-mono text-xs">
                        {t.time ?? "-"} ms
                      </td>

                      {/* Memory */}
                      <td className="text-right pr-4 font-mono text-xs">
                        {t.memory ?? "-"} KB
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>

        {/* ================= Submitted Code ================= */}
        <div
          className={`rounded-lg border overflow-hidden ${darkMode ? "border-gray-700" : "border-gray-300"}`}
        >
          <div
            className={`px-4 py-2 text-sm font-semibold ${darkMode ? "bg-gray-800" : "bg-gray-200"}`}
          >
            Submitted Code
          </div>

          <MonacoEditor
            height="420px"
            language={
              submission.language?.includes("python") ? "python" : "cpp"
            }
            theme={darkMode ? "vs-dark" : "light"}
            value={submission.submission_code}
            options={{
              readOnly: true,
              minimap: { enabled: false },
              fontSize: 14,
            }}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
};

/* ================= Small Meta Component ================= */
const Meta = ({ label, value }) => (
  <div>
    <div className="text-gray-400">{label}</div>
    <div className="font-medium">{value}</div>
  </div>
);

export default SubmissionInfo;
