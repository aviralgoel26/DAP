import { Loader2 } from "lucide-react";

function LoadingSpinner({
  size = 32,
}) {
  return (
    <div className="loading-spinner">
      <Loader2
        size={size}
        className="spinner"
      />
    </div>
  );
}

export default LoadingSpinner;