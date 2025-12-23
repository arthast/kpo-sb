import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import AccountManager from './components/AccountManager';
import OrderManager from './components/OrderManager';

const API_BASE_URL = process.env.REACT_APP_API_URL || '';

function App() {
  const [userId, setUserId] = useState(1);
  const [account, setAccount] = useState(null);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchAccount = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get(`${API_BASE_URL}/api/accounts/${userId}`);
      setAccount(response.data);
    } catch (err) {
      if (err.response?.status === 404) {
        setAccount(null);
      } else {
        setError('Ошибка загрузки счета: ' + (err.response?.data?.message || err.message));
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get(`${API_BASE_URL}/api/orders/user/${userId}`);
      setOrders(response.data);
    } catch (err) {
      if (err.response?.status !== 404) {
        setError('Ошибка загрузки заказов: ' + (err.response?.data?.message || err.message));
      }
    } finally {
      setLoading(false);
    }
  };

  const createAccount = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await axios.post(`${API_BASE_URL}/api/accounts`, { userId });
      setAccount(response.data);
    } catch (err) {
      setError('Ошибка создания счета: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const depositMoney = async (amount) => {
    try {
      setLoading(true);
      setError(null);
      await axios.post(`${API_BASE_URL}/api/accounts/deposit`, { userId, amount });
      await fetchAccount();
    } catch (err) {
      setError('Ошибка пополнения счета: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const createOrder = async (amount, description) => {
    try {
      setLoading(true);
      setError(null);
      await axios.post(`${API_BASE_URL}/api/orders`, { userId, amount, description });
      await fetchOrders();
      await fetchAccount();
    } catch (err) {
      setError('Ошибка создания заказа: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccount();
    fetchOrders();
    // eslint-disable-next-line
  }, [userId]);

  // Автообновление каждые 5 секунд
  useEffect(() => {
    const interval = setInterval(() => {
      fetchAccount();
      fetchOrders();
    }, 5000);
    return () => clearInterval(interval);
    // eslint-disable-next-line
  }, [userId]);

  return (
    <div className="App">
      <header className="App-header">
        <h1>🛒 Интернет-магазин Гоzон</h1>
        <div className="user-selector">
          <label>
            Пользователь ID:
            <input
              type="number"
              min="1"
              value={userId}
              onChange={(e) => setUserId(parseInt(e.target.value) || 1)}
            />
          </label>
        </div>
      </header>

      <main className="App-main">
        {error && <div className="error-message">{error}</div>}
        {loading && <div className="loading">Загрузка...</div>}

        <div className="content-grid">
          <AccountManager
            account={account}
            onCreateAccount={createAccount}
            onDeposit={depositMoney}
            loading={loading}
          />
          <OrderManager
            orders={orders}
            onCreateOrder={createOrder}
            loading={loading}
            hasAccount={!!account}
          />
        </div>
      </main>
    </div>
  );
}

export default App;

