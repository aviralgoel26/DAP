import { NavLink } from "react-router-dom";
import { cn } from "../../utils/cn";

function SidebarItem({
  icon,
  label,
  to,
}) {

  return (

    <NavLink
      to={to}
      className={({ isActive }) =>
        cn(
          "sidebar-item",
          isActive && "sidebar-item-active"
        )
      }
    >

      {icon}

      <span>{label}</span>

    </NavLink>

  );

}

export default SidebarItem;