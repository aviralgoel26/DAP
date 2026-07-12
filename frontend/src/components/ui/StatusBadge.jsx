import { cn } from "../../utils/cn";

function StatusBadge({
  status = "info",
  children,
  className = "",
}) {
  return (
    <span
      className={cn(
        "status-badge",
        `status-${status}`,
        className
      )}
    >
      {children}
    </span>
  );
}

export default StatusBadge;