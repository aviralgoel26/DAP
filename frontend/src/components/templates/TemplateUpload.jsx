import { useState } from "react";
import Button from "../ui/Button";

function TemplateUpload({ onUpload }) {

    const [templateId, setTemplateId] = useState("");

    const [file, setFile] = useState(null);

    function submit() {

    console.log("Template ID:", templateId);
    console.log("File:", file);

    if (!templateId || !file) {

        console.log("Validation failed");

        return;

    }

    console.log("Calling onUpload...");

    onUpload(templateId, file);

}

    return (

        <div className="template-upload">

            <input
    style={{
        width: "300px",
        height: "40px",
        border: "2px solid red",
        fontSize: "18px",
        padding: "8px"
    }}
    value={templateId}
    onChange={(e) => {
        console.log("Typing:", e.target.value);
        setTemplateId(e.target.value);
    }}
/>

            <input

                type="file"

                accept=".docx,.xlsx"

                onChange={(e) => setFile(e.target.files[0])}

            />

            <Button onClick={submit}>

                Upload Template

            </Button>

        </div>

    );

}

export default TemplateUpload;