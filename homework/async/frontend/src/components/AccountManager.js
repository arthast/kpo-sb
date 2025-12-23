import React, { useState } from 'react';

function AccountManager({ account, onCreateAccount, onDeposit, loading }) {
  const [depositAmount, setDepositAmount] = useState(100);

  const handleDeposit = (e) => {
    e.preventDefault();
    if (depositAmount > 0) {
      onDeposit(depositAmount);
      setDepositAmount(100);
    }
  };

  if (!account) {
    return (
      <div className="card">
        <h2>💳 Счет</h2>
        <div className="empty-state">
          <p>У вас еще нет счета</p>
          <button
            className="button"
            onClick={onCreateAccount}
            disabled={loading}
          >
            Создать счет
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <h2>💳 Счет #{account.id}</h2>

      <div className="balance-display">
        {account.balance.toFixed(2)} ₽
      </div>

      <form onSubmit={handleDeposit}>
        <div className="input-group">
          <label>Сумма пополнения:</label>
          <input
            type="number"
            min="1"
            step="0.01"
            value={depositAmount}
            onChange={(e) => setDepositAmount(parseFloat(e.target.value) || 0)}
            disabled={loading}
          />
        </div>
        <button
          type="submit"
          className="button"
          disabled={loading || depositAmount <= 0}
        >
          Пополнить счет
        </button>
      </form>

      <div className="order-details" style={{ marginTop: '1rem' }}>
        <p><strong>Создан:</strong> {new Date(account.createdAt).toLocaleString('ru-RU')}</p>
        {account.updatedAt && (
          <p><strong>Обновлен:</strong> {new Date(account.updatedAt).toLocaleString('ru-RU')}</p>
        )}
      </div>
    </div>
  );
}

export default AccountManager;

