import { cn } from "../../utils/cn";

function Input({
  label,
  placeholder = "",
  value,
  onChange,
  type = "text",
  name,
  required = false,
  disabled = false,
  error = "",
  helperText = "",
  className = "",
}) {
  return (
    <div className={cn("input-group", className)}>
      {label && (
        <label className="input-label">
          {label}

          {required && (
            <span className="input-required">*</span>
          )}
        </label>
      )}

      <input
        className={cn(
          "input-field",
          error && "input-error"
        )}
        type={type}
        name={name}
        value={value}
        placeholder={placeholder}
        onChange={onChange}
        disabled={disabled}
      />

      {error ? (
        <span className="input-error-text">
          {error}
        </span>
      ) : (
        helperText && (
          <span className="input-helper">
            {helperText}
          </span>
        )
      )}
    </div>
  );
}

export default Input;