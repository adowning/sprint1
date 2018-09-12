package com.andrews.app.tracker;


import com.cloudant.sync.documentstore.DocumentRevision;
import java.util.HashMap;
import java.util.Map;


public class Location {

    private Location() {}

    public Location(Map<String, String> params) {
        this.setDescription("locationUpdate");
        this.setParams(params);
        this.setCompleted(false);
        this.setType(DOC_TYPE);
    }

    // this is the revision in the database representing this task
    private DocumentRevision rev;
    public DocumentRevision getDocumentRevision() {
        return rev;
    }

    static final String DOC_TYPE = "com.andrews.app.tracker.Location";
    private String type = DOC_TYPE;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    private boolean completed;
    public boolean isCompleted() {
        return this.completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    private String description;
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String desc) {
        this.description = desc;
    }

    private Map<String, String> params;
    public Map<String, String> getParams() {
        return this.params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "{ desc: " + getDescription() + ", completed: " + isCompleted() + "}";
    }

    public static Location fromRevision(DocumentRevision rev) {
        Location t = new Location();
        t.rev = rev;
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.getBody().asMap();
        if(map.containsKey("type") && map.get("type").equals(Location.DOC_TYPE)) {
           t.setType((String) map.get("type"));
            t.setParams((Map<String, String>) map.get("params"));

            return t;
        }
        return null;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("type", type);
//        map.put("params", params);
        map.put("deviceId", params.get("deviceId"));
        map.put("clientTime", params.get("time"));
        map.put("lon", params.get("lon"));
        map.put("lat", params.get("lat"));
        map.put("accuracy", params.get("accuracy"));
        map.put("provider", params.get("provider"));

        return map;
    }

}