import { Routes, Route } from "react-router-dom";

import PaymentHistory from "./pages/PaymentPage/PaymentPage.PaymentHistory";
import PaymentPage from "./pages/PaymentPage/PaymentPage";
import TravelAccountPage from "./pages/TravelAccount/TravelAccountPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import MainPage from "./pages/MainPage/MainPage";
import TransferList from "./pages/TransferPage/TransferPage.TransferList";
import TransferLists from "./pages/TransferPage/TransferPage.TransferLists";
import BankSelectionPage from "./pages/AccountCreationPage/BankSelectionPage";
// import CreationTravelPage from "./pages/AccountCreationPage/BankSelectionPage";
import AccountAuthPage from "./pages/AccountCreationPage/AccountAuthPage";
import IdentityAuthPage from "./pages/AccountCreationPage/IdentityAuthPage";

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
        <Route path="/payment/qr" element={<PaymentPage />} />
        <Route path="/payment/list" element={<PaymentHistory />} />
        <Route path="/transfer/list" element={<TransferLists />} />
        <Route path="/transfer/list/:account" element={<TransferList />} />
        <Route
          path="/creation/travel/bank-selection"
          element={<BankSelectionPage />}
        />
        <Route
          path="/creation/travel/account-auth"
          element={<AccountAuthPage />}
        />
        <Route
          path="/creation/travel/identity-auth"
          element={<IdentityAuthPage />}
        />
      </Routes>
    </div>
  );
}

export default App;
