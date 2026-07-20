function SettingsToggle({ label, description, checked, onChange }) {
  return (
    <div className="toggle-wrapper">
      <div style={{ paddingRight: "20px" }}>
        <div className="toggle-label">{label}</div>
        {description && (
          <div style={{ fontSize: "12.5px", color: "var(--text-secondary)", marginTop: "2px" }}>
            {description}
          </div>
        )}
      </div>
      
      <label className="toggle-switch">
        <input
          type="checkbox"
          checked={checked}
          onChange={(e) => onChange(e.target.checked)}
        />
        <span className="toggle-slider"></span>
      </label>
    </div>
  );
}

export default SettingsToggle;