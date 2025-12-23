import React, { useState } from 'react';

function OrderManager({ orders, onCreateOrder, loading, hasAccount }) {
  const [amount, setAmount] = useState(150);
  const [description, setDescription] = useState('');

  const handleCreateOrder = (e) => {
    e.preventDefault();
    if (amount > 0 && description.trim()) {
      onCreateOrder(amount, description);
      setAmount(150);
      setDescription('');
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'NEW':
        return 'Обрабатывается';
      case 'FINISHED':
        return 'Завершен';
      case 'CANCELLED':
        return 'Отменен';
      default:
        return status;
    }
  };

  return (
    <div className="card">
      <h2>📦 Заказы</h2>

      {!hasAccount && (
        <div className="empty-state">
          <p>⚠️ Сначала создайте счет для оформления заказов</p>
        </div>
      )}

      {hasAccount && (
        <form onSubmit={handleCreateOrder} style={{ marginBottom: '2rem' }}>
          <div className="input-group">
            <label>Сумма заказа:</label>
            <input
              type="number"
              min="1"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(parseFloat(e.target.value) || 0)}
              disabled={loading}
            />
          </div>
          <div className="input-group">
            <label>Описание:</label>
            <textarea
              rows="3"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Например: Свитер с оленями"
              disabled={loading}
            />
          </div>
          <button
            type="submit"
            className="button"
            disabled={loading || amount <= 0 || !description.trim()}
          >
            Создать заказ
          </button>
        </form>
      )}

      <h3>История заказов</h3>
      <div className="order-list">
        {orders.length === 0 ? (
          <div className="empty-state">
            <p>У вас пока нет заказов</p>
          </div>
        ) : (
          orders.map((order) => (
            <div key={order.id} className="order-item">
              <h4>
                Заказ #{order.id}
                <span className={`order-status ${order.status}`}>
                  {getStatusText(order.status)}
                </span>
              </h4>
              <div className="order-details">
                <p><strong>Описание:</strong> {order.description}</p>
                <p><strong>Сумма:</strong> {order.amount.toFixed(2)} ₽</p>
                <p><strong>Создан:</strong> {new Date(order.createdAt).toLocaleString('ru-RU')}</p>
                {order.updatedAt && order.createdAt !== order.updatedAt && (
                  <p><strong>Обновлен:</strong> {new Date(order.updatedAt).toLocaleString('ru-RU')}</p>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default OrderManager;

