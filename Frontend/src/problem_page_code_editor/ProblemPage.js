import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
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
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  useEffect(() => {
    const fetchFileContent = async () => {
      try {
        const res = await fetch(`${baseURL}/api/problem/v2/get/${id}`);
        const data = await res.json();
        setFileContent(data.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchFileContent();
  }, [id]);

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

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${baseURL}/api/submission/v1/submit`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ problem_id: id, submission_code: code, language }),
      });
      const data = await response.json();

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

  return (
    <>
      <NavBar />
      <div className="flex flex-col md:flex-row h-screen overflow-hidden">
        {/* Left Panel */}
        <div className="bg-white p-5 overflow-y-auto border-b md:border-r md:border-gray-300 w-full md:w-1/2">
          <h1 className="text-2xl font-bold mb-3">{fileContent.title || "Problem Title"}</h1>
          <div dangerouslySetInnerHTML={{ __html: fileContent.problemStatement || "" }} />
        </div>

        {/* Right Panel */}
        <div className="bg-gray-100 p-5 w-full md:w-1/2 flex flex-col">
          <h2 className="text-xl font-semibold mb-3">Code Editor</h2>
          <CodeEditor code={code} setCode={setCode} language={language} setLanguage={setLanguage} />

          <button
            onClick={handleSubmit}
            disabled={loading}
            className={`mt-5 px-6 py-3 font-bold rounded-lg transition-colors ${
              loading ? "bg-gray-400 cursor-not-allowed" : "bg-green-500 hover:bg-green-600 text-white"
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
