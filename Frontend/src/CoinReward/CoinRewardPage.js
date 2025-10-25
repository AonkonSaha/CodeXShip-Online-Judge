import React, { useEffect, useState, useContext, useCallback } from "react";
import axios from "axios";
import { AuthContext } from "../auth_component/AuthContext";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { FaCoins, FaShoppingCart, FaArrowLeft, FaArrowRight } from "react-icons/fa";
import { motion } from "framer-motion";

const COIN_TO_BDT_RATE = 1; // 1 coin = 1 BDT

export default function CoinRewardPage() {
  const { user, coins: initialCoins, darkMode } = useContext(AuthContext);
  const [balance, setBalance] = useState(initialCoins ?? 0);
  const [buyOpen, setBuyOpen] = useState(false);
  const [buyAmount, setBuyAmount] = useState(100);
  const [shopLoading, setShopLoading] = useState(false);
  const [verifyLoading, setVerifyLoading] = useState(false);
  const [purchaseId, setPurchaseId] = useState(null);
  const [paymentUrl, setPaymentUrl] = useState(null);
  const [products, setProducts] = useState([]);
  const [redeemLoading, setRedeemLoading] = useState(null);
  const [flyingCoins, setFlyingCoins] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(6);
  const [totalPages, setTotalPages] = useState(1);
  const [search, setSearch] = useState("");

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const bdtForCoins = (c) => (c * COIN_TO_BDT_RATE).toLocaleString();

  // 🔹 Fetch products with pagination and filter
  const fetchProducts = useCallback(async () => {
    try {
      const res = await axios.get(`${baseURL}/api/product/v1/get`, {
        params: { page, size, search },
        headers,
      });
      const data = res.data?.data;
      setProducts(data?.content || []);
      setTotalPages(data?.totalPages || 1);
    } catch (err) {
      console.error("Failed to fetch products:", err);
    }
  }, [baseURL, page, size, search]);

  useEffect(() => {
    if (token) fetchProducts();
  }, [fetchProducts, token]);

  // 🔹 Animate coins on redeem
  const animateCoins = () => {
    const id = Date.now();
    setFlyingCoins((prev) => [...prev, id]);
    setTimeout(() => {
      setFlyingCoins((prev) => prev.filter((coinId) => coinId !== id));
    }, 1000);
  };

  // 🔹 Redeem product
  const redeemProduct = async (id, price) => {
    if (balance < price) return alert("Not enough coins to redeem this product.");
    try {
      setRedeemLoading(id);
      await axios.post(`${baseURL}/api/product/v1/buy/${id}`, {}, { headers });
      animateCoins();
      alert("Product redeemed successfully!");
      setBalance((prev) => prev - price);
      fetchProducts();
    } catch (err) {
      console.error("Redeem failed:", err);
      alert("Error redeeming product.");
    } finally {
      setRedeemLoading(null);
    }
  };

  // 🔹 Buy Coins
  async function initiatePurchase() {
    if (!buyAmount || buyAmount <= 0) return alert("Enter a valid coin amount.");
    try {
      setShopLoading(true);
      const amountBDT = buyAmount * COIN_TO_BDT_RATE;
      const res = await axios.post(
        `${baseURL}/api/coin/purchase`,
        { coins: buyAmount, amount: amountBDT, gateway: "BKASH" },
        { headers }
      );
      const { purchaseId: pid, paymentUrl: url } = res.data.data || {};
      setPurchaseId(pid);
      setPaymentUrl(url);
      if (url) window.open(url, "_blank");
      setBuyOpen(true);
    } catch (err) {
      console.error("Purchase failed:", err);
      alert("Failed to initiate purchase.");
    } finally {
      setShopLoading(false);
    }
  }

  async function verifyPurchase() {
    if (!purchaseId) return alert("No purchase in progress.");
    try {
      setVerifyLoading(true);
      const res = await axios.post(`${baseURL}/api/coin/purchase/verify`, { purchaseId }, { headers });
      if (res.data.data?.success) {
        const newBalance = res.data.data?.newBalance ?? null;
        if (newBalance !== null) setBalance(newBalance);
        alert("Payment verified. Coins added!");
        setBuyOpen(false);
        setPurchaseId(null);
        setPaymentUrl(null);
      } else {
        alert("Payment not verified yet. Please retry later.");
      }
    } catch (err) {
      console.error("Verify failed:", err);
      alert("Failed to verify payment.");
    } finally {
      setVerifyLoading(false);
    }
  }

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"} min-h-screen flex flex-col`}>
      <NavBar />
      <main className="container mx-auto px-4 py-8 flex-1 relative">
        {/* 🔹 Balance Section */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className={`p-6 rounded-2xl shadow-md mb-8 ${darkMode ? "bg-gray-800" : "bg-white"}`}
        >
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-medium text-gray-400">Your Balance</h3>
              <div className="flex items-center gap-3 mt-3">
                <div className="flex items-center gap-2 bg-yellow-400 text-gray-900 px-3 py-2 rounded-full font-bold text-xl">
                  <FaCoins /> {balance}
                </div>
                <div className="text-sm text-gray-400">Coins</div>
              </div>
            </div>
            <button
              onClick={() => setBuyOpen(true)}
              className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 flex items-center gap-2"
            >
              <FaShoppingCart /> Buy Coins
            </button>
          </div>
          <p className="mt-4 text-sm text-gray-400">
            Earn coins by solving challenges. Use them to redeem official CodeXShip merch!
          </p>
        </motion.div>

        {/* 🔹 Filter/Search */}
        <div className="flex flex-col sm:flex-row items-center gap-4 mb-6">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search products..."
            className="p-2 rounded border w-full sm:w-1/3 text-black"
          />
          <button
            onClick={() => { setPage(0); fetchProducts(); }}
            className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
          >
            Search
          </button>
        </div>

        {/* 🔹 Product Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.length > 0 ? (
            products.map((p) => (
              <motion.div
                key={p.id}
                whileHover={{ scale: 1.05, boxShadow: "0 15px 25px rgba(0,0,0,0.3)" }}
                className={`relative flex flex-col items-center text-center p-6 rounded-3xl shadow-xl transition-transform duration-300 ${
                  darkMode ? "bg-gray-800" : "bg-white"
                }`}
              >
                {/* Badges */}
                <div className="absolute top-4 right-4 flex flex-col gap-1">
                  {p.isNew && <span className="px-2 py-1 rounded-full bg-blue-500 text-white text-xs font-semibold">New</span>}
                  {p.isPopular && <span className="px-2 py-1 rounded-full bg-yellow-400 text-gray-900 text-xs font-semibold">Popular</span>}
                  {p.isLimited && <span className="px-2 py-1 rounded-full bg-red-500 text-white text-xs font-semibold">Limited</span>}
                </div>

                <div className="w-full h-52 mb-4">
                  <img src={p.image_url} alt={p.title} className="w-full h-full object-contain rounded-xl" />
                </div>

                <h3 className="text-xl font-semibold mb-1">{p.title}</h3>
                <p className="text-sm text-gray-400 mb-3">{p.description || "No description available."}</p>
                <div className="font-bold text-lg mb-3">{p.coins ?? p.price} coins</div>

                <motion.button
                  onClick={() => redeemProduct(p.id, p.coins ?? p.price)}
                  disabled={balance < (p.coins ?? p.price) || redeemLoading === p.id}
                  className={`px-6 py-2 rounded-full font-medium transition shadow-lg ${
                    balance < (p.coins ?? p.price)
                      ? "bg-gray-400 cursor-not-allowed"
                      : redeemLoading === p.id
                      ? "bg-green-400 cursor-wait"
                      : "bg-green-600 text-white hover:bg-green-500"
                  }`}
                  whileTap={{ scale: 0.95 }}
                >
                  {redeemLoading === p.id ? "Processing..." : "Redeem"}
                </motion.button>
              </motion.div>
            ))
          ) : (
            <p className="text-gray-500 text-center col-span-full">No products available right now.</p>
          )}
        </div>

        {/* 🔹 Pagination */}
        <div className="flex justify-center items-center gap-4 mt-6">
          <button
            onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
            disabled={page === 0}
            className="px-3 py-1 rounded bg-gray-500 text-white disabled:opacity-50"
          >
            <FaArrowLeft /> Prev
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
            disabled={page + 1 >= totalPages}
            className="px-3 py-1 rounded bg-gray-500 text-white disabled:opacity-50"
          >
            Next <FaArrowRight />
          </button>
        </div>

        {/* 🔹 Buy Coins Modal */}
        {buyOpen && (
          <Modal onClose={() => setBuyOpen(false)} darkMode={darkMode}>
            <div className="p-4">
              <h3 className="text-lg font-semibold">Buy Coins</h3>
              <p className="text-sm text-gray-500 mt-2">Enter the number of coins to purchase via bKash.</p>
              <div className="mt-4 flex flex-col sm:flex-row gap-3">
                <input
                  type="number"
                  value={buyAmount}
                  onChange={(e) => setBuyAmount(Number(e.target.value))}
                  className="p-2 rounded border text-black w-full sm:w-1/2"
                />
                <div className="p-2 rounded border flex items-center justify-between w-full sm:w-1/2">
                  Amount: <strong>{bdtForCoins(buyAmount)} BDT</strong>
                </div>
              </div>
              <div className="mt-4 flex gap-3">
                {!purchaseId ? (
                  <button
                    onClick={initiatePurchase}
                    className={`px-4 py-2 rounded bg-blue-600 text-white ${shopLoading ? "opacity-60 cursor-not-allowed" : ""}`}
                  >
                    Proceed to Pay
                  </button>
                ) : (
                  <>
                    <a href={paymentUrl} target="_blank" rel="noreferrer" className="px-4 py-2 rounded bg-blue-500 text-white">
                      Open bKash
                    </a>
                    <button
                      onClick={verifyPurchase}
                      className={`px-4 py-2 rounded bg-green-600 text-white ${verifyLoading ? "opacity-60 cursor-not-allowed" : ""}`}
                    >
                      Verify Payment
                    </button>
                  </>
                )}
              </div>
            </div>
          </Modal>
        )}

        {/* 🔹 Flying Coins Animation */}
        {flyingCoins.map((id) => (
          <motion.div
            key={id}
            initial={{ top: "50%", left: "50%", scale: 1, opacity: 1 }}
            animate={{ top: "5%", left: "90%", scale: 0.3, opacity: 0 }}
            transition={{ duration: 1, ease: "easeInOut" }}
            className="absolute w-6 h-6 bg-yellow-400 rounded-full shadow-xl flex items-center justify-center z-50"
          >
            <FaCoins className="text-white w-4 h-4" />
          </motion.div>
        ))}
      </main>
      <Footer />
    </div>
  );
}

// 🔹 Reusable Modal Component
function Modal({ children, onClose, darkMode }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className={`w-full max-w-md mx-4 rounded-2xl shadow-lg ${darkMode ? "bg-gray-800 text-white" : "bg-white text-gray-900"}`}>
        <div className="flex justify-end p-2">
          <button onClick={onClose} className="text-2xl leading-none">&times;</button>
        </div>
        <div className="px-6 pb-6">{children}</div>
      </div>
    </div>
  );
}
