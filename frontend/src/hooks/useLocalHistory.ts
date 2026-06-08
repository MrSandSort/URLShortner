import { useEffect, useState } from "react";
import type { ShortUrlResponse } from "../types/url";

const STORAGE_KEY = "shortlink-console-history";
const MAX_ITEMS = 8;

export function useLocalHistory() {
  const [items, setItems] = useState<ShortUrlResponse[]>([]);

  useEffect(() => {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return;
    }

    try {
      setItems(JSON.parse(raw) as ShortUrlResponse[]);
    } catch {
      window.localStorage.removeItem(STORAGE_KEY);
    }
  }, []);

  function addItem(item: ShortUrlResponse) {
    setItems((current) => {
      const next = [item, ...current.filter((existing) => existing.shortCode !== item.shortCode)].slice(0, MAX_ITEMS);
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
      return next;
    });
  }

  function clearItems() {
    window.localStorage.removeItem(STORAGE_KEY);
    setItems([]);
  }

  return { items, addItem, clearItems };
}
