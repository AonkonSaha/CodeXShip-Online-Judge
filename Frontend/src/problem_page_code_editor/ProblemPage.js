import React, { useState, useEffect, useContext, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FiClipboard } from "react-icons/fi";
import MonacoEditor from "@monaco-editor/react";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { Coins } from "lucide-react";
import { toast } from "react-hot-toast";
import he from "he";
import { CheckCircle2, Clock, Cpu } from "lucide-react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const defaultTemplates = {
  c: `#include <stdio.h>\n\nint main() {\n    // Your code here\n    return 0;\n}`,
  cpp: `#include <bits/stdc++.h>\nusing namespace std;\n\nint main() {\n    // Your code here\n    return 0;\n}`,
  java: `import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        // Your code here\n    }\n}`,
  csharp: `using System;\nclass Program {\n    static void Main() {\n        // Your code here\n    }\n}`,
  python: `# Your code here\nif __name__ == "__main__":\n    pass`,
};

const getMonacoLanguage = (lang) => {
  if (lang.startsWith("cpp")) return "cpp";
  if (lang.startsWith("csharp")) return "csharp";
  if (lang.startsWith("python")) return "python";
  if (lang.startsWith("java")) return "java";
  if (lang.startsWith("c-")) return "c";
  return "plaintext";
};

const ProblemDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { darkMode, plusUserCoins } = useContext(AuthContext);
  const containerRef = useRef(null);

  const [code, setCode] = useState(defaultTemplates["java"]);
  const [language, setLanguage] = useState("java-jdk-14.0.1");
  const [fileContent, setFileContent] = useState({});
  const [loading, setLoading] = useState(false);
  const [processingDots, setProcessingDots] = useState("");
  const [leftWidth, setLeftWidth] = useState(50);
  const [sampleResult, setSampleResult] = useState(null);
  const [previousSubmissions, setPreviousSubmissions] = useState([]);
  const [activeTab, setActiveTab] = useState("Statement");

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const token = localStorage.getItem("token");
  const didFetch = useRef(false);

  const [judging, setJudging] = useState(false);
  const [submissionResults, setSubmissionResults] = useState([]);
  const [finalVerdict, setFinalVerdict] = useState(null);
  const stompRef = useRef(null);
  const [submissionId, setSubmissionId] = useState(null);
  const [showSubmissionTable, setShowSubmissionTable] = useState(false);
  const [psPage, setPsPage] = useState(0);
  const [psSize] = useState(10);
  const [psTotalPages, setPsTotalPages] = useState(0);


  const [currentResults, setCurrentResults] = useState([]);
  const [verdict, setVerdict] = useState(null);

  const [lastResult, setLastResult] = useState(null);

  const liveTime = lastResult?.time;
  const liveMemory = lastResult?.memory;
  const liveSubmissionAt = lastResult?.created_at;




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

  // Suppress ResizeObserver warnings
  useEffect(() => {
    const suppressed = [
      "ResizeObserver loop limit exceeded",
      "ResizeObserver loop completed with undelivered notifications",
    ];
    const origError = console.error;
    console.error = (...args) => {
      if (
        args[0] &&
        typeof args[0] === "string" &&
        suppressed.some((m) => args[0].includes(m))
      )
        return;
      origError(...args);
    };
    return () => {
      console.error = origError;
    };
  }, []);

  // Fetch Problem Details and Previous Submissions
  useEffect(() => {
    if (didFetch.current) return;
    didFetch.current = true;

    const fetchProblem = async () => {
      try {
        const res = await fetch(`${baseURL}/api/v1/problems/${id}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token || ""}`,
          },
        });
        const data = await res.json();
        setFileContent(data.data);
        if (data.data.sampleCode?.[language]) {
          setCode(data.data.sampleCode[language]);
        }
      } catch (err) {
        console.error("Problem fetch failed:", err);
      }
    };
    fetchProblem();
  }, [id, language, baseURL, token]);

  useEffect(() => {
    if (activeTab !== "Submission" || !token) return;

    const fetchPreviousSubmissions = async () => {
      try {
        const res = await fetch(
          `${baseURL}/api/v1/submissions/problems/${id}?page=${psPage}&size=${psSize}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        const json = await res.json();
        const pageData = json.data;

        setPreviousSubmissions(pageData?.content || []);
        setPsTotalPages(pageData?.totalPages || 0);
      } catch (e) {
        console.error("Failed to load previous submissions", e);
      }
    };

    fetchPreviousSubmissions();
  }, [activeTab, id, token, psPage]);


  // Update code when language changes
  useEffect(() => {
    const langKey = language.includes("cpp") ? "cpp" :
      language.includes("csharp") ? "csharp" :
        language.includes("python") ? "python" :
          language.includes("java") ? "java" : "c";
    setCode(fileContent.sampleCode?.[language] || defaultTemplates[langKey]);
  }, [language, fileContent]);

  // Loading dots animation
  useEffect(() => {
    if (!loading) return;
    let count = 0;
    const interval = setInterval(() => {
      count = (count + 1) % 4;
      setProcessingDots(".".repeat(count));
    }, 500);
    return () => clearInterval(interval);
  }, [loading]);

  // Resizer logic
  const handleMouseDown = (e) => {
    e.preventDefault();
    const container = containerRef.current;
    if (!container) return;

    const handleMouseMove = (e) => {
      const containerWidth = container.offsetWidth;
      let newLeftWidth = (e.clientX / containerWidth) * 100;
      newLeftWidth = Math.max(20, Math.min(newLeftWidth, 80));
      setLeftWidth(newLeftWidth);
    };

    const handleMouseUp = () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
  };

  /* =========================
       2. WEBSOCKET LISTENER
       ========================= */
  useEffect(() => {
    if (!submissionId) return;

    const socket = new SockJS(`${baseURL}/ws`);

    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("WebSocket connected");

        client.subscribe(
          `/topic/submission/${submissionId}`,
          (message) => {

            console.log("STOMP connected!");
            const data = JSON.parse(message.body);
            if (!data.completed) {
              setSubmissionResults((prev) => [...prev, data]);
              setFinalVerdict("Running");
            }

            if (data.completed) {
              console.log("Finished Juding.............!");
              setFinalVerdict(data);
              setJudging(false);
              plusUserCoins?.(data.coins || 0);
              // toast.success("Judging completed");
              setPsPage(0);

              client.deactivate();
            }
          },

        );
      },

      onStompError: (err) => {
        console.error("STOMP error:", err);
      },
      onWebSocketError: (err) => {
        console.error("WebSocket error:", err);
      },

    });

    client.activate();
    stompRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [submissionId, baseURL, plusUserCoins]);

  const handleSubmit = async () => {
    if (!token) {
      toast.error("Please login");
      return;
    }

    setActiveTab("Submission");
    setShowSubmissionTable(true);
    setJudging(true);
    setSubmissionResults([]);
    setFinalVerdict("Queue");


    try {
      const res = await fetch(`${baseURL}/api/v1/submissions`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          problem_id: id,
          submission_code: code,
          language,
        }),
      });

      const json = await res.json();

      if (!json?.data) {
        throw new Error("Invalid submission response");
      }
      setSubmissionId(json.data);
      setFinalVerdict("Running");
      // toast.success("Submission started");

    } catch (err) {
      console.error(err);
      toast.error("Submission failed");
      setJudging(false);
    }


  };


  const handleRunSample = async () => {
    if (!token) {
      toast.error("Please login");
      return;
    }

    setActiveTab("Submission");
    setJudging(true);
    setShowSubmissionTable(true);
    setSubmissionResults([]);
    setFinalVerdict(null);

    try {
      const res = await fetch(`${baseURL}/api/v1/submissions/sample`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          problem_id: id,
          submission_code: code,
          language,
        }),
      });

      const json = await res.json();
      setSubmissionResults(json.data.results || []);
      setFinalVerdict(json.data.verdict || "Done");
    } catch (e) {
      toast.error("Sample test failed");
    } finally {
      setJudging(false);
      setPsPage(0);

    }
  };

  const VerdictBadge = ({ verdict }) => {
    const styles = {
      Accepted: "bg-green-100 text-green-700 dark:bg-green-500/20 dark:text-green-400",
      Wrong: "bg-red-100 text-red-700 dark:bg-red-500/20 dark:text-red-400",
      "Time Limit": "bg-yellow-100 text-yellow-700 dark:bg-yellow-500/20 dark:text-yellow-400",
      Running: "bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-400",
    };

    return (
      <span
        className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap
        ${styles[verdict] || "bg-gray-200 text-gray-700 dark:bg-gray-600 dark:text-gray-200"}
      `}
      >
        {verdict}
      </span>
    );
  };



  const formattedInput = fileContent.sampleTestcase?.join("\n") || "";
  const formattedOutput = fileContent.sampleOutput?.join("\n") || "";
  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    toast.success("Copied to clipboard!");
  };



  return (
    <div className={`flex flex-col min-h-screen ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"}`}>
      <NavBar />

      <main ref={containerRef} className="flex flex-col md:flex-row flex-grow overflow-hidden relative min-h-[600px]">
        {/* Left Panel */}
        <div style={{ width: `${leftWidth}%`, transition: "width 0.1s ease" }} className={`p-5 overflow-y-auto border-b md:border-r flex-shrink-0 ${darkMode ? "bg-gray-900 border-gray-800" : "bg-white border-gray-300"}`}>

          {/* Problem Header */}
          {/* Problem Header */}
          <div className="flex flex-col gap-2 mb-4">
            {/* Top row: Title + Solved (left) and Difficulty + Coins (right) */}
            <div className="flex justify-between items-center flex-wrap">
              {/* Left: Title + Solved */}
              <div className="flex items-center gap-3 flex-wrap">
                <h1 className={`text-3xl font-semibold tracking-tight ${darkMode ? "text-gray-100" : "text-gray-900"}`}>
                  {fileContent.title || ""}
                </h1>

                {/* Solved Status */}
                {fileContent.is_solved && (
                  <span
                    className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-sm font-semibold shadow-sm border ${darkMode
                      ? "bg-emerald-500/20 border-emerald-500 text-emerald-300"
                      : "bg-emerald-100 border-emerald-400 text-emerald-700"
                      }`}
                  >
                    <CheckCircle2
                      size={16}
                      className={darkMode ? "text-emerald-300" : "text-emerald-600"}
                    />
                    Solved
                  </span>
                )}
              </div>

              {/* Right: Difficulty + Coins */}
              <div className="flex items-center gap-2 flex-wrap mt-2 md:mt-0">
                {/* Difficulty Badge */}
                <span
                  className={`px-4 py-1.5 rounded-full text-sm font-medium tracking-wide ${fileContent.difficulty === "Easy"
                    ? "bg-green-600 text-white"
                    : fileContent.difficulty === "Hard"
                      ? "bg-red-600 text-white"
                      : "bg-blue-600 text-white"
                    }`}
                >
                  {fileContent.difficulty || "Medium"}
                </span>

                {/* Coins */}
                <span className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-sm font-semibold shadow-sm ${darkMode ? "bg-yellow-500 text-gray-900" : "bg-amber-400 text-gray-900"
                  }`}>
                  <Coins size={16} /> {fileContent.coins || 0}
                </span>
              </div>
            </div>

            {/* Second row: Time & Memory Limits */}
            <div className="flex gap-2 mt-1 flex-wrap">
              {/* Time Limit */}
              <span className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-sm font-semibold shadow-sm border ${darkMode ? "bg-gray-800 border-gray-700 text-gray-200" : "bg-gray-50 border-gray-300 text-gray-800"
                }`}>
                <Clock size={14} />
                {fileContent.time_limit ? `${fileContent.time_limit} sec` : "—"}
              </span>

              {/* Memory Limit */}
              <span className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-sm font-semibold shadow-sm border ${darkMode ? "bg-gray-800 border-gray-700 text-gray-200" : "bg-gray-50 border-gray-300 text-gray-800"
                }`}>
                <Cpu size={14} />
                {fileContent.memory_limit ? `${fileContent.memory_limit} MB` : "—"}
              </span>
            </div>
          </div>


          {/* Tabs */}
          <div className="flex gap-3 mb-4 border-b pb-2">
            {["Statement", "Submission", "Editorial"].map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`px-4 py-2 rounded-t-lg font-semibold ${activeTab === tab
                  ? darkMode
                    ? "bg-gray-800 text-white border-t border-l border-r border-gray-700"
                    : "bg-white text-gray-900 border-t border-l border-r border-gray-300"
                  : darkMode
                    ? "text-gray-400 hover:text-white"
                    : "text-gray-500 hover:text-gray-900"
                  }`}
              >
                {tab}
              </button>
            ))}
          </div>

          {/* Tab Content */}
          {activeTab === "Statement" && (
            <div>
              <div className="prose dark:prose-invert" dangerouslySetInnerHTML={{ __html: he.decode(fileContent.problemStatement || "") }} />
              {(fileContent.sampleTestcase || fileContent.sampleOutput) && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-5">
                  <div className={`border p-4 rounded-md relative ${darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"}`}>
                    <h3 className="text-lg font-semibold mb-2">Input</h3>
                    <textarea className={`w-full p-3 text-sm font-mono rounded-md resize-none shadow-inner ${darkMode ? "bg-gray-700 text-gray-100 border-gray-600" : "bg-white text-gray-700 border-gray-300"}`} value={formattedInput} readOnly rows={10} />
                    <FiClipboard className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-400" onClick={() => copyToClipboard(formattedInput)} />
                  </div>
                  <div className={`border p-4 rounded-md relative ${darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"}`}>
                    <h3 className="text-lg font-semibold mb-2">Output</h3>
                    <textarea className={`w-full p-3 text-sm font-mono rounded-md resize-none shadow-inner ${darkMode ? "bg-gray-700 text-gray-100 border-gray-600" : "bg-white text-gray-700 border-gray-300"}`} value={formattedOutput} readOnly rows={10} />
                    <FiClipboard className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-400" onClick={() => copyToClipboard(formattedOutput)} />
                  </div>
                </div>
              )}
              <div className="prose dark:prose-invert mt-4" dangerouslySetInnerHTML={{ __html: he.decode(fileContent.explanation || "") }} />
            </div>
          )}

          {activeTab === "Submission" && (
            <div className="mt-4">
              {judging && (
                <div
                  className={`inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-semibold mt-2
                  ${darkMode
                      ? "bg-yellow-500/20 text-yellow-300 border border-yellow-500/40"
                      : "bg-yellow-100 text-yellow-700 border border-yellow-300"
                    }`}
                >
                  <span className="animate-spin">⏳</span>
                  Judging in progress…
                </div>
              )}

              {showSubmissionTable && submissionId && (
                <table className="w-full mt-4 border">
                  <thead>
                    <tr>
                      <th>Submission Id</th>
                      <th>Submitted At</th>
                      <th>Language</th>
                      <th>Status</th>
                      <th>Time</th>
                      <th>Memory</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      className={`text-center text-sm ${finalVerdict?.verdict === "Accepted"
                          ? darkMode
                            ? "bg-green-500/10 text-green-300"
                            : "bg-green-50 text-green-700"
                          : darkMode
                            ? "bg-red-500/10 text-red-300"
                            : "bg-red-50 text-red-700"
                        }`}
                    >
                      <td>{submissionId}</td>
                      <td>{finalVerdict?.created_at ? new Date(finalVerdict.created_at).toLocaleString() : "0"}</td>

                      <td>{formatLanguage(language)}</td>
                      <td>{finalVerdict?.verdict || finalVerdict || "0"}</td>
                      <td>{finalVerdict?.time ?? "0"}</td>
                      <td>{finalVerdict?.memory ?? "0"}</td>
                    </tr>
                  </tbody>
                </table>
              )}

              {showSubmissionTable && (
                <table className="w-full mt-4 border">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Status</th>
                      <th>Time</th>
                      <th>Memory</th>
                    </tr>
                  </thead>
                  <tbody>
                    {submissionResults.map((r, i) => (
                      <tr
                        key={i}
                        className={`text-center text-sm
                     ${r.passed
                            ? darkMode
                              ? "bg-green-500/10 text-green-300"
                              : "bg-green-50 text-green-700"
                            : darkMode
                              ? "bg-red-500/10 text-red-300"
                              : "bg-red-50 text-red-700"
                          }`}
                      >
                        <td>{i + 1}</td>
                        <td>{r.status}</td>
                        <td>{r.time}</td>
                        <td>{r.memory}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}


              <div className="mt-8">
                <h3 className="text-lg font-semibold mb-4">
                  Previous Submissions
                </h3>

                {previousSubmissions.length === 0 ? (
                  <div
                    className={`text-sm p-4 rounded-md border text-center
        ${darkMode
                        ? "bg-gray-800 border-gray-700 text-gray-400"
                        : "bg-gray-50 border-gray-300 text-gray-500"
                      }`}
                  >
                    No submissions yet for this problem
                  </div>
                ) : (
                  <div className="space-y-3">
                    {previousSubmissions.map((s) => (
                      <div
                        key={s.id}
                        className={`flex items-center justify-between gap-4 p-4 rounded-lg border transition
                        ${darkMode
                            ? "bg-gray-800 border-gray-700 hover:bg-gray-750"
                            : "bg-white border-gray-300 hover:bg-gray-50"
                          }`}
                      >

                        <div className="flex items-center gap-3 min-w-[200px]">

                          <button
                            onClick={() => navigate(`/submission/${s.id}`)}
                            className="text-sm font-semibold text-blue-500 hover:underline"
                          >
                            #{s.id}
                          </button>
                          <VerdictBadge verdict={s.verdict} />


                          <span className="text-sm text-gray-500">
                            {formatLanguage(s.language)}
                          </span>
                        </div>


                        {/* Middle: Stats */}
                        <div className="flex items-center gap-6 text-sm">
                          <span className="flex items-center gap-1">
                            ⏱ {s.time} ms
                          </span>
                          <span className="flex items-center gap-1">
                            💾 {s.memory} KB
                          </span>
                        </div>

                        {/* Right: Date */}
                        <div className="text-xs text-gray-500 text-right">
                          {new Date(s.created_at).toLocaleString()}
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                {psTotalPages > 1 && (
                  <div className="flex justify-center items-center gap-4 mt-4">
                    <button
                      disabled={psPage === 0}
                      onClick={() => setPsPage((p) => p - 1)}
                      className={`px-4 py-2 rounded-lg text-sm font-semibold transition
        ${psPage === 0
                          ? "bg-gray-400 cursor-not-allowed text-gray-700"
                          : darkMode
                            ? "bg-gray-700 hover:bg-gray-600 text-white"
                            : "bg-gray-200 hover:bg-gray-300 text-gray-800"
                        }`}
                    >
                      Previous
                    </button>

                    <span className="text-sm font-medium">
                      Page {psPage + 1} of {psTotalPages}
                    </span>

                    <button
                      disabled={psPage + 1 >= psTotalPages}
                      onClick={() => setPsPage((p) => p + 1)}
                      className={`px-4 py-2 rounded-lg text-sm font-semibold transition
        ${psPage + 1 >= psTotalPages
                          ? "bg-gray-400 cursor-not-allowed text-gray-700"
                          : darkMode
                            ? "bg-gray-700 hover:bg-gray-600 text-white"
                            : "bg-gray-200 hover:bg-gray-300 text-gray-800"
                        }`}
                    >
                      Next
                    </button>
                  </div>
                )}

              </div>



            </div>

          )}

          {activeTab === "Editorial" && (
            <div className="prose dark:prose-invert">
              {fileContent.editorial ? (
                <div dangerouslySetInnerHTML={{ __html: he.decode(fileContent.editorial) }} />
              ) : (
                <p className="text-gray-500 text-center mt-4">Editorial not available yet.</p>
              )}
            </div>
          )}

        </div>

        {/* Resizer */}
        <div onMouseDown={handleMouseDown} className={`cursor-col-resize hidden md:block ${darkMode ? "bg-gray-700" : "bg-gray-300"}`} style={{ width: "6px", zIndex: 10 }} />

        {/* Right Panel */}
        <div style={{ width: `${100 - leftWidth}%`, transition: "width 0.1s ease" }} className={`p-5 flex flex-col ${darkMode ? "bg-gray-900" : "bg-gray-50"} overflow-hidden`}>
          {/* Header */}
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xl font-semibold">Code Editor</h2>
            <div className="flex items-center gap-2">
              <label className="font-medium">Language:</label>
              <select
                id="language"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                className={`px-3 py-2 rounded-md border focus:ring-2 focus:ring-blue-500 ${darkMode ? "bg-gray-800 text-gray-100 border-gray-700" : "bg-white text-gray-900 border-gray-300"
                  }`}
              >
                <optgroup label="C / C++">
                  <option value="c-clang-10.0.1-17">C</option>
                  <option value="cpp-clang-9.0.1-14">C++14</option>
                  <option value="cpp-clang-10.0.1-17">C++17</option>
                </optgroup>
                <optgroup label="Java / C#">
                  <option value="java-jdk-14.0.1">Java 14</option>
                  <option value="csharp-sdk-3.1.406">C# 8</option>
                  <option value="csharp-sdk-8.0.302">C# 11</option>
                </optgroup>
                <optgroup label="Python">
                  <option value="python-pypy-7.3.12-3.9">Python 3.9</option>
                  <option value="python-pypy-7.3.12-3.10">Python 3.10</option>
                </optgroup>
              </select>
            </div>
          </div>

          {/* Monaco Editor */}
          <div className="relative flex-grow rounded-md border overflow-hidden">
            <MonacoEditor
              language={getMonacoLanguage(language)}
              theme={darkMode ? "vs-dark" : "light"}
              value={code}
              onChange={setCode}
              options={{ fontSize: 14, minimap: { enabled: false }, automaticLayout: true, tabSize: 2 }}
            />
            {loading && (
              <div className="absolute inset-0 bg-black/40 flex flex-col items-center justify-center z-50 rounded-md">
                <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                <p className="text-white mt-3">Processing{processingDots}</p>
              </div>
            )}
          </div>
          {/* Buttons */}
          <div className="mt-5 flex gap-4">
            {/* Submit */}
            <button
              onClick={handleSubmit}
              disabled={judging}
              className={`
      flex-1 flex items-center justify-center gap-2 px-6 py-3 
      font-semibold rounded-lg transition-all duration-200
      ${judging
                  ? "bg-gray-400 cursor-not-allowed text-gray-700"
                  : darkMode
                    ? "bg-emerald-600 hover:bg-emerald-700 text-white shadow-lg shadow-emerald-900/40"
                    : "bg-emerald-500 hover:bg-emerald-600 text-white shadow-md"
                }
    `}
            >
              <span className="text-lg"></span>
              Submit Solution
            </button>

            {/* Run Sample */}
            <button
              onClick={handleRunSample}
              disabled={judging}
              className={`
      flex-1 flex items-center justify-center gap-2 px-6 py-3 
      font-semibold rounded-lg transition-all duration-200
      ${judging
                  ? "bg-gray-400 cursor-not-allowed text-gray-700"
                  : darkMode
                    ? "bg-blue-600 hover:bg-blue-700 text-white shadow-lg shadow-blue-900/40"
                    : "bg-blue-500 hover:bg-blue-600 text-white shadow-md"
                }
    `}
            >
              <span className="text-lg">▶</span>
              Run Sample Test
            </button>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default ProblemDetail;
