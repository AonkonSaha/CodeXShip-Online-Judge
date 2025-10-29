import React, { useState, useEffect, useContext } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import Footer from "../NavBar_Footer/Footer";
import NavBar from "../NavBar_Footer/NavBarCus";
import RichTextEditor from "./RichTextEditor";
import { AuthContext } from "../auth_component/AuthContext";
import { Toaster, toast } from "react-hot-toast";
import { useRef } from "react";

const ProblemEditor = () => {
  const { id } = useParams();
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const { darkMode } = useContext(AuthContext);
  const didFetch=useRef(false);

  const [problem, setProblem] = useState({
    title: "",
    handle: "",
    difficulty: "",
    type: "",
    coins: "",
    problem_statement: "",
    explanation: "",
  });

  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);

  // ✅ Fetch problem if editing
  useEffect(() => {
    if(didFetch.current)return;
    didFetch.current=true;
    if (id && id !== "null") {
      const fetchProblem = async () => {
        try {
          setLoading(true);
          const response = await axios.get(`${baseURL}/api/problem/v1/get/${id}`, {
            headers: { Authorization: `Bearer ${token}` },
          });

          const data = response.data.data;
          const {
            title,
            handle,
            difficulty,
            type,
            coins,
            problem_statement,
            explanation,
            testCaseNameWithPath,
          
          } = data;

          const existingFiles = Object.entries(testCaseNameWithPath || {}).map(
            ([name, path]) => ({ name, path })
          );

          setProblem({ title, handle, difficulty, type, coins, problem_statement, explanation });
          setFiles(existingFiles);
          toast.success("✅ Problem loaded successfully!");
        } catch (error) {
          console.error("❌ Error fetching problem:", error);
          toast.error("Failed to fetch problem for editing.");
        } finally {
          setLoading(false);
        }
      };
      fetchProblem();
    }
  }, [id, token, baseURL]);

  // ✅ Handle input changes
  const handleChange = (e) => {
    setProblem({ ...problem, [e.target.name]: e.target.value });
  };

  // ✅ Handle new file uploads
  const handleFileChange = (e) => {
    const newFiles = Array.from(e.target.files).map((file) => ({
      name: file.name,
      file,
    }));
    setFiles((prev) => [...prev, ...newFiles]);
  };

  // ✅ Remove file (only from UI)
  const handleFileRemove = (index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
    toast("File removed from list", { icon: "🗑️" });
  };

  // ✅ Submit form
  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!problem.title || !problem.handle || !problem.problem_statement ) {
      toast.error("⚠️ Please fill all required fields.");
      return;
    }

    const formData = new FormData();
    formData.append("title", problem.title);
    formData.append("handle", problem.handle);
    formData.append("difficulty", problem.difficulty);
    formData.append("type", problem.type);
    formData.append("problemStatement", problem.problem_statement);
    formData.append("explanation", problem.explanation);
    formData.append("coin", problem.coins || 0);

    // Append only new uploaded files
    files.forEach((fileObj) => {
      if (fileObj.file instanceof File) {
        formData.append("testCaseFile", fileObj.file);
      }
    });

    try {
      setLoading(true);
      const url =
        id && id !== "null"
          ? `${baseURL}/api/problem/v1/update/${id}`
          : `${baseURL}/api/problem/v1/save`;
      const method = id && id !== "null" ? "put" : "post";

      const toastId = toast.loading("Saving problem...");
      const response = await axios[method](url, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });
      toast.dismiss(toastId);

      if ([200, 201, 204].includes(response.status)) {
        toast.success(
          id && id !== "null"
            ? "Problem updated successfully!"
            : "Problem created successfully!"
        );
      }
    } catch (error) {
      console.error("Error submitting problem:", error);
      toast.error("Failed to create or update problem!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-black"}>
      <Toaster position="top-center" />
      <NavBar />
      <div className="min-h-screen flex items-center justify-center p-4 sm:p-10">
        <div
          className={`w-full max-w-3xl shadow-lg rounded-lg p-6 sm:p-10 ${
            darkMode ? "bg-gray-800" : "bg-white"
          }`}
        >
          <h1 className="text-2xl font-bold mb-6 text-center">
            {id && id !== "null" ? "✏️ Edit Problem" : "➕ Create Problem"}
          </h1>

          {loading ? (
            <p className="text-center text-blue-400">Loading...</p>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Problem Info Fields */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                {["title", "handle", "difficulty", "type", "coins"].map((field) => (
                  <div key={field}>
                    <label htmlFor={field} className="block capitalize font-medium mb-1">
                      {field}
                    </label>
                    <input
                      type={field === "coins" ? "number" : "text"}
                      id={field}
                      name={field}
                      value={problem[field]}
                      onChange={handleChange}
                      required
                      className={`w-full p-3 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        darkMode ? "bg-gray-700 border-gray-600 text-white" : "bg-white"
                      }`}
                    />
                  </div>
                ))}
              </div>

              {/* ✅ Fixed Rich Text Editor */}
              <div>
                <RichTextEditor
                  value={problem.problem_statement}
                  onChange={(content) =>
                    setProblem({ ...problem, problem_statement: content })
                  }
                  darkMode={darkMode}
                  heading= "Problem Statement"

                />
              </div>

              {/* ✅ Fixed Rich Text Editor */}
              <div>
                <RichTextEditor
                  value={problem.explanation}
                  onChange={(content) =>
                    setProblem({ ...problem, explanation: content })
                  }
                  darkMode={darkMode}
                  heading= "Explanation"
                />
              </div>

              {/* File Upload Section */}
              <details
                open
                className={`mt-6 border rounded-lg p-4 ${
                  darkMode ? "border-gray-600 bg-gray-700" : "border-gray-300 bg-gray-50"
                }`}
              >
                <summary className="font-semibold text-lg cursor-pointer select-none">
                  🧪 Test Case Files
                </summary>

                <div className="mt-4 space-y-4">
                  <input
                    type="file"
                    multiple
                    onChange={handleFileChange}
                    accept=".zip,.tar,.txt,.in,.out"
                    className={`w-full p-3 border rounded-md ${
                      darkMode ? "bg-gray-700 border-gray-600" : "bg-white border-gray-300"
                    }`}
                  />

                  {files.length > 0 && (
                    <div
                      className={`mt-4 rounded-lg shadow-md overflow-hidden ${
                        darkMode ? "bg-gray-800" : "bg-white"
                      }`}
                    >
                      <table className="w-full text-sm">
                        <thead
                          className={`text-left uppercase text-xs ${
                            darkMode
                              ? "bg-gray-900 text-gray-300"
                              : "bg-gray-200 text-gray-700"
                          }`}
                        >
                          <tr>
                            <th className="p-3">#</th>
                            <th className="p-3">File Name</th>
                            <th className="p-3">Preview / Link</th>
                            <th className="p-3 text-right">Action</th>
                          </tr>
                        </thead>
                        <tbody>
                          {files.map((file, index) => (
                            <tr
                              key={index}
                              className={`border-b ${
                                darkMode
                                  ? "border-gray-700 hover:bg-gray-900"
                                  : "border-gray-200 hover:bg-gray-100"
                              }`}
                            >
                              <td className="p-3 text-center">{index + 1}</td>
                              <td className="p-3 font-medium truncate">{file.name}</td>
                              <td className="p-3">
                                {file.path ? (
                                  <a
                                    href={file.path}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-blue-400 hover:text-blue-500 underline"
                                  >
                                    View File
                                  </a>
                                ) : (
                                  <span className="text-gray-400 italic">
                                    New Upload
                                  </span>
                                )}
                              </td>
                              <td className="p-3 text-right">
                                <button
                                  type="button"
                                  onClick={() => handleFileRemove(index)}
                                  className="text-red-500 hover:text-red-600 font-medium transition-colors"
                                >
                                  Remove
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </details>

              {/* Submit Button */}
              <div className="flex justify-center">
                <button
                  type="submit"
                  disabled={loading}
                  className={`bg-blue-500 text-white py-3 px-6 rounded-lg hover:bg-blue-600 transition-all w-full sm:w-auto ${
                    loading ? "opacity-70 cursor-not-allowed" : ""
                  }`}
                >
                  {loading
                    ? "Saving..."
                    : id && id !== "null"
                    ? "Update Problem"
                    : "Create Problem"}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ProblemEditor;
