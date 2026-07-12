import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:5050/api",
});

export const getTemplates = () => {

    return API.get("/templates");

};

export const getPlaceholders = (template) => {

    return API.get(`/documents/${template}/placeholders`);

};

export const generateDocument = async (formData) => {

    const response = await API.post(
        "/documents/generate",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
            }
        }
    );

    return response.data;

};
