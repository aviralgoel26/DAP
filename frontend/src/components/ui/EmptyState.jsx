import Button from "./Button";

function EmptyState({
  icon,
  title,
  description,
  actionLabel,
  onAction,
}) {
  return (
    <div className="empty-state">

      {icon && (
        <div className="empty-icon">
          {icon}
        </div>
      )}

      <h2 className="heading-md">
        {title}
      </h2>

      <p className="body">
        {description}
      </p>

      {actionLabel && (
        <Button onClick={onAction}>
          {actionLabel}
        </Button>
      )}

    </div>
  );
}

export default EmptyState;