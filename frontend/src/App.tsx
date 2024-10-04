import { Routes, Route } from "react-router-dom";

import TravelAccountPage from "./pages/TravelAccount/TravelAccountPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import MainPage from "./pages/MainPage/MainPage";
import JoinPage from "./pages/JoinPage/JoinPage";
import MailInfo from "./pages/JoinPage/JoinPage.MailInfo";
import TransferList from "./pages/TransferPage/TransferPage.TransferList";
import TransferLists from "./pages/TransferPage/TransferPage.TransferLists";
import ExchangeRates from "./pages/Exchange/ExchangePage";

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
        <Route path="/join" element={<JoinPage />} />
        <Route path="/mailInfo" element={<MailInfo />} />
        <Route path="/transfer/list" element={<TransferLists />} />
        <Route path="/transfer/list/:account" element={<TransferList />} />
        <Route path="/exchange" element={<ExchangeRates />} />
      </Routes>
    </div>
  );
}

export default App;
