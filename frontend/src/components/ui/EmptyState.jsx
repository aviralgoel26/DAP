import Button from "./Button";

function EmptyState({ icon, title, description, actionLabel, onAction }) {
  return (
    <div className="empty-state">
      {icon && <div className="empty-icon">{icon}</div>}
      <h2>{title}</h2>
      {description && <p>{description}</p>}
      {actionLabel && (
        <Button onClick={onAction} style={{ marginTop: "4px" }}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
}

export default EmptyState;