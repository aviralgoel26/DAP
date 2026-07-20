import axios from "axios";

const API = axios.create({

    baseURL: "http://localhost:5050/api"

});

export const getHistory = async () => {

    const response = await API.get(

        "/history"

    );

    return response.data;

};

export const downloadHistoryFile = async (filename) => {

    const response = await API.get(

        `/documents/download/${filename}`,

        {

            responseType: "blob"

        }

    );

    return response;

};