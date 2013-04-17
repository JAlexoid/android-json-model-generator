android-json-model-generator
============================

A simple JSON Object Model generator for Android (JSON to Java)
Specifically lightweeight object generator for Android.

Run 
`$ java -cp json-20090211.jar eu.activelogic.json.parser.JSONFileGenerator JSONFile`

The Java object will be output into the stdout.

The object will have a constructor taking a JSONObject as input:

		"org.json.datatype":{
			"java.date.format":"YYYY-MM-DD'T'HH:mm:ss'Z'", - Will create a SimpleDateFormat for date parsing (Not Thread safe)
			java.date.format.tz":"GMT",
			java.class.type":"eu.activelogic.android.uone.files.UONode", - the name of the target class
			resource_path":"string:rw", - 
			kind":"enum:file|directory:ro", - Will create an enum with only a getter and make the field final
			path":"string:rw", - Will create a writable string property with the name of path
			is_public":"boolean:rw", - will create a a isPublic writable Boolean(object) property
			parent_path":"string:ro", - will create a parentPath string read only and final(constructor initiated) property
			volume_path":"string:ro",
			key":"string:ro",
			when_created":"date:ro", - Read only java.util.Date property with parsing by the SimpleDateFormat and with the timezone
			when_changed":"date:ro",
			generation":"integer:ro",
			generation_created":"integer:ro",
			content_path":"string:ro",
			hash":"string:rw",
			public_url":"string:ro",
			size":"integer:rw",
			has_children":"boolean:ro",
			is_root":"boolean:ro",
			children":"list:type:UONode" - a list of types(expecting a constructor taking a JSONObject), alternatively a list of value objects Integer, String and similar.
		}

License
===========
   Copyright 2013 Aleksandr Panzin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.