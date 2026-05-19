import axios, { AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from "axios";
import type { ApiResponse } from "@/types/api";
import {
  clearAccessToken,
  dispatchUnauthorizedEvent,
  getAccessToken
} from "@/utils/auth-token";
import { UnauthorizedError } from "@/utils/request-error";

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080",
  timeout: 10000
});

function getRequestPath(requestUrl: string): string {
  if (!requestUrl) {
    return "";
  }
  if (requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
    try {
      return new URL(requestUrl).pathname;
    } catch {
      return requestUrl;
    }
  }
  return requestUrl;
}

request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const accessToken = getAccessToken();
  if (accessToken) {
    config.headers.set("Authorization", `Bearer ${accessToken}`);
  }
  return config;
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
    const status = error.response?.status;
    const requestPath = getRequestPath(error.config?.url ?? "");
    const isLoginRequest = requestPath.endsWith("/auth/login");
    const isCurrentUserRequest = requestPath.endsWith("/auth/me");

    if (status === 401 && !isLoginRequest) {
      clearAccessToken();
      if (!isCurrentUserRequest) {
        dispatchUnauthorizedEvent(window.location.pathname + window.location.search + window.location.hash);
      }
      return Promise.reject(new UnauthorizedError(error.response?.data?.message || "login required"));
    }

    const message = error.response?.data?.message || error.message || "network error";
    return Promise.reject(new Error(message));
  }
);

export default request;
