package com.iav.et.data.signal;

import com.google.gson.JsonObject;

public class Bus extends Signal{

    final private String pdu;
    final private String node;
    final private String frame;
    public Bus(String name, String path, String category, String accessType, String systemIdentifier, String pdu, String node, String frame) {
        super(name, path, category, accessType, systemIdentifier);
        this.pdu = pdu;
        this.node = node;
        this.frame = frame;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("Pdu", pdu);
        jsonObject.addProperty("Node", node);
        jsonObject.addProperty("Frame", frame);
        return jsonObject;
    }

    public String getPdu() {
        return pdu;
    }

    public String getNode() {
        return node;
    }

    public String getFrame() {
        return frame;
    }
}
