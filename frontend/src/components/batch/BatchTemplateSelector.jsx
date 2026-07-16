function BatchTemplateSelector({
    templates,
    selected,
    onChange
}) {

    function toggleTemplate(templateName) {

        if (selected.includes(templateName)) {

            onChange(
                selected.filter(
                    t => t !== templateName
                )
            );

        } else {

            onChange([
                ...selected,
                templateName
            ]);

        }

    }

    return (

        <div className="batch-selector">

            <h3>Select Templates</h3>

            {

                templates.map(template => (

                    <label
                        key={template.name}
                        className="batch-template"
                    >

                        <input
                            type="checkbox"
                            checked={
                                selected.includes(
                                    template.name
                                )
                            }
                            onChange={() =>
                                toggleTemplate(
                                    template.name
                                )
                            }
                        />

                        <span>

                            {template.name}

                        </span>

                    </label>

                ))

            }

        </div>

    );

}

export default BatchTemplateSelector;