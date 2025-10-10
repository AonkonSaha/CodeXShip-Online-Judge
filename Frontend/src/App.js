import './App.css';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './auth_component/AuthContext';

import HomePage from './home/home';
import ProblemPage from './problem_page_code_editor/ProblemPage';
import DeleteProblem from './problem_creator_delete/DeleteProblem';
import VerifyEmail from './VerifyEmail';
import ProtectedRoute from './auth_component/ProtectedRoute';
import Login from './auth_component/Login';
import Register from './auth_component/Register';
import Logout from './auth_component/Logout';
import ProblemEditor from './problem_creator_delete/ProblemEditor';
import ProblemCategoryList from './problemlist/ProblemCategoryList';
import ProblemCategoryPage from './problemlist/ProblemCategoryPage';
import SubmissionHistory from './problem_page_code_editor/SubmissionHistory';
import Profile from './components/profile';
import SubmissionResult from './problem_page_code_editor/SubmissionResult';
import SubmissionsPage from './problem_page_code_editor/SubmissionPage';

function App() {
  return (
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/problem/page/:id" element={<ProblemPage />} />
          <Route path="/category/:category" element={<ProblemCategoryPage />} />
          <Route path="/problem/category" element={<ProblemCategoryList />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/verify-email" element={<VerifyEmail />} />

          {/* ================= Protected Routes ================= */}

          {/* Shared access for all authenticated users */}
          <Route element={<ProtectedRoute roles={["NORMAL_USER", "CONTEST_USER", "ADMIN"]} />}>
            <Route path="/submission" element={<SubmissionsPage />} />
            <Route path="/submission-result" element={<SubmissionResult />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/submissions/history" element={<SubmissionHistory />} />
            <Route path="/logout" element={<Logout />} />
          </Route>

          {/* Contest users + Admin can later share contest-only routes */}
          <Route element={<ProtectedRoute roles={["CONTEST_USER", "ADMIN"]} />}>
            {/* Add contest-only pages here */}
          </Route>

          {/* Admin-only routes */}
          <Route element={<ProtectedRoute roles={["ADMIN"]} />}>
            <Route path="/editproblem/:id" element={<ProblemEditor />} />
            <Route path="/deleteproblem" element={<DeleteProblem />} />
          </Route>
        </Routes>
      </AuthProvider>
  );
}

export default App;
