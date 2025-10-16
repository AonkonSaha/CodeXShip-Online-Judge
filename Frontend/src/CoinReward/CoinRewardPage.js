import React, { useEffect, useState, useContext, useCallback } from "react";
import axios from "axios";
import { AuthContext } from "../auth_component/AuthContext";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { FaCoins, FaShoppingCart, FaPlus, FaGifts } from "react-icons/fa";
import { motion } from "framer-motion";

const COIN_TO_BDT_RATE = 1; // 1 coin = 1 BDT

export default function CoinRewardPage() {
  const { user, coins: initialCoins, darkMode, isAdmin } = useContext(AuthContext);
  const [balance, setBalance] = useState(initialCoins ?? 0);
  const [rewards, setRewards] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [shopLoading, setShopLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(12);

  // Buy modal
  const [buyOpen, setBuyOpen] = useState(false);
  const [buyAmount, setBuyAmount] = useState(100);
  const [purchaseId, setPurchaseId] = useState(null);
  const [paymentUrl, setPaymentUrl] = useState(null);
  const [verifyLoading, setVerifyLoading] = useState(false);

  // Redeem modal
  const [redeemOpen, setRedeemOpen] = useState(false);
  const [selectedReward, setSelectedReward] = useState(null);
  const [redeemLoading, setRedeemLoading] = useState(false);

  // Admin mode
  const [adminMode, setAdminMode] = useState(false);
  const [adminForm, setAdminForm] = useState({ title: "", price: 0, stock: 0, imageUrl: "", description: "" });
  const [adminLoading, setAdminLoading] = useState(false);

  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;
  const token = localStorage.getItem("token");

  // 🧠 Fetch all data including default merch
  const fetchAll = useCallback(async () => {
    setLoading(true);
    try {
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const [coinRes, rewardRes] = await Promise.all([
        axios.get(`${baseURL}/api/coin/get`, { headers }),
        axios.get(`${baseURL}/api/reward/list`, { headers }),
      ]);

      setBalance(coinRes.data.data?.balance ?? 0);
      setTransactions(coinRes.data.data?.transactions ?? []);

      // 🎽 Default merch items (always shown)
      const defaultRewards = [
        {
          id: "local-tshirt",
          title: "Official OJ T-Shirt",
          price: 800,
          stock: 999,
          imageUrl:
            "https://cdn.pixabay.com/photo/2017/10/10/21/46/t-shirt-2833545_1280.png",
          description: "Premium cotton T-shirt with your username printed on the sleeve.",
        },
        {
          id: "local-hoodie",
          title: "Official OJ Hoodie",
          price: 1500,
          stock: 999,
          imageUrl:
            "https://cdn.pixabay.com/photo/2014/12/21/23/28/hoodie-575590_1280.png",
          description: "Soft fleece hoodie — wear your coder pride in style.",
        },
      ];

      const backendRewards = rewardRes.data.data ?? [];
      const merged = [
        ...backendRewards,
        ...defaultRewards.filter(
          (d) => !backendRewards.some((r) => r.title === d.title)
        ),
      ];

      setRewards(merged);
    } catch (err) {
      console.error("Failed to load coin/shop data:", err);
    } finally {
      setLoading(false);
    }
  }, [baseURL, token]);

  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  useEffect(() => {
    if (initialCoins !== undefined && initialCoins !== null) setBalance(initialCoins);
  }, [initialCoins]);

  // ---------- Buy Coins ----------
  async function initiatePurchase() {
    if (!buyAmount || buyAmount <= 0) return alert("Enter a valid coin amount.");
    try {
      setShopLoading(true);
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
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
      console.error("Failed to initiate purchase:", err);
      alert("Failed to initiate purchase. Please try again.");
    } finally {
      setShopLoading(false);
    }
  }

  async function verifyPurchase() {
    if (!purchaseId) return alert("No purchase in progress.");
    try {
      setVerifyLoading(true);
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const res = await axios.post(`${baseURL}/api/coin/purchase/verify`, { purchaseId }, { headers });

      if (res.data.data?.success) {
        const newBalance = res.data.data?.newBalance ?? null;
        if (newBalance !== null) setBalance(newBalance);
        alert("Payment verified. Coins added!");
        await fetchAll();
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

  // ---------- Redeem ----------
  async function openRedeem(reward) {
    setSelectedReward(reward);
    setRedeemOpen(true);
  }

  async function confirmRedeem() {
    if (!selectedReward) return;
    if (balance < selectedReward.price) return alert("Not enough coins.");

    try {
      setRedeemLoading(true);
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const res = await axios.post(`${baseURL}/api/reward/redeem`, { rewardId: selectedReward.id }, { headers });

      if (res.data.data?.success) {
        alert("Redeemed successfully!");
        setBalance((b) => b - selectedReward.price);
        await fetchAll();
        setRedeemOpen(false);
        setSelectedReward(null);
      } else {
        alert("Redemption failed.");
      }
    } catch (err) {
      console.error("Redeem failed:", err);
      alert("Redeem failed. Try again.");
    } finally {
      setRedeemLoading(false);
    }
  }

  // ---------- Admin ----------
  async function adminAddReward(e) {
    e.preventDefault();
    if (!adminForm.title || !adminForm.price) return alert("Title and price are required.");
    try {
      setAdminLoading(true);
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      await axios.post(`${baseURL}/api/reward/add`, adminForm, { headers });
      alert("Reward added.");
      setAdminForm({ title: "", price: 0, stock: 0, imageUrl: "", description: "" });
      await fetchAll();
    } catch (err) {
      console.error("Admin add failed:", err);
      alert("Failed to add reward.");
    } finally {
      setAdminLoading(false);
    }
  }

  async function adminDeleteReward(id) {
    if (id.startsWith("local-")) return alert("Default merch cannot be deleted.");
    try {
      setAdminLoading(true);
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      await axios.delete(`${baseURL}/api/reward/${id}`, { headers });
      alert("Deleted.");
      await fetchAll();
    } catch (err) {
      console.error(err);
      alert("Failed to delete.");
    } finally {
      setAdminLoading(false);
    }
  }

  const bdtForCoins = (c) => (c * COIN_TO_BDT_RATE).toLocaleString();

  return (
    <div className={`${darkMode ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"} min-h-screen flex flex-col`}>
      <NavBar />

      <main className="container mx-auto px-4 py-8 flex-1">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Balance */}
          <section className="lg:col-span-1">
            <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className={`p-6 rounded-2xl shadow-md ${darkMode ? "bg-gray-800" : "bg-white"}`}>
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
                <div className="space-y-2">
                  <button onClick={() => setBuyOpen(true)} className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 flex items-center gap-2">
                    <FaShoppingCart /> Buy Coins
                  </button>
                </div>
              </div>
              <div className="mt-6 text-sm text-gray-500">
                Earn coins by solving problems or contests. Use them to redeem exclusive OJ merch!
              </div>
            </motion.div>

            {/* Transactions */}
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className={`mt-6 p-6 rounded-2xl ${darkMode ? "bg-gray-800" : "bg-white"} shadow-md`}>
              <div className="flex items-center justify-between">
                <h4 className="font-medium">Transactions</h4>
                <span className="text-sm text-gray-400">Recent</span>
              </div>
              <ul className="mt-4 space-y-3 max-h-64 overflow-auto">
                {transactions.length === 0 && <li className="text-sm text-gray-400">No transactions yet.</li>}
                {transactions.slice(0, 8).map((t) => (
                  <li key={t.id} className="flex items-center justify-between text-sm">
                    <div>
                      <div className="font-medium">{t.type}</div>
                      <div className="text-xs text-gray-400">{new Date(t.date).toLocaleString()}</div>
                    </div>
                    <div className={`font-semibold ${t.amount > 0 ? 'text-green-500' : 'text-red-500'}`}>{t.amount > 0 ? `+${t.amount}` : t.amount}</div>
                  </li>
                ))}
              </ul>
            </motion.div>
          </section>

          {/* Reward Store */}
          <section className="lg:col-span-2">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-2xl font-bold flex items-center gap-2"><FaGifts /> Reward Store</h2>
              <div className="flex items-center gap-3">
                <div className="text-sm text-gray-400">1 coin = {COIN_TO_BDT_RATE} BDT</div>
                {isAdmin && (
                  <button onClick={() => setAdminMode((s) => !s)} className="px-3 py-1 rounded-md bg-indigo-600 text-white flex items-center gap-2">
                    <FaPlus /> {adminMode ? 'Close' : 'Manage'}
                  </button>
                )}
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {rewards.map((r) => (
                <motion.article
                  key={r.id}
                  whileHover={{ scale: 1.02 }}
                  className={`p-4 rounded-xl shadow transition-all duration-200 ${
                    r.id.startsWith("local-")
                      ? darkMode
                        ? "bg-gradient-to-br from-yellow-500/20 to-orange-500/10 border border-yellow-500"
                        : "bg-gradient-to-br from-yellow-100 to-orange-50 border border-yellow-400"
                      : darkMode
                      ? "bg-gray-800"
                      : "bg-white"
                  }`}
                >
                  <img src={r.imageUrl || '/placeholder.png'} alt={r.title} className="w-full h-40 object-cover rounded-md mb-3" />
                  <h3 className="font-semibold text-lg">{r.title}</h3>
                  <p className="text-sm text-gray-400 mt-1 line-clamp-2">{r.description}</p>
                  <div className="mt-4 flex items-center justify-between">
                    <div className="font-bold">{r.price} coins</div>
                    <div className="text-xs text-gray-400">Stock: {r.stock}</div>
                  </div>

                  <div className="mt-4 flex gap-3">
                    <button
                      disabled={balance < r.price || r.stock <= 0}
                      onClick={() => openRedeem(r)}
                      className={`flex-1 px-3 py-2 rounded-md font-medium ${
                        balance < r.price || r.stock <= 0
                          ? 'bg-gray-400 cursor-not-allowed'
                          : 'bg-green-600 text-white hover:bg-green-500'
                      }`}
                    >
                      Redeem
                    </button>
                    {isAdmin && adminMode && !r.id.startsWith("local-") && (
                      <button onClick={() => adminDeleteReward(r.id)} className="px-3 py-2 rounded-md bg-red-600 text-white">Delete</button>
                    )}
                  </div>
                </motion.article>
              ))}
            </div>

            {/* Admin add/edit */}
            {isAdmin && adminMode && (
              <motion.form onSubmit={adminAddReward} className={`mt-6 p-4 rounded-xl shadow ${darkMode ? 'bg-gray-800' : 'bg-white'}`}>
                <h4 className="font-semibold mb-3">Add / Edit Reward</h4>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <input required value={adminForm.title} onChange={(e) => setAdminForm((s) => ({ ...s, title: e.target.value }))} placeholder="Title" className="p-2 rounded border" />
                  <input required type="number" value={adminForm.price} onChange={(e) => setAdminForm((s) => ({ ...s, price: Number(e.target.value) }))} placeholder="Price (coins)" className="p-2 rounded border" />
                  <input type="number" value={adminForm.stock} onChange={(e) => setAdminForm((s) => ({ ...s, stock: Number(e.target.value) }))} placeholder="Stock" className="p-2 rounded border" />
                  <input value={adminForm.imageUrl} onChange={(e) => setAdminForm((s) => ({ ...s, imageUrl: e.target.value }))} placeholder="Image URL" className="p-2 rounded border" />
                </div>
                <textarea value={adminForm.description} onChange={(e) => setAdminForm((s) => ({ ...s, description: e.target.value }))} placeholder="Description" className="mt-3 p-2 rounded border w-full" rows={3} />
                <div className="mt-3 flex gap-3">
                  <button type="submit" disabled={adminLoading} className="px-4 py-2 rounded bg-indigo-600 text-white">Save</button>
                  <button type="button" onClick={() => setAdminForm({ title: "", price: 0, stock: 0, imageUrl: "", description: "" })} className="px-4 py-2 rounded bg-gray-200">Reset</button>
                </div>
              </motion.form>
            )}
          </section>
        </div>

        {/* Buy Modal */}
        {buyOpen && (
          <Modal onClose={() => { setBuyOpen(false); setPurchaseId(null); setPaymentUrl(null); }} darkMode={darkMode}>
            <div className="p-4">
              <h3 className="text-lg font-semibold">Buy Coins</h3>
              <p className="text-sm text-gray-500 mt-2">Enter coins to purchase via bKash.</p>
              <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input type="number" value={buyAmount} onChange={(e) => setBuyAmount(Number(e.target.value))} className="p-2 rounded border" />
                <div className="p-2 rounded border flex items-center justify-between">Amount: <strong>{bdtForCoins(buyAmount)} BDT</strong></div>
              </div>
              <div className="mt-4 flex gap-3">
                {!purchaseId ? (
                  <button onClick={initiatePurchase} className={`px-4 py-2 rounded bg-blue-600 text-white ${shopLoading ? 'opacity-60 cursor-not-allowed' : ''}`}>Proceed to Pay</button>
                ) : (
                  <>
                    <a href={paymentUrl} target="_blank" rel="noreferrer" className="px-4 py-2 rounded bg-blue-500 text-white">Open bKash</a>
                    <button onClick={verifyPurchase} className={`px-4 py-2 rounded bg-green-600 text-white ${verifyLoading ? 'opacity-60 cursor-not-allowed' : ''}`}>Verify</button>
                  </>
                )}
              </div>
            </div>
          </Modal>
        )}

        {/* Redeem Modal */}
        {redeemOpen && selectedReward && (
          <Modal onClose={() => { setRedeemOpen(false); setSelectedReward(null); }} darkMode={darkMode}>
            <div className="p-4">
              <h3 className="text-lg font-semibold">Redeem Reward</h3>
              <p className="text-sm text-gray-500 mt-2">Confirm to redeem <strong>{selectedReward.title}</strong> for {selectedReward.price} coins.</p>
              <div className="mt-4 flex gap-3">
                <button disabled={redeemLoading} onClick={confirmRedeem} className="px-4 py-2 rounded bg-green-600 text-white">Confirm</button>
                <button onClick={() => setRedeemOpen(false)} className="px-4 py-2 rounded bg-gray-200">Cancel</button>
              </div>
            </div>
          </Modal>
        )}
      </main>

      <Footer />
    </div>
  );
}

// Generic modal
function Modal({ children, onClose, darkMode }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className={`w-full max-w-md mx-4 rounded-xl shadow-lg ${darkMode ? 'bg-gray-800 text-white' : 'bg-white text-gray-900'}`}>
        <div className="flex justify-end p-2">
          <button onClick={onClose} className="text-2xl leading-none">&times;</button>
        </div>
        <div className="px-6 pb-6">{children}</div>
      </div>
    </div>
  );
}
