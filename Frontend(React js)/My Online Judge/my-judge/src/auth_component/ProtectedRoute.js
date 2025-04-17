import { useContext } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { AuthContext } from "./AuthContext";

const ProtectedRoute = ({ roles }) => {
  const { user } = useContext(AuthContext);

  if (!user) return <Navigate to="/login" />;
  // alert(user.role);
  // alert(roles[0]);
  //  if (roles && !roles.includes(user.role)) return <Navigate to="/" />;

  return <Outlet/>;
};

export default ProtectedRoute;
