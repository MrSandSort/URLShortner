import { BarChart3, Loader2, Search } from "lucide-react";
import { FormEvent, useState } from "react";
import { getShortUrlStats } from "../api/urlShortenerApi";
import type { ShortUrlStatsResponse } from "../types/url";
import { formatDate } from "../utils/date";

export function StatsLookup() {
  const [shortCode, setShortCode] = useState("");
  const [stats, setStats] = useState<ShortUrlStatsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const result = await getShortUrlStats(shortCode.trim());
      setStats(result);
    } catch (err) {
      setStats(null);
      setError(err instanceof Error ? err.message : "Could not load stats");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <section className="tool-panel tool-panel--compact">
      <div className="section-title">
        <BarChart3 size={20} />
        <h2>Stats</h2>
      </div>

      <form className="lookup-row" onSubmit={handleSubmit}>
        <input
          required
          value={shortCode}
          placeholder="Enter short code"
          onChange={(event) => setShortCode(event.target.value)}
        />
        <button className="icon-button icon-button--solid" type="submit" disabled={isLoading} title="Search stats">
          {isLoading ? <Loader2 className="spin" size={18} /> : <Search size={18} />}
        </button>
      </form>

      {error && <div className="error-message">{error}</div>}

      {stats && (
        <dl className="stats-grid">
          <div>
            <dt>Clicks</dt>
            <dd>{stats.clickCount.toLocaleString()}</dd>
          </div>
          <div>
            <dt>Status</dt>
            <dd>{stats.active ? "Active" : "Inactive"}</dd>
          </div>
          <div>
            <dt>Last access</dt>
            <dd>{formatDate(stats.lastAccessedAt)}</dd>
          </div>
          <div>
            <dt>Expires</dt>
            <dd>{formatDate(stats.expiresAt)}</dd>
          </div>
        </dl>
      )}
    </section>
  );
}
