import { cn } from "../../utils/cn";

function Section({
  children,
  className = "",
}) {
  return (
    <section className={cn("section", className)}>
      {children}
    </section>
  );
}

export default Section;