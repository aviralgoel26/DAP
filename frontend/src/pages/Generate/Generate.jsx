import "./generate.css";
import { useEffect, useState } from "react";
import { Play } from "lucide-react";
import { toast } from "sonner";

import PageHeader from "../../components/ui/PageHeader";
import Card from "../../components/ui/Card";
import Button from "../../components/ui/Button";

import TemplateSelector from "../../components/generate/TemplateSelector";
import PlaceholderForm from "../../components/generate/PlaceholderForm";
import LogoUpload from "../../components/generate/LogoUpload";
import DocumentSummary from "../../components/generate/DocumentSummary";

import {
  getTemplates,
  getPlaceholders,
  generateDocument,
  downloadDocument
} from "../../services/documentService";

function Generate() {
  const [templates, setTemplates] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState("");
  const [placeholders, setPlaceholders] = useState([]);
  const [values, setValues] = useState({});
  const [logo, setLogo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingPlaceholders, setLoadingPlaceholders] = useState(false);
  const [loadingTemplates, setLoadingTemplates] = useState(false);

  useEffect(() => {
    loadTemplates();
  }, []);

  async function loadTemplates() {
    try {
      setLoadingTemplates(true);
      const response = await getTemplates();
      setTemplates(Array.isArray(response.data) ? response.data : []);
    } catch (e) {
      toast.error("Failed to load templates.");
      setTemplates([]);
    } finally {
      setLoadingTemplates(false);
    }
  }

  useEffect(() => {
    if (!selectedTemplate) {
      setPlaceholders([]);
      setValues({});
      setLogo(null);
      return;
    }
    loadPlaceholders(selectedTemplate);
  }, [selectedTemplate]);

  async function loadPlaceholders(templateName) {
    try {
      setLoadingPlaceholders(true);
      const response = await getPlaceholders(templateName);
      setPlaceholders(Array.isArray(response.data) ? response.data : []);
      // Reset values when switching templates
      setValues({});
    } catch (e) {
      toast.error(`Failed to load placeholders for ${templateName}`);
      setPlaceholders([]);
    } finally {
      setLoadingPlaceholders(false);
    }
  }

  function updateValue(key, value) {
    setValues((prev) => ({ ...prev, [key]: value }));
  }

  async function handleGenerate() {
    try {
      setLoading(true);
      const request = {
        templateName: selectedTemplate,
        placeholders: values,
      };

      const formData = new FormData();
      formData.append("data", JSON.stringify(request));

      if (logo) {
        formData.append("logo", logo);
      }

      const response = await generateDocument(formData);
      const filename = response.generatedFile;
      
      toast.success("Document generated! Starting download...");

      // Use the API client to stream the download properly
      const downloadRes = await downloadDocument(filename);
      const url = window.URL.createObjectURL(new Blob([downloadRes.data]));
      const link = document.createElement("a");
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (e) {
      const msg = e?.response?.data?.message || "Failed to generate document";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <PageHeader
        title="Generate documents"
        description="Fill your templates with data in one guided flow."
      />

      <div className="generate-grid">
        <div className="generate-main">
          <Card>
            <div className="generate-form-section">
              <TemplateSelector
                templates={templates}
                selectedTemplate={selectedTemplate}
                onChange={setSelectedTemplate}
                disabled={loadingTemplates || loading}
              />

              {loadingPlaceholders && (
                <div style={{ color: "var(--text-secondary)", fontSize: "14px" }}>
                  Loading placeholders...
                </div>
              )}

              {selectedTemplate && !loadingPlaceholders && placeholders.length > 0 && (
                <PlaceholderForm
                  placeholders={placeholders}
                  values={values}
                  onChange={updateValue}
                  disabled={loading}
                />
              )}

              {selectedTemplate && !loadingPlaceholders && (
                <LogoUpload logo={logo} onChange={setLogo} disabled={loading} />
              )}
            </div>

            <div className="generate-actions">
              <Button
                size="lg"
                loading={loading}
                disabled={!selectedTemplate || loadingPlaceholders}
                onClick={handleGenerate}
                rightIcon={<Play size={16} fill="currentColor" />}
              >
                Generate
              </Button>
            </div>
          </Card>
        </div>

        <div className="generate-sidebar">
          <DocumentSummary
            template={selectedTemplate}
            placeholders={placeholders}
            logo={logo}
          />
        </div>
      </div>
    </>
  );
}

export default Generate;