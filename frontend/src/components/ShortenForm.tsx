import { CalendarClock, Link2, Loader2, Wand2 } from "lucide-react";
import { FormEvent, useState } from "react";
import { createShortUrl } from "../api/urlShortenerApi";
import type { ShortUrlResponse } from "../types/url";
import { toIsoFromLocalDateTime } from "../utils/date";

type ShortenFormProps = {
  onCreated: (item: ShortUrlResponse) => void;
};

export function ShortenForm({ onCreated }: ShortenFormProps) {
  const [longUrl, setLongUrl] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [expiresAt, setExpiresAt] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const created = await createShortUrl({
        longUrl: longUrl.trim(),
        customAlias: customAlias.trim() || undefined,
        expiresAt: toIsoFromLocalDateTime(expiresAt)
      });
      onCreated(created);
      setLongUrl("");
      setCustomAlias("");
      setExpiresAt("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not create short URL");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <form className="tool-panel" onSubmit={handleSubmit}>
      <div className="panel-heading">
        <div>
          <p className="eyebrow">Create</p>
          <h1>Shortlink Console</h1>
        </div>
      </div>

      <label className="field">
        <span>Destination URL</span>
        <div className="input-shell">
          <Link2 size={18} />
          <input
            required
            type="url"
            value={longUrl}
            maxLength={2048}
            placeholder="https://example.com/long/path"
            onChange={(event) => setLongUrl(event.target.value)}
          />
        </div>
      </label>

      <div className="form-grid">
        <label className="field">
          <span>Custom alias</span>
          <div className="input-shell">
            <Wand2 size={18} />
            <input
              type="text"
              value={customAlias}
              minLength={4}
              maxLength={32}
              pattern="[A-Za-z0-9_-]+"
              placeholder="spring"
              onChange={(event) => setCustomAlias(event.target.value)}
            />
          </div>
        </label>

        <label className="field">
          <span>Expires at</span>
          <div className="input-shell">
            <CalendarClock size={18} />
            <input
              type="datetime-local"
              value={expiresAt}
              onChange={(event) => setExpiresAt(event.target.value)}
            />
          </div>
        </label>
      </div>

      {error && <div className="error-message">{error}</div>}

      <button className="primary-button" type="submit" disabled={isLoading}>
        {isLoading ? <Loader2 className="spin" size={18} /> : <Link2 size={18} />}
        <span>{isLoading ? "Creating" : "Create short URL"}</span>
      </button>
    </form>
  );
}
