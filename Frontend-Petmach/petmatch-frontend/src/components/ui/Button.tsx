import type { ButtonHTMLAttributes } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger" | "ghost";
}

export function Button({
  children,
  className = "",
  variant = "primary",
  ...props
}: ButtonProps) {
  const variants = {
    primary:
      "bg-[#f5c400] text-black hover:bg-[#ffd21a] shadow-[0_0_24px_rgba(245,196,0,0.18)]",
    secondary:
      "bg-[#242429] text-white hover:bg-[#303036] border border-[#313138]",
    danger:
      "bg-[#ef4444] text-white hover:bg-[#dc2626]",
    ghost:
      "bg-transparent text-[#f5c400] hover:bg-[#f5c400]/10",
  };

  return (
    <button
      className={`h-12 rounded-xl px-5 font-bold transition disabled:cursor-not-allowed disabled:opacity-60 ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}