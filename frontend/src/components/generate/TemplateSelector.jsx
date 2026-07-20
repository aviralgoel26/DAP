import { FileText } from "lucide-react";

function TemplateSelector({ templates, selectedTemplate, onChange }) {
  return (
    <div>
      <div className="form-section-header">
        <h3 className="form-section-title">Choose a template</h3>
        <p className="form-section-desc">Select the document template you want to generate.</p>
      </div>

      <div className="input-group">
        <div style={{ position: "relative" }}>
          <select
            className="input-field"
            value={selectedTemplate}
            onChange={(e) => onChange(e.target.value)}
            style={{ paddingLeft: "36px", cursor: "pointer" }}
          >
            <option value="">Select a template...</option>
            {templates.map(template => (
              <option key={template.name} value={template.name}>
                {template.name}
              </option>
            ))}
          </select>
          <div style={{ position: "absolute", left: "12px", top: "50%", transform: "translateY(-50%)", color: "var(--text-tertiary)", pointerEvents: "none" }}>
            <FileText size={16} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default TemplateSelector;