import {
  FileText,
  FileSpreadsheet,
  Eye,
  Trash2,
} from "lucide-react";

import Button from "../ui/Button";

function TemplateCard({
  template,
  onDelete,
  deletingId,
  onPreview,
}) {

  const extension = template.name
    .split(".")
    .pop()
    ?.toLowerCase();

  const isDeleting =
    deletingId === template.name;

  const isWord = extension === "docx";
  const isExcel = extension === "xlsx";

  return (
    <div className="template-card">

      <div className="template-card-header">

        <div className="template-card-icon">

          {isWord ? (
            <FileText size={22} />
          ) : (
            <FileSpreadsheet size={22} />
          )}

        </div>

        <div className="template-card-info">

          <div
            className="template-card-name"
            title={template.name}
          >
            {template.name}
          </div>

          <div className="template-card-type">

            {isWord
              ? "Word Template"
              : "Excel Template"}

          </div>

        </div>

      </div>

      <div className="template-card-actions">

        <Button
          variant="secondary"
          size="sm"
          leftIcon={<Eye size={16} />}
          onClick={() => onPreview(template)}
        >
          Preview
        </Button>

        <Button
          variant="danger"
          size="sm"
          leftIcon={<Trash2 size={16} />}
          loading={isDeleting}
          disabled={!!deletingId}
          onClick={() => onDelete(template.name)}
        >
          Delete
        </Button>

      </div>

    </div>
  );
}

export default TemplateCard;