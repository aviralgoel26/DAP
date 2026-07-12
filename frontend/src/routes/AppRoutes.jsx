import { BrowserRouter, Routes, Route } from "react-router-dom";

import AppLayout from "../layouts/AppLayout";

import Dashboard from "../pages/Dashboard/Dashboard";
import Generate from "../pages/Generate/Generate";
import Batch from "../pages/Batch/Batch";
import Templates from "../pages/Templates/Templates";
import History from "../pages/History/History";
import Settings from "../pages/Settings/Settings";

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>

        <Route element={<AppLayout />}>

          <Route
            path="/"
            element={<Dashboard />}
          />

          <Route
            path="/generate"
            element={<Generate />}
          />

          <Route
            path="/batch"
            element={<Batch />}
          />

          <Route
            path="/templates"
            element={<Templates />}
          />

          <Route
            path="/history"
            element={<History />}
          />

          <Route
            path="/settings"
            element={<Settings />}
          />

        </Route>

      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;