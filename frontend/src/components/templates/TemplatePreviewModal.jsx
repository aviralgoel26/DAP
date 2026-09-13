import { useEffect, useRef, useState } from "react";
import { X, Download, Loader2, FileText, Sheet } from "lucide-react";
import * as XLSX from "xlsx";
import { renderAsync } from "docx-preview";

import { previewTemplate } from "../../services/templateService";

import "./TemplatePreviewModal.css";

function TemplatePreviewModal({ open, template, onClose }) {
  const previewRef = useRef(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [excelHtml, setExcelHtml] = useState("");

  useEffect(() => {
    if (!open || !template) return;

    loadPreview();
  }, [open, template]);

  async function loadPreview() {
    console.log(template);
    setLoading(true);
    setError("");
    setExcelHtml("");

    if (previewRef.current) {
      previewRef.current.innerHTML = "";
    }

    try {
      const blob = await previewTemplate(template.name);

      const extension =
    template.type?.toLowerCase() ||
    template.name.split(".").pop().toLowerCase();

    
      if (extension === "docx") {
        await renderDocx(blob);
      } else if (extension === "xlsx") {
        await renderExcel(blob);
      } else {
        setError("Preview not supported.");
      }
    } catch (e) {
      console.error(e);
      setError("Unable to preview template.");
    } finally {
      setLoading(false);
    }
  }

  async function renderDocx(blob) {
    const arrayBuffer = await blob.arrayBuffer();

    await renderAsync(
      arrayBuffer,
      previewRef.current,
      null,
      {
        className: "docx-preview",
        inWrapper: true,
        ignoreWidth: false,
        ignoreHeight: false,
        ignoreFonts: false,
        breakPages: true
      }
    );
  }

  async function renderExcel(blob) {
    const arrayBuffer = await blob.arrayBuffer();

    const workbook = XLSX.read(arrayBuffer, {
      type: "array",
    });

    let html = "";

    workbook.SheetNames.forEach((sheetName) => {
      html += `<h3>${sheetName}</h3>`;
      html += XLSX.utils.sheet_to_html(
        workbook.Sheets[sheetName]
      );
    });

    setExcelHtml(html);
  }

  function downloadOriginal() {
    window.open(
  `${import.meta.env.VITE_API_URL}/api/templates/preview/${encodeURIComponent(template.name)}`,
  "_blank"
);
  }

  if (!open) return null;

  return (
    <div className="preview-overlay">

      <div className="preview-modal">

        <div className="preview-header">

          <div className="preview-title">

            {template.name.endsWith(".docx") ? (
              <FileText size={18}/>
            ) : (
              <Sheet size={18}/>
            )}

            <span>{template.name}</span>

          </div>

          <button
            className="preview-close"
            onClick={onClose}
          >
            <X size={20}/>
          </button>

        </div>

        <div className="preview-body">

          {loading && (

            <div className="preview-loading">

              <Loader2
                className="spin"
                size={36}
              />

              <p>Loading preview...</p>

            </div>

          )}

          {!loading && error && (

            <div className="preview-error">
              {error}
            </div>

          )}

          {!loading &&
            !error &&
            excelHtml && (

              <div
                className="excel-preview"
                dangerouslySetInnerHTML={{
                  __html: excelHtml,
                }}
              />

          )}

          <div
            ref={previewRef}
            className="word-preview"
          />

        </div>

        <div className="preview-footer">

          <button
            className="download-btn"
            onClick={downloadOriginal}
          >
            <Download size={16}/>
            Download Original
          </button>

        </div>

      </div>

    </div>
  );
}

export default TemplatePreviewModal;