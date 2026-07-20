import Button from "../ui/Button";
import Input from "../ui/Input";
import SettingsSection from "./SettingsSection";
import SettingsToggle from "./SettingsToggle";

function SettingsForm({ settings, setSettings, onSave, saving }) {
  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        onSave();
      }}
    >
      <SettingsSection
        title="Company Information"
        description="These details can be used as default placeholders in your templates."
      >
        <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <Input
            label="Company Name"
            placeholder="Acme Corp"
            value={settings.companyName || ""}
            onChange={(e) =>
              setSettings({ ...settings, companyName: e.target.value })
            }
          />
          <Input
            label="Company Address"
            placeholder="123 Business St."
            value={settings.companyAddress || ""}
            onChange={(e) =>
              setSettings({ ...settings, companyAddress: e.target.value })
            }
          />
          <Input
            label="Company Email"
            type="email"
            placeholder="hello@acmecorp.com"
            value={settings.companyEmail || ""}
            onChange={(e) =>
              setSettings({ ...settings, companyEmail: e.target.value })
            }
          />
        </div>
      </SettingsSection>

      <SettingsSection
        title="Preferences"
        description="Application behavior and defaults."
      >
        <SettingsToggle
          label="Auto Download"
          description="Automatically start downloading generated documents."
          checked={settings.autoDownload ?? true}
          onChange={(value) =>
            setSettings({ ...settings, autoDownload: value })
          }
        />
        <SettingsToggle
          label="Keep History"
          description="Save generation records in the history tab."
          checked={settings.keepHistory ?? true}
          onChange={(value) =>
            setSettings({ ...settings, keepHistory: value })
          }
        />
      </SettingsSection>

      <div style={{ marginTop: "32px", display: "flex", justifyContent: "flex-end" }}>
        <Button type="submit" loading={saving}>
          Save Settings
        </Button>
      </div>
    </form>
  );
}

export default SettingsForm;