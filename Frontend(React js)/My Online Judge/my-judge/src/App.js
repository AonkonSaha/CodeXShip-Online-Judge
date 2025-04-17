import logo from './logo.svg';
import './App.css';
import {Router, Routes, Route } from 'react-router-dom';

import { BrowserRouter } from 'react-router-dom';
import HomePage from './home/home';
import ProblemPage from './problem_page_code_editor/ProblemPage';
import DeleteProblem from './problem_creator_delete/DeleteProblem';
import VerifyEmail from './VerifyEmail';
import ProtectedRoute from './auth_component/ProtectedRoute';
import { AuthProvider } from './auth_component/AuthContext';
import Login from './auth_component/Login';
import Register from './auth_component/Register';
import Logout from './auth_component/Logout';
import ProblemEditor from './problem_creator_delete/ProblemEditor';
import ProblemCategoryList from './problemlist/ProblemCategoryList';
import ProblemCategoryPage from './problemlist/ProblemCategoryPage';
import SubmissionHistory from './problem_page_code_editor/SubmissionHistory';


function App() {
  return (
   <>
   {/* <FixedFooter/> */}
   <AuthProvider>
   <Routes>
        <Route path="/" element={<HomePage/>} />
        <Route path="/problem/page/:id" element={<ProblemPage/>} />
        <Route path="/category/:category" element={<ProblemCategoryPage/>} />
        <Route path="/problem/category" element={ <ProblemCategoryList/> } />
        <Route path="/register" element={<Register/>} />
        <Route path="/login" element={<Login/>} />
        <Route path="/verify-email" element={<VerifyEmail/>} />
         {/* Protected Routes */}
         <Route element={<ProtectedRoute roles={["USER"]} />}>
            {/* <Route path="/profile" element={<Profile />} /> */}
            
            <Route path="/submissions/history" element={ <SubmissionHistory/> }/>
            <Route path="/logout" element={<Logout/>}/>
          </Route>

          <Route element={<ProtectedRoute roles={["ADMIN"]} />}>
          <Route path="/editproblem/:id" element={<ProblemEditor/>} />
          <Route path="/deleteproblem" element={<DeleteProblem/>} />
          <Route path="/logout" element={<Logout/>}/>
          </Route>
        
      </Routes>

   </AuthProvider>
     
    
   </>
  );
}

export default App;
