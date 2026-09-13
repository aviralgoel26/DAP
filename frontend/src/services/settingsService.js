import apiClient from "./apiClient";

export const getSettings = async () => {
  const response = await apiClient.get("/settings");
  return response.data;
};

export const saveSettings = async (settings) => {
  const response = await apiClient.put("/settings", settings);
  return response.data;
};