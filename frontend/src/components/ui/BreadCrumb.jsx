import { useLocation } from "react-router-dom";
import { ChevronRight } from "lucide-react";

const ROUTE_LABELS = {
  "/": "Dashboard",
  "/generate": "Generate Documents",
  "/batch": "Batch Generation",
  "/templates": "Templates",
  "/history": "History",
  "/settings": "Settings",
};

function Breadcrumb() {
  const { pathname } = useLocation();
  const page = ROUTE_LABELS[pathname] || pathname.replace("/", "").replace(/-/g, " ");

  return (
    <div className="header-breadcrumb">
      <span className="header-breadcrumb-root">DAP</span>
      <ChevronRight size={14} color="var(--text-tertiary)" />
      <span className="header-breadcrumb-current">{page}</span>
    </div>
  );
}

export default Breadcrumb;