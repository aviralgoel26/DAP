import { useState, useRef } from "react";
import { Paperclip } from "lucide-react";
import Button from "../ui/Button";
import "../../pages/Templates/templates.css";

function TemplateUpload({ onUpload, onCancel }) {
  const [templateId, setTemplateId] = useState("");
  const [file, setFile] = useState(null);
  const fileRef = useRef(null);

  function submit() {
    if (!templateId || !file) return;
    onUpload(templateId, file);
  }

  return (
    <div className="upload-panel">
      <div className="upload-panel-title">Upload New Template</div>
      <div className="upload-panel-fields">

        <div className="input-group">
          <label className="input-label">Template Name</label>
          <input
            className="input-field"
            placeholder="e.g. Invoice_2024"
            value={templateId}
            onChange={e => setTemplateId(e.target.value)}
          />
        </div>

        <div className="input-group" style={{ flexShrink: 0 }}>
          <label className="input-label">File</label>
          <label className="upload-file-label" onClick={() => fileRef.current?.click()}>
            <Paperclip size={15} />
            {file ? file.name : "Choose .docx or .xlsx"}
          </label>
          <input
            ref={fileRef}
            type="file"
            hidden
            accept=".docx,.xlsx"
            onChange={e => setFile(e.target.files[0])}
          />
        </div>

        <div style={{ display: "flex", gap: "8px", alignItems: "flex-end" }}>
          <Button onClick={submit} disabled={!templateId || !file}>
            Upload
          </Button>
          {onCancel && (
            <Button variant="secondary" onClick={onCancel}>
              Cancel
            </Button>
          )}
        </div>

      </div>
    </div>
  );
}

export default TemplateUpload;