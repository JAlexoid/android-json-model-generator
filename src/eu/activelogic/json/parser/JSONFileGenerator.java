package eu.activelogic.json.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TimeZone;

import org.json.JSONObject;

public class JSONFileGenerator {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	if (args.length < 1)
	    return;

	JSONObject object = new JSONObject(inputStreamToString(new FileInputStream(args[0])).toString());

	if (object.has("org.json.datatype")) {
	    JSONObject dataType = object.getJSONObject("org.json.datatype");
	    object.remove("org.json.datatype");

	    String type = dataType.getString("java.class.type");

	    dataType.remove("java.class.type");

	    String className = type.lastIndexOf(".") > 0 && type.lastIndexOf(".") < type.length() - 1 ? type.substring(type.lastIndexOf(".") + 1) : null;
	    String packageName = type.lastIndexOf(".") > 0 ? type.substring(0, type.lastIndexOf(".")) : null;
	    // System.out.printf("Class: %s and package %s %n", className, packageName);

	    StringBuilder sb = new StringBuilder();

	    if (packageName != null)
		sb.append("package ").append(packageName).append(";\n");

	    StringBuilder parser = new StringBuilder();

	    StringBuilder complexParse = new StringBuilder();

	    parser.append("public ").append(className).append("(org.json.JSONObject in) throws Exception {\n super();\n");

	    StringBuilder constructor = new StringBuilder();

	    constructor.append("public JSONObject toJSONObject() throws JSONException { \n" +
		    "JSONObject me = new JSONObject();\n");

	    sb.append("public class ").append(className).append(" { \n");

	    String timeZone = null;

	    if (dataType.has("java.date.format")) {
		sb.append("public static final java.text.SimpleDateFormat S_DATE_FORMAT = new java.text.SimpleDateFormat(\"")
			.append(dataType.getString("java.date.format")).append("\");\n");
		dataType.remove("java.date.format");
		if (dataType.has("java.date.format.tz")) {
		    timeZone = dataType.getString("java.date.format.tz");
		    dataType.remove("java.date.format.tz");
		    timeZone = TimeZone.getTimeZone(timeZone).getID();

		    sb.append("static{\n S_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(\"").append(timeZone).append("\"));}\n");

		}

	    } else {
		sb.append("public static final java.text.SimpleDateFormat S_DATE_FORMAT = new java.text.SimpleDateFormat();\n");
	    }

	    StringBuilder fields = new StringBuilder();

	    StringBuilder enums = new StringBuilder();

	    for (String key : dataType.getNames(dataType)) {
		String[] typeValue = dataType.getString(key).split(":");

		String name = nameToJavaName(key);

		boolean readOnly = typeValue[typeValue.length - 1].toLowerCase().equals("ro");

		fields.append("public ");
		if (readOnly)
		    fields.append("final ");

		switch (typeValue[0]) {
		case "string":
		    fields.append("String ").append(name).append(";\n");
		    parser.append("this.").append(name).append(" = safeString(in,\"").append(key).append("\");\n");
		    constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(");\n");
		    break;
		case "boolean":
		    fields.append("Boolean ").append(name).append(";\n");
		    parser.append("this.").append(name).append(" = safeBoolean(in,\"").append(key).append("\") ;\n");
		    constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(");\n");
		    break;
		case "date":
		    fields.append("java.util.Date ").append(name).append(";\n");
		    parser.append("this.").append(name).append(" = safeDate(in,\"").append(key).append("\") ;\n");
		    constructor.append("me.put(\"").append(key).append("\", formatDate(this.").append(name).append("));\n");
		    break;
		case "integer":
		    fields.append("Integer ").append(name).append(";\n");
		    parser.append("this.").append(name).append(" = safeInteger(in,\"").append(key).append("\");\n");
		    constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(");\n");
		    break;
		case "long":
		    fields.append("Long ").append(name).append(";\n");
		    parser.append("this.").append(name).append(" = safeLong(in,\"").append(key).append("\");\n");
		    constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(");\n");
		    break;
		case "double":
		    fields.append("Double ").append(name).append(";\n");
		    parser.append("this.").append(name).append(" = safeDouble(in,\"").append(key).append("\") ;\n");
		    constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(");\n");
		    break;
		case "enum":
		    char[] enumName = name.toCharArray();
		    enumName[0] = Character.toUpperCase(enumName[0]);
		    fields.append("").append(enumName).append(" ").append(name).append(";\n");

		    parser.append("this.").append(name).append(" = safeEnum(").append(enumName).append(".class,in,\"").append(key).append("\");\n");

		    constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(" != null ? this.").append(name)
			    .append(".toString() : null);\n");

		    enums.append("static enum ").append(enumName).append(" { \n").append(typeValue[1].toUpperCase().replaceAll("\\|", ", \n"))
			    .append(";\n}\n");

		    break;
		case "list":
		    fields.append("java.util.List<").append(typeValue[2]).append("> ").append(name).append(";\n");

		    if (typeValue[1].equals("type")) {

			complexParse.append("if (in.has(\"").append(key).append("\")) {\n");
			complexParse.append("this.").append(name).append(" = new ArrayList<").append(typeValue[2]).append(">();\n");
			complexParse.append("JSONArray ").append(name).append("ArrayParse = in.getJSONArray(\"").append(key).append("\");\n");
			complexParse.append("for (int i = 0; i < ").append(name).append("ArrayParse.length(); i++) {\n");
			complexParse.append("this.").append(name).append(".add(new ").append(typeValue[2]).append("(").append(name)
				.append("ArrayParse.getJSONObject(i)));\n");
			complexParse.append("}\n");
			complexParse.append("}\n");
			complexParse.append("else {\n");
			complexParse.append("this.").append(name).append(" = null;");
			complexParse.append("}\n");

			constructor.append("if(this.").append(name).append(" != null){\n");
			constructor.append("List<JSONObject> objects = new ArrayList<JSONObject>();\n");
			constructor.append("for(").append(typeValue[2]).append(" item: this.").append(name)
				.append("){\n objects.add(item.toJSONObject());\n }\n");
			constructor.append("me.put(\"").append(key).append("\", objects);\n");
			constructor.append("}\n");

		    } else if (typeValue[1].equals("value")) {
			complexParse.append("if (in.has(\"").append(key).append("\")) {\n");
			complexParse.append("this.").append(name).append(" = new ArrayList<").append(typeValue[2]).append(">();\n");
			complexParse.append("JSONArray ").append(name).append("ArrayParse = in.getJSONArray(\"").append(key).append("\");\n");
			complexParse.append("for (int i = 0; i < ").append(name).append("ArrayParse.length(); i++) {\n");
			complexParse.append("this.").append(name).append(".add(");

			switch (typeValue[2].toLowerCase()) {
			case "string":
			    complexParse.append(name).append("ArrayParse.getString(i)");
			    break;
			case "boolean":
			    complexParse.append("toBoolean(").append(name).append("ArrayParse.getString(i))");
			    break;
			case "date":
			    complexParse.append("toDate(").append(name).append("ArrayParse.getString(i))");
			    break;
			case "integer":
			    complexParse.append(name).append("ArrayParse.getInt(i)");
			    break;
			case "long":
			    complexParse.append(name).append("ArrayParse.getLong(i)");
			    break;
			case "double":
			    complexParse.append(name).append("ArrayParse.getDouble(i)");
			    break;
			}

			complexParse.append(");\n");

			complexParse.append("}\n");
			complexParse.append("}\n");
			complexParse.append("else {\n");
			complexParse.append("this.").append(name).append(" = null;");
			complexParse.append("}\n");

			constructor.append("if(this.").append(name).append(" != null){\n");
			constructor.append("me.put(\"").append(key).append("\", this.").append(name).append(");\n");
			constructor.append("}");

		    }

		    break;
		default:
		    System.err.println("Unknown: " + typeValue[0]);
		    break;
		}

	    }

	    sb.append(enums);
	    sb.append(fields);

	    parser.append(complexParse);
	    parser.append("}\n");
	    sb.append(parser);

	    constructor.append("\nreturn me;\n}\n");

	    sb.append(constructor);

	    sb.append("    String safeString(org.json.JSONObject in, String key) throws JSONException {\n" +
		    "	return in.has(key) ? in.getString(key) : null;\n" +
		    "    }\n" +
		    "\n" +
		    "    Boolean safeBoolean(org.json.JSONObject in, String key) throws JSONException {\n" +
		    "	return in.has(key) ? toBoolean(in.getString(key)) : null;\n" +
		    "    }\n" +
		    "    \n" +
		    "    Boolean toBoolean(String s){\n" +
		    "	return s.equalsIgnoreCase(\"TRUE\") || s.equals(\"1\") || s.toUpperCase().startsWith(\"Y\");\n" +
		    "    }\n" +
		    "\n" +
		    "    java.util.Date safeDate(org.json.JSONObject in, String key) throws JSONException, ParseException {\n" +
		    "	return in.has(key) ? toDate(in.getString(key)) : null;\n" +
		    "    }\n" +
		    "\n" +
		    "    java.util.Date toDate(String date) throws ParseException{\n" +
		    "	return S_DATE_FORMAT.parse(date);\n" +
		    "    }\n" +
		    "    String formatDate(java.util.Date date) {\n" +
		    "	return date != null ? S_DATE_FORMAT.format(date) : null;\n" +
		    "    }\n" +
		    "    \n" +
		    "    Integer safeInteger(org.json.JSONObject in, String key) throws JSONException {\n" +
		    "	return in.has(key) ? Integer.valueOf(in.getString(key)) : null;\n" +
		    "    }\n" +
		    "" +
		    "    Long safeLong(org.json.JSONObject in, String key) throws JSONException {\n" +
		    "	return in.has(key) ? Long.valueOf(in.getString(key)) : null;\n" +
		    "    }\n" +
		    "\n" +
		    "    Double safeDouble(org.json.JSONObject in, String key) throws JSONException {\n" +
		    "	return in.has(key) ? Double.valueOf(in.getString(key)) : null;\n" +
		    "    }\n" +
		    "    <T extends Enum<T>> T safeEnum(Class<T> clz, org.json.JSONObject in, String key) throws JSONException {\n" +
		    "	return in.has(key) ? Enum.valueOf(clz, in.getString(key).toUpperCase()) : null;\n" +
		    "    }\n");

	    sb.append("}\n");

	    System.out.println(sb);

	}
    }

    private static StringBuilder inputStreamToString(InputStream is) throws IOException {
	String line = "";
	StringBuilder total = new StringBuilder();

	// Wrap a BufferedReader around the InputStream
	BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	// Read response until the end
	while ((line = rd.readLine()) != null) {
	    total.append(line);
	}

	// Return full string
	return total;
    }

    public static String nameToJavaName(String s) {
	char[] characters = s.toCharArray();
	characters[0] = Character.toLowerCase(characters[0]);
	for (int i = 0; i < characters.length; i++) {
	    if (i - 1 > 0 && characters[i - 1] == '_') {
		characters[i - 1] = ' ';
		characters[i] = Character.toUpperCase(characters[i]);
	    }
	}
	return String.valueOf(characters).replaceAll(" ", "");
    }

}
