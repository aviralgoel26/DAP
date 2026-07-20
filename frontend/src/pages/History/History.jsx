import "./history.css";
import { useEffect, useMemo, useState } from "react";
import { Search, Clock } from "lucide-react";

import PageHeader from "../../components/ui/PageHeader";
import HistoryTable from "../../components/history/HistoryTable";
import HistoryEmpty from "../../components/history/HistoryEmpty";
import LoadingSpinner from "../../components/ui/LoadingSpinner";

import { getHistory, downloadHistoryFile } from "../../services/historyService";

function History() {
  const [history, setHistory] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadHistory();
  }, []);

  async function loadHistory() {
    try {
      setLoading(true);
      const data = await getHistory();
      setHistory(data);
    } finally {
      setLoading(false);
    }
  }

  async function handleDownload(filename) {
    const response = await downloadHistoryFile(filename);
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }

  const filteredHistory = useMemo(
    () => history.filter(item =>
      item.filename.toLowerCase().includes(search.toLowerCase())
    ),
    [history, search]
  );

  return (
    <>
      <PageHeader
        title="History"
        description="A record of every document generation run."
      />

      <div className="history-toolbar">
        <div className="search-box">
          <Search size={15} />
          <input
            type="text"
            placeholder="Search generated documents..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : filteredHistory.length === 0 ? (
        <HistoryEmpty hasSearch={!!search} />
      ) : (
        <HistoryTable history={filteredHistory} onDownload={handleDownload} />
      )}
    </>
  );
}

export default History;