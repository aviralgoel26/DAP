import { useEffect, useState } from "react";
import { getTemplates } from "../../services/documentService";

function TemplateSelector({
    templates,
    selectedTemplate,
    onChange
}) {

    return (

        <div>

            <label className="input-label">

                Template

            </label>

            <select
                className="input-field"
                value={selectedTemplate}
                onChange={(e) => onChange(e.target.value)}
            >

                <option value="">

                    Select Template

                </option>

                {

                    templates.map(template => (

                        <option
                            key={template.name}
                            value={template.name}
                        >

                            {template.name}

                        </option>

                    ))

                }

            </select>

        </div>

    );

}

export default TemplateSelector;