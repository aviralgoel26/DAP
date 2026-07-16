import TemplateCard from "./TemplateCard";

function TemplateGrid({

    templates,

    onDelete

}) {

    return (

        <div className="template-grid">

            {

                templates.map(template => (

                    <TemplateCard

                        key={template.name}

                        template={template}

                        onDelete={onDelete}

                    />

                ))

            }

        </div>

    );

}

export default TemplateGrid;