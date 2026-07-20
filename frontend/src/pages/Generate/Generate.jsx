import "./generate.css";
import { useEffect, useState } from "react";
import { Play } from "lucide-react";

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
  generateDocument
} from "../../services/documentService";

function Generate() {
  const [templates, setTemplates] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState("");
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
    if (!selectedTemplate) {
      setPlaceholders([]);
      setValues({});
      return;
    }
    loadPlaceholders();
  }, [selectedTemplate]);

  async function loadPlaceholders() {
    try {
      const response = await getPlaceholders(selectedTemplate);
      setPlaceholders(response.data);
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
        templateName: selectedTemplate,
        placeholders: values
      };

      const formData = new FormData();
      formData.append("data", JSON.stringify(request));
      
      if (logo) {
        formData.append("logo", logo);
      }

      const response = await generateDocument(formData);
      const filename = response.generatedFile;
      
      // Original logic for download
      const url = `http://localhost:5050/api/documents/download/${filename}`;
      const link = document.createElement("a");
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();

    } catch (e) {
      console.error(e);
      alert("Failed to generate document");
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
              />

              {selectedTemplate && placeholders.length > 0 && (
                <PlaceholderForm
                  placeholders={placeholders}
                  values={values}
                  onChange={updateValue}
                />
              )}

              {selectedTemplate && (
                <LogoUpload logo={logo} onChange={setLogo} />
              )}
            </div>

            <div className="generate-actions">
              <Button
                size="lg"
                loading={loading}
                disabled={!selectedTemplate}
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