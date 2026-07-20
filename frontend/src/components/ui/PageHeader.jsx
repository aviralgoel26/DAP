import { cn } from "../../utils/cn";

function PageHeader({ title, description, action, className = "" }) {
  return (
    <div className={cn("page-header", className)}>
      <div className="page-header-content">
        <h1 className="heading-xl">{title}</h1>
        {description && (
          <p className="page-description">{description}</p>
        )}
      </div>
      {action && (
        <div className="page-header-action">{action}</div>
      )}
    </div>
  );
}

export default PageHeader;