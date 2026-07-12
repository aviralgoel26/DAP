import { cn } from "../../utils/cn";

function Card({
  title,
  subtitle,
  action,
  children,
  footer,
  className = "",
}) {
  return (
    <div className={cn("card", className)}>
      {(title || subtitle || action) && (
        <div className="card-header">
          <div className="card-header-content">
            {title && <h3 className="heading-md">{title}</h3>}

            {subtitle && (
              <p className="caption">
                {subtitle}
              </p>
            )}
          </div>

          {action && (
            <div className="card-action">
              {action}
            </div>
          )}
        </div>
      )}

      <div className="card-body">
        {children}
      </div>

      {footer && (
        <div className="card-footer">
          {footer}
        </div>
      )}
    </div>
  );
}

export default Card;