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

        <h2>DAP</h2>

        <p>Document Automation Platform</p>

      </div>

      <nav className="sidebar-nav">

        <SidebarItem
          icon={<LayoutDashboard size={20} />}
          label="Dashboard"
          to="/"
        />

        <SidebarItem
          icon={<FileText size={20} />}
          label="Generate"
          to="/generate"
        />

        <SidebarItem
          icon={<Layers3 size={20} />}
          label="Batch"
          to="/batch"
        />

        <SidebarItem
          icon={<FolderOpen size={20} />}
          label="Templates"
          to="/templates"
        />

        <SidebarItem
          icon={<History size={20} />}
          label="History"
          to="/history"
        />

        <SidebarItem
          icon={<Settings size={20} />}
          label="Settings"
          to="/settings"
        />

      </nav>

    </aside>

  );

}

export default Sidebar;