import type {
  ApiError,
  CreateShortUrlPayload,
  ShortUrlResponse,
  ShortUrlStatsResponse
} from "../types/url";

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers
    },
    ...options
  });

  if (!response.ok) {
    let error: ApiError = {};
    try {
      error = await response.json();
    } catch {
      error = { detail: response.statusText };
    }
    throw new Error(error.detail || error.title || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export function createShortUrl(payload: CreateShortUrlPayload) {
  return request<ShortUrlResponse>("/api/v1/urls", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function getShortUrlStats(shortCode: string) {
  return request<ShortUrlStatsResponse>(`/api/v1/urls/${encodeURIComponent(shortCode)}/stats`);
}

export async function getHealth() {
  return request<{ status: string }>("/actuator/health");
}
