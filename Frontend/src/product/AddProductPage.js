import React, { useState, useContext } from "react";
import axios from "axios";
import { Toaster, toast } from "react-hot-toast";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";
import { motion } from "framer-motion";
import { FaCloudUploadAlt, FaCoins, FaBoxOpen, FaTag, FaTshirt } from "react-icons/fa";

const createAxiosInstance = () => {
  const token = localStorage.getItem("token");
  return axios.create({
    baseURL: process.env.REACT_APP_BACK_END_BASE_URL || "http://localhost:8080",
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
};

const AddProduct = () => {
  const { darkMode } = useContext(AuthContext);
  const axiosInstance = createAxiosInstance();

  const [product, setProduct] = useState({
    title: "",
    type: "",
    price: "",
    quantity: "",
    coins: "",
    description: "",
  });
  const [image, setImage] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setProduct({ ...product, [e.target.name]: e.target.value });

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    setImage(file);
    setPreview(URL.createObjectURL(file));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!product.title || !product.type || !product.price || !product.coins || !image) {
      return toast.error("Please fill in all required fields!");
    }

    try {
      setLoading(true);
      const formData = new FormData();
      Object.entries(product).forEach(([key, value]) => formData.append(key, value));
      formData.append("image_file", image);

      await axiosInstance.post("/api/v1/admin/products", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      toast.success("Product added successfully!");
      setProduct({
        title: "",
        type: "",
        price: "",
        quantity: "",
        coins: "",
        description: "",
      });
      setImage(null);
      setPreview(null);
    } catch (err) {
      console.error(err);
      toast.error("Failed to add product!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"} min-h-screen`}>
      <Toaster position="top-right" />
      <NavBar />

      <main className="flex flex-col lg:flex-row justify-center items-start py-10 px-6 gap-8">
        {/* Left: Add Product Form */}
        <motion.div
          initial={{ opacity: 0, x: -50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.6 }}
          className={`w-full lg:w-2/3 rounded-3xl shadow-2xl p-8 ${
            darkMode ? "bg-gray-800" : "bg-white"
          }`}
        >
          <h2 className="text-3xl font-bold mb-6 text-center flex justify-center items-center gap-2">
            <FaBoxOpen className="text-blue-500" /> Add New Product
          </h2>

          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Product Image Upload */}
            <div className="flex flex-col items-center">
              <label
                htmlFor="imageUpload"
                className={`w-40 h-40 border-2 border-dashed rounded-2xl flex flex-col items-center justify-center cursor-pointer transition-all ${
                  darkMode ? "border-gray-600 hover:border-blue-500" : "border-gray-300 hover:border-blue-600"
                }`}
              >
                {preview ? (
                  <img src={preview} alt="Preview" className="w-full h-full object-cover rounded-2xl" />
                ) : (
                  <div className="flex flex-col items-center text-center">
                    <FaCloudUploadAlt className="text-4xl text-blue-500 mb-2" />
                    <p className="text-sm opacity-70">Upload Product Image</p>
                  </div>
                )}
              </label>
              <input id="imageUpload" type="file" accept="image/*" onChange={handleImageChange} className="hidden" />
            </div>

            {/* Product Inputs */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
              <InputField name="title" value={product.title} onChange={handleChange} label="Product Title *" />
              <InputField name="type" value={product.type} onChange={handleChange} label="Product Type *" icon={<FaTshirt />} />
              <InputField name="price" value={product.price} onChange={handleChange} label="Price (BDT) *" type="number" />
              <InputField name="quantity" value={product.quantity} onChange={handleChange} label="Quantity" type="number" />
              <InputField name="coins" value={product.coins} onChange={handleChange} label="Coins Required *" type="number" icon={<FaCoins className="text-yellow-400" />} />
            </div>

            <div>
              <label className="block mb-2 font-semibold">Description</label>
              <textarea
                name="description"
                value={product.description}
                onChange={handleChange}
                rows={4}
                className={`w-full rounded-xl px-4 py-3 border focus:outline-none transition ${
                  darkMode
                    ? "bg-gray-700 border-gray-600 focus:border-blue-500 text-white"
                    : "bg-gray-100 border-gray-300 focus:border-blue-600"
                }`}
                placeholder="Write a short description about this product..."
              ></textarea>
            </div>

            <div className="flex justify-center">
              <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-xl text-lg font-semibold transition disabled:opacity-70 disabled:cursor-not-allowed"
              >
                {loading ? "Saving..." : "Add Product"}
              </button>
            </div>
          </form>
        </motion.div>

        {/* Right: Live Product Preview */}
        <motion.div
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
          className={`w-full lg:w-1/3 rounded-3xl shadow-2xl p-6 ${
            darkMode ? "bg-gray-800" : "bg-white"
          }`}
        >
          <h3 className="text-2xl font-semibold mb-4 text-center flex justify-center gap-2">
            <FaTag className="text-green-500" /> Live Preview
          </h3>

          <div
            className={`rounded-2xl overflow-hidden border ${
              darkMode ? "border-gray-700" : "border-gray-300"
            }`}
          >
            {preview ? (
              <img src={preview} alt="Product" className="w-full h-56 object-cover" />
            ) : (
              <div
                className={`w-full h-56 flex justify-center items-center ${
                  darkMode ? "bg-gray-700 text-gray-400" : "bg-gray-200 text-gray-500"
                }`}
              >
                <FaCloudUploadAlt className="text-4xl" />
              </div>
            )}

            <div className="p-4">
              <h4 className="text-xl font-bold mb-2">{product.title || "Product Title"}</h4>
              <p className="text-sm opacity-80 mb-2">
                {product.type || "Product Type"} • {product.quantity || 1} pcs
              </p>
              <p className="font-semibold text-blue-500 mb-2">{product.price ? `${product.price} BDT` : "Price TBD"}</p>
              <div className="flex items-center gap-1 text-yellow-400 mb-2">
                <FaCoins /> <span>{product.coins || 0} Coins</span>
              </div>
              <p className="text-sm opacity-70">{product.description || "Product description will appear here."}</p>
            </div>
          </div>
        </motion.div>
      </main>

      <Footer />
    </div>
  );
};

const InputField = ({ label, name, value, onChange, type = "text", icon }) => {
  const { darkMode } = useContext(AuthContext);
  return (
    <div>
      <label className="block mb-2 font-semibold">{label}</label>
      <div className="relative">
        {icon && <div className="absolute left-3 top-3 text-lg text-gray-400">{icon}</div>}
        <input
          name={name}
          type={type}
          value={value}
          onChange={onChange}
          className={`w-full rounded-xl px-4 py-3 border focus:outline-none transition ${
            icon ? "pl-10" : ""
          } ${
            darkMode
              ? "bg-gray-700 border-gray-600 focus:border-blue-500 text-white"
              : "bg-gray-100 border-gray-300 focus:border-blue-600"
          }`}
          placeholder={label}
        />
      </div>
    </div>
  );
};

export default AddProduct;
