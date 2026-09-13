import apiClient from "./apiClient";

export const getHistory = async () => {
  const response = await apiClient.get("/history");
  return response.data;
};

export const downloadHistoryFile = async (filename) => {
  const response = await apiClient.get(`/documents/download/${filename}`, {
    responseType: "blob",
  });
  return response;
};