import { FileText } from "lucide-react";
import Button from "../ui/Button";
import "../../pages/Templates/templates.css";

function TemplateCard({ template, onDelete }) {
  const ext = template.name.split(".").pop()?.toUpperCase() || "DOC";

  return (
    <div className="template-card">
      <div className="template-card-header">
        <div className="template-card-icon">
          <FileText size={20} />
        </div>
        <div className="template-card-info">
          <div className="template-card-name" title={template.name}>
            {template.name}
          </div>
          <div className="template-card-type">{template.type || ext}</div>
        </div>
      </div>

      <div className="template-card-actions">
        <Button
          variant="danger"
          size="sm"
          onClick={() => onDelete(template.name)}
        >
          Delete
        </Button>
      </div>
    </div>
  );
}

export default TemplateCard;