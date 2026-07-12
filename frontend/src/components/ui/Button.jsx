import { Loader2 } from "lucide-react";
import { cn } from "../../utils/cn";

function Button({
  children,
  variant = "primary",
  size = "md",
  loading = false,
  disabled = false,
  leftIcon,
  rightIcon,
  className = "",
  type = "button",
  onClick,
}) {
  return (
    <button
      type={type}
      className={cn(
        "btn",
        `btn-${variant}`,
        `btn-${size}`,
        loading && "btn-loading",
        (disabled || loading) && "btn-disabled",
        className
      )}
      disabled={disabled || loading}
      onClick={onClick}
    >
      {loading ? (
        <Loader2 size={18} className="btn-spinner" />
      ) : (
        <>
          {leftIcon && <span className="btn-icon">{leftIcon}</span>}

          <span>{children}</span>

          {rightIcon && <span className="btn-icon">{rightIcon}</span>}
        </>
      )}
    </button>
  );
}

export default Button;