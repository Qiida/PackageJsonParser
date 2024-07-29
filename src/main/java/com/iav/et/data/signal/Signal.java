package com.iav.et.data.signal;

import com.google.gson.JsonObject;

public class Signal {
    final private String name;
    final private String path;
    final private String category;
    final private String accessType;
    final private String systemIdentifier;

    public Signal(String name, String path, String category, String accessType, String systemIdentifier) {
        this.name = name;
        this.path = path;
        this.category = category;
        this.accessType = accessType;
        this.systemIdentifier = systemIdentifier;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Path", path);
        jsonObject.addProperty("Category", category);
        jsonObject.addProperty("AccessType", accessType);
        jsonObject.addProperty("SystemIdentifier", systemIdentifier);
        return jsonObject;
    }
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getCategory() {
        return category;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getSystemIdentifier() {
        return systemIdentifier;
    }
}
