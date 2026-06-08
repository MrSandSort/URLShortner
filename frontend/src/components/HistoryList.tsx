import { Clock, Copy, ExternalLink, Trash2 } from "lucide-react";
import type { ShortUrlResponse } from "../types/url";
import { formatDate } from "../utils/date";

type HistoryListProps = {
  items: ShortUrlResponse[];
  onClear: () => void;
};

export function HistoryList({ items, onClear }: HistoryListProps) {
  async function copy(value: string) {
    await navigator.clipboard.writeText(value);
  }

  return (
    <section className="tool-panel tool-panel--compact">
      <div className="section-title section-title--split">
        <div>
          <Clock size={20} />
          <h2>Recent</h2>
        </div>
        {items.length > 0 && (
          <button className="icon-button icon-button--ghost" type="button" onClick={onClear} title="Clear history">
            <Trash2 size={17} />
          </button>
        )}
      </div>

      {items.length === 0 ? (
        <p className="empty-state">Created links will appear here.</p>
      ) : (
        <div className="history-list">
          {items.map((item) => (
            <article className="history-item" key={item.shortCode}>
              <div>
                <a href={item.shortUrl} target="_blank" rel="noreferrer">
                  {item.shortCode}
                </a>
                <p>{formatDate(item.createdAt)}</p>
              </div>
              <div className="action-row">
                <button className="icon-button" type="button" onClick={() => copy(item.shortUrl)} title="Copy short URL">
                  <Copy size={16} />
                </button>
                <a className="icon-button" href={item.shortUrl} target="_blank" rel="noreferrer" title="Open short URL">
                  <ExternalLink size={16} />
                </a>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
