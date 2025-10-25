import React, { useContext, useEffect, useRef, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import Button from "./button";
import { Toaster, toast } from "react-hot-toast";
import {
  FaEdit,
  FaEnvelope,
  FaPhone,
  FaMapMarkerAlt,
  FaBirthdayCake,
  FaLock,
  FaCamera,
  FaTrophy,
  FaCheckCircle,
  FaCode,
  FaUserAlt,
  FaLinkedin,
  FaGithub,
  FaFacebook,
  FaRegCopy,
} from "react-icons/fa";
import { motion } from "framer-motion";
import { AuthContext } from "../auth_component/AuthContext";

// Axios instance with token
const createAxiosInstance = () => {
  const token = localStorage.getItem("token");
  return axios.create({
    baseURL: process.env.REACT_APP_BACK_END_BASE_URL || "http://localhost:8080",
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
};

const Profile = () => {
  const auth = useContext(AuthContext);
  const {updateUserImage}=useContext(AuthContext);
  const user = auth?.user;
  const setUser = auth?.setUser;
  const darkMode = auth?.darkMode;

  const { username, userId } = useParams();
  const [profileUser, setProfileUser] = useState(null);
  const [formData, setFormData] = useState({});
  const [file, setFile] = useState(null);
  const [previewPic, setPreviewPic] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [showPicModal, setShowPicModal] = useState(false);
  const [showCoverModal, setShowCoverModal] = useState(false);

  const axiosInstance = createAxiosInstance();
  const didFetch = useRef(false);
  const isOwnProfile = !username || !userId || (user && Number(user.userId) === Number(userId));

  // Fetch profile
  useEffect(() => {
    if (didFetch.current) return;
    didFetch.current = true;
    const fetchProfile = async () => {
      try {
        const endpoint = isOwnProfile
          ? `/api/auth/v1/profile`
          : `/api/auth/v1/profile/${username}/${userId}`;
        const { data } = await axiosInstance.get(endpoint);
        const profileData = data.data || data;

        setProfileUser(profileData);
        setFormData(profileData);
        setPreviewPic(profileData.image_url || null);

        if (!username && setUser) setUser(profileData);
      } catch (err) {
        console.error("Profile fetch failed:", err);
        toast.error("Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [username, profileUser]);

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handlePicChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreviewPic(URL.createObjectURL(selectedFile));
    }
  };

  const handleUploadProfilePic = async () => {
    if (!file) return toast.error("Please select an image first!");
    try {
      setUploading(true);
      const formDataPic = new FormData();
      formDataPic.append("file", file);

      const { data } = await axiosInstance.put(`/api/auth/v1/update/profile-pic`, formDataPic);
      const imageUrl = data.data;
      if (imageUrl) {
        setPreviewPic(imageUrl);
        setProfileUser((prev) => ({ ...prev, image_url: imageUrl }));
        updateUserImage(imageUrl);
        toast.success("Profile picture updated!");
      }

      setFile(null);
    } catch (err) {
      console.error("Upload failed:", err);
      toast.error("Image upload failed!");
    } finally {
      setUploading(false);
    }
  };

  const handleSaveProfile = async () => {
    try {
      setSaving(true);
      const updatedData = { ...formData };

      const { data } = await axiosInstance.put(`/api/auth/v1/update`, updatedData);
      const updatedProfile = data.data || data;
      setProfileUser(updatedProfile);
      setFormData(updatedProfile);
      if (setUser) setUser(updatedProfile);
      toast.success("Profile information updated!");
    } catch (err) {
      console.error("Profile update failed:", err);
      toast.error("Profile update failed!");
    } finally {
      setIsEditing(false);
      setSaving(false);
      setLoading(false);
      didFetch.current = false;
    }
  };

  const handleSavePassword = async () => {
    if (newPassword !== confirmPassword) return toast.error("Passwords do not match!");
    try {
      await axiosInstance.put(`/api/auth/v1/update/password`, { newPassword, oldPassword,confirmPassword});
      toast.success("Password updated successfully!");
      setIsChangingPassword(false);
      setNewPassword("");
      setConfirmPassword("");
      setOldPassword("");
    } catch (err) {
      console.error(err);
      toast.error(err.message);
    }
  };

  if (loading || !profileUser) {
    return (
      <div
        className={`min-h-screen flex flex-col justify-center items-center ${
          darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-800"
        }`}
      >
        <div className="relative w-14 h-14">
          <div className="absolute inset-0 rounded-full border-4 border-transparent border-t-blue-500 animate-spin"></div>
          <div className="absolute inset-2 rounded-full border-4 border-transparent border-b-purple-500 animate-spin-slow"></div>
        </div>
        <p className="mt-5 text-lg font-medium">Loading profile...</p>
      </div>
    );
  }

  // Stats
  const stats = [
    { label: "Total Coins", value: profileUser?.total_present_coins ?? 0, icon: <FaTrophy /> },
    { label: "Problems Solved", value: profileUser?.total_problems_solved ?? 0, icon: <FaCheckCircle /> },
    { label: "Problems Attempted", value: profileUser?.total_problems_attempted ?? 0, icon: <FaCode /> },
    {
      label: "Status",
      value: profileUser?.activity_status ? "Active" : "Inactive",
      icon: (
        <div className="relative flex items-center justify-center">
          <span
            className={`absolute inline-flex h-5 w-5 rounded-full ${
              profileUser?.activity_status ? "bg-green-400 opacity-75 animate-ping" : "bg-gray-400 opacity-75"
            }`}
          ></span>
          <span
            className={`relative inline-flex rounded-full h-5 w-5 ${
              profileUser?.activity_status ? "bg-green-500" : "bg-gray-500"
            }`}
          ></span>
        </div>
      ),
    },
  ];

  // Extended info fields
  const infoFields = [
    { label: "Email", value: profileUser?.email, icon: <FaEnvelope /> },
    { label: "City", value: profileUser?.city, icon: <FaMapMarkerAlt /> },
    { label: "State", value: profileUser?.state, icon: <FaMapMarkerAlt /> },
    { label: "Country", value: profileUser?.country, icon: <FaMapMarkerAlt /> },
    { label: "Postal Code", value: profileUser?.postal_code, icon: <FaMapMarkerAlt /> },
    { label: "Date of Birth", value: profileUser?.date_of_birth, icon: <FaBirthdayCake /> },
    { label: "Gender", value: profileUser?.gender, icon: <FaUserAlt /> },
    { label: "LinkedIn", value: profileUser?.linkedin_url, icon: <FaLinkedin /> },
    { label: "GitHub", value: profileUser?.github_url, icon: <FaGithub /> },
    { label: "Facebook", value: profileUser?.facebook_url, icon: <FaFacebook /> },
  ];

  // Copy-to-clipboard
  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    toast.success("Copied to clipboard!");
  };

  return (
    <div className={`flex flex-col min-h-screen ${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"}`}>
      <Toaster position="top-right" />
      <NavBar />

      <main className="flex-grow p-6 overflow-y-auto scrollbar-thin scrollbar-thumb-indigo-400 scrollbar-track-gray-300">
        <div
          className={`max-w-6xl mx-auto rounded-3xl shadow-2xl overflow-hidden border-0 ${
            darkMode ? "bg-gray-800" : "bg-white"
          }`}
        >
          {/* Header with cover and profile */}
          <div className="relative h-52 border-b-0">
            <img
              src="https://res.cloudinary.com/dagkiubxf/image/upload/v1760829859/247f8cd6-72d9-4651-a88a-6425619f2747_ozvawf.jpg"
              alt="Cover"
              className="w-full h-full object-cover cursor-pointer"
              onClick={() => setShowCoverModal(true)}
            />
            <div className="absolute bottom-0 left-6 transform translate-y-1/2 flex items-center space-x-6">
              <div className="relative">
                <img
                  src={previewPic || `https://ui-avatars.com/api/?name=${profileUser?.username}`}
                  alt="Profile"
                  className="w-28 h-28 rounded-full border-4 border-white/20 object-cover shadow-lg cursor-pointer"
                  onClick={() => setShowPicModal(true)}
                />
                {isOwnProfile && (
                  <>
                    <label
                      htmlFor="profile-pic-input"
                      className="absolute bottom-1 right-1 bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-full cursor-pointer shadow-lg"
                    >
                      <FaCamera />
                    </label>
                    <input
                      id="profile-pic-input"
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={handlePicChange}
                    />
                  </>
                )}
              </div>
              <div className="flex flex-col justify-center mt-10">
                <h1 className={`text-4xl font-bold ${darkMode ? "text-white" : "text-gray-900"}`}>
                  {profileUser?.username}
                </h1>
              </div>
            </div>
          </div>

          {/* Stats */}
          <div className="mt-20 grid grid-cols-1 md:grid-cols-4 gap-6 px-6 py-6">
            {stats.map((s, i) => (
              <div
                key={i}
                className={`p-5 rounded-2xl shadow-md text-center border-0 ${
                  darkMode ? "bg-gray-700" : "bg-gray-100"
                }`}
              >
                <div className="text-blue-500 text-2xl mb-2">{s.icon}</div>
                <h3 className="text-sm font-semibold opacity-70">{s.label}</h3>
                <p className="text-xl font-bold">{s.value}</p>
              </div>
            ))}
          </div>
{/* Redesigned Personal Information - Single Card with Location Row */}
<section className="px-6 py-6 border-t border-gray-600/20">
  <h2 className={`text-2xl font-bold mb-4 ${darkMode ? "text-white" : "text-gray-900"}`}>
    Personal Information
  </h2>

  <div
    className={`p-6 rounded-2xl shadow-md transition-transform transform hover:-translate-y-1 hover:shadow-lg ${
      darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"
    }`}
  >
    {/* Upper Info Fields (Username, Email, Mobile, DOB, Gender, Social Links) */}
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
      {infoFields
        .filter((f) => !["City", "State", "Country", "Postal Code"].includes(f.label))
        .map(
          (field, idx) =>
            field.value && (
              <div
                key={idx}
                className={`flex items-center gap-3 p-3 rounded-lg shadow-sm transition-shadow hover:shadow-md ${
                  darkMode ? "bg-gray-700 text-white" : "bg-gray-100 text-gray-900"
                }`}
              >
                <div
                  className={`flex items-center justify-center w-10 h-10 rounded-full text-white ${
                    darkMode ? "bg-blue-500" : "bg-blue-600"
                  }`}
                >
                  {field.icon}
                </div>
                <div className="flex flex-col w-full">
                  <span className="font-semibold text-sm">{field.label}</span>
                  <div className="flex items-center justify-between mt-1 break-words">
                    {field.label === "Email" ? (
                      <a
                        href={`mailto:${field.value}`}
                        className="text-blue-500 hover:underline break-words"
                      >
                        {field.value}
                      </a>
                    ) : field.label === "Mobile" ? (
                      <a
                        href={`tel:${field.value}`}
                        className="text-blue-500 hover:underline break-words"
                      >
                        {field.value}
                      </a>
                    ) : ["LinkedIn", "GitHub", "Facebook"].includes(field.label) ? (
                      <a
                        href={field.value}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-2 text-blue-500 hover:underline break-words"
                      >
                        {field.label === "LinkedIn" && <FaLinkedin />}
                        {field.label === "GitHub" && <FaGithub />}
                        {field.label === "Facebook" && <FaFacebook />}
                        <span className="truncate max-w-[200px]">{field.value}</span>
                      </a>
                    ) : (
                      <span>{field.value}</span>
                    )}

                    {["Email", "Mobile", "LinkedIn", "GitHub", "Facebook"].includes(field.label) && (
                      <button
                        onClick={() => copyToClipboard(field.value)}
                        className="ml-2 p-1 text-gray-400 hover:text-gray-200 dark:hover:text-white transition-colors"
                        title="Copy"
                      >
                        <FaRegCopy />
                      </button>
                    )}
                  </div>
                </div>
              </div>
            )
        )}
    </div>

    {/* Divider */}
    <div className={`my-4 border-b ${darkMode ? "border-gray-500/30" : "border-gray-400/30"}`}></div>

    {/* Location Row */}
    <h3 className={`text-lg font-semibold mb-3 ${darkMode ? "text-white" : "text-gray-900"}`}>
      Location
    </h3>
    <div className="flex flex-wrap gap-4">
      {["City", "State", "Country", "Postal Code"].map((label) => {
        const field = infoFields.find((f) => f.label === label);
        return field && field.value ? (
          <div
            key={label}
            className={`flex-1 min-w-[120px] flex items-center gap-2 p-3 rounded-lg shadow-sm transition-shadow hover:shadow-md ${
              darkMode ? "bg-gray-700 text-white" : "bg-gray-100 text-gray-900"
            }`}
          >
            <div
              className={`flex items-center justify-center w-8 h-8 rounded-full text-white ${
                darkMode ? "bg-blue-500" : "bg-blue-600"
              }`}
            >
              {field.icon}
            </div>
            <div className="flex flex-col">
              <span className="text-sm font-semibold">{field.label}</span>
              <span className="text-base break-words">{field.value}</span>
            </div>
          </div>
        ) : null;
      })}
    </div>
  </div>
</section>

          {/* Buttons */}
          {isOwnProfile && (
            <div className="flex flex-wrap justify-end gap-3 px-6 pb-6 border-t border-gray-600/20">
              <Button onClick={() => setIsEditing(true)} className="bg-blue-600 text-white">
                <FaEdit className="inline mr-2" /> Edit Profile
              </Button>
              <Button onClick={() => setIsChangingPassword(true)} className="bg-purple-600 text-white">
                <FaLock className="inline mr-2" /> Change Password
              </Button>
              {file && (
                <Button
                  onClick={handleUploadProfilePic}
                  disabled={uploading}
                  className="bg-green-600 text-white"
                >
                  {uploading ? "Uploading..." : "Save Picture"}
                </Button>
              )}
            </div>
          )}
        </div>
      </main>

      <Footer />

      {/* Profile picture modal */}
      {showPicModal && (
        <ModalFullScreen onClose={() => setShowPicModal(false)}>
          <img
            src={previewPic}
            alt="Profile Full"
            className="max-h-[90%] max-w-[90%] rounded-xl shadow-2xl object-contain"
          />
        </ModalFullScreen>
      )}

      {/* Cover image modal */}
      {showCoverModal && (
        <ModalFullScreen onClose={() => setShowCoverModal(false)}>
          <img
            src="https://res.cloudinary.com/dagkiubxf/image/upload/v1760829754/b393fc23-c270-4456-a71d-216b7b04d1e6_hwrbnq.jpg"
            alt="Cover Full"
            className="max-h-[90%] max-w-[90%] rounded-xl shadow-2xl object-contain"
          />
        </ModalFullScreen>
      )}

      {/* Edit & Password Modals */}
      {isEditing && (
        <Modal darkMode={darkMode} title="Edit Profile" onClose={() => setIsEditing(false)}>
          <div className="space-y-4 max-h-[60vh] overflow-y-auto scrollbar-thin scrollbar-thumb-indigo-400 scrollbar-track-gray-300">
            {[
              "username",
              "email",
              "mobile",
              "city",
              "state",
              "country",
              "postal_code",
              "gender",
              "linkedin_url",
              "github_url",
              "facebook_url",
            ].map((f) => (
              <input
                key={f}
                name={f}
                value={formData[f] || ""}
                onChange={handleChange}
                placeholder={f.replace("_", " ").toUpperCase()}
                className="w-full p-3 border-none rounded-lg bg-gray-100 dark:bg-gray-700 text-black dark:text-white"
              />
            ))}
            <input
              type="date"
              name="date_of_birth"
              value={formData.date_of_birth || ""}
              onChange={handleChange}
              className="w-full p-3 border-none rounded-lg bg-gray-100 dark:bg-gray-700 text-black dark:text-white"
            />
          </div>
          <div className="flex justify-end mt-4 gap-3">
            <Button onClick={() => setIsEditing(false)} className="bg-gray-400 text-white">
              Cancel
            </Button>
            <Button onClick={handleSaveProfile} className="bg-blue-600 text-white" disabled={saving}>
              {saving ? "Saving..." : "Save"}
            </Button>
          </div>
        </Modal>
      )}

      {isChangingPassword && (
        <Modal darkMode={darkMode} title="Change Password" onClose={() => setIsChangingPassword(false)}>
          <input
            type="password"
            placeholder="Old Password"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            className="w-full p-3 mb-3 border-none rounded-lg bg-gray-100 dark:bg-gray-700 text-black dark:text-white"
          />
          <input
            type="password"
            placeholder="New Password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            className="w-full p-3 mb-3 border-none rounded-lg bg-gray-100 dark:bg-gray-700 text-black dark:text-white"
          />
          <input
            type="password"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className="w-full p-3 mb-3 border-none rounded-lg bg-gray-100 dark:bg-gray-700 text-black dark:text-white"
          />
          <div className="flex justify-end gap-3">
            <Button onClick={() => setIsChangingPassword(false)} className="bg-gray-400 text-white">
              Cancel
            </Button>
            <Button onClick={handleSavePassword} className="bg-purple-600 text-white">
              Save
            </Button>
          </div>
        </Modal>
      )}
    </div>
  );
};

// Info component (kept for reference if needed)
const Info = ({ icon, text }) => (
  <div className="flex items-center gap-3">
    <span className="text-blue-500">{icon}</span>
    <span>{text || "—"}</span>
  </div>
);

// Generic modal
const Modal = ({ darkMode, title, children, onClose }) => (
  <div
    className="fixed inset-0 flex items-center justify-center bg-black/60 z-50 backdrop-blur-sm"
    onClick={onClose}
  >
    <div
      className={`${
        darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"
      } w-full max-w-lg p-6 rounded-2xl shadow-2xl`}
      onClick={(e) => e.stopPropagation()}
    >
      <h2 className="text-2xl font-bold mb-4">{title}</h2>
      {children}
    </div>
  </div>
);

// Fullscreen modal
const ModalFullScreen = ({ children, onClose }) => (
  <div
    className="fixed inset-0 flex items-center justify-center bg-black/80 z-50 cursor-zoom-out"
    onClick={onClose}
  >
    {children}
  </div>
);

export default Profile;
