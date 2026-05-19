import axios, { AxiosError, type AxiosInstance } from "axios";
import type { ApiResponse } from "@/types/api";

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080",
  timeout: 10000,
  withCredentials: true
});

request.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResponse<unknown>;
    if (typeof payload?.code === "number" && payload.code !== 0) {
      return Promise.reject(new Error(payload.message || "request failed"));
    }
    return response;
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const message = error.response?.data?.message || error.message || "network error";
    return Promise.reject(new Error(message));
  }
);

export default request;
