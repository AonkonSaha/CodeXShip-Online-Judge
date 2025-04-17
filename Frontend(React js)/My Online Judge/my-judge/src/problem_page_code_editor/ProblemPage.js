import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { FiClipboard } from "react-icons/fi"; 
import CodeEditor from "./CodeEditor";
import Footer from "../NavBar_Footer/Footer";
import { jwtDecode } from "jwt-decode";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import NavBar from "../NavBar_Footer/NavBarCus";

const ProblemDetail = () => {
  const { id } = useParams();
  const [leftWidth, setLeftWidth] = useState(50); // Initial left panel width percentage
  const [code, setCode] = useState("");
  const [language, setLanguage] = useState("javascript");
  const [fileContent, setFileContent] = useState({});
  const [executionResult, setExecutionResult] = useState({});
  const [error, setError] = useState("");
  const [submissionStatus, setSubmissionStatus] = useState(null);
  const token = localStorage.getItem("token");
  const decoded = token?jwtDecode(token):null;
  const userName = token?decoded.sub:null;
  const baseURL=process.env.REACT_APP_BACK_END_BASE_URL;
  const handleMouseDown = (e) => {
    e.preventDefault();
    const handleMouseMove = (e) => {
      const newLeftWidth = (e.clientX / window.innerWidth) * 100;
      if (newLeftWidth > 20 && newLeftWidth < 80) {
        setLeftWidth(newLeftWidth);
      }
    };
    const handleMouseUp = () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
  };

  useEffect(() => {
    const fetchFileContent = async () => {
      try {
        const response = await fetch(`${baseURL}/api/problems/${id}`);
        const data = await response.json();
        setFileContent(data.problem);
      } catch (err) {
        setError("Failed to load the file content. Please try again later.");
        console.error(err);
      }
    };

    fetchFileContent();
  }, [id]);

  // useEffect(() => {
  //   const socket = new SockJS(`${baseURL}/ws`);
  //   const client = new Client({
  //     webSocketFactory: () => socket,
  //     reconnectDelay: 5000,
  //     debug: (str) => console.log("WebSocket Debug:", str),
  //   });

  //   client.onConnect = () => {
  //     console.log("Connected to WebSocket");
  //     client.subscribe(`/topic/submissions/${userName}`, (message) => {
  //       const submissionUpdate = JSON.parse(message.body);
  //       setSubmissionStatus(submissionUpdate.status);
  //     });
  //   };

  //   client.onStompError = (error) => {
  //     console.error("STOMP Error: ", error);
  //   };

  //   client.activate();

  //   return () => {
  //     client.deactivate(); 
  //   };
  // }, [userName]);

  const formattedInput = fileContent.input ? fileContent.input.join("\n") : ""; 
  const formattedOutput = fileContent.output ? fileContent.output.join("\n") : ""; 

  const handleSubmit = async () => {
    try {
      const response = await fetch(`${baseURL}/code/submit`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          problemId: id,
          userName:userName,
          userCode:code,
          language,
        }),
      });

      const data = await response.json();
      setExecutionResult(data.execution);
      setSubmissionStatus("Processing...");
      
      
    } catch (error) {
      setError("Error submitting code. Please try again.");
    }
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(
      () => alert("Copied to clipboard!"),
      (err) => console.error("Error copying text: ", err)
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
            <h1 className="text-2xl font-bold text-gray-800">
              {fileContent.name || "Problem Title"}
            </h1>
            <span className="bg-blue-500 text-white px-4 py-1 rounded-full text-sm uppercase">
              {fileContent.difficulty || "Medium"}
            </span>
          </div>

          <div className="mb-5">
            <div
              className="mt-3"
              dangerouslySetInnerHTML={{
                __html: fileContent.statement || "",
              }}
            ></div>
          </div>

          <div className="grid grid-cols-2 gap-4 mt-5">
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

          <div className="mb-5">
            <h2 className="text-xl font-semibold text-gray-700">Explanation</h2>
            <p className="text-gray-600">
              For n = 5, the sum of the first 5 natural numbers is 15.
            </p>
            <p className="text-gray-600">
              The formula for the sum of the first n natural numbers is: Sum = n * (n + 1) / 2.
            </p>
          </div>
        </div>

        {/* Resizer */}
        <div
          className="cursor-col-resize bg-gray-300 md:block hidden"
          style={{ width: "5px" }}
          onMouseDown={handleMouseDown}
        />

        {/* Right Panel */}
        <div
          className="bg-gray-100 flex flex-col p-5 overflow-y-auto"
          style={{ width: `${100 - leftWidth}%` }}
        >
          <h2 className="text-xl font-semibold">Code Editor</h2>
          <CodeEditor code={code} setCode={setCode} language={language} setLanguage={setLanguage} />

          <button
            onClick={handleSubmit}
            className="mt-5 px-6 py-3 bg-green-500 text-white font-bold rounded-lg hover:bg-green-600 transition-colors"
          >
            Submit Solution
          </button>
          {submissionStatus && <p className="mt-3 font-semibold">Status: {submissionStatus}</p>}
          {submissionStatus && 
          
        <div>
         <h1>Results: </h1>
         <ul>
          {executionResult.results.map((result, index) => (
            <li key={index}>{result.testcaseName+" ------- "+result.isPassed}</li>
          ))}
        </ul>
       </div>
          
          
          }
        </div>
        




      </div>
      <Footer />
    </>
  );
};

export default ProblemDetail;















