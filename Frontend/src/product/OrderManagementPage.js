import React, {
  useEffect,
  useState,
  useContext,
  useRef,
  useCallback,
} from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { FaTimesCircle, FaTruck, FaInfoCircle } from "react-icons/fa";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";
import { AuthContext } from "../auth_component/AuthContext";

const OrderManagementPage = () => {
  const { darkMode, isAdmin,plusUserCoins,minusUserCoins } = useContext(AuthContext);
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedOrder, setSelectedOrder] = useState(null);

  const observerRef = useRef(null);
  const isFetchingRef = useRef(false);

  // decode token (optional check)
  useEffect(() => {
    if (token) {
      try {
        jwtDecode(token);
      } catch {
        console.warn("Invalid token");
      }
    }
  }, [token]);

  // fetch orders
  const fetchOrders = useCallback(async () => {
    if (!hasMore || isFetchingRef.current ) return;
    isFetchingRef.current = true;
    setLoading(true);
    try {
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const res = await axios.get(`${baseURL}/api/v1/admin/orders`, {
        headers,
        params: { page, size: 8, search: search.trim() },
      });
      const newOrders = res.data.data?.content || [];
      if (!newOrders.length) {
        setHasMore(false);
        return;
      }
      const isLast = res.data?.data?.last ?? true;
      setOrders((prev) =>
        page === 0 ? newOrders : [...prev, ...newOrders]
      );
      setHasMore(!isLast);

    } catch (err) {
      console.error("Error fetching orders:", err.message);
      setError("Failed to load orders. Please try again later.");
      setHasMore(false);
    } finally {
      setLoading(false);
      isFetchingRef.current = false;
    }
  }, [baseURL, token, page, search, hasMore]);

  // reset data when search/status changes
  useEffect(() => {
    setOrders([]);
    setPage(0);
    setHasMore(true);
  }, [search, statusFilter]);

  // infinite scroll observer
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !isFetchingRef.current && hasMore) {
          setPage((prev) => prev + 1);
        }
      },
      { threshold: 1 }
    );
    if (observerRef.current) observer.observe(observerRef.current);
    return () => observer.disconnect();
  }, [hasMore,page]);

  useEffect(() => {
    fetchOrders();
  }, [page, fetchOrders]);

  // handle order action
  const handleAction = async (id,coins,status, action) => {
    if (!window.confirm(`Are you sure to ${action} this order?`)) return;
    try {
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      if(action==="delete"){
        await axios.delete(`${baseURL}/api/v1/admin/orders/${id}`, { headers });
        if(status!=="DECLINED"){
          plusUserCoins(coins);
        }
      }else{
      await axios.post(`${baseURL}/api/v1/admin/orders/${action}/${id}`, {}, { headers });
      if(action==='decline' && status!=='DECLINED'){
      plusUserCoins(coins);
      }else if(action==='ship' && status==='DECLINED'){
      minusUserCoins(coins);
      }
      }
      
      setOrders((prev) =>
        prev.map((o) =>
          o.order_id === id
            ? { ...o, status: action === "ship" ? "SHIPPED" : "DECLINED" }
            : o
        )
      );
    } catch (err) {
      console.error(err);
    }
  };

  // filter orders by status (frontend filter)
  const filteredOrders = statusFilter
    ? orders.filter((o) => o.status?.toLowerCase() === statusFilter.toLowerCase())
    : orders;

  return (
    <div
      className={`min-h-screen flex flex-col transition-colors duration-300 ${
        darkMode ? "bg-gray-900 text-gray-100" : "bg-gray-50 text-gray-900"
      }`}
    >
      <NavBar />
      <div className="container mx-auto px-6 py-6 flex-grow">
        {/* Header */}
        <div className="flex flex-col md:flex-row justify-between gap-4 mb-6">
          <h1 className="text-3xl font-extrabold">Order Management</h1>
          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              placeholder="Search by username or product..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className={`p-2 rounded-md border focus:ring-2 focus:ring-indigo-500 ${
                darkMode
                  ? "bg-gray-800 border-gray-700 text-white"
                  : "bg-white border-gray-300"
              }`}
            />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className={`p-2 rounded-md border ${
                darkMode
                  ? "bg-gray-800 border-gray-700 text-white"
                  : "bg-white border-gray-300"
              }`}
            >
              <option value="">All Status</option>
              <option value="Pending">Pending</option>
              <option value="Shipped">Shipped</option>
              <option value="Declined">Declined</option>
            </select>
          </div>
        </div>

        {/* Orders Table */}
        <div
          className={`overflow-x-auto rounded-xl shadow-md ${
            darkMode ? "bg-gray-800 border border-gray-700" : "bg-white"
          }`}
        >
          <table className="min-w-full text-sm">
            <thead
              className={`${
                darkMode ? "bg-gray-700" : "bg-gray-100"
              } text-gray-200 uppercase text-xs`}
            >
              <tr>
                <th className="px-4 py-3 text-left">Order ID</th>
                <th className="px-4 py-3 text-left">User</th>
                <th className="px-4 py-3 text-left">Product</th>
                <th className="px-4 py-3 text-left">Coins</th>
                <th className="px-4 py-3 text-left">Status</th>
                <th className="px-4 py-3 text-left">Order Date</th>
                <th className="px-4 py-3 text-center">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((order) => (
                <tr
                  key={order.order_id}
                  className={`border-t ${
                    darkMode ? "border-gray-700" : "border-gray-200"
                  } hover:bg-gray-100 dark:hover:bg-gray-700 transition`}
                >
                  <td className="px-4 py-3 font-medium">#{order.order_id}</td>
                  <td className="px-4 py-3">
                    <div className="font-semibold">{order.username}</div>
                    <div className="text-xs text-gray-500">{order.mobile}</div>
                    <div className="text-xs text-gray-500">
                      {order.country}, {order.city}
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <p className="font-semibold">{order.product_title}</p>
                    <p className="text-xs text-gray-500">{order.product_type}</p>
                    <p className="text-xs text-gray-400">
                      Qty: {order.num_of_products}
                    </p>
                  </td>
                  <td className="px-4 py-3 font-semibold text-indigo-600">
                    {order.coins} 💰
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-bold ${
                        order.status === "PENDING"
                          ? "bg-yellow-400 text-gray-900"
                          : order.status === "DECLINED"
                          ? "bg-red-500 text-white-500"
                          : order.status === "SHIPPED"
                          ? "bg-green-500 text-white"
                          : order.status === "CONFIRMED"? "bg-green-500 text-white" :"bg-green-500 text-white"
                      }`}
                    >
                      {order.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-xs">
                    {new Date(order.order_at).toLocaleString()}
                  </td>
                  <td className="px-4 py-3 text-center">
                    <div className="flex justify-center gap-2">
                      <button
                        onClick={() => setSelectedOrder(order)}
                        className="p-2 bg-blue-600 hover:bg-blue-500 rounded-full text-white"
                        title="View Details"
                      >
                        <FaInfoCircle />
                      </button>
                      {isAdmin && (
                        <>
                          <button
                            onClick={() => handleAction(order.order_id,order.coins,order.status, "ship")}
                            className="p-2 bg-green-600 hover:bg-green-500 rounded-full text-white"
                            title="Mark as Shipped"
                          >
                            <FaTruck />
                          </button>
                          <button
                            onClick={() =>
                              handleAction(order.order_id,order.coins,order.status, "decline")
                            }
                            className="p-2 bg-yellow-600 hover:bg-red-500 rounded-full text-white"
                            title="Decline"
                          >
                            
                            <FaTimesCircle />
                          </button>
                          <button
                            onClick={() =>
                              handleAction(order.order_id,order.coins,order.status, "delete")
                            }
                            className="p-2 bg-red-600 hover:bg-red-500 rounded-full text-white"
                            title="Delete"
                          >
                            
                         <FaTimesCircle />
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {loading && (
            <p className="text-center py-4 text-gray-400 animate-pulse">
              Loading more orders...
            </p>
          )}
          {!loading && orders.length === 0 && (
            <p className="text-center py-6 text-gray-400">No orders found.</p>
          )}
          {error && <p className="text-center text-red-500 py-4">{error}</p>}
          <div ref={observerRef} className="h-10"></div>
        </div>
      </div>

      {/* Order Detail Modal */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
          <div
            className={`max-w-lg w-full p-6 rounded-xl shadow-xl ${
              darkMode ? "bg-gray-800 text-white" : "bg-white"
            }`}
          >
            <h2 className="text-xl font-bold mb-4">Order Details</h2>
            <div className="space-y-2 text-sm">
              <p>
                <strong>Order ID:</strong> {selectedOrder.order_id}
              </p>
              <p>
                <strong>User:</strong> {selectedOrder.username} ({selectedOrder.user_id})
              </p>
              <p>
                <strong>Contact:</strong> {selectedOrder.mobile}
              </p>
              <p>
                <strong>Address:</strong> {selectedOrder.city}, {selectedOrder.state},{" "}
                {selectedOrder.country} ({selectedOrder.postal_code})
              </p>
              <p>
                <strong>Product:</strong> {selectedOrder.productTitle} (
                {selectedOrder.productType})
              </p>
              <p>
                <strong>Coins:</strong> {selectedOrder.coins}
              </p>
              <p>
                <strong>Status:</strong> {selectedOrder.status}
              </p>
              <p>
                <strong>Ordered At:</strong>{" "}
                {new Date(selectedOrder.order_at).toLocaleString()}
              </p>
              {selectedOrder.delivery_at && (
                <p>
                  <strong>Delivered At:</strong>{" "}
                  {new Date(selectedOrder.delivery_at).toLocaleString()}
                </p>
              )}
            </div>
            <div className="flex justify-end mt-5">
              <button
                onClick={() => setSelectedOrder(null)}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded-md text-white"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
      <Footer />
    </div>
  );
};

export default OrderManagementPage;
