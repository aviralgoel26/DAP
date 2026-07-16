import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:5050/api",
});

export const getTemplates = async () => {

    const response = await API.get("/templates");

    return response.data;

};

export const uploadTemplate = async (templateId, file) => {

    const formData = new FormData();

    formData.append("templateId", templateId);

    formData.append("file", file);

    const response = await API.post(

        "/templates/upload",

        formData,

        {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        }

    );

    return response.data;

};


export const deleteTemplate = async (templateId) => {

    const response = await API.delete(

        `/templates/${templateId}`

    );

    return response.data;

};