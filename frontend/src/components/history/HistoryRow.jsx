import { Download, FileText } from "lucide-react";
import Button from "../ui/Button";

function HistoryRow({ item, onDownload }) {
  const sizeKb = (item.size / 1024).toFixed(1);
  const date = new Date(item.lastModified).toLocaleDateString("en-US", {
    year: "numeric", month: "short", day: "numeric",
  });
  const time = new Date(item.lastModified).toLocaleTimeString("en-US", {
    hour: "2-digit", minute: "2-digit",
  });

  return (
    <tr>
      <td>
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <span style={{
            width: 32, height: 32, borderRadius: 8,
            background: "var(--primary-light)", color: "var(--primary)",
            display: "flex", alignItems: "center", justifyContent: "center",
            flexShrink: 0
          }}>
            <FileText size={15} />
          </span>
          <span style={{ fontWeight: 500, fontSize: 13.5 }}>{item.filename}</span>
        </div>
      </td>
      <td style={{ color: "var(--text-secondary)", fontSize: 13.5 }}>{sizeKb} KB</td>
      <td>
        <div style={{ fontSize: 13.5 }}>{date}</div>
        <div style={{ fontSize: 12, color: "var(--text-secondary)" }}>{time}</div>
      </td>
      <td style={{ textAlign: "right" }}>
        <Button
          size="sm"
          variant="secondary"
          leftIcon={<Download size={13} />}
          onClick={() => onDownload(item.filename)}
        >
          Download
        </Button>
      </td>
    </tr>
  );
}

export default HistoryRow;