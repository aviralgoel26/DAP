import HistoryRow from "./HistoryRow";

function HistoryTable({ history, onDownload }) {
  return (
    <div className="history-table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            <th>Filename</th>
            <th>Size</th>
            <th>Date</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {history.map(item => (
            <HistoryRow
              key={item.filename}
              item={item}
              onDownload={onDownload}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default HistoryTable;