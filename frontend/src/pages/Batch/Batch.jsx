import "./batch.css";
import { useEffect, useState } from "react";
import { Play } from "lucide-react";
import { toast } from "sonner";

import PageHeader from "../../components/ui/PageHeader";
import Card from "../../components/ui/Card";
import Button from "../../components/ui/Button";

import BatchTemplateSelector from "../../components/batch/BatchTemplateSelector";
import BatchSummary from "../../components/batch/BatchSummary";
import PlaceholderForm from "../../components/generate/PlaceholderForm";
import LogoUpload from "../../components/generate/LogoUpload";

import {
  getTemplates,
  getPlaceholders,
  generateBatch,
  downloadBatch
} from "../../services/documentService";

function Batch() {
  const [templates, setTemplates] = useState([]);
  const [selectedTemplates, setSelectedTemplates] = useState([]);
  const [placeholders, setPlaceholders] = useState([]);
  const [values, setValues] = useState({});
  const [logo, setLogo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingTemplates, setLoadingTemplates] = useState(false);
  const [loadingPlaceholders, setLoadingPlaceholders] = useState(false);

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
    if (selectedTemplates.length === 0) {
      setPlaceholders([]);
      setValues({});
      return;
    }
    loadPlaceholders(selectedTemplates);
  }, [selectedTemplates]);

  async function loadPlaceholders(templateNames) {
    try {
      setLoadingPlaceholders(true);
      const all = new Set();
      const responses = await Promise.all(
        templateNames.map(template => getPlaceholders(template).catch(e => {
            console.error(`Failed to load placeholders for ${template}`, e);
            return { data: [] };
        }))
      );

      responses.forEach(response => {
        if (Array.isArray(response.data)) {
            response.data.forEach(item => all.add(item));
        }
      });

      setPlaceholders([...all]);
    } catch (e) {
      toast.error("Failed to load placeholders.");
      setPlaceholders([]);
    } finally {
      setLoadingPlaceholders(false);
    }
  }

  function updateValue(key, value) {
    setValues(prev => ({ ...prev, [key]: value }));
  }

  async function handleGenerate() {
    try {
      setLoading(true);
      const request = {
        templates: selectedTemplates,
        placeholders: values
      };

      const formData = new FormData();
      formData.append("data", JSON.stringify(request));

      if (logo) {
        formData.append("logo", logo);
      }

      const response = await generateBatch(formData);
      const filename = response.zipFile;
      
      toast.success("Batch generated! Starting download...");
      
      const download = await downloadBatch(filename);

      const url = window.URL.createObjectURL(new Blob([download.data]));
      const link = document.createElement("a");
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (e) {
      const msg = e?.response?.data?.message || "Failed to generate batch";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <PageHeader
        title="Batch Generation"
        description="Generate multiple documents and download them as a ZIP archive."
      />

      <div className="generate-grid">
        <div className="generate-main">
          <Card>
            <div className="generate-form-section">
              <BatchTemplateSelector
                templates={templates}
                selected={selectedTemplates}
                onChange={setSelectedTemplates}
                disabled={loadingTemplates || loading}
              />

              {loadingPlaceholders && (
                <div style={{ color: "var(--text-secondary)", fontSize: "14px" }}>
                  Loading placeholders...
                </div>
              )}

              {selectedTemplates.length > 0 && !loadingPlaceholders && placeholders.length > 0 && (
                <PlaceholderForm
                  placeholders={placeholders}
                  values={values}
                  onChange={updateValue}
                  disabled={loading}
                />
              )}

              {selectedTemplates.length > 0 && !loadingPlaceholders && (
                <LogoUpload logo={logo} onChange={setLogo} disabled={loading} />
              )}
            </div>

            <div className="generate-actions">
              <Button
                size="lg"
                loading={loading}
                disabled={selectedTemplates.length === 0 || loadingPlaceholders}
                onClick={handleGenerate}
                rightIcon={<Play size={16} fill="currentColor" />}
              >
                Generate ZIP
              </Button>
            </div>
          </Card>
        </div>

        <div className="generate-sidebar">
          <BatchSummary
            selectedTemplates={selectedTemplates}
            placeholders={placeholders}
            logo={logo}
          />
        </div>
      </div>
    </>
  );
}

export default Batch;