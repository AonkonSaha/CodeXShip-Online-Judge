import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FiClipboard } from "react-icons/fi";
import CodeEditor from "./CodeEditor";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";

const ProblemDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [code, setCode] = useState("");
  const [language, setLanguage] = useState("javascript");
  const [fileContent, setFileContent] = useState({});
  const [loading, setLoading] = useState(false);
  const [processingDots, setProcessingDots] = useState("");
  const [leftWidth, setLeftWidth] = useState(50); // resizable panel
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  // Fetch problem data
  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const res = await fetch(`${baseURL}/api/problem/v2/get/${id}`);
        const data = await res.json();
        setFileContent(data.data);
      } catch (err) {
        console.error("Failed to fetch problem:", err);
      }
    };
    fetchProblem();
  }, [id]);

  // Loading dots animation
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

  // Resizer handler
  const handleMouseDown = (e) => {
    e.preventDefault();
    const handleMouseMove = (e) => {
      const newLeftWidth = (e.clientX / window.innerWidth) * 100;
      if (newLeftWidth > 20 && newLeftWidth < 80) setLeftWidth(newLeftWidth);
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

  // Format sample input/output
  const formattedInput = fileContent.sampleTestcase
    ? fileContent.sampleTestcase.join("\n")
    : "";
  const formattedOutput = fileContent.sampleOutput
    ? fileContent.sampleOutput.join("\n")
    : "";

  // Clipboard copy
  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(
      () => alert("Copied to clipboard!"),
      (err) => console.error("Copy failed:", err)
    );
  };

  return (
    <>
      <NavBar />
      <div className="flex flex-col md:flex-row h-screen overflow-hidden">
        {/* Left Panel */}
        <div
          className="bg-white p-5 overflow-y-auto border-b md:border-r md:border-gray-300"
          style={{ width: `${leftWidth}%` }}
        >
          <div className="flex justify-between items-center mb-5">
            <h1 className="text-2xl font-bold">{fileContent.title || "Problem Title"}</h1>
            <span className="bg-blue-500 text-white px-4 py-1 rounded-full text-sm uppercase">
              {fileContent.difficulty || "Medium"}
            </span>
          </div>

          <div dangerouslySetInnerHTML={{ __html: fileContent.problemStatement || "" }} />

          {(fileContent.sampleTestcase || fileContent.sampleOutput) && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-5">
              <div className="border p-4 flex flex-col items-center relative">
                <h3 className="text-lg font-semibold">Input</h3>
                <textarea
                  className="w-full p-3 text-sm font-mono text-gray-700 border border-gray-300 rounded-md bg-white resize-none shadow-inner"
                  value={formattedInput}
                  readOnly
                  rows={10}
                />
                <FiClipboard
                  className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-600"
                  onClick={() => copyToClipboard(formattedInput)}
                  title="Copy to clipboard"
                />
              </div>

              <div className="border p-4 flex flex-col items-center relative">
                <h3 className="text-lg font-semibold">Output</h3>
                <textarea
                  className="w-full p-3 text-sm font-mono text-gray-700 border border-gray-300 rounded-md bg-white resize-none shadow-inner"
                  value={formattedOutput}
                  readOnly
                  rows={10}
                />
                <FiClipboard
                  className="absolute top-3 right-3 cursor-pointer text-xl hover:text-blue-600"
                  onClick={() => copyToClipboard(formattedOutput)}
                  title="Copy to clipboard"
                />
              </div>
            </div>
          )}
        </div>

        {/* Resizer */}
        <div
          className="cursor-col-resize bg-gray-300 md:block hidden"
          style={{ width: "5px" }}
          onMouseDown={handleMouseDown}
        />

        {/* Right Panel */}
        <div className="bg-gray-100 p-5 w-full md:w-1/2 flex flex-col">
          <h2 className="text-xl font-semibold mb-3">Code Editor</h2>
          <CodeEditor
            code={code}
            setCode={setCode}
            language={language}
            setLanguage={setLanguage}
          />

          <button
            onClick={handleSubmit}
            disabled={loading}
            className={`mt-5 px-6 py-3 font-bold rounded-lg transition-colors ${
              loading
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-green-500 hover:bg-green-600 text-white"
            }`}
          >
            {loading ? "Submitting..." : "Submit Solution"}
          </button>

          {loading && (
            <div className="mt-5 flex items-center gap-3">
              <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
              <span className="text-blue-500 font-semibold text-lg">
                Processing{processingDots}
              </span>
            </div>
          )}
        </div>
      </div>
      <Footer />
    </>
  );
};

export default ProblemDetail;
