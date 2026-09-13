import apiClient from "./apiClient";

export const getTemplates = async () => {
  const response = await apiClient.get("/templates");
  return response.data;
};

export const uploadTemplate = async (templateId, file) => {
  const formData = new FormData();
  formData.append("templateId", templateId);
  formData.append("file", file);

  const response = await apiClient.post("/templates/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  return response.data;
};

export const deleteTemplate = async (templateId) => {
  const response = await apiClient.delete(`/templates/${templateId}`);
  return response.data;
};

export async function previewTemplate(templateName) {
  const response = await apiClient.get(
    `/templates/preview/${encodeURIComponent(templateName)}`,
    {
      responseType: "blob",
    }
  );

  return response.data;
}