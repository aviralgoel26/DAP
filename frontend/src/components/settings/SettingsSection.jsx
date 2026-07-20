function SettingsSection({ title, description, children }) {
  return (
    <section style={{ 
      background: "var(--surface)", 
      border: "1px solid var(--border)", 
      borderRadius: "var(--radius)", 
      padding: "24px", 
      marginBottom: "24px",
      boxShadow: "var(--shadow-sm)"
    }}>
      <div style={{ marginBottom: "20px" }}>
        <h2 style={{ fontSize: "16px", fontWeight: "600", color: "var(--text)", marginBottom: "4px" }}>
          {title}
        </h2>
        {description && (
          <p style={{ fontSize: "13.5px", color: "var(--text-secondary)" }}>
            {description}
          </p>
        )}
      </div>
      <div>{children}</div>
    </section>
  );
}

export default SettingsSection;