import { Clock } from "lucide-react";
import EmptyState from "../ui/EmptyState";

function HistoryEmpty({ hasSearch }) {
  return (
    <EmptyState
      icon={<Clock size={24} />}
      title={hasSearch ? "No results found" : "No history yet"}
      description={
        hasSearch
          ? "Try a different search term."
          : "Generate documents to see them appear here."
      }
    />
  );
}

export default HistoryEmpty;