import Breadcrumb from "../ui/Breadcrumb";
import SearchBar from "./SearchBar";
import UserMenu from "./UserMenu";

function Header() {

  return (

    <header className="header">

      <Breadcrumb />

      <div className="header-right">

        <SearchBar />

        <UserMenu />

      </div>

    </header>

  );

}

export default Header;