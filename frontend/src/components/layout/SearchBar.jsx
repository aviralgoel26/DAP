import { Search } from "lucide-react";

function SearchBar() {
  return (
    <div className="search-box">
      <Search size={15} />
      <input type="text" placeholder="Search..." />
    </div>
  );
}

export default SearchBar;