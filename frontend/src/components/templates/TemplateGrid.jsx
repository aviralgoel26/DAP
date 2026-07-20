import TemplateCard from "./TemplateCard";
import "../../pages/Templates/templates.css";

function TemplateGrid({ templates, onDelete }) {
  return (
    <div className="template-grid">
      {templates.map(template => (
        <TemplateCard
          key={template.name}
          template={template}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}

export default TemplateGrid;