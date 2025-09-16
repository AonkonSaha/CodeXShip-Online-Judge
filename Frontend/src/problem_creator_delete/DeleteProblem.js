import React, { useState, useContext } from 'react';
import axios from 'axios';
import Footer from '../NavBar_Footer/Footer';
import NavBar from '../NavBar_Footer/NavBarCus';
import { AuthContext } from '../auth_component/AuthContext';

const DeleteProblem = () => {
  const { darkMode } = useContext(AuthContext);
  const [handle, setProblemHandle] = useState('');
  const [error, setError] = useState('');
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const deleteProblem = async () => {
    try {
      const response = await axios.delete(`${baseURL}/api/problem/v1/remove/${handle}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      alert(response.data === "Problem doesn't exist" ? response.data : `Problem Id-${handle} Successfully Removed!`);
      setError('');
    } catch (err) {
      console.error('Error deleting problem:', err);
      setError('Problem not found or an error occurred!');
    }
  };

  const allDeleteProblem = async () => {
    try {
      const response = await axios.delete(`${baseURL}/api/problem/v1/remove/all`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      alert(response.data === "Problem doesn't exist" ? response.data : "Each Problem Successfully Removed!");
      setError('');
    } catch (err) {
      console.error('Error deleting problem:', err);
      setError('Problem not found or an error occurred!');
    }
  };

  return (
    <div className={`min-h-screen ${darkMode ? 'bg-gray-950 text-gray-300' : 'bg-gray-100 text-gray-800'} flex flex-col`}> 
      <NavBar />
      <main className='flex-grow flex justify-center items-center p-4'>
        <div className={`shadow-xl rounded-xl p-6 w-full max-w-lg ${darkMode ? 'bg-gray-900 border border-gray-700' : 'bg-white border border-gray-300'}`}> 
          <h1 className='text-3xl font-semibold mb-6 text-center'>Delete Problem</h1>

          <div className='mb-5'>
            <label htmlFor="problemId" className='block text-lg font-medium mb-2'>Problem ID</label>
            <input
              type="text"
              id="problemId"
              value={handle}
              onChange={(e) => setProblemHandle(e.target.value)}
              className={`p-3 rounded-lg w-full focus:outline-none focus:ring-2 focus:ring-blue-500 ${darkMode ? 'bg-gray-800 border border-gray-600 text-gray-300' : 'bg-white border border-gray-300'}`}
              placeholder='Enter problem ID'
            />
          </div>

          <div className='flex flex-col sm:flex-row gap-4'>
            <button
              onClick={deleteProblem}
              className='bg-blue-600 text-white py-3 px-6 rounded-lg hover:bg-blue-700 transition-all w-full sm:w-auto'
            >
              Delete Problem
            </button>
            <button
              onClick={allDeleteProblem}
              className='bg-red-600 text-white py-3 px-6 rounded-lg hover:bg-red-700 transition-all w-full sm:w-auto'
            >
              Delete All Problems
            </button>
          </div>

          {error && <p className='mt-4 text-center text-sm p-2 rounded-lg bg-red-600 text-white'>{error}</p>}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default DeleteProblem;