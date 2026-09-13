import apiClient from "./apiClient";

export const getTemplates = () => {
  return apiClient.get("/templates");
};

export const getPlaceholders = (template) => {
  return apiClient.get(`/documents/${template}/placeholders`);
};

export const generateDocument = async (formData) => {
  const response = await apiClient.post("/documents/generate", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

export const generateBatch = async (formData) => {
  const response = await apiClient.post("/batch/generate", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

export const downloadBatch = (filename) => {
  return apiClient.get(`/batch/download/${filename}`, {
    responseType: "blob",
  });
};

export const downloadDocument = (filename) => {
  return apiClient.get(`/documents/download/${filename}`, {
    responseType: "blob",
  });
};
