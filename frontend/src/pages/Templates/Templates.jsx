import "./templates.css";
import { useEffect, useMemo, useState } from "react";
import { Search, Upload, FolderOpen } from "lucide-react";

import PageHeader from "../../components/ui/PageHeader";
import Button from "../../components/ui/Button";
import TemplateUpload from "../../components/templates/TemplateUpload";
import TemplateGrid from "../../components/templates/TemplateGrid";
import EmptyState from "../../components/ui/EmptyState";
import LoadingSpinner from "../../components/ui/LoadingSpinner";

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

  useEffect(() => {
    loadTemplates();
  }, []);

  async function loadTemplates() {
    try {
      setLoading(true);
      const data = await getTemplates();
      setTemplates(data);
    } finally {
      setLoading(false);
    }
  }

  async function handleUpload(templateId, file) {
    try {
      setLoading(true);
      await uploadTemplate(templateId, file);
      setShowUpload(false);
      await loadTemplates();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(templateId) {
    const confirmed = window.confirm(`Delete template "${templateId}"?`);
    if (!confirmed) return;
    try {
      setLoading(true);
      await deleteTemplate(templateId);
      await loadTemplates();
    } finally {
      setLoading(false);
    }
  }

  const filteredTemplates = useMemo(
    () => templates.filter(t => t.name.toLowerCase().includes(search.toLowerCase())),
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
            onClick={() => setShowUpload(v => !v)}
          >
            Upload Template
          </Button>
        }
      />

      {showUpload && (
        <div className="template-upload-panel">
          <TemplateUpload onUpload={handleUpload} onCancel={() => setShowUpload(false)} />
        </div>
      )}

      <div className="templates-toolbar">
        <div className="search-box">
          <Search size={15} />
          <input
            type="text"
            placeholder="Search templates..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : filteredTemplates.length === 0 ? (
        <EmptyState
          icon={<FolderOpen size={24} />}
          title="No templates yet"
          description="Upload a DOCX or XLSX template to get started."
          actionLabel="Upload Template"
          onAction={() => setShowUpload(true)}
        />
      ) : (
        <TemplateGrid templates={filteredTemplates} onDelete={handleDelete} />
      )}
    </>
  );
}

export default Templates;