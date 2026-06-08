import { Activity, AlertCircle } from "lucide-react";

type StatusBadgeProps = {
  status: "checking" | "up" | "down";
};

export function StatusBadge({ status }: StatusBadgeProps) {
  const label = status === "checking" ? "Checking API" : status === "up" ? "API online" : "API offline";

  return (
    <div className={`status-badge status-badge--${status}`} title={label}>
      {status === "down" ? <AlertCircle size={16} /> : <Activity size={16} />}
      <span>{label}</span>
    </div>
  );
}
