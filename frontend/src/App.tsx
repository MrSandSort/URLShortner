import { useEffect, useState } from "react";
import { getHealth } from "./api/urlShortenerApi";
import { HistoryList } from "./components/HistoryList";
import { ResultCard } from "./components/ResultCard";
import { ShortenForm } from "./components/ShortenForm";
import { StatsLookup } from "./components/StatsLookup";
import { StatusBadge } from "./components/StatusBadge";
import { useLocalHistory } from "./hooks/useLocalHistory";
import type { ShortUrlResponse } from "./types/url";

function App() {
  const [latest, setLatest] = useState<ShortUrlResponse | null>(null);
  const [apiStatus, setApiStatus] = useState<"checking" | "up" | "down">("checking");
  const { items, addItem, clearItems } = useLocalHistory();

  useEffect(() => {
    getHealth()
      .then((health) => setApiStatus(health.status === "UP" ? "up" : "down"))
      .catch(() => setApiStatus("down"));
  }, []);

  function handleCreated(item: ShortUrlResponse) {
    setLatest(item);
    addItem(item);
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div className="brand-mark">S</div>
        <StatusBadge status={apiStatus} />
      </header>

      <div className="workspace">
        <section className="primary-column">
          <ShortenForm onCreated={handleCreated} />
          {latest && <ResultCard item={latest} />}
        </section>

        <aside className="side-column">
          <StatsLookup />
          <HistoryList items={items} onClear={clearItems} />
        </aside>
      </div>
    </main>
  );
}

export default App;
