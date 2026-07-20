import Breadcrumb from "../ui/BreadCrumb";
import SearchBar from "./SearchBar";
import UserMenu from "./UserMenu";

function Header() {
  return (
    <header className="header">
      <div className="header-left">
        <Breadcrumb />
      </div>

      <div className="header-right">
        <SearchBar />
        <UserMenu />
      </div>
    </header>
  );
}

export default Header;