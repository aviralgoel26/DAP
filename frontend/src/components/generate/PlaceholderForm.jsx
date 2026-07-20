import Input from "../ui/Input";

function PlaceholderForm({ placeholders, values, onChange }) {
  const dataPlaceholders = placeholders.filter(p => p !== "logo");

  if (dataPlaceholders.length === 0) return null;

  return (
    <div>
      <div className="form-section-header">
        <h3 className="form-section-title">Fill placeholders</h3>
        <p className="form-section-desc">Provide data to populate the document.</p>
      </div>
      
      <div className="placeholder-grid">
        {dataPlaceholders.map(name => (
          <Input
            key={name}
            label={name}
            placeholder={`Enter ${name}...`}
            value={values[name] || ""}
            onChange={(e) => onChange(name, e.target.value)}
          />
        ))}
      </div>
    </div>
  );
}

export default PlaceholderForm;