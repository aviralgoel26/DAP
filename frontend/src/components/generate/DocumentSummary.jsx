import Card from "../ui/Card";

function DocumentSummary({ template, placeholders, logo }) {
  return (
    <Card title="Summary">
      <div className="summary-list">
        
        <div className="summary-item">
          <span className="summary-label">Template</span>
          {template ? (
            <span className="summary-value">{template}</span>
          ) : (
            <span className="summary-value-empty">Not selected</span>
          )}
        </div>

        <div className="summary-item">
          <span className="summary-label">Detected Placeholders</span>
          {placeholders && placeholders.length > 0 ? (
            <div className="summary-tags">
              {placeholders.map(p => (
                <span key={p} className="summary-tag">{p}</span>
              ))}
            </div>
          ) : (
            <span className="summary-value-empty">None</span>
          )}
        </div>

        <div className="summary-item">
          <span className="summary-label">Logo</span>
          {logo ? (
            <span className="summary-value" style={{ color: "var(--success)" }}>Ready</span>
          ) : (
            <span className="summary-value-empty">Not uploaded</span>
          )}
        </div>

        <div className="summary-item">
          <span className="summary-label">Output format</span>
          <span className="summary-value">DOCX / XLSX</span>
        </div>

      </div>
    </Card>
  );
}

export default DocumentSummary;