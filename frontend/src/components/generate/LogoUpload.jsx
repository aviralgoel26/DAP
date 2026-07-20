import { useRef, useState } from "react";
import { UploadCloud, CheckCircle2 } from "lucide-react";

function LogoUpload({ logo, onChange }) {
  const inputRef = useRef(null);

  const handleSelect = (event) => {
    if (event.target.files.length > 0) {
      onChange(event.target.files[0]);
    }
  };

  return (
    <div>
      <div className="form-section-header">
        <h3 className="form-section-title">Company Logo</h3>
        <p className="form-section-desc">Upload a logo to be inserted into the document.</p>
      </div>

      <div className="upload-box" onClick={() => inputRef.current.click()}>
        <div className="upload-box-icon" style={{ 
          background: logo ? "var(--success-bg)" : "white",
          color: logo ? "var(--success)" : "var(--primary)"
        }}>
          {logo ? <CheckCircle2 size={24} /> : <UploadCloud size={24} />}
        </div>
        
        <div style={{ textAlign: "center" }}>
          <div className="upload-box-text">
            {logo ? logo.name : "Click to upload"}
          </div>
          <div className="upload-box-subtext">
            {logo ? "Logo ready" : "PNG or JPG (max 2MB)"}
          </div>
        </div>
      </div>

      <input
        ref={inputRef}
        type="file"
        hidden
        accept=".png,.jpg,.jpeg"
        onChange={handleSelect}
      />
    </div>
  );
}

export default LogoUpload;