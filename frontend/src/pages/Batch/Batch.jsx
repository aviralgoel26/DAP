import "./batch.css";
import { useEffect, useState } from "react";
import { Play } from "lucide-react";

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

  useEffect(() => {
    loadTemplates();
  }, []);

  async function loadTemplates() {
    try {
      const response = await getTemplates();
      setTemplates(response.data);
    } catch (e) {
      console.error(e);
    }
  }

  useEffect(() => {
    if (selectedTemplates.length === 0) {
      setPlaceholders([]);
      return;
    }
    loadPlaceholders();
  }, [selectedTemplates]);

  async function loadPlaceholders() {
    try {
      const all = new Set();
      const responses = await Promise.all(
        selectedTemplates.map(template => getPlaceholders(template))
      );

      responses.forEach(response => {
        response.data.forEach(item => all.add(item));
      });

      setPlaceholders([...all]);
    } catch (e) {
      console.error(e);
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
      console.error(e);
      alert("Failed to generate batch");
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
              />

              {selectedTemplates.length > 0 && placeholders.length > 0 && (
                <PlaceholderForm
                  placeholders={placeholders}
                  values={values}
                  onChange={updateValue}
                />
              )}

              {selectedTemplates.length > 0 && (
                <LogoUpload logo={logo} onChange={setLogo} />
              )}
            </div>

            <div className="generate-actions">
              <Button
                size="lg"
                loading={loading}
                disabled={selectedTemplates.length === 0}
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