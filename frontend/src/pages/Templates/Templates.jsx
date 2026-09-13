import "./templates.css";
import { useEffect, useMemo, useState } from "react";
import { Search, Upload, FolderOpen } from "lucide-react";
import { toast } from "sonner";

import PageHeader from "../../components/ui/PageHeader";
import Button from "../../components/ui/Button";
import TemplateUpload from "../../components/templates/TemplateUpload";
import TemplateGrid from "../../components/templates/TemplateGrid";
import EmptyState from "../../components/ui/EmptyState";
import LoadingSpinner from "../../components/ui/LoadingSpinner";
import TemplatePreviewModal from "../../components/templates/TemplatePreviewModal";

import {
  getTemplates,
  uploadTemplate,
  deleteTemplate,
} from "../../services/templateService";

function Templates() {
  const [templates, setTemplates] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [showUpload, setShowUpload] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [previewTemplate, setPreviewTemplate] = useState(null);

  useEffect(() => {
    loadTemplates();
  }, []);

  async function loadTemplates() {
    try {
      setLoading(true);
      const data = await getTemplates();
      setTemplates(Array.isArray(data) ? data : []);
    } catch {
      toast.error("Failed to load templates. Is the server running?");
      setTemplates([]);
    } finally {
      setLoading(false);
    }
  }

  async function handleUpload(templateId, file) {
    try {
      setLoading(true);
      await uploadTemplate(templateId, file);
      setShowUpload(false);
      toast.success(`Template "${templateId}" uploaded successfully.`);
      await loadTemplates();
    } catch (err) {
      const message =
        err?.response?.data?.message || "Upload failed. Please try again.";
      toast.error(message);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(templateId) {
    if (deletingId) return;
    setDeletingId(templateId);
    try {
      await deleteTemplate(templateId);
      toast.success(`Template "${templateId}" deleted.`);
      await loadTemplates();
    } catch (err) {
      const message =
        err?.response?.data?.message || "Delete failed. Please try again.";
      toast.error(message);
    } finally {
      setDeletingId(null);
    }
  }

  const filteredTemplates = useMemo(
    () =>
      templates.filter((t) =>
        t.name.toLowerCase().includes(search.toLowerCase())
      ),
    [templates, search]
  );

  return (
    <>
      <PageHeader
        title="Templates"
        description="Word and Excel templates with placeholders your team can fill."
        action={
          <Button
            leftIcon={<Upload size={15} />}
            onClick={() => setShowUpload((v) => !v)}
          >
            Upload Template
          </Button>
        }
      />
      <TemplatePreviewModal
    template={previewTemplate}
    open={!!previewTemplate}
    onClose={() => setPreviewTemplate(null)}
/>

      {showUpload && (
        <div className="template-upload-panel">
          <TemplateUpload
            onUpload={handleUpload}
            onCancel={() => setShowUpload(false)}
            loading={loading}
          />
        </div>
      )}

      <div className="templates-toolbar">
        <div className="search-box">
          <Search size={15} />
          <input
            type="text"
            placeholder="Search templates..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            aria-label="Search templates"
          />
        </div>
      </div>
      

      {loading && !showUpload ? (
        <LoadingSpinner />
      ) : filteredTemplates.length === 0 ? (
        <EmptyState
          icon={<FolderOpen size={24} />}
          title={search ? "No templates match your search" : "No templates yet"}
          description={
            search
              ? "Try a different search term."
              : "Upload a DOCX or XLSX template to get started."
          }
          actionLabel={search ? undefined : "Upload Template"}
          onAction={search ? undefined : () => setShowUpload(true)}
        />
      ) : (
        <TemplateGrid
          templates={filteredTemplates}
          onDelete={handleDelete}
          deletingId={deletingId}
          onPreview={setPreviewTemplate}
        />
      )}
    </>
  );
}

export default Templates;