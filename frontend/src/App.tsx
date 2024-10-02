import { Routes, Route } from "react-router-dom";

import PaymentList from "./pages/PaymentPage/PaymentPage.PaymentList";
import PaymentPage from "./pages/PaymentPage/PaymentPage";
import TravelAccountPage from "./pages/TravelAccount/TravelAccountPage";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/payment/qr" element={<PaymentPage />} />
        <Route path="/payment/list" element={<PaymentList />} />

        <Route
          path="/accounts/travel/domestic"
          element={<TravelAccountPage />}
        />
      </Routes>
    </div>
  );
}

export default App;
