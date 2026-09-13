import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { Toaster } from "sonner";
import "./styles/variables.css";
import "./styles/global.css";
import "./styles/typography.css";
import "./styles/layout.css";
import "./styles/components.css";
import App from "./App";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
      <Toaster position="bottom-right" richColors closeButton />
    </BrowserRouter>
  </React.StrictMode>
);