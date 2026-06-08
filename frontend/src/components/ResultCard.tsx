import { Check, Copy, ExternalLink } from "lucide-react";
import { useState } from "react";
import type { ShortUrlResponse } from "../types/url";
import { formatDate } from "../utils/date";

type ResultCardProps = {
  item: ShortUrlResponse;
};

export function ResultCard({ item }: ResultCardProps) {
  const [copied, setCopied] = useState(false);

  async function copyShortUrl() {
    await navigator.clipboard.writeText(item.shortUrl);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1400);
  }

  return (
    <article className="result-card">
      <div className="result-main">
        <p className="eyebrow">Latest short URL</p>
        <a className="short-url" href={item.shortUrl} target="_blank" rel="noreferrer">
          {item.shortUrl}
        </a>
        <p className="long-url">{item.longUrl}</p>
      </div>

      <div className="action-row">
        <button className="icon-button" type="button" onClick={copyShortUrl} title="Copy short URL">
          {copied ? <Check size={18} /> : <Copy size={18} />}
        </button>
        <a className="icon-button" href={item.shortUrl} target="_blank" rel="noreferrer" title="Open short URL">
          <ExternalLink size={18} />
        </a>
      </div>

      <dl className="meta-grid">
        <div>
          <dt>Code</dt>
          <dd>{item.shortCode}</dd>
        </div>
        <div>
          <dt>Created</dt>
          <dd>{formatDate(item.createdAt)}</dd>
        </div>
        <div>
          <dt>Expiry</dt>
          <dd>{formatDate(item.expiresAt)}</dd>
        </div>
      </dl>
    </article>
  );
}
