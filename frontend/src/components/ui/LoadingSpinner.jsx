import { Loader2 } from "lucide-react";

function LoadingSpinner({ text = "Loading..." }) {
  return (
    <div className="loading-spinner">
      <Loader2 size={28} className="spinner" />
      <span className="loading-text">{text}</span>
    </div>
  );
}

export default LoadingSpinner;