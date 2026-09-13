import { useEffect, useState } from "react";
import { toast } from "sonner";
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
      setLoading(true);
      const data = await getSettings();
      // Only update if data is present to prevent wiping defaults on error
      if (data) {
        setSettings(data);
      }
    } catch (e) {
      toast.error("Failed to load settings. Using defaults.");
    } finally {
      setLoading(false);
    }
  }

  async function handleSave() {
    try {
      setSaving(true);
      const updated = await saveSettings(settings);
      setSettings(updated);
      toast.success("Settings saved successfully.");
    } catch (e) {
      const msg = e?.response?.data?.message || "Failed to save settings.";
      toast.error(msg);
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