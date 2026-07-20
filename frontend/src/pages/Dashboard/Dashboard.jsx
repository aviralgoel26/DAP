import { useNavigate } from "react-router-dom";
import { FileText, Zap, History, UploadCloud } from "lucide-react";
import PageHeader from "../../components/ui/PageHeader";
import Button from "../../components/ui/Button";
import Card from "../../components/ui/Card";
import "./dashboard.css";

function Dashboard() {
  const navigate = useNavigate();

  return (
    <>
      <PageHeader
        title="Dashboard"
        description="Overview of your document automation workspace."
        action={
          <Button leftIcon={<Zap size={15} />} onClick={() => navigate("/generate")}>
            Generate Documents
          </Button>
        }
      />

      <div className="dashboard-body">

        {/* Quick Actions */}
        <Card title="Quick Actions" subtitle="Jump right in.">
          <div className="quick-actions-list">

            <button className="quick-action-item" onClick={() => navigate("/generate")}>
              <span className="quick-action-icon">
                <Zap size={18} />
              </span>
              <div className="quick-action-text">
                <span className="quick-action-label">Generate documents</span>
                <span className="quick-action-desc">Fill placeholders and export</span>
              </div>
            </button>

            <button className="quick-action-item" onClick={() => navigate("/templates")}>
              <span className="quick-action-icon">
                <UploadCloud size={18} />
              </span>
              <div className="quick-action-text">
                <span className="quick-action-label">Upload template</span>
                <span className="quick-action-desc">Add DOCX or XLSX</span>
              </div>
            </button>

            <button className="quick-action-item" onClick={() => navigate("/batch")}>
              <span className="quick-action-icon">
                <FileText size={18} />
              </span>
              <div className="quick-action-text">
                <span className="quick-action-label">Batch generation</span>
                <span className="quick-action-desc">Generate multiple documents</span>
              </div>
            </button>

            <button className="quick-action-item" onClick={() => navigate("/history")}>
              <span className="quick-action-icon">
                <History size={18} />
              </span>
              <div className="quick-action-text">
                <span className="quick-action-label">Recent activity</span>
                <span className="quick-action-desc">View generation history</span>
              </div>
            </button>

          </div>
        </Card>

      </div>
    </>
  );
}

export default Dashboard;