import React, { useContext, useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { FiClipboard } from "react-icons/fi";
import { AuthContext } from "../auth_component/AuthContext";
import toast, { Toaster } from "react-hot-toast";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const SubmissionResult = () => {
  const { darkMode, plusUserCoins } = useContext(AuthContext);
  const location = useLocation();
  const navigate = useNavigate();

  const { submission_code, language, problem_id, langKey, problemName } =
    location.state || {};

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const [submissionId, setSubmissionId] = useState(null);
  const [executionResult, setExecutionResult] = useState(null);
  const [loading, setLoading] = useState(true);

  const stompClientRef = useRef(null);
  const submittedRef = useRef(false);
  const [results,setResults]=useState([]);


  /* =========================
     1. SUBMIT CODE (REST)
     ========================= */
  useEffect(() => {
    if (!problem_id || !submission_code || !language || submittedRef.current)
      return;

    submittedRef.current = true;

    const submit = async () => {
      try {
        const token = localStorage.getItem("token");

        const res = await fetch(
          `${baseURL}/api/submission/v1/submit`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              problem_id,
              submission_code,
              language,
            }),
          }
        );

        const json = await res.json();

        if (!json?.data) {
          throw new Error("Invalid submission response");
        }
        setSubmissionId(json.data);
        
        toast.success("Submission started");

      } catch (err) {
        console.error(err);
        toast.error("Submission failed");
        setLoading(false);
      }
    };

    submit();
  }, [problem_id, submission_code, language, baseURL]);

  
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
            console.log("Received WS message Data: ",data );

            if(!data.completed){
            setResults(prev => [...prev, data]);
            }
            
            if (data.completed) {
            console.log("Finished Juding.............!");
              setLoading(false);
              setExecutionResult(data);
              plusUserCoins?.(data.coins || 0);
              toast.success("Judging completed");
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
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [submissionId, baseURL, plusUserCoins]);

  /* =========================
     HELPERS
     ========================= */
  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    toast.success("Copied");
  };

  const getStatusColor = (status) => {
    if (!status) return "text-gray-400";
    const s = status.toLowerCase();
    if (s.includes("accept") || s.includes("pass")) return "text-green-500 font-semibold";
    if (s.includes("wrong")) return "text-red-500 font-semibold";
    if (s.includes("tle") || s.includes("error") || s.includes("runtime"))
      return "text-orange-400 font-semibold";
    return "text-blue-500 font-semibold";
  };

  if (!problem_id || !submission_code) {
    return (
      <div className="min-h-screen flex flex-col">
        <NavBar />
        <div className="flex-grow flex flex-col items-center justify-center">
          <h2>No submission data found</h2>
          <button onClick={() => navigate(-1)} className="btn-primary mt-4">
            Go Back
          </button>
        </div>
        <Footer />
      </div>
    );
  }


  /* =========================
     UI
     ========================= */
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
                <th className="px-3 md:px-4 py-2 text-left">Date</th>
                <th className="px-3 md:px-4 py-2 text-left">#SubmissionID</th>
                <th className="px-3 md:px-4 py-2 text-left">Problem</th>
                <th className="px-3 md:px-4 py-2 text-left">Language</th>
                <th className="px-3 md:px-4 py-2 text-left">Status</th>
                <th className="px-3 md:px-4 py-2 text-left">Time (s)</th>
                <th className="px-3 md:px-4 py-2 text-left">Memory (KB)</th>
              </tr>
            </thead>
            <tbody className={`divide-y ${darkMode ? "divide-gray-700" : "divide-gray-200"}`}>
              <tr>
                <td className="px-3 md:px-4 py-2">{loading ?"-": new Date(executionResult.created_at).toLocaleString()}</td>
                <td className="px-3 md:px-4 py-2">{loading?'-':executionResult.id}</td>
                <td className="px-3 md:px-4 py-2">{problemName}</td>
                <td className="px-3 md:px-4 py-2">{langKey}</td>
                <td className={`px-3 md:px-4 py-2 ${getStatusColor(executionResult?.verdict)}`}>{loading ? <span className="text-blue-500 animate-pulse">Processing...</span> : executionResult?.verdict}</td>
                <td className="px-3 md:px-4 py-2">{executionResult?.time}</td>
                <td className="px-3 md:px-4 py-2">{executionResult?.memory}</td>
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
