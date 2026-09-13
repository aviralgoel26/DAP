import TemplateCard from "./TemplateCard";

function TemplateGrid({ templates, onDelete, deletingId, onPreview}) {
  return (
    <div className="template-grid">
      {templates.map((template) => (
        <TemplateCard
          key={template.name}
          template={template}
          onDelete={onDelete}
          deletingId={deletingId}
          onPreview={onPreview}
        />
      ))}
    </div>
  );
}

export default TemplateGrid;