import { useRef, useState } from "react";
import { UploadCloud, CheckCircle2 } from "lucide-react";
import { toast } from "sonner";

const MAX_FILE_SIZE_MB = 2;

function LogoUpload({ logo, onChange, disabled }) {
  const inputRef = useRef(null);

  const handleSelect = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
      toast.error(`Logo file must be smaller than ${MAX_FILE_SIZE_MB}MB.`);
      event.target.value = "";
      return;
    }

    onChange(file);
  };

  return (
    <div>
      <div className="form-section-header">
        <h3 className="form-section-title">Company Logo</h3>
        <p className="form-section-desc">Upload a logo to be inserted into the document.</p>
      </div>

      <div 
        className={`upload-box ${disabled ? 'upload-box-disabled' : ''}`}
        onClick={() => !disabled && inputRef.current.click()}
        style={{ cursor: disabled ? "not-allowed" : "pointer", opacity: disabled ? 0.6 : 1 }}
      >
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
        disabled={disabled}
      />
    </div>
  );
}

export default LogoUpload;