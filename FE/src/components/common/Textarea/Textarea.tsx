import './Textarea.css';

interface TextareaProps {
  name?: string;
  placeholder?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  error?: string;
  label?: string;
  required?: boolean;
  rows?: number;
  disabled?: boolean;
}

export const Textarea = ({
  name,
  placeholder,
  value,
  onChange,
  error,
  label,
  required = false,
  rows = 5,
  disabled = false,
}: TextareaProps) => {
  return (
    <div className="textarea-wrapper">
      {label && (
        <label className="textarea-label">
          {label}
          {required && <span className="required">*</span>}
        </label>
      )}
      <textarea
        name={name}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        disabled={disabled}
        rows={rows}
        className={`textarea ${error ? 'textarea-error' : ''}`}
      />
      {error && <span className="textarea-error-message">{error}</span>}
    </div>
  );
};

