import Sidebar from "../components/layout/Sidebar";
import Header from "../components/layout/Header";
import { Outlet } from "react-router-dom";

function AppLayout() {
  return (
    <div className="app-layout">

      <Sidebar />

      <div className="main-layout">

        <Header />

        <main className="page-content">
          <Outlet />
        </main>

      </div>

    </div>
  );
}

export default AppLayout;