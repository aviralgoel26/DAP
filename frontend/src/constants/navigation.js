import {
    LayoutDashboard,
    FileText,
    Files,
    FolderOpen,
    History,
    Settings
} from "lucide-react";

export const navigation = [

    {
        name: "Dashboard",
        path: "/",
        icon: LayoutDashboard
    },

    {
        name: "Generate Document",
        path: "/generate",
        icon: FileText
    },

    {
        name: "Batch Generation",
        path: "/batch",
        icon: Files
    },

    {
        name: "Templates",
        path: "/templates",
        icon: FolderOpen
    },

    {
        name: "History",
        path: "/history",
        icon: History
    },

    {
        name: "Settings",
        path: "/settings",
        icon: Settings
    }

];