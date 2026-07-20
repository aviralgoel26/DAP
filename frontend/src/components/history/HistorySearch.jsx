function HistorySearch({

    value,

    onChange

}) {

    return (

        <input

            type="text"

            placeholder="Search generated documents..."

            value={value}

            onChange={(e)=>onChange(e.target.value)}

        />

    );

}

export default HistorySearch;