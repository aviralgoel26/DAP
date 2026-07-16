import "./batch.css";

import { useEffect, useState } from "react";

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

        const response = await getTemplates();

        setTemplates(response.data);

    }

    useEffect(() => {

        if (selectedTemplates.length === 0) {

            setPlaceholders([]);

            return;

        }

        loadPlaceholders();

    }, [selectedTemplates]);

    async function loadPlaceholders() {

        const all = new Set();

        const responses = await Promise.all(
    selectedTemplates.map(template =>
        getPlaceholders(template)
    )
);

responses.forEach(response => {

    response.data.forEach(item => all.add(item));

});

setPlaceholders([...all]);

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

            templates: selectedTemplates,

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
            await generateBatch(formData);

        const filename =
            response.zipFile;

        const download =
            await downloadBatch(filename);

        const url =
            window.URL.createObjectURL(
                new Blob([download.data])
            );

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
                title="Batch Generation"
                description="Generate multiple documents and download them as ZIP."
            />

            <div className="batch-grid">

                <Card>

                    <BatchTemplateSelector
                        templates={templates}
                        selected={selectedTemplates}
                        onChange={setSelectedTemplates}
                    />

                    <PlaceholderForm
    placeholders={placeholders}
    values={values}
    onChange={updateValue}
/>
                    <BatchSummary
    selectedTemplates={selectedTemplates}
    placeholders={placeholders}
    logo={logo}
/>

                    <LogoUpload
    logo={logo}
    onChange={setLogo}
/>

                    <Button
                        loading={loading}
                        onClick={handleGenerate}
                    >
                        Generate ZIP
                    </Button>

                </Card>

                <BatchSummary />

            </div>

        </>

    );

}

export default Batch;