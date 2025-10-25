import React, { useEffect, useState, useContext } from "react";
import axios from "axios";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { Toaster, toast } from "react-hot-toast";
import {
  FaSearch,
  FaUserShield,
  FaTimes,
  FaCheck,
  FaCircle,
  FaTrashAlt,
  FaUserMinus,
} from "react-icons/fa";
import { MdArrowBack, MdArrowForward } from "react-icons/md";

const UsersPage = () => {
  const { darkMode } = useContext(AuthContext);
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [roleName, setRoleName] = useState("");
  const token = localStorage.getItem("token");

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const fetchUsers = async (pageNumber = page, searchTerm = search) => {
    try {
      setLoading(true);
      const response = await axios.get(
        `${baseURL}/api/auth/v1/get/users?page=${pageNumber}&size=${size}&search=${searchTerm}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const data = response.data.data;
      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error(error);
      toast.error("Failed to fetch users");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    fetchUsers(0, search);
  };

  const openRoleModal = (user) => {
    setSelectedUser(user);
    setRoleName("");
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedUser(null);
  };

  const handleSaveRole = async () => {
    if (!roleName) {
      toast.error("Please select a role");
      return;
    }
    try {
      await axios.post(
        `${baseURL}/api/role/v1/register`,
        { mobile: selectedUser.mobile, role_name: roleName },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      toast.success(`Role '${roleName}' assigned successfully`);
      fetchUsers();
      closeModal();
    } catch (error) {
      console.error(error);
      toast.error("Failed to update role");
    }
  };

  // 🟡 NEW: Remove role handler
  const handleRemoveRole = async (user, role) => {
    try {
      await axios.delete(`${baseURL}/api/role/v1/remove`, {
        headers: { Authorization: `Bearer ${token}` },
        data: { mobile: user.mobile, role_name: role },
      });
      toast.success(`Role '${role}' removed from ${user.username}`);
      fetchUsers();
    } catch (error) {
      console.error(error);
      toast.error("Failed to remove role");
    }
  };

  // 🔴 NEW: Delete user handler
  const handleDeleteUser = async (user) => {
    if (!window.confirm(`Are you sure you want to delete ${user.username}?`)) return;
    try {
      await axios.delete(`${baseURL}/api/auth/v1/delete/${user.mobile}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success(`User '${user.username}' deleted successfully`);
      fetchUsers();
    } catch (error) {
      console.error(error);
      toast.error("Failed to delete user");
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "-";
    const date = new Date(dateStr);
    return date.toLocaleString();
  };

  return (
    <div
      className={`min-h-screen flex flex-col ${
        darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-800"
      }`}
    >
      <NavBar />
      <Toaster position="top-right" />

      <div className="container mx-auto px-4 sm:px-6 lg:px-10 py-8 flex-grow">
        <div
          className={`rounded-2xl shadow-md p-6 ${
            darkMode
              ? "bg-gray-800 border border-gray-700"
              : "bg-white border border-gray-200"
          }`}
        >
          {/* Header + Search */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-6">
            <h2
              className={`text-2xl font-semibold ${
                darkMode ? "text-white" : "text-gray-800"
              }`}
            >
              Users Management
            </h2>

            <form
              onSubmit={handleSearch}
              className={`flex items-center rounded-lg px-3 py-2 mt-3 sm:mt-0 ${
                darkMode ? "bg-gray-700" : "bg-gray-100"
              }`}
            >
              <FaSearch
                className={`mr-2 ${
                  darkMode ? "text-gray-300" : "text-gray-500"
                }`}
              />
              <input
                type="text"
                placeholder="Search by username or email"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className={`bg-transparent outline-none w-52 ${
                  darkMode ? "text-white placeholder-gray-400" : "text-gray-800"
                }`}
              />
              <button
                type="submit"
                className="ml-3 bg-indigo-600 text-white px-3 py-1 rounded-lg hover:bg-indigo-700 transition"
              >
                Search
              </button>
            </form>
          </div>

          {/* Table */}
          <div className="overflow-x-auto">
            <table
              className={`min-w-full rounded-lg border ${
                darkMode ? "border-gray-700" : "border-gray-200"
              }`}
            >
              <thead
                className={`${
                  darkMode ? "bg-indigo-700" : "bg-indigo-600"
                } text-white`}
              >
                <tr>
                  <th className="py-3 px-4 text-left">#</th>
                  <th className="py-3 px-4 text-left">User</th>
                  <th className="py-3 px-4 text-center">Mobile</th>
                  <th className="py-3 px-4 text-center">Country</th>
                  <th className="py-3 px-4 text-center">Coins</th>
                  <th className="py-3 px-4 text-center">Solved</th>
                  <th className="py-3 px-4 text-center">Attempted</th>
                  <th className="py-3 px-4 text-center">WA</th>
                  <th className="py-3 px-4 text-center">RE</th>
                  <th className="py-3 px-4 text-center">TLE</th>
                  <th className="py-3 px-4 text-center">CE</th>
                  <th className="py-3 px-4 text-center">Status</th>
                  <th className="py-3 px-4 text-center">Created</th>
                  <th className="py-3 px-4 text-center">Updated</th>
                  <th className="py-3 px-4 text-center">Roles</th>
                  <th className="py-3 px-4 text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="15" className="py-6 text-center text-gray-500">
                      Loading users...
                    </td>
                  </tr>
                ) : users.length === 0 ? (
                  <tr>
                    <td colSpan="15" className="py-6 text-center text-gray-500">
                      No users found
                    </td>
                  </tr>
                ) : (
                  users.map((user, index) => (
                    <tr
                      key={index}
                      className={`border-t ${
                        darkMode
                          ? "border-gray-700 hover:bg-gray-700"
                          : "border-gray-200 hover:bg-gray-50"
                      }`}
                    >
                      <td className="py-3 px-4">{page * size + index + 1}</td>

                      {/* User Info */}
                      <td className="py-3 px-4 flex items-center gap-3">
                        <img
                          src={
                            user.image_url ||
                            "https://res.cloudinary.com/dagkiubxf/image/upload/v1760908494/Default_Men_ujdzoj.png"
                          }
                          alt="User"
                          className="w-10 h-10 rounded-full border border-gray-300 object-cover"
                        />
                        <div>
                          <p className="font-semibold">{user.username}</p>
                          <p className="text-sm text-gray-500">{user.email}</p>
                        </div>
                      </td>

                      <td className="py-3 px-4 text-center">
                        {user.mobile}
                      </td>

                      <td className="py-3 px-4 text-center">
                        {user.country || "-"}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_present_coins}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_problems_solved}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_problems_attempted}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_problems_wa}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_problems_re}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_problems_tle}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {user.total_problems_ce}
                      </td>

                      {/* Status */}
                      <td className="py-3 px-4 text-center">
                        <span className="flex justify-center items-center">
                          <FaCircle
                            className={`text-sm ${
                              user.activity_status
                                ? "text-green-500"
                                : "text-red-500"
                            }`}
                          />
                          <span className="ml-2">
                            {user.activity_status ? "Active" : "Inactive"}
                          </span>
                        </span>
                      </td>

                      {/* Dates */}
                      <td className="py-3 px-4 text-center">
                        {formatDate(user.created_time)}
                      </td>
                      <td className="py-3 px-4 text-center">
                        {formatDate(user.updated_time)}
                      </td>

                      {/* Roles */}
                      <td className="py-3 px-4 text-center space-y-1">
                        {user.roles && user.roles.length > 0 ? (
                          user.roles.map((role, idx) => (
                            <div
                              key={idx}
                              className="flex justify-center items-center gap-2"
                            >
                              <span className="bg-indigo-100 text-indigo-700 px-2 py-1 rounded text-xs">
                                {role}
                              </span>
                              <button
                                onClick={() => handleRemoveRole(user, role)}
                                className="text-red-500 hover:text-red-700"
                                title="Remove role"
                              >
                                <FaUserMinus />
                              </button>
                            </div>
                          ))
                        ) : (
                          <span className="text-gray-500 text-sm">N/A</span>
                        )}
                      </td>

                      {/* Actions */}
                      <td className="py-3 px-4 text-center flex justify-center gap-2">
                        <button
                          onClick={() => openRoleModal(user)}
                          className="flex items-center gap-1 bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded-lg transition"
                        >
                          <FaUserShield /> Set
                        </button>
                        <button
                          onClick={() => handleDeleteUser(user)}
                          className="flex items-center gap-1 bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded-lg transition"
                        >
                          <FaTrashAlt /> Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="flex justify-between items-center mt-6">
            <button
              disabled={page === 0}
              onClick={() => setPage((p) => p - 1)}
              className={`flex items-center gap-1 px-4 py-2 rounded-lg ${
                page === 0
                  ? "bg-gray-400/30 text-gray-400 cursor-not-allowed"
                  : "bg-indigo-600 text-white hover:bg-indigo-700"
              }`}
            >
              <MdArrowBack /> Prev
            </button>

            <span className={`${darkMode ? "text-gray-300" : "text-gray-700"}`}>
              Page {page + 1} of {totalPages || 1}
            </span>

            <button
              disabled={page + 1 >= totalPages}
              onClick={() => setPage((p) => p + 1)}
              className={`flex items-center gap-1 px-4 py-2 rounded-lg ${
                page + 1 >= totalPages
                  ? "bg-gray-400/30 text-gray-400 cursor-not-allowed"
                  : "bg-indigo-600 text-white hover:bg-indigo-700"
              }`}
            >
              Next <MdArrowForward />
            </button>
          </div>
        </div>
      </div>

      <Footer />

      {/* Role Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 backdrop-blur-sm">
          <div
            className={`w-96 rounded-xl shadow-lg p-6 relative ${
              darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-800"
            }`}
          >
            <button
              onClick={closeModal}
              className={`absolute top-3 right-3 ${
                darkMode
                  ? "text-gray-300 hover:text-gray-100"
                  : "text-gray-500 hover:text-gray-700"
              }`}
            >
              <FaTimes size={18} />
            </button>
            <h3 className="text-xl font-semibold mb-4 text-center">
              Set Role for{" "}
              <span className="text-indigo-500">{selectedUser.username}</span>
            </h3>

            <label className="block mb-2">Select Role</label>
            <select
              value={roleName}
              onChange={(e) => setRoleName(e.target.value)}
              className={`w-full border rounded-lg px-3 py-2 mb-5 focus:ring-2 focus:ring-indigo-500 ${
                darkMode
                  ? "bg-gray-700 border-gray-600 text-white"
                  : "bg-white border-gray-300 text-gray-800"
              }`}
            >
              <option value="">-- Choose Role --</option>
              <option value="ADMIN">ADMIN</option>
              <option value="NORMAL_USER">NORMAL_USER</option>
            </select>

            <div className="flex justify-end gap-3">
              <button
                onClick={closeModal}
                className={`px-4 py-2 rounded-lg ${
                  darkMode
                    ? "bg-gray-600 hover:bg-gray-500"
                    : "bg-gray-300 hover:bg-gray-400"
                }`}
              >
                Cancel
              </button>
              <button
                onClick={handleSaveRole}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
              >
                <FaCheck /> Save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default UsersPage;
