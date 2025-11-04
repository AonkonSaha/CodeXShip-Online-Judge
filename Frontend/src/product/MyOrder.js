import React, { useEffect, useState, useRef, useCallback, useContext } from "react";
import axios from "axios";
import { FaCoins, FaBoxOpen, FaClock } from "react-icons/fa";
import { AuthContext } from "../auth_component/AuthContext";
import NavBar from "../NavBar_Footer/NavBarCus";
import Footer from "../NavBar_Footer/Footer";

const MyOrderPage = () => {
  const { darkMode } = useContext(AuthContext);
  const token = localStorage.getItem("token");
  const baseURL = process.env.REACT_APP_BACK_END_BASE_URL;

  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const isFetchingRef = useRef(false);
  const observerRef = useRef(null);
  const observerInstance = useRef(null);

  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  /** ✅ Fetch Orders (continuous pagination) */
  const fetchOrders = useCallback(async () => {
    if (!hasMore || isFetchingRef.current) return;

    isFetchingRef.current = true;
    setLoading(true);

    try {
      const res = await axios.get(`${baseURL}/api/product/v1/history/order`, {
        headers,
        params: { page, size: 8 },
      });

      const content = res.data?.data?.content || [];
      const isLast = res.data?.data?.last ?? true;

      // ✅ Append fetched data
      setOrders((prev) => [...prev, ...content]);
      setHasMore(!isLast);

      // ✅ Increment page only if more data available
      if (!isLast) setPage((prev) => prev + 1);
    } catch (err) {
      console.error("Error fetching orders:", err);
      setError("Failed to load orders.");
      setHasMore(false);
    } finally {
      setLoading(false);
      isFetchingRef.current = false;
    }
  }, [page, hasMore, baseURL, headers]);

  /** ✅ First fetch on mount */
  useEffect(() => {
    setOrders([]);
    setPage(0);
    setHasMore(true);
    fetchOrders();
  }, [baseURL]);

  /** ✅ Infinite scroll setup */
  useEffect(() => {
    if (observerInstance.current) observerInstance.current.disconnect();

    observerInstance.current = new IntersectionObserver(
      (entries) => {
        const first = entries[0];
        if (first.isIntersecting && hasMore && !isFetchingRef.current && !loading) {
          fetchOrders();
        }
      },
      { threshold: 1 }
    );

    const currentRef = observerRef.current;
    if (currentRef) observerInstance.current.observe(currentRef);

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [hasMore, loading, fetchOrders]);

  return (
    <div
      className={`min-h-screen flex flex-col ${
        darkMode ? "bg-gray-900 text-gray-100" : "bg-gray-50 text-gray-900"
      }`}
    >
      <NavBar />
      <div className="container mx-auto px-4 py-8 flex-grow">
        <h1 className="text-3xl font-bold mb-6 flex items-center gap-2">
          <FaBoxOpen className="text-indigo-600" /> My Orders
        </h1>

        {error && <p className="text-center text-red-500">{error}</p>}

        {orders.length === 0 && !loading ? (
          <p className="text-center mt-20 text-gray-500 text-lg">
            📦 You haven't placed any orders yet.
          </p>
        ) : (
          <>
            <div className="overflow-x-auto rounded-lg shadow-md">
              <table className="min-w-full text-sm">
                <thead
                  className={`uppercase text-xs ${
                    darkMode ? "bg-gray-700" : "bg-gray-100"
                  }`}
                >
                  <tr>
                    <th className="px-4 py-3 text-left">ID</th>
                    <th className="px-4 py-3 text-left">Product</th>
                    <th className="px-4 py-3 text-center">Type</th>
                    <th className="px-4 py-3 text-center">Coins</th>
                    <th className="px-4 py-3 text-center">Quantity</th>
                    <th className="px-4 py-3 text-center">Status</th>
                    <th className="px-4 py-3 text-center">Ordered</th>
                    <th className="px-4 py-3 text-center">Delivery</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr
                      key={order.order_id}
                      className={`border-t transition hover:scale-[1.01] ${
                        darkMode
                          ? "border-gray-700 hover:bg-gray-800"
                          : "border-gray-200 hover:bg-gray-100"
                      }`}
                    >
                      <td className="px-4 py-3 font-semibold text-left">
                        #{order.order_id}
                      </td>
                      <td className="px-4 py-3 font-semibold text-left">
                        {order.product_title}
                      </td>
                      <td className="px-4 py-3 text-center">{order.product_type}</td>
                      <td className="px-4 py-3 text-center text-yellow-500 flex items-center justify-center gap-1">
                        <FaCoins /> {order.coins}
                      </td>
                      <td className="px-4 py-3 text-center">{order.num_of_products}</td>

                      <td
                        className={`px-4 py-3 text-center font-semibold ${
                          order.status === "CONFIRMED"
                            ? "text-green-500"
                            : order.status === "SHIPPED"
                            ? "text-green-500"
                            : order.status === "PENDING"
                            ? "text-yellow-500"
                            : "text-red-500"
                        }`}
                      >
                        {order.status}
                      </td>
                      <td className="px-4 py-3 text-center flex justify-center items-center gap-1 text-gray-400">
                        <FaClock className="text-sm" />{" "}
                        {order.order_at
                          ? new Date(order.order_at).toLocaleDateString()
                          : "-"}
                      </td>
                      <td className="px-4 py-3 text-center text-gray-400">
                        {order.delivery_at
                          ? new Date(order.delivery_at).toLocaleDateString()
                          : "Not yet"}
                      </td>
                    </tr>
                  ))}
                  {loading && (
                    <tr>
                      <td
                        colSpan="8"
                        className="text-center py-4 text-gray-400 animate-pulse"
                      >
                        Loading more orders...
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Sentinel for Infinite Scroll */}
            <div ref={observerRef} className="h-10"></div>
          </>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default MyOrderPage;
