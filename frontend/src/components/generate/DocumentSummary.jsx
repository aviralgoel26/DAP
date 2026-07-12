import Card from "../ui/Card";

function DocumentSummary() {

    return (

        <Card>

            <h2>Live Summary</h2>

            <div className="summary-item">

                <span>Template</span>

                <strong>MRM</strong>

            </div>

            <div className="summary-item">

                <span>Detected Placeholders</span>

                <ul>

                    <li>companyName</li>

                    <li>address</li>

                    <li>logo</li>

                </ul>

            </div>

            <div className="summary-item">

                <span>Logo</span>

                <strong>Not Uploaded</strong>

            </div>

            <div className="summary-item">

                <span>Output</span>

                <strong>DOCX</strong>

            </div>

        </Card>

    );

}

export default DocumentSummary;