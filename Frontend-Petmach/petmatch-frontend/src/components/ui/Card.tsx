export function Card({
  children,
  className = "",
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div className={`rounded-2xl border border-[#24242a] bg-[#17171b] ${className}`}>
      {children}
    </div>
  );
}