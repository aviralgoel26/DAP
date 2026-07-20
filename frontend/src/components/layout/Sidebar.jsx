import {
  LayoutDashboard,
  FileText,
  Layers3,
  FolderOpen,
  History,
  Settings
} from "lucide-react";

import SidebarItem from "./SidebarItem";

function Sidebar() {
  return (
    <aside className="sidebar">

      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">D</div>
        <div className="sidebar-logo-text">
          <h2>DAP</h2>
          <p>Document Automation</p>
        </div>
      </div>

      <p className="sidebar-section-label">Workspace</p>

      <nav className="sidebar-nav">
        <SidebarItem icon={<LayoutDashboard size={17} />} label="Dashboard" to="/" />
        <SidebarItem icon={<FileText size={17} />} label="Generate" to="/generate" />
        <SidebarItem icon={<Layers3 size={17} />} label="Batch" to="/batch" />
        <SidebarItem icon={<FolderOpen size={17} />} label="Templates" to="/templates" />
        <SidebarItem icon={<History size={17} />} label="History" to="/history" />
      </nav>

      <p className="sidebar-section-label" style={{ marginTop: "12px" }}>System</p>

      <nav className="sidebar-nav">
        <SidebarItem icon={<Settings size={17} />} label="Settings" to="/settings" />
      </nav>

    </aside>
  );
}

export default Sidebar;