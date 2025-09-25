import React, { useState, useEffect, useContext } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import Footer from "../NavBar_Footer/Footer";
import RichTextEditor from "./RichTextEditor";
import NavBar from "../NavBar_Footer/NavBarCus";
import { AuthContext } from "../auth_component/AuthContext"; // Import dark mode context

const ProblemEditor = () => {
  const { id } = useParams();
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const { darkMode } = useContext(AuthContext); // Access dark mode context

  const [problem, setProblem] = useState({
    title: "",
    handle: "",
    difficulty: "",
    type: "",
    problemStatement: "",
  });
  const [files, setFiles] = useState([]);

  useEffect(() => {
    if (id && id !== "null") {
      const fetchProblem = async () => {
        try {
          const response = await axios.get(
            `${baseURL}/api/problem/v1/get/${id}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          const { title, handle, difficulty, type, problemStatement, testcase } = response.data.data;
          alert(testcase);
          setProblem({ title, handle, difficulty, type, problemStatement });
          setFiles(testcase.map((tc) => ({ name: tc.fileName, file: tc })));
        } catch (error) {
          alert("Problem Cannot Fetch for Updating!");
        }
      };
      fetchProblem();
    }
  }, [id, token]);

  const handleChange = (e) => {
    setProblem({ ...problem, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    setFiles((prevFiles) => [
      ...prevFiles,
      ...Array.from(e.target.files).map((file) => ({ name: file.name, file })),
    ]);
  };

  const handleFileRemove = (index) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (files.length === 0) {
      alert("Please upload at least one file!");
      return;
    }

    const formData = new FormData();
    Object.entries(problem).forEach(([key, value]) => formData.append(key, value));
    files.forEach((file) => formData.append("testCaseFile", file.file));

    try {
      const url =
        id && id !== "null"
          ? `${baseURL}/api/problem/v1/update/${id}`
          : `${baseURL}/api/problem/v1/save`;

      const method = id && id !== "null" ? "put" : "post";

      const response = await axios[method](url, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });

      if (response.status === 200) {
        alert(id && id !== "null" ? "Problem updated successfully!" : "Problem created successfully!");
      }
    } catch (error) {
      console.error("Error submitting the problem:", error);
      alert("Failed to create problem!");
    }
  };

  return (
    <div className={darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-black"}>
      <NavBar />
      <div className="min-h-screen flex items-center justify-center p-4 sm:p-10">
        <div className={`w-full max-w-3xl shadow-lg rounded-lg p-6 sm:p-10 ${darkMode ? 'bg-gray-800' : 'bg-white'}`}>
          <h1 className="text-2xl font-bold mb-6 text-center">
            {id && id !== "null" ? "Edit Problem" : "Create Problem"}
          </h1>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Grid layout for form fields */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {Object.keys(problem).map(
                (field) =>
                  field !== "problemStatement" && (
                    <div key={field}>
                      <label htmlFor={field} className="block capitalize font-medium">
                        {field}
                      </label>
                      <input
                        type="text"
                        id={field}
                        name={field}
                        value={problem[field]}
                        onChange={handleChange}
                        required
                        className={`w-full p-3 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${darkMode ? 'bg-gray-700 text-white' : 'bg-white'}`}
                      />
                    </div>
                  )
              )}
            </div>

            {/* Problem Statement Editor */}
            <div>
              <RichTextEditor
                value={problem.problemStatement}
                onChange={(content) => setProblem({ ...problem, problemStatement: content })}
                darkMode={darkMode}  // Pass dark mode state to editor
              />
            </div>

            {/* File Upload */}
            <div className="space-y-4">
              <label htmlFor="testCaseFile" className="block font-medium">
                Upload Test Case File
              </label>
              <input
                type="file"
                multiple
                onChange={handleFileChange}
                accept=".zip,.tar,.txt,.in,.out"
                className={`w-full p-3 border rounded-md ${darkMode ? 'bg-gray-700' : 'bg-white'}`}
              />

              {/* File List */}
              {files.length > 0 && (
                <ul className="bg-gray-100 p-4 rounded-md">
                  {files.map((file, index) => (
                    <li key={index} className="flex justify-between items-center py-1">
                      <span className="truncate">{file.name}</span>
                      <button
                        className="text-red-500 text-sm hover:underline"
                        onClick={() => handleFileRemove(index)}
                      >
                        Remove
                      </button>
                    </li>
                  ))}
                </ul>
              )}
            </div>

            {/* Submit Button */}
            <div className="flex justify-center">
              <button
                type="submit"
                className="bg-blue-500 text-white py-3 px-6 rounded-lg hover:bg-blue-600 transition-all w-full sm:w-auto"
              >
                {id && id !== "null" ? "Update Problem" : "Create Problem"}
              </button>
            </div>
          </form>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ProblemEditor;
