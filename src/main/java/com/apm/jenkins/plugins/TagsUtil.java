package com.apm.jenkins.plugins;

import java.util.*;
import java.util.logging.Logger;
import net.sf.json.JSONObject;

public class TagsUtil {
    private static final Logger logger = Logger.getLogger(TagsUtil.class.getName());

    public static HashMap<String, Set<String>> merge(Map<String, Set<String>> dest, Map<String, Set<String>> orig) {
        if (dest == null) {
            dest = new HashMap<>();
        }
        if (orig == null) {
            orig = new HashMap<>();
        }
        for (final Iterator<HashMap.Entry<String, Set<String>>> iter = orig.entrySet().iterator(); iter.hasNext();){
            HashMap.Entry<String, Set<String>> entry = iter.next();
            final String oName = entry.getKey();
            Set<String> dValues = dest.containsKey(oName) ? dest.get(oName) : new HashSet<String>();
            if (dValues == null) {
                dValues = new HashSet<>();
            }
            Set<String> oValues = entry.getValue();
            if (oValues != null) {
                dValues.addAll(oValues);
            }
            dest.put(oName, dValues);
        }
        return (HashMap<String, Set<String>>) dest;
    }

    public static JSONObject convertHashMapToJSONObject(HashMap<String, Object> tags){
    	JSONObject result = new JSONObject();        
        for (final Iterator<HashMap.Entry<String, Object>> iter = tags.entrySet().iterator(); iter.hasNext();){
        	JSONObject jsonObject = new JSONObject();
            HashMap.Entry<String, Object> entry = iter.next();
            String name = entry.getKey();
            Object value = entry.getValue();            
            jsonObject.put(name, value);
            
            result.putAll(jsonObject);            
        }           	
            
        logger.fine("Metrics added to Json:" + result.toString());
        return result;        
    }

    public static Map<String,Set<String>> addTagToTags(Map<String, Set<String>> tags, String name, String value) {
        if(tags == null){
            tags = new HashMap<>();
        }
        if(!tags.containsKey(name)){
            tags.put(name, new HashSet<>());
        }
        tags.get(name).add(value);
        return tags;
    }
}