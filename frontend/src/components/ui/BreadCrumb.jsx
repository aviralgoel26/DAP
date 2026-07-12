import { useLocation } from "react-router-dom";

function Breadcrumb() {
  const { pathname } = useLocation();

  const page = pathname === "/"
    ? "Dashboard"
    : pathname
        .replace("/", "")
        .replace("-", " ");

  return (
    <div className="breadcrumb">
      <span className="heading-md">
        {page
          .split(" ")
          .map(
            word =>
              word.charAt(0).toUpperCase() +
              word.slice(1)
          )
          .join(" ")}
      </span>
    </div>
  );
}

export default Breadcrumb;