export type ShortUrlResponse = {
  shortCode: string;
  shortUrl: string;
  longUrl: string;
  createdAt: string;
  expiresAt: string | null;
};

export type ShortUrlStatsResponse = {
  shortCode: string;
  longUrl: string;
  clickCount: number;
  createdAt: string;
  expiresAt: string | null;
  lastAccessedAt: string | null;
  active: boolean;
};

export type CreateShortUrlPayload = {
  longUrl: string;
  customAlias?: string;
  expiresAt?: string;
};

export type ApiError = {
  title?: string;
  detail?: string;
  status?: number;
};
