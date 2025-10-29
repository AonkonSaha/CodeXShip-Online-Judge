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
  FaEdit,
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

  const [showRoleModal, setShowRoleModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [roleName, setRoleName] = useState("");
  const [editForm, setEditForm] = useState({});

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
    setShowRoleModal(true);
  };

  const closeRoleModal = () => {
    setShowRoleModal(false);
    setSelectedUser(null);
  };

  const handleSaveRole = async () => {
    if (!roleName) return toast.error("Please select a role");
    try {
      await axios.post(
        `${baseURL}/api/role/v1/register`,
        { mobile: selectedUser.mobile, role_name: roleName },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      toast.success(`Role '${roleName}' assigned successfully`);
      fetchUsers();
      closeRoleModal();
    } catch (error) {
      console.error(error);
      toast.error("Failed to update role");
    }
  };

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

  const handleDeleteUser = async (user) => {
    if (!window.confirm(`Are you sure you want to delete ${user.username}?`))
      return;
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
    return new Date(dateStr).toLocaleString();
  };

  // 🟢 Edit User Modal Handlers
  const openEditModal = (user) => {
    setSelectedUser(user);
    setEditForm({
      username: user.username || "",
      email: user.email || "",
      mobile: user.mobile || "",
      country: user.country || "",
      gender: user.gender || "",
      state: user.state || "",
      postal_code: user.postal_code || "",
      total_present_coins: user.total_present_coins || 0,
      total_problems_solved: user.total_problems_solved || 0,
      total_problems_attempted: user.total_problems_attempted || 0,
      total_problems_wa: user.total_problems_wa || 0,
      total_problems_re: user.total_problems_re || 0,
      total_problems_tle: user.total_problems_tle || 0,
      total_problems_ce: user.total_problems_ce || 0,
      activity_status: user.activity_status || false,
      image_url: user.image_url || "",
    });
    setShowEditModal(true);
  };

  const closeEditModal = () => {
    setShowEditModal(false);
    setSelectedUser(null);
    setEditForm({});
  };

  const handleEditChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSaveEdit = async () => {
    try {
      await axios.put(
        `${baseURL}/api/auth/v2/update`,
        { ...editForm, mobile: selectedUser.mobile },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      toast.success("User updated successfully");
      fetchUsers();
      closeEditModal();
    } catch (error) {
      console.error(error);
      toast.error("Failed to update user");
    }
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
                    <td colSpan="16" className="py-6 text-center text-gray-500">
                      Loading users...
                    </td>
                  </tr>
                ) : users.length === 0 ? (
                  <tr>
                    <td colSpan="16" className="py-6 text-center text-gray-500">
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

                      <td className="py-3 px-4 text-center">{user.mobile}</td>
                      <td className="py-3 px-4 text-center">{user.country || "-"}</td>
                      <td className="py-3 px-4 text-center">{user.total_present_coins}</td>
                      <td className="py-3 px-4 text-center">{user.total_problems_solved}</td>
                      <td className="py-3 px-4 text-center">{user.total_problems_attempted}</td>
                      <td className="py-3 px-4 text-center">{user.total_problems_wa}</td>
                      <td className="py-3 px-4 text-center">{user.total_problems_re}</td>
                      <td className="py-3 px-4 text-center">{user.total_problems_tle}</td>
                      <td className="py-3 px-4 text-center">{user.total_problems_ce}</td>

                      <td className="py-3 px-4 text-center">
                        <span className="flex justify-center items-center">
                          <FaCircle
                            className={`text-sm ${
                              user.activity_status ? "text-green-500" : "text-red-500"
                            }`}
                          />
                          <span className="ml-2">{user.activity_status ? "Active" : "Inactive"}</span>
                        </span>
                      </td>

                      <td className="py-3 px-4 text-center">{formatDate(user.created_time)}</td>
                      <td className="py-3 px-4 text-center">{formatDate(user.updated_time)}</td>

                      <td className="py-3 px-4 text-center space-y-1">
                        {user.roles && user.roles.length > 0 ? (
                          user.roles.map((role, idx) => (
                            <div key={idx} className="flex justify-center items-center gap-2">
                              <span className="bg-indigo-100 text-indigo-700 px-2 py-1 rounded text-xs">{role}</span>
                              <button onClick={() => handleRemoveRole(user, role)} className="text-red-500 hover:text-red-700">
                                <FaUserMinus />
                              </button>
                            </div>
                          ))
                        ) : (
                          <span className="text-gray-500 text-sm">N/A</span>
                        )}
                      </td>

                      <td className="py-3 px-4 text-center flex justify-center gap-2">
                        <button
                          onClick={() => openRoleModal(user)}
                          className="flex items-center gap-1 bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded-lg transition"
                        >
                          <FaUserShield /> Set
                        </button>
                        <button
                          onClick={() => openEditModal(user)}
                          className="flex items-center gap-1 bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded-lg transition"
                        >
                          <FaEdit /> Edit
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
      {showRoleModal && selectedUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 backdrop-blur-sm">
          <div className={`w-96 rounded-xl shadow-lg p-6 relative ${darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-800"}`}>
            <button onClick={closeRoleModal} className={`absolute top-3 right-3 ${darkMode ? "text-gray-300 hover:text-gray-100" : "text-gray-500 hover:text-gray-700"}`}>
              <FaTimes size={18} />
            </button>
            <h3 className="text-xl font-semibold mb-4 text-center">
              Set Role for <span className="text-indigo-500">{selectedUser.username}</span>
            </h3>
            <label className="block mb-2">Select Role</label>
            <select
              value={roleName}
              onChange={(e) => setRoleName(e.target.value)}
              className={`w-full border rounded-lg px-3 py-2 mb-5 focus:ring-2 focus:ring-indigo-500 ${
                darkMode ? "bg-gray-700 border-gray-600 text-white" : "bg-white border-gray-300 text-gray-800"
              }`}
            >
              <option value="">-- Choose Role --</option>
              <option value="ADMIN">ADMIN</option>
              <option value="PROBLEM_EDITOR">PROBLEM_EDITOR</option>
              <option value="NORMAL_USER">NORMAL_USER</option>
            </select>
            <div className="flex justify-end gap-3">
              <button onClick={closeRoleModal} className={`px-4 py-2 rounded-lg ${darkMode ? "bg-gray-600 hover:bg-gray-500" : "bg-gray-300 hover:bg-gray-400"}`}>Cancel</button>
              <button onClick={handleSaveRole} className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"><FaCheck /> Save</button>
            </div>
          </div>
        </div>
      )}

{/* Edit Modal */}
{showEditModal && selectedUser && (
  <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-auto backdrop-blur-sm">
    <div
      className={`w-full max-w-4xl rounded-xl shadow-lg relative flex flex-col bg-white dark:bg-gray-800 text-gray-800 dark:text-white max-h-[90vh] overflow-hidden`}
    >
      {/* Close Button */}
      <button
        onClick={closeEditModal}
        className="absolute top-3 right-3 text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-white"
      >
        <FaTimes size={20} />
      </button>

      {/* Header */}
      <h3 className="text-2xl font-semibold text-center my-4">
        Edit User: <span className="text-indigo-500">{selectedUser.username}</span>
      </h3>

      {/* Modal Body */}
      <div className="flex flex-col md:flex-row overflow-auto px-6 py-4 gap-6 flex-grow">
        {/* Left Column - Basic Info */}
        <div className="flex flex-col md:w-1/2 gap-4">
          {/* Profile Image */}
          <div className="flex flex-col items-center">
            <img
              src={editForm.image_url || "https://res.cloudinary.com/dagkiubxf/image/upload/v1760908494/Default_Men_ujdzoj.png"}
              alt="User"
              className="w-24 h-24 rounded-full object-cover border border-gray-300 mb-2"
            />
            <label className="cursor-pointer bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm">
              Upload Image
              <input
                type="file"
                accept="image/*"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files[0];
                  if (file) {
                    const reader = new FileReader();
                    reader.onload = (ev) => setEditForm(prev => ({ ...prev, image_url: ev.target.result }));
                    reader.readAsDataURL(file);
                  }
                }}
              />
            </label>
          </div>

          {/* Basic Info */}
          <div className="space-y-3">
            <label>Username</label>
            <input
              name="username"
              value={editForm.username}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            />

            <label>Email</label>
            <input
              name="email"
              value={editForm.email}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            />

            <label>Mobile</label>
            <input
              name="mobile"
              value={editForm.mobile}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            />

            <label>Country</label>
            <input
              name="country"
              value={editForm.country}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            />

            <label>State</label>
            <input
              name="state"
              value={editForm.state || ""}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            />

            <label>Postal Code</label>
            <input
              name="postalCode"
              value={editForm.postal_code || ""}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            />

            <label>Gender</label>
            <select
              name="gender"
              value={editForm.gender || ""}
              onChange={handleEditChange}
              className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
            >
              <option value="">-- Select Gender --</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>

            <label className="flex items-center gap-2 mt-2">
              <input
                type="checkbox"
                name="activity_status"
                checked={editForm.activity_status}
                onChange={handleEditChange}
              />
              Active Status
            </label>
          </div>
        </div>

        {/* Right Column - Stats */}
        <div className="flex flex-col md:w-1/2 gap-3">
          <label>Coins</label>
          <input
            type="number"
            name="total_present_coins"
            value={editForm.total_present_coins}
            onChange={handleEditChange}
            className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
          />

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label>Solved</label>
              <input
                type="number"
                name="total_problems_solved"
                value={editForm.total_problems_solved}
                onChange={handleEditChange}
                className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
            <div>
              <label>Attempted</label>
              <input
                type="number"
                name="total_problems_attempted"
                value={editForm.total_problems_attempted}
                onChange={handleEditChange}
                className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label>WA</label>
              <input
                type="number"
                name="total_problems_wa"
                value={editForm.total_problems_wa}
                onChange={handleEditChange}
                className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
            <div>
              <label>RE</label>
              <input
                type="number"
                name="total_problems_re"
                value={editForm.total_problems_re}
                onChange={handleEditChange}
                className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label>TLE</label>
              <input
                type="number"
                name="total_problems_tle"
                value={editForm.total_problems_tle}
                onChange={handleEditChange}
                className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
            <div>
              <label>CE</label>
              <input
                type="number"
                name="total_problems_ce"
                value={editForm.total_problems_ce}
                onChange={handleEditChange}
                className="w-full border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Modal Footer */}
      <div className="flex justify-end gap-3 px-6 py-4 border-t border-gray-300 dark:border-gray-700">
        <button
          onClick={closeEditModal}
          className="px-4 py-2 rounded-lg bg-gray-300 dark:bg-gray-600 hover:bg-gray-400 dark:hover:bg-gray-500"
        >
          Cancel
        </button>
        <button
          onClick={handleSaveEdit}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
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
