import { useState, useRef } from "react";
import { Paperclip } from "lucide-react";
import Button from "../ui/Button";

const ALLOWED_EXTENSIONS = [".docx", ".xlsx"];
const MAX_FILE_SIZE_MB = 50;

function TemplateUpload({ onUpload, onCancel, loading }) {
  const [templateId, setTemplateId] = useState("");
  const [file, setFile] = useState(null);
  const [error, setError] = useState("");
  const fileRef = useRef(null);

  function handleFileChange(e) {
    const selected = e.target.files[0];
    if (!selected) return;

    const ext = "." + selected.name.split(".").pop().toLowerCase();
    if (!ALLOWED_EXTENSIONS.includes(ext)) {
      setError("Only .docx and .xlsx files are supported.");
      setFile(null);
      e.target.value = "";
      return;
    }

    if (selected.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
      setError(`File must be smaller than ${MAX_FILE_SIZE_MB}MB.`);
      setFile(null);
      e.target.value = "";
      return;
    }

    setError("");
    setFile(selected);
  }

  function handleTemplateIdChange(e) {
    setTemplateId(e.target.value);
    if (error) setError("");
  }

  function submit() {
    const trimmedId = templateId.trim();
    if (!trimmedId) {
      setError("Template name is required.");
      return;
    }
    if (!file) {
      setError("Please select a file.");
      return;
    }
    setError("");
    onUpload(trimmedId, file);
  }

  const isDisabled = !templateId.trim() || !file || loading;

  return (
    <div className="upload-panel">
      <div className="upload-panel-title">Upload New Template</div>
      <div className="upload-panel-fields">
        <div className="input-group">
          <label className="input-label">Template Name</label>
          <input
            className={`input-field${error && !file ? " input-error" : ""}`}
            placeholder="e.g. Invoice_2024"
            value={templateId}
            onChange={handleTemplateIdChange}
            disabled={loading}
          />
        </div>

        <div className="input-group" style={{ flexShrink: 0 }}>
          <label className="input-label">File</label>
          <label
            className="upload-file-label"
            onClick={() => !loading && fileRef.current?.click()}
            style={{ cursor: loading ? "not-allowed" : "pointer" }}
          >
            <Paperclip size={15} />
            {file ? file.name : "Choose .docx or .xlsx"}
          </label>
          <input
            ref={fileRef}
            type="file"
            hidden
            accept=".docx,.xlsx"
            onChange={handleFileChange}
            disabled={loading}
          />
        </div>

        <div style={{ display: "flex", gap: "8px", alignItems: "flex-end" }}>
          <Button onClick={submit} disabled={isDisabled} loading={loading}>
            Upload
          </Button>
          {onCancel && (
            <Button variant="secondary" onClick={onCancel} disabled={loading}>
              Cancel
            </Button>
          )}
        </div>
      </div>

      {error && (
        <div style={{ marginTop: "10px" }}>
          <span className="input-error-text">{error}</span>
        </div>
      )}
    </div>
  );
}

export default TemplateUpload;