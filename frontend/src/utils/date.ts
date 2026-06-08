export function formatDate(value: string | null) {
  if (!value) {
    return "No expiry";
  }

  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(new Date(value));
}

export function toIsoFromLocalDateTime(value: string) {
  if (!value) {
    return undefined;
  }

  return new Date(value).toISOString();
}
