import './Input.css';

interface InputProps {
  type?: string;
  name?: string;
  placeholder?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onKeyPress?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  error?: string;
  label?: string;
  required?: boolean;
  disabled?: boolean;
}

export const Input = ({
  type = 'text',
  name,
  placeholder,
  value,
  onChange,
  onKeyPress,
  error,
  label,
  required = false,
  disabled = false,
}: InputProps) => {
  return (
    <div className="input-wrapper">
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="required">*</span>}
        </label>
      )}
      <input
        type={type}
        name={name}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onKeyPress={onKeyPress}
        disabled={disabled}
        className={`input ${error ? 'input-error' : ''}`}
      />
      {error && <span className="input-error-message">{error}</span>}
    </div>
  );
};

