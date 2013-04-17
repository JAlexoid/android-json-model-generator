package eu.activelogic.android.uone.files;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UOVolumes {
    public static final java.text.SimpleDateFormat S_DATE_FORMAT = new java.text.SimpleDateFormat();
    public final Long maxBytes;
    public final java.util.List<String> userNodePaths;
    public final String resourcePath;
    public final String visibleName;
    public final Long usedBytes;
    public final Integer userId;
    public final String rootNodePath;

    public UOVolumes(org.json.JSONObject in) throws Exception {
	super();
	this.maxBytes = safeLong(in, "max_bytes");
	this.resourcePath = safeString(in, "resource_path");
	this.visibleName = safeString(in, "visible_name");
	this.usedBytes = safeLong(in, "used_bytes");
	this.userId = safeInteger(in, "user_id");
	this.rootNodePath = safeString(in, "root_node_path");
	if (in.has("user_node_paths")) {
	    this.userNodePaths = new ArrayList<String>();
	    JSONArray userNodePathsArrayParse = in.getJSONArray("user_node_paths");
	    for (int i = 0; i < userNodePathsArrayParse.length(); i++) {
		this.userNodePaths.add(userNodePathsArrayParse.getString(i));
	    }
	}
	else {
	    this.userNodePaths = null;
	}
    }

    public JSONObject toJSONObject() throws JSONException {
	JSONObject me = new JSONObject();
	me.put("max_bytes", this.maxBytes);
	if (this.userNodePaths != null) {
	    me.put("user_node_paths", this.userNodePaths);
	}
	me.put("resource_path", this.resourcePath);
	me.put("visible_name", this.visibleName);
	me.put("used_bytes", this.usedBytes);
	me.put("user_id", this.userId);
	me.put("root_node_path", this.rootNodePath);

	return me;
    }

    String safeString(org.json.JSONObject in, String key) throws JSONException {
	return in.has(key) ? in.getString(key) : null;
    }

    Boolean safeBoolean(org.json.JSONObject in, String key) throws JSONException {
	return in.has(key) ? toBoolean(in.getString(key)) : null;
    }

    Boolean toBoolean(String s) {
	return s.equalsIgnoreCase("TRUE") || s.equals("1") || s.toUpperCase().startsWith("Y");
    }

    java.util.Date safeDate(org.json.JSONObject in, String key) throws JSONException, ParseException {
	return in.has(key) ? toDate(in.getString(key)) : null;
    }

    java.util.Date toDate(String date) throws ParseException {
	return S_DATE_FORMAT.parse(date);
    }

    String formatDate(java.util.Date date) {
	return date != null ? S_DATE_FORMAT.format(date) : null;
    }

    Integer safeInteger(org.json.JSONObject in, String key) throws JSONException {
	return in.has(key) ? Integer.valueOf(in.getString(key)) : null;
    }

    Long safeLong(org.json.JSONObject in, String key) throws JSONException {
	return in.has(key) ? Long.valueOf(in.getString(key)) : null;
    }

    Double safeDouble(org.json.JSONObject in, String key) throws JSONException {
	return in.has(key) ? Double.valueOf(in.getString(key)) : null;
    }

    <T extends Enum<T>> T safeEnum(Class<T> clz, org.json.JSONObject in, String key) throws JSONException {
	return in.has(key) ? Enum.valueOf(clz, in.getString(key).toUpperCase()) : null;
    }
    
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append("[").append(visibleName).append(" = ").append(rootNodePath).append("; ").append(userNodePaths).append("]");

	return sb.toString();
    }
    
}
