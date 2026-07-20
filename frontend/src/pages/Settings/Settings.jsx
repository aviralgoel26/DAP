import { useEffect, useState } from "react";
import PageHeader from "../../components/ui/PageHeader";
import SettingsForm from "../../components/settings/SettingsForm";
import LoadingSpinner from "../../components/ui/LoadingSpinner";
import { getSettings, saveSettings } from "../../services/settingsService";

function Settings() {
  const [settings, setSettings] = useState({
    companyName: "",
    companyAddress: "",
    companyEmail: "",
    autoDownload: true,
    keepHistory: true
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadSettings();
  }, []);

  async function loadSettings() {
    try {
      const data = await getSettings();
      setSettings(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  async function handleSave() {
    try {
      setSaving(true);
      const updated = await saveSettings(settings);
      setSettings(updated);
      alert("Settings saved successfully.");
    } catch (e) {
      console.error(e);
      alert("Failed to save settings.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <PageHeader
        title="Settings"
        description="Configure application preferences and defaults."
      />

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div style={{ maxWidth: "700px" }}>
          <SettingsForm
            settings={settings}
            setSettings={setSettings}
            onSave={handleSave}
            saving={saving}
          />
        </div>
      )}
    </>
  );
}

export default Settings;