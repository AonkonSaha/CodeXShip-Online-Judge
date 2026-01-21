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
import {CheckCircle2, Clock, Cpu } from "lucide-react"; // 👈 add this import



const defaultTemplates = {
  c: `#include <stdio.h>\n\nint main() {\n    // Your code here\n    return 0;\n}`,
  cpp: `#include <bits/stdc++.h>\nusing namespace std;\n\nint main() {\n    // Your code here\n    return 0;\n}`,
  java: `import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        // Your code here\n    }\n}`,
  csharp: `using System;\nclass Program {\n    static void Main() {\n        // Your code here\n    }\n}`,
  python: `# Your code here\nif __name__ == "__main__":\n    pass`,
};
// Map Judge language IDs to Monaco language names
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
  const { darkMode } = useContext(AuthContext);
  const containerRef = useRef(null);

  const [code, setCode] = useState(defaultTemplates["java"]);
  const [language, setLanguage] = useState("java-jdk-14.0.1");
  const [fileContent, setFileContent] = useState({});
  const [loading, setLoading] = useState(false);
  const [processingDots, setProcessingDots] = useState("");
  const [leftWidth, setLeftWidth] = useState(50);
  const [sampleResult, setSampleResult] = useState(null);

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const token = localStorage.getItem("token");
  const didFetch = useRef(false);

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

  // Fetch Problem Details
  useEffect(() => {
    if(didFetch.current)return;
    didFetch.current=true;
    const fetchProblem = async () => {
      try {
        const res = await fetch(`${baseURL}/api/v1/problems/${id}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token?token:""}`, 
        },
      });        const data = await res.json();
        setFileContent(data.data);

        if (data.data.sampleCode?.[language]) {
          setCode(data.data.sampleCode[language]);
        }
      } catch (err) {
        console.error("Problem fetch failed:", err);
      }
    };
    fetchProblem();
  }, [id, language,baseURL,token]);

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

  // Submit code
  const handleSubmit = () => {
    if (!token) {
      toast.error("Please login before submitting code");
      navigate("/login");
      return;
    }
    if (!code.trim()) {
      toast.error("Please write code before submitting");
      return;
    }
    setLoading(true);
    try {
      
    const langKey = language.includes("cpp-clang-9.0.1-14")
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
      navigate("/submission-result", {
        state: {
          problem_id: id,
          submission_code: code,
          language,
          langKey,
          problemName: fileContent.title,
        },
      });
    } catch (err) {
      console.error("Submission failed:", err);
      toast.error("Submission failed. Try again.");
    } finally {
      setLoading(false);
    }
  };

  // Run sample test
  const handleRunSample = async () => {
    if (!token) {
      toast.error("Please login before running sample test");
      navigate("/login");
      return;
    }
    if (!code.trim()) {
      toast.error("Please write code first.");
      return;
    }
    if (!fileContent.sampleTestcase) {
      toast.error("No sample test available.");
      return;
    }
    setLoading(true);
    setSampleResult(null);
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
      const data = await res.json();
      setSampleResult(data.data);
    } catch (err) {
      console.error("Sample run failed:", err);
      toast.error("Sample test failed. Try again.");
    } finally {
      setLoading(false);
    }
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
<div className="flex flex-col gap-5 mb-6">
  {/* Title and Tags */}
  <div className="flex justify-between items-center flex-wrap gap-3">
    <h1
      className={`text-2xl font-semibold tracking-tight ${
        darkMode ? "text-gray-100" : "text-gray-900"
      }`}
    >
      {fileContent.title || "Untitled Problem"}
    </h1>
    

    <div className="flex items-center gap-2">
      <span
        className={`px-4 py-1.5 rounded-full text-sm font-medium tracking-wide ${
          fileContent.difficulty === "Easy"
            ? "bg-green-600 text-white"
            : fileContent.difficulty === "Hard"
            ? "bg-red-600 text-white"
            : "bg-blue-600 text-white"
        }`}
      >
        {fileContent.difficulty || "Medium"}
      </span>
      <span
        className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-sm font-semibold shadow-sm ${
          darkMode
            ? "bg-yellow-500 text-gray-900"
            : "bg-amber-400 text-gray-900"
        }`}
      >
        <Coins size={16} /> {fileContent.coins || 0}
      </span>
    </div>
  </div>

  {/* Meta Information Bar */}
  <div className="flex flex-wrap items-center gap-3 text-sm">
    {[
      {
        icon: <Clock size={15} />,
        label: "Time Limit",
        value: fileContent.time_limit
          ? `${fileContent.time_limit} sec`
          : "—",
      },
      {
        icon: <Cpu size={15} />,
        label: "Memory Limit",
        value: fileContent.memory_limit
          ? `${fileContent.memory_limit} MB`
          : "—",
      },
      // {
      //   icon: <FileText size={15} />,
      //   label: "Type",
      //   value: fileContent.type || "—",
      // },
    ].map((item, index) => (
      <div
        key={index}
        className={`flex items-center gap-2 px-3 py-1.5 rounded-md border transition-all duration-150 ${
          darkMode
            ? "bg-gray-800 border-gray-700 text-gray-200 hover:bg-gray-750"
            : "bg-gray-50 border-gray-300 text-gray-800 hover:bg-gray-100"
        }`}
      >
        <span
          className={`${
            darkMode ? "text-blue-400" : "text-blue-600"
          } flex items-center`}
        >
          {item.icon}
        </span>
        <span className="font-medium">{item.label}:</span>
        <span>{item.value}</span>
      </div>
      
    ))}
         {/* ✅ Solved Icon */}
      {fileContent.is_solved && (
        <span
          className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-sm font-semibold shadow-sm border ${
            darkMode
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
</div>


          <div className="prose dark:prose-invert" dangerouslySetInnerHTML={{ __html: he.decode(fileContent.problemStatement || "") }} />

          {/* Sample Input/Output */}
          {(fileContent.sampleTestcase || fileContent.sampleOutput) && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-5">
              {/* Input */}
              <div className={`border p-4 rounded-md relative ${darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"}`}>
                <h3 className="text-lg font-semibold mb-2">Input</h3>
                <textarea className={`w-full p-3 text-sm font-mono rounded-md resize-none shadow-inner ${darkMode ? "bg-gray-700 text-gray-100 border-gray-600" : "bg-white text-gray-700 border-gray-300"}`} value={formattedInput} readOnly rows={10} />
                <FiClipboard className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-400" onClick={() => copyToClipboard(formattedInput)} />
              </div>
              {/* Output */}
              <div className={`border p-4 rounded-md relative ${darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"}`}>
                <h3 className="text-lg font-semibold mb-2">Output</h3>
                <textarea className={`w-full p-3 text-sm font-mono rounded-md resize-none shadow-inner ${darkMode ? "bg-gray-700 text-gray-100 border-gray-600" : "bg-white text-gray-700 border-gray-300"}`} value={formattedOutput} readOnly rows={10} />
                <FiClipboard className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-400" onClick={() => copyToClipboard(formattedOutput)} />
              </div>
            </div>
          )}
          <div className="prose dark:prose-invert mt-4" dangerouslySetInnerHTML={{ __html: he.decode(fileContent.explanation || "") }} />

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
                className={`px-3 py-2 rounded-md border focus:ring-2 focus:ring-blue-500 ${
                  darkMode ? "bg-gray-800 text-gray-100 border-gray-700" : "bg-white text-gray-900 border-gray-300"
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
          <div className="mt-5 flex gap-3">
            <button onClick={handleSubmit} disabled={loading} className={`flex-1 px-6 py-3 font-bold rounded-lg ${loading ? "bg-gray-400 cursor-not-allowed" : "bg-green-500 hover:bg-green-600 text-white"}`}>Submit Solution</button>
            <button onClick={handleRunSample} disabled={loading} className="flex-1 px-6 py-3 font-bold rounded-lg bg-blue-500 hover:bg-blue-600 text-white">Run Sample Test</button>
          </div>

          {/* Sample Test Result */}
          {sampleResult && (
            <div className={`mt-6 p-4 rounded-lg shadow-md ${darkMode ? "bg-gray-800" : "bg-gray-100"}`}>
              <h3 className="text-lg font-semibold mb-4 text-center">Sample Test Result</h3>
              <div className="overflow-x-auto">
                <table className="min-w-full border-collapse text-sm">
                  <thead>
                    <tr className={darkMode ? "bg-gray-700 text-white" : "bg-gray-200"}>
                      <th className="px-4 py-2 border">#</th>
                      <th className="px-4 py-2 border">Status</th>
                      <th className="px-4 py-2 border">Input</th>
                      <th className="px-4 py-2 border">Your Output</th>
                      <th className="px-4 py-2 border">Actual Output</th>
                      <th className="px-4 py-2 border">Time</th>
                      <th className="px-4 py-2 border">Memory</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sampleResult.results.map((res, idx) => (
                      <tr key={idx} className={res.status === "Accepted" ? darkMode ? "bg-green-900/20" : "bg-green-100" : darkMode ? "bg-red-900/20" : "bg-red-100"}>
                        <td className="px-4 py-2 border text-center">{idx + 1}</td>
                        <td className="px-4 py-2 border text-center">
                          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${res.status === "Accepted" ? "bg-green-500 text-white" : "bg-red-500 text-white"}`}>{res.status}</span>
                        </td>
                        <td className="px-4 py-2 border"><pre className="whitespace-pre-wrap">{res.input || "-"}</pre></td>
                        <td className="px-4 py-2 border"><pre className="whitespace-pre-wrap">{res.stdout || "-"}</pre></td>
                        <td className="px-4 py-2 border"><pre className="whitespace-pre-wrap">{res.expected_output || "-"}</pre></td>
                        <td className="px-4 py-2 border text-center">{res.time || "-"}</td>
                        <td className="px-4 py-2 border text-center">{res.memory || "-"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default ProblemDetail;
