import React, { useEffect, useState, useContext, useCallback, useRef } from "react";
import axios from "axios";
import { AuthContext } from "../auth_component/AuthContext";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { FaCoins, FaShoppingCart, FaArrowLeft, FaArrowRight } from "react-icons/fa";
import { motion, AnimatePresence } from "framer-motion";

const COIN_TO_BDT_RATE = 1; // 1 coin = 1 BDT

export default function CoinRewardPage() {
  const { user, coins: initialCoins, darkMode, minusUserCoins } = useContext(AuthContext);
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
  const [redeemSuccess, setRedeemSuccess] = useState(null); // { title: string }
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(6);
  const [totalPages, setTotalPages] = useState(1);
  const [search, setSearch] = useState("");
  const [toasts, setToasts] = useState([]);

  const observerRef = useRef();
  const lastProductRef = useRef(null);

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const bdtForCoins = (c) => (c * COIN_TO_BDT_RATE).toLocaleString();

  // 🔹 Toast helper
  const showToast = (message, type = "success") => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => setToasts((prev) => prev.filter((t) => t.id !== id)), 3000);
  };

  // 🔹 Fetch products (supports pagination)
  const fetchProducts = useCallback(async (reset = false) => {
    try {
      const res = await axios.get(`${baseURL}/api/v1/products`, {
        params: { page, size, search },
        headers,
      });
      const data = res.data?.data;
      if (reset) {
        setProducts(data?.content || []);
      } else {
        setProducts((prev) => [...prev, ...(data?.content || [])]);
      }
      setTotalPages(data?.totalPages || 1);
    } catch (err) {
      console.error("Failed to fetch products:", err);
      showToast("Failed to fetch products.", "error");
    }
  }, [baseURL, page, size, search]);

  // 🔹 Reset products when search changes
  useEffect(() => {
    if (token) {
      setPage(0);
      fetchProducts(true);
    }
  }, [search, token, fetchProducts]);

  // 🔹 Infinite scroll IntersectionObserver
  useEffect(() => {
    if (!token) return;
    if (observerRef.current) observerRef.current.disconnect();

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && page + 1 < totalPages) {
          setPage((prev) => prev + 1);
        }
      },
      { root: null, rootMargin: "0px", threshold: 1.0 }
    );

    if (lastProductRef.current) observerRef.current.observe(lastProductRef.current);

    return () => observerRef.current?.disconnect();
  }, [products, page, totalPages, token]);

  // 🔹 Fetch new page when page increments
  useEffect(() => {
    if (token && page > 0) fetchProducts();
  }, [page, token, fetchProducts]);

  // 🔹 Animate coins on redeem
  const animateCoins = () => {
    const id = Date.now();
    setFlyingCoins((prev) => [...prev, id]);
    setTimeout(() => setFlyingCoins((prev) => prev.filter((coinId) => coinId !== id)), 1000);
  };

  // 🔹 Redeem product with success animation
  const redeemProduct = async (id, price, title) => {
    if (balance < price) return showToast("Not enough coins to redeem this product.", "error");
    try {
      setRedeemLoading(id);
      await axios.post(`${baseURL}/api/v1/orders`, {}, {
         headers,
         params: {
          product_id:id
         }

        });

      // ✅ Trigger animations
      animateCoins();
      setRedeemSuccess({ title });
      setTimeout(() => setRedeemSuccess(null), 2000);

      // showToast(`Product "${title}" redeemed successfully!`);
      setBalance((prev) => prev - price);
      minusUserCoins(price);
      fetchProducts(true);
    } catch (err) {
      console.error("Redeem failed:", err);
      showToast("Error redeeming product.", "error");
    } finally {
      setRedeemLoading(null);
    }
  };

  // 🔹 Buy Coins
  async function initiatePurchase() {
    if (!buyAmount || buyAmount <= 0) return showToast("Enter a valid coin amount.", "error");
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
      showToast("Failed to initiate purchase.", "error");
    } finally {
      setShopLoading(false);
    }
  }

  async function verifyPurchase() {
    if (!purchaseId) return showToast("No purchase in progress.", "error");
    try {
      setVerifyLoading(true);
      const res = await axios.post(`${baseURL}/api/coin/purchase/verify`, { purchaseId }, { headers });
      if (res.data.data?.success) {
        const newBalance = res.data.data?.newBalance ?? null;
        if (newBalance !== null) setBalance(newBalance);
        showToast("Payment verified. Coins added!");
        setBuyOpen(false);
        setPurchaseId(null);
        setPaymentUrl(null);
      } else {
        showToast("Payment not verified yet. Please retry later.", "error");
      }
    } catch (err) {
      console.error("Verify failed:", err);
      showToast("Failed to verify payment.", "error");
    } finally {
      setVerifyLoading(false);
    }
  }

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"} min-h-screen flex flex-col`}>
      <NavBar />
      <main className="container mx-auto px-4 py-8 flex-1 relative">
        {/* 🔹 Toasts */}
        <div className="fixed top-4 right-4 flex flex-col gap-2 z-50">
          <AnimatePresence>
            {toasts.map((t) => (
              <motion.div
                key={t.id}
                initial={{ opacity: 0, x: 50 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 50 }}
                className={`px-4 py-2 rounded shadow-lg text-white font-medium ${t.type === "success" ? "bg-green-500" : "bg-red-500"}`}
              >
                {t.message}
              </motion.div>
            ))}
          </AnimatePresence>
        </div>

        {/* 🔹 Redeem Success Modal */}
        <AnimatePresence>
          {redeemSuccess && (
            <motion.div
              initial={{ opacity: 0, y: -50 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -50 }}
              className="fixed top-20 left-1/2 -translate-x-1/2 z-50 bg-green-600 text-white px-6 py-3 rounded-xl shadow-lg flex items-center gap-2"
            >
              <FaCoins className="w-5 h-5" />
              <span>Product "{redeemSuccess.title}" redeemed successfully!</span>
            </motion.div>
          )}
        </AnimatePresence>

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
            onClick={() => { setPage(0); fetchProducts(true); }}
            className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
          >
            Search
          </button>
        </div>

        {/* 🔹 Product Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.length > 0 ? (
            products.map((p, idx) => (
              <motion.div
                key={p.id}
                ref={idx === products.length - 1 ? lastProductRef : null}
                whileHover={{ scale: 1.05, boxShadow: "0 15px 25px rgba(0,0,0,0.3)" }}
                className={`relative flex flex-col items-center text-center p-6 rounded-3xl shadow-xl transition-transform duration-300 ${darkMode ? "bg-gray-800" : "bg-white"}`}
              >
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
                  onClick={() => redeemProduct(p.id, p.coins ?? p.price, p.title)}
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
