import { Routes, Route } from "react-router-dom";

import PaymentHistory from "./pages/PaymentPage/PaymentPage.PaymentHistory";
import PaymentPage from "./pages/PaymentPage/PaymentPage";
import TravelAccountPage from "./pages/TravelAccount/TravelAccountPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import MainPage from "./pages/MainPage/MainPage";
import TransferList from "./pages/TransferPage/TransferPage.TransferList";
import MemberManageMentPage from "./pages/TravelAccount/MemberManagementPage";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/accounts/travel/domestic"
          element={<TravelAccountPage />}
        />
        <Route
          path="/accounts/travel/domestic/:accountId/members"
          element={<MemberManageMentPage />}
        />

        <Route path="/payment/qr" element={<PaymentPage />} />
        <Route path="/payment/list" element={<PaymentHistory />} />
        <Route path="/transfer/list" element={<TransferList />} />
      </Routes>
    </div>
  );
}

export default App;
