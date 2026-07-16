import "./templates.css";

import { useEffect, useMemo, useState } from "react";

import PageHeader from "../../components/ui/PageHeader";

import SearchBar from "../../components/templates/SearchBar";
import TemplateUpload from "../../components/templates/TemplateUpload";
import TemplateGrid from "../../components/templates/TemplateGrid";

import {
    getTemplates,
    uploadTemplate,
    deleteTemplate
} from "../../services/templateService";

function Templates() {

    const [templates, setTemplates] = useState([]);

    const [search, setSearch] = useState("");

    const [loading, setLoading] = useState(false);

    useEffect(() => {

        loadTemplates();

    }, []);

    async function loadTemplates() {

        try {

            setLoading(true);

            const data = await getTemplates();

            setTemplates(data);

        }

        finally {

            setLoading(false);

        }

    }

    async function handleUpload(templateId, file) {

    console.log("handleUpload called");

    console.log(templateId);

    console.log(file);

    try {

        setLoading(true);

        await uploadTemplate(templateId, file);

        console.log("Upload finished");

        await loadTemplates();

    }

    catch(err){

        console.error(err);

    }

    finally{

        setLoading(false);

    }

}
async function handleDelete(templateId) {

    const confirmed = window.confirm(

        `Delete template "${templateId}"?`

    );

    if (!confirmed) return;

    try {

        setLoading(true);

        await deleteTemplate(templateId);

        await loadTemplates();

    }

    finally {

        setLoading(false);

    }

}

    const filteredTemplates = useMemo(() => {

        return templates.filter(template =>

            template.name
                .toLowerCase()
                .includes(
                    search.toLowerCase()
                )

        );

    }, [templates, search]);

    return (

        <>

            <PageHeader

                title="Templates"

                description="Manage all document templates."

            />

            <SearchBar

                value={search}

                onChange={setSearch}

            />

            <TemplateUpload

                onUpload={handleUpload}

            />

            {

                loading ?

                    <p>Loading...</p>

                    :

                   <TemplateGrid

    templates={filteredTemplates}

    onDelete={handleDelete}

/>

            }

        </>

    );

}

export default Templates;