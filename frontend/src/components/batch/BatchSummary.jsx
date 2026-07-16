import Card from "../ui/Card";

function BatchSummary({
    selectedTemplates = [],
    placeholders = [],
    logo
}) {

    return (

        <Card title="Batch Summary">

            <div className="summary-item">

                <span>Templates Selected</span>

                <strong>

                    {selectedTemplates.length}

                </strong>

            </div>

            <div className="summary-item">

                <span>Templates</span>

                <ul>

                    {

                        selectedTemplates.length === 0 ?

                        <li>No template selected</li>

                        :

                        selectedTemplates.map(template => (

                            <li key={template}>

                                {template}

                            </li>

                        ))

                    }

                </ul>

            </div>

            <div className="summary-item">

                <span>Detected Placeholders</span>

                <ul>

                    {

                        placeholders.length === 0 ?

                        <li>None</li>

                        :

                        placeholders.map(item => (

                            <li key={item}>

                                {item}

                            </li>

                        ))

                    }

                </ul>

            </div>

            <div className="summary-item">

                <span>Logo</span>

                <strong>

                    {

                        logo ?

                        logo.name :

                        "Not Uploaded"

                    }

                </strong>

            </div>

            <div className="summary-item">

                <span>Output</span>

                <strong>ZIP</strong>

            </div>

        </Card>

    );

}

export default BatchSummary;