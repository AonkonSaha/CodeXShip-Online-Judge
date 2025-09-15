import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { AuthContext } from "../auth_component/AuthContext";
import {
  FaEdit,
  FaEnvelope,
  FaPhone,
  FaMapMarkerAlt,
  FaBirthdayCake,
  FaLock,
  FaCamera,
  FaCode,
  FaTrophy,
  FaCheckCircle,
  FaClock,
} from "react-icons/fa";
import Button from "./button";
import Footer from "../NavBar_Footer/Footer";
import NavBar from "../NavBar_Footer/NavBarCus";

const Profile = () => {
  const { user, setUser, darkMode } = useContext(AuthContext);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [formData, setFormData] = useState({});
  const [previewPic, setPreviewPic] = useState(null);
  const [file, setFile] = useState(null);
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");


  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL || "http://localhost:8080";

  const axiosConfig = {
    headers: {
      Authorization: `Bearer ${token}`,
    }
  };

  // Fetch latest user data
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const { data } = await axios.get(
          `${baseURL}/api/auth/v1/get/user/details`,
          axiosConfig
        );
        
        setUser(data);
        setFormData(data);
        setPreviewPic(data.profilePic || data.image_url);
      } catch (err) {
        console.error(err);
      }
    };
    fetchUser();
  }, [setUser, baseURL, token]);

  if (!user) {
    return (
      <div className={`min-h-screen flex items-center justify-center ${darkMode ? "bg-gray-900" : "bg-gray-100"}`}>
        <p className={`text-xl ${darkMode ? "text-gray-300" : "text-gray-500"}`}>Please log in to view your profile.</p>
      </div>
    );
  }

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handlePicChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreviewPic(URL.createObjectURL(selectedFile));
    }
  };

  const handlePicUpload = async () => {
    if (!file) return null;
    try {
      setLoading(true);
      const formDataPic = new FormData();
      formDataPic.append("file", file);

      const { data } = await axios.post(
        `${baseURL}/api/users/upload-profile-pic`,
        formDataPic,
        axiosConfig
      );

      return data.image_url;
    } catch (err) {
      console.error(err);
      alert("Image upload failed");
      return null;
    } finally {
      setLoading(false);
    }
  };

  const handleSaveProfile = async () => {
    try {
      let profilePicUrl = formData.profilePic || previewPic;
      if (file) {
        const uploadedUrl = await handlePicUpload();
        if (uploadedUrl) profilePicUrl = uploadedUrl;
      }

      const updatedUser = { ...formData, profilePic: profilePicUrl };
      const { data } = await axios.put(
        `${baseURL}/api/users/update-profile`,
        updatedUser,
        axiosConfig
      );

      setUser(data);
      setFormData(data);
      setPreviewPic(data.profilePic);
      setFile(null);
      setIsEditing(false);
      alert("Profile updated successfully!");
    } catch (err) {
      console.error(err);
      alert("Profile update failed");
    }
  };

  const handleSavePassword = async () => {
    if (!newPassword || !confirmPassword) return alert("Please enter both fields");
    if (newPassword !== confirmPassword) return alert("Passwords do not match!");

    try {
      await axios.put(
        `${baseURL}/api/users/change-password`,
        { newPassword },
        axiosConfig
      );
      alert("Password updated successfully!");
      setIsChangingPassword(false);
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      console.error(err);
      alert("Password update failed");
    }
  };

  const stats = [
    { label: "Total Coins", value: user.TotalPresentCoins || 0, icon: <FaTrophy /> },
    { label: "Problems Solved", value: user.TotalProblemsSolved || 0, icon: <FaCheckCircle /> },
    { label: "Submissions", value: user.TotalSubmissions || 0, icon: <FaCode /> },
    { label: "Active Days", value: user.ActiveDays || 0, icon: <FaClock /> },
  ];

  return (
    <>
      <NavBar />
      <main className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"} min-h-screen p-6 transition-colors duration-300`}>
        <div className={`max-w-6xl mx-auto ${darkMode ? "bg-gray-800" : "bg-white"} shadow-2xl rounded-3xl overflow-hidden transition-colors duration-300`}>
          {/* Header */}
          <div className="relative bg-gradient-to-r from-blue-500 to-purple-600 h-52">
            <div className="absolute bottom-0 left-6 transform translate-y-1/2 flex items-center space-x-6">
              <div className="relative">
                <img
                  src={previewPic || "https://ui-avatars.com/api/?name=User&background=0D8ABC&color=fff&size=128"}
                  alt="Profile"
                  className="w-28 h-28 rounded-full border-4 border-white dark:border-gray-800 object-cover shadow-lg"
                />
                <label htmlFor="profile-pic-input" className="absolute bottom-1 right-1 bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-full shadow cursor-pointer">
                  <FaCamera />
                </label>
                <input type="file" id="profile-pic-input" accept="image/*" className="hidden" onChange={handlePicChange} />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-white">{user.username}</h1>
                <p className="text-white/90">{user.email}</p>
              </div>
            </div>
          </div>

          {/* Stats */}
          <div className="mt-20 px-6 py-6 grid grid-cols-1 md:grid-cols-4 gap-6">
            {stats.map((stat, idx) => (
              <div key={idx} className={`${darkMode ? "bg-gray-700" : "bg-gray-100"} p-5 rounded-2xl shadow-md flex flex-col items-center text-center transition-colors duration-300`}>
                <div className="text-blue-600 dark:text-blue-400 mb-2 text-2xl">{stat.icon}</div>
                <h3 className={`${darkMode ? "text-gray-300" : "text-gray-500"} text-sm font-semibold`}>{stat.label}</h3>
                <p className={`${darkMode ? "text-white" : "text-gray-900"} text-2xl font-bold`}>{stat.value}</p>
              </div>
            ))}
          </div>

          {/* Personal Info */}
          <section className={`mt-6 px-6 py-6 ${darkMode ? "bg-gray-700" : "bg-gray-100"} rounded-3xl shadow-md space-y-4 transition-colors duration-300`}>
            <h2 className={`text-xl font-bold ${darkMode ? "text-white" : "text-gray-900"}`}>Personal Information</h2>
            <div className="flex flex-col md:flex-row md:space-x-6 space-y-2 md:space-y-0 flex-wrap">
              <div className="flex items-center space-x-3"><FaEnvelope className="text-gray-500 dark:text-gray-300" /><span className={`${darkMode ? "text-gray-200" : "text-gray-700"}`}>{user.email}</span></div>
              {user.mobileNumber && <div className="flex items-center space-x-3"><FaPhone className="text-gray-500 dark:text-gray-300" /><span className={`${darkMode ? "text-gray-200" : "text-gray-700"}`}>{user.mobileNumber}</span></div>}
              {user.city && <div className="flex items-center space-x-3"><FaMapMarkerAlt className="text-gray-500 dark:text-gray-300" /><span className={`${darkMode ? "text-gray-200" : "text-gray-700"}`}>{`${user.city}${user.state ? `, ${user.state}` : ""}${user.country ? `, ${user.country}` : ""}`}</span></div>}
              {user.dateOfBirth && <div className="flex items-center space-x-3"><FaBirthdayCake className="text-gray-500 dark:text-gray-300" /><span className={`${darkMode ? "text-gray-200" : "text-gray-700"}`}>{user.dateOfBirth}</span></div>}
            </div>
          </section>

          {/* Social Links */}
          <section className={`mt-6 px-6 py-6 ${darkMode ? "bg-gray-700" : "bg-gray-100"} rounded-3xl shadow-md space-y-3 transition-colors duration-300`}>
            <h2 className={`text-xl font-bold ${darkMode ? "text-white" : "text-gray-900"}`}>Social Links</h2>
            <div className="flex flex-wrap gap-4">
              {user.facebookUrl && <Button as="a" href={user.facebookUrl} target="_blank" className="bg-blue-600 hover:bg-blue-700 text-white">Facebook</Button>}
              {user.linkedinUrl && <Button as="a" href={user.linkedinUrl} target="_blank" className="bg-blue-700 hover:bg-blue-800 text-white">LinkedIn</Button>}
              {user.githubUrl && <Button as="a" href={user.githubUrl} target="_blank" className="bg-gray-800 hover:bg-gray-900 text-white">GitHub</Button>}
            </div>
          </section>

          {/* Action Buttons */}
          <div className="mt-6 flex justify-end gap-3 px-6 pb-6">
            <Button onClick={() => setIsEditing(true)} className="bg-blue-600 hover:bg-blue-700 text-white shadow-lg"><FaEdit className="inline mr-2" /> Edit Profile</Button>
            <Button onClick={() => setIsChangingPassword(true)} className="bg-purple-600 hover:bg-purple-700 text-white shadow-lg"><FaLock className="inline mr-2" /> Change Password</Button>
            {file && <Button onClick={handleSaveProfile} disabled={loading} className="bg-green-600 hover:bg-green-700 text-white shadow-lg"><FaCamera className="inline mr-2" /> {loading ? "Saving..." : "Save Picture"}</Button>}
          </div>
        </div>

        {/* Modals */}
        {isEditing && (
          <Modal darkMode={darkMode} title="Edit Profile" onClose={() => setIsEditing(false)}>
            <div className="space-y-4">
              {["username", "email", "mobile_number", "city", "state", "country"].map((field) => (
                <input
                  key={field}
                  type={field === "email" ? "email" : "text"}
                  name={field}
                  value={formData.field }
                  onChange={handleChange}
                  placeholder={field.charAt(0).toUpperCase() + field.slice(1)}
                  className={`${darkMode ? "dark:bg-gray-700 text-white" : "bg-gray-100"} w-full p-3 border rounded-lg`}
                />
              ))}
              <input type="date" name="dateOfBirth" value={formData.date_of_birth || ""} onChange={handleChange} className={`${darkMode ? "dark:bg-gray-700 text-white" : "bg-gray-100"} w-full p-3 border rounded-lg`} />
            </div>
            <div className="flex justify-end mt-6 space-x-3">
              <Button onClick={() => setIsEditing(false)} className="bg-gray-400 hover:bg-gray-500 text-white">Cancel</Button>
              <Button onClick={handleSaveProfile} className="bg-blue-600 hover:bg-blue-700 text-white">Save</Button>
            </div>
          </Modal>
        )}

        {isChangingPassword && (
          <Modal darkMode={darkMode} title="Change Password" onClose={() => setIsChangingPassword(false)}>
            <input type="password" placeholder="New Password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} className={`${darkMode ? "dark:bg-gray-700 text-white" : "bg-gray-100"} w-full p-3 mb-3 border rounded-lg`} />
            <input type="password" placeholder="Confirm Password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} className={`${darkMode ? "dark:bg-gray-700 text-white" : "bg-gray-100"} w-full p-3 mb-3 border rounded-lg`} />
            <div className="flex justify-end space-x-3 mt-4">
              <Button onClick={() => setIsChangingPassword(false)} className="bg-gray-400 hover:bg-gray-500 text-white">Cancel</Button>
              <Button onClick={handleSavePassword} className="bg-purple-600 hover:bg-purple-700 text-white">Save</Button>
            </div>
          </Modal>
        )}
      </main>
      <Footer />
    </>
  );
};

// Modal component
const Modal = ({ darkMode, title, children, onClose }) => (
  <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50" onClick={onClose}>
    <div className={`${darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"} rounded-2xl shadow-xl w-full max-w-lg p-6 max-h-[90vh] overflow-y-auto transition-colors duration-300`} onClick={(e) => e.stopPropagation()}>
      <h2 className="text-2xl font-bold mb-4">{title}</h2>
      {children}
    </div>
  </div>
);

export default Profile;
