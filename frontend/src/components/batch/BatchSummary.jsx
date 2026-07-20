import Card from "../ui/Card";

function BatchSummary({ selectedTemplates = [], placeholders = [], logo }) {
  return (
    <Card title="Batch Summary">
      <div className="summary-list">
        
        <div className="summary-item">
          <span className="summary-label">Templates Selected</span>
          <span className="summary-value">{selectedTemplates.length}</span>
        </div>

        <div className="summary-item">
          <span className="summary-label">Selected Files</span>
          {selectedTemplates.length > 0 ? (
            <div className="summary-tags">
              {selectedTemplates.map(template => (
                <span key={template} className="summary-tag">{template}</span>
              ))}
            </div>
          ) : (
            <span className="summary-value-empty">None</span>
          )}
        </div>

        <div className="summary-item">
          <span className="summary-label">Detected Placeholders</span>
          {placeholders.length > 0 ? (
            <div className="summary-tags">
              {placeholders.map(item => (
                <span key={item} className="summary-tag">{item}</span>
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
          <span className="summary-label">Output Format</span>
          <span className="summary-value">ZIP Archive</span>
        </div>

      </div>
    </Card>
  );
}

export default BatchSummary;