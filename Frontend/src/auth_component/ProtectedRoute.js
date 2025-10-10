// src/auth_component/ProtectedRoute.js
import { useContext } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { AuthContext } from "./AuthContext";

// helper to normalize roles
const normalize = (r) => String(r || "").toUpperCase().replace(/^ROLE_/, "");

const ProtectedRoute = ({ roles = [] }) => {
  const { user, loading } = useContext(AuthContext);
  const location = useLocation();

  // Wait for auth state initialization
  if (loading) return null; // or <div>Loading...</div>

  // Not authenticated
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // No roles required => any logged-in user can access
  if (roles.length === 0) return <Outlet />;

  // Normalize user roles
  const userRoles = Array.isArray(user.roles)
    ? user.roles.map(normalize)
    : [normalize(user.roles)];

  const requiredRoles = roles.map(normalize);

  // Allow if any match
  const allowed = requiredRoles.some((r) => userRoles.includes(r));

  if (!allowed) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;
