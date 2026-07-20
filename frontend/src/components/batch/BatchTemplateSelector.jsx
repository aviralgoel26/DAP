import { cn } from "../../utils/cn";

function BatchTemplateSelector({ templates, selected, onChange }) {
  
  function toggleTemplate(templateName) {
    if (selected.includes(templateName)) {
      onChange(selected.filter(t => t !== templateName));
    } else {
      onChange([...selected, templateName]);
    }
  }

  return (
    <div>
      <div className="form-section-header">
        <h3 className="form-section-title">Select Templates</h3>
        <p className="form-section-desc">Choose one or multiple templates for batch processing.</p>
      </div>

      <div className="batch-selector-grid">
        {templates.map(template => {
          const isChecked = selected.includes(template.name);
          return (
            <label
              key={template.name}
              className={cn("batch-template-checkbox", isChecked && "checked")}
            >
              <input
                type="checkbox"
                checked={isChecked}
                onChange={() => toggleTemplate(template.name)}
              />
              <span title={template.name}>{template.name}</span>
            </label>
          );
        })}
      </div>
    </div>
  );
}

export default BatchTemplateSelector;