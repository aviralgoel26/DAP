import { NavLink } from "react-router-dom";
import { cn } from "../../utils/cn";

function SidebarItem({ icon, label, to }) {
  return (
    <NavLink
      to={to}
      end={to === "/"}
      className={({ isActive }) =>
        cn("sidebar-item", isActive && "sidebar-item-active")
      }
    >
      <span className="sidebar-item-icon">{icon}</span>
      <span>{label}</span>
    </NavLink>
  );
}

export default SidebarItem;