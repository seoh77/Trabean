import { Routes, Route } from "react-router-dom";

import PaymentHistory from "./pages/PaymentPage/PaymentPage.PaymentHistory";
import PaymentPage from "./pages/PaymentPage/PaymentPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import MainPage from "./pages/MainPage/MainPage";
import TransferList from "./pages/TransferPage/TransferPage.TransferList";
import DomesticTravelAccountPage from "./pages/TravelAccount/page/DomesticTravelAccountPage";
import DomesticTravelAccountDetailPage from "./pages/TravelAccount/page/DomesticTravelAccountDetailPage";
import MemberManagementPage from "./pages/TravelAccount/page/MemberManagementPage";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/accounts/travel/domestic"
          element={<DomesticTravelAccountPage />}
        />
        <Route
          path="/accounts/travel/domestic/:accountId"
          element={<DomesticTravelAccountDetailPage />}
        />
        <Route
          path="/accounts/travel/domestic/:accountId/members"
          element={<MemberManagementPage />}
        />

        <Route path="/payment/qr" element={<PaymentPage />} />
        <Route path="/payment/list" element={<PaymentHistory />} />
        <Route path="/transfer/list" element={<TransferList />} />
      </Routes>
    </div>
  );
}

export default App;
