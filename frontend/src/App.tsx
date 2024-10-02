import React from "react";
import { Routes, Route } from "react-router-dom";
import PaymentList from "./pages/PaymentPage/PaymentPage.PaymentList";
import PaymentPage from "./pages/PaymentPage/PaymentPage";
import TransferList from "./pages/TransferPage/TransferPage.TransferList";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/payment/qr" element={<PaymentPage />} />
        <Route path="/payment/list" element={<PaymentList />} />
        <Route path="/transfer/list" element={<TransferList />} />
      </Routes>
    </div>
  );
}

export default App;
