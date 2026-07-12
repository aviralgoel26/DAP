import Input from "../ui/Input";

function PlaceholderForm({

    placeholders,

    values,

    onChange

}) {

    return (

        <div className="placeholder-section">

            <h3>Placeholder Values</h3>

            {

                placeholders.map(name => (

                    name !== "logo" && (

                        <Input
                            key={name}
                            label={name}
                            value={values[name] || ""}
                            onChange={(e) =>
                                onChange(
                                    name,
                                    e.target.value
                                )
                            }
                        />

                    )

                ))

            }

        </div>

    );

}

export default PlaceholderForm;