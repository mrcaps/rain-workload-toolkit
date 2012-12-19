package com.mrcaps;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONTweaker {
	private static final Log log = LogFactory.getLog(JSONTweaker.class);
	protected JSONObject obj;
	
	public JSONTweaker(JSONObject obj) {
		this.obj = obj;
	}
	
	protected String[] splitPath(String path) {
		String[] split = path.split("\\.");
		//return split.length == 0 ? new String[]{path} : split;
		return split;
	}
	
	/**
	 * Get or set the object at the given path to a value
	 * @param path
	 * @param val a JSON-parseable value, or null if we should not set anything
	 * @return displaced object, or null if there was none.
	 * @throws JSONException for an unparseable string or lookup error
	 */
	public Object modJSON(String path, String val) throws JSONException {
		return mod(path, new JSONObject(val));
	}

	/**
	 * Get or set the object at the given path to a value
	 * @param path
	 * @param val an Object (Number, String, or JSONObject) to put
	 * @return displaced object, or null if there was none.
	 * @throws JSONException for a lookup error
	 */
	public Object mod(String path, Object val) throws JSONException {
		String[] split = splitPath(path);
		
		Object cur = obj;
		//get object down to next-to-last split
		for (int i = 0; i < split.length; ++i) {
			String part = split[i];
			int arrayDx = -1;
			if (part.endsWith("]")) {
				arrayDx = Integer.parseInt(
						part.substring(part.indexOf("[") + 1, part.length() - 1));
				part = part.substring(0, part.indexOf("["));
			}
			
			if (!(obj instanceof JSONObject)) {
				throw new JSONException("Object was indexable at part " + part + " of " 
							+ Arrays.toString(split));
			}
			JSONObject curObj = (JSONObject) cur;
			if (arrayDx < 0) {
				cur = curObj.get(part);
				
				if (i == split.length - 1 && null != val) {
					curObj.put(part, val);
				}
			} else {
				JSONArray curArray = curObj.getJSONArray(part);
				//index into array if possible
				cur = curArray.get(arrayDx);
				
				if (i == split.length - 1 && null != val) {
					curArray.put(arrayDx, val);
				}
			}
		}
		
		return cur;
	}
	
	/**
	 * 
	 * @param props properties to use when overriding
	 * @param prefix prefix for command-line args
	 * @param obj the object to modify
	 * @return
	 * @throws JSONException 
	 */
	public static JSONObject overrideJSON(
			Map<Object, Object> props, 
			String prefix, 
			JSONObject obj) {
		
		//TODO: modifies the underlying object now.
		JSONObject oc = obj;
		JSONTweaker tweak = new JSONTweaker(oc);
		for (Map.Entry<Object, Object> en : props.entrySet()) {
			String key = en.getKey().toString();
			String val = en.getValue().toString();
			if (key.startsWith(prefix)) {
				key = key.substring(prefix.length());
				try {
					if (val.startsWith("\"")) {
						tweak.mod(key, val.substring(1, val.length()-2));
					} else {
						//try a Number.
						//XXX: currently only deals with Integers
						try {
							int ival = Integer.parseInt(val);
							tweak.mod(key, ival);
						} catch (NumberFormatException ex) {
							tweak.modJSON(key, val);
						}
					}
				} catch (JSONException ex) {
					//skip keys we can't set.
					log.info("Skipping override key " + key);
				}
			}
		}
		return oc;
	}
}
