import "./generate.css";

import PageHeader from "../../components/ui/PageHeader";
import Card from "../../components/ui/Card";
import Button from "../../components/ui/Button";

import TemplateSelector from "../../components/generate/TemplateSelector";
import PlaceholderForm from "../../components/generate/PlaceholderForm";
import LogoUpload from "../../components/generate/LogoUpload";
import DocumentSummary from "../../components/generate/DocumentSummary";
import { useEffect, useState } from "react";
import {generateDocument} from "../../services/documentService";

import {
    getTemplates,
    getPlaceholders
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

    const response = await getTemplates();

    setTemplates(response.data);

}
useEffect(() => {

    if (!selectedTemplate) return;

    loadPlaceholders();

}, [selectedTemplate]);

async function loadPlaceholders() {

    const response =
        await getPlaceholders(selectedTemplate);

    setPlaceholders(response.data);

}
function updateValue(key, value) {

    setValues(prev => ({

        ...prev,

        [key]: value

    }));

}
async function handleGenerate() {

    try {

        setLoading(true);

        const request = {

            templateName: selectedTemplate,

            placeholders: values

        };

        const formData = new FormData();

        formData.append(

            "data",

            JSON.stringify(request)

        );

        if (logo) {

            formData.append(

                "logo",

                logo

            );

        }

       const response =
    await generateDocument(formData);

const filename =
    response.generatedFile;

window.location.href =
    `http://localhost:5050/api/documents/download/${filename}`;


const link =
    document.createElement("a");

link.href = url;

link.download = filename;

document.body.appendChild(link);

link.click();

link.remove();

window.URL.revokeObjectURL(url);
    }

    finally {

        setLoading(false);

    }

}

    return (

        <>

            <PageHeader
                title="Generate Document"
                description="Generate customized documents using uploaded templates."
            />

            <div className="generate-grid">

                <Card>

                    <TemplateSelector
    templates={templates}
    selectedTemplate={selectedTemplate}
    onChange={setSelectedTemplate}
/>

                    <PlaceholderForm
    placeholders={placeholders}
    values={values}
    onChange={updateValue}
/>

                    <LogoUpload

    logo={logo}

    onChange={setLogo}

/>

                    <Button

    loading={loading}

    onClick={handleGenerate}

>

Generate Document

</Button>

                </Card>

                <DocumentSummary />

            </div>

        </>

    );

}

export default Generate;