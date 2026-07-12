import { Routes, Route, Navigate } from "react-router-dom";

import Layout from "../components/layout/Layout";

import Dashboard from "../pages/Dashboard/Dashboard";
import Generate from "../pages/Generate/Generate";
import Batch from "../pages/Batch/Batch";
import Templates from "../pages/Templates/Templates";
import History from "../pages/History/History";
import Settings from "../pages/Settings/Settings";

function AppRoutes() {
    return (
        <Routes>

    <Route element={<Layout />}>

        <Route path="/" element={<Dashboard />} />

        <Route path="/generate" element={<Generate />} />

        <Route path="/batch" element={<Batch />} />

        <Route path="/templates" element={<Templates />} />

        <Route path="/history" element={<History />} />

        <Route path="/settings" element={<Settings />} />

    </Route>

</Routes>
    );
}

export default AppRoutes;