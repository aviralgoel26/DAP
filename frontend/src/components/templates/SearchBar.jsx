function SearchBar({ value, onChange }) {

    return (

        <input
        className="searchBar"

            type="text"

            placeholder="Search templates..."

            value={value}

            onChange={(e) => onChange(e.target.value)}

        />

    );

}

export default SearchBar;