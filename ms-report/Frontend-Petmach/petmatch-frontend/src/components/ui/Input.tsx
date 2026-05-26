import type { InputHTMLAttributes } from "react";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  icon?: React.ReactNode;
}

export function Input({ label, icon, className = "", ...props }: InputProps) {
  return (
    <label className="block">
      {label && (
        <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
          {label}
        </span>
      )}

      <div className="flex h-14 items-center gap-3 rounded-xl border border-[#2b2b31] bg-[#151519] px-4 focus-within:border-[#f5c400]">
        {icon && <span className="text-[#81818b]">{icon}</span>}

        <input
          className={`w-full bg-transparent text-white placeholder:text-[#6f6f79] outline-none ${className}`}
          {...props}
        />
      </div>
    </label>
  );
}