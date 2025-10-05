import React, { useState, useEffect, useContext, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FiClipboard } from "react-icons/fi";
import MonacoEditor from "@monaco-editor/react";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { BadgeDollarSign } from "lucide-react"; // Coins icon

const ProblemDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { darkMode, coins } = useContext(AuthContext);

  const [code, setCode] = useState("");
  const [language, setLanguage] = useState("javascript");
  const [fileContent, setFileContent] = useState({});
  const [loading, setLoading] = useState(false);
  const [processingDots, setProcessingDots] = useState("");
  const [leftWidth, setLeftWidth] = useState(50);
  const containerRef = useRef(null);
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  // Fetch problem
  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const res = await fetch(`${baseURL}/api/problem/v2/get/${id}`);
        const data = await res.json();
        setFileContent(data.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchProblem();
  }, [id]);

  // Loading animation dots
  useEffect(() => {
    let interval;
    if (loading) {
      let count = 0;
      interval = setInterval(() => {
        count = (count + 1) % 4;
        setProcessingDots(".".repeat(count));
      }, 500);
    } else {
      setProcessingDots("");
      clearInterval(interval);
    }
    return () => clearInterval(interval);
  }, [loading]);

  // Smart bidirectional panel resizer
  const handleMouseDown = (e) => {
    e.preventDefault();
    const container = containerRef.current;
    if (!container) return;

    const containerRect = container.getBoundingClientRect();

    const handleMouseMove = (e) => {
      let newLeftWidth = ((e.clientX - containerRect.left) / containerRect.width) * 100;

      // Clamp width between 20% and 80%
      if (newLeftWidth < 20) newLeftWidth = 20;
      if (newLeftWidth > 80) newLeftWidth = 80;

      setLeftWidth(newLeftWidth);
    };

    const handleMouseUp = () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
  };

  // Submit solution
  const handleSubmit = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`${baseURL}/api/submission/v1/submit`, {
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

      navigate("/submission-result", {
        state: {
          submissionStatus: "Processed",
          executionResult: data.data,
        },
      });
    } catch (err) {
      console.error(err);
      alert("Submission failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  // Run sample test
  const handleRunSample = () => {
    if (!fileContent.sampleTestcase) return;
    alert("Sample Test Run:\n\nInput:\n" + fileContent.sampleTestcase.join("\n"));
  };

  // Format sample input/output
  const formattedInput = fileContent.sampleTestcase ? fileContent.sampleTestcase.join("\n") : "";
  const formattedOutput = fileContent.sampleOutput ? fileContent.sampleOutput.join("\n") : "";

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(
      () => alert("Copied to clipboard!"),
      (err) => console.error("Copy failed:", err)
    );
  };

  return (
    <>
      <NavBar />
      <div
        ref={containerRef}
        className={`flex flex-col md:flex-row h-screen overflow-hidden transition-colors duration-300 ${
          darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"
        }`}
      >
        {/* Left Panel */}
        <div
          style={{ width: `${leftWidth}%` }}
          className={`p-5 overflow-y-auto border-b md:border-r transition-all duration-300 rounded-lg ${
            darkMode ? "bg-gray-900 border-gray-800" : "bg-white border-gray-300"
          }`}
        >
          <div className="flex justify-between items-center mb-5">
            <h1 className="text-2xl font-bold">{fileContent.title || "Problem Title"}</h1>
            <div className="flex items-center gap-2">
              <span className="bg-blue-500 text-white px-4 py-1 rounded-full text-sm uppercase">
                {fileContent.difficulty || "Medium"}
              </span>
              <span className="bg-yellow-400 text-gray-900 px-3 py-1 rounded-full text-sm font-semibold flex items-center gap-1">
                <BadgeDollarSign size={16} /> {fileContent.coins || 0}
              </span>
              {/* {coins !== null && (
                <span className="bg-green-500 text-white px-3 py-1 rounded-full text-sm font-semibold flex items-center gap-1">
                  <BadgeDollarSign size={16} /> {coins}
                </span>
              )} */}
            </div>
          </div>

          <div
            className="prose dark:prose-invert"
            dangerouslySetInnerHTML={{ __html: fileContent.problemStatement || "" }}
          />

          {(fileContent.sampleTestcase || fileContent.sampleOutput) && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-5">
              {/* Input */}
              <div
                className={`border p-4 flex flex-col items-center relative rounded-md transition-all duration-300 ${
                  darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"
                }`}
              >
                <h3 className="text-lg font-semibold mb-2">Input</h3>
                <textarea
                  className={`w-full p-3 text-sm font-mono rounded-md resize-none shadow-inner transition-all duration-300 ${
                    darkMode ? "bg-gray-700 text-gray-100 border-gray-600" : "bg-white text-gray-700 border-gray-300"
                  }`}
                  value={formattedInput}
                  readOnly
                  rows={10}
                />
                <FiClipboard
                  className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-400 transition-colors"
                  onClick={() => copyToClipboard(formattedInput)}
                  title="Copy to clipboard"
                />
              </div>

              {/* Output */}
              <div
                className={`border p-4 flex flex-col items-center relative rounded-md transition-all duration-300 ${
                  darkMode ? "bg-gray-800 border-gray-700" : "bg-white border-gray-300"
                }`}
              >
                <h3 className="text-lg font-semibold mb-2">Output</h3>
                <textarea
                  className={`w-full p-3 text-sm font-mono rounded-md resize-none shadow-inner transition-all duration-300 ${
                    darkMode ? "bg-gray-700 text-gray-100 border-gray-600" : "bg-white text-gray-700 border-gray-300"
                  }`}
                  value={formattedOutput}
                  readOnly
                  rows={10}
                />
                <FiClipboard
                  className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-400 transition-colors"
                  onClick={() => copyToClipboard(formattedOutput)}
                  title="Copy to clipboard"
                />
              </div>
            </div>
          )}
        </div>

        {/* Resizer */}
        <div
          className={`cursor-col-resize md:block hidden transition-colors duration-300 ${
            darkMode ? "bg-gray-800" : "bg-gray-300"
          }`}
          style={{ width: "5px" }}
          onMouseDown={handleMouseDown}
        />

        {/* Right Panel */}
        <div
          className={`p-5 w-full md:w-1/2 flex flex-col transition-colors duration-300 ${
            darkMode ? "bg-gray-900" : "bg-gray-100"
          }`}
        >
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xl font-semibold">Code Editor</h2>
            <div className="flex items-center gap-2">
              <label htmlFor="language" className="font-medium">
                Language:
              </label>
              <select
                id="language"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                className={`px-2 py-1 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-300 ${
                  darkMode ? "bg-gray-800 text-gray-100 border-gray-600" : "bg-white text-gray-900 border-gray-300"
                }`}
              >
                <option value="javascript">JavaScript</option>
                <option value="python">Python</option>
                <option value="cpp">C++</option>
                <option value="java">Java</option>
                <option value="csharp">C#</option>
                <option value="ruby">Ruby</option>
                <option value="go">Go</option>
              </select>
            </div>
          </div>

          <MonacoEditor
            language={language}
            theme="vs-dark"
            value={code}
            onChange={(value) => setCode(value)}
            options={{
              selectOnLineNumbers: true,
              automaticLayout: true,
              fontSize: 14,
              minimap: { enabled: false },
              tabSize: 2,
              insertSpaces: true,
            }}
            className="flex-grow rounded-md border transition-all duration-300"
          />

          <div className="mt-5 flex gap-3">
            <button
              onClick={handleSubmit}
              disabled={loading}
              className={`flex-1 px-6 py-3 font-bold rounded-lg transition-all duration-300 ${
                loading ? "bg-gray-400 cursor-not-allowed" : "bg-green-500 hover:bg-green-600 text-white"
              }`}
            >
              {loading ? "Submitting..." : "Submit Solution"}
            </button>
            <button
              onClick={handleRunSample}
              disabled={loading}
              className="flex-1 px-6 py-3 font-bold rounded-lg bg-blue-500 hover:bg-blue-600 text-white transition-all duration-300"
            >
              Run Sample Test
            </button>
          </div>

          {loading && (
            <div className="mt-5 flex items-center gap-3 animate-fadeIn">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
              <span className="text-blue-500 font-semibold text-lg">Processing{processingDots}</span>
            </div>
          )}
        </div>
      </div>
      <Footer />
    </>
  );
};

export default ProblemDetail;
