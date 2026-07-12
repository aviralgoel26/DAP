import PageHeader from "../../components/ui/PageHeader";
import Section from "../../components/ui/Section";
import Card from "../../components/ui/Card";
import Button from "../../components/ui/Button";
import "./dashboard.css";

function Dashboard() {
  return (
    <>
      <PageHeader
        title="Dashboard"
        description="Welcome back! Manage templates, generate documents and monitor activity."
        action={
          <Button>
            Generate Document
          </Button>
        }
      />

      <Section>

        {/* Statistics */}

        <div className="stats-grid">

          <Card
            title="Templates"
          >
            <h2 className="stat-number">
              12
            </h2>

            <p className="caption">
              Available Templates
            </p>
          </Card>

          <Card
            title="Generated"
          >
            <h2 className="stat-number">
              328
            </h2>

            <p className="caption">
              Documents Generated
            </p>
          </Card>

          <Card
            title="Batch Jobs"
          >
            <h2 className="stat-number">
              27
            </h2>

            <p className="caption">
              Completed Successfully
            </p>
          </Card>

          <Card
            title="Storage"
          >
            <h2 className="stat-number">
              84 MB
            </h2>

            <p className="caption">
              Generated Files
            </p>
          </Card>

        </div>

        {/* Quick Actions */}

        <Card
          title="Quick Actions"
          subtitle="Frequently used operations"
        >

          <div className="quick-actions">

            <Button>
              Generate Document
            </Button>

            <Button variant="secondary">
              Batch Generation
            </Button>

            <Button variant="secondary">
              Upload Template
            </Button>

            <Button variant="ghost">
              View History
            </Button>

          </div>

        </Card>

      </Section>
    </>
  );
}

export default Dashboard;