package com.apm.jenkins.plugins;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;
import java.util.logging.Logger;

public class TagsUtil {

    private static transient final Logger LOGGER = Logger.getLogger(TagsUtil.class.getName());

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

    public static JSONArray convertTagsToJSONArray(Map<String, Set<String>> tags){
        JSONArray result = new JSONArray();
        for (final Iterator<Map.Entry<String, Set<String>>> iter = tags.entrySet().iterator(); iter.hasNext();){
            Map.Entry<String, Set<String>> entry = iter.next();
            String name = entry.getKey();
            Set<String> values = entry.getValue();
            for (String value : values){
                if ("".equals(value)){
                    result.add(name); // Tag with no value
                }else{
                    result.add(String.format("%s:%s", name, value));
                }
            }
        }
        return result;
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
            
        System.out.println("Metrics added to Json:" + result.toString());
        return result;        
    }

    public static String[] convertTagsToArray(Map<String, Set<String>> tags){
        List<String> result = new ArrayList<>();
        for (final Iterator<Map.Entry<String, Set<String>>> iter = tags.entrySet().iterator(); iter.hasNext();){
            Map.Entry<String, Set<String>> entry = iter.next();
            String name = entry.getKey();
            Set<String> values = entry.getValue();
            for (String value : values){
                if("".equals(value)){
                    result.add(name);
                }else{
                    result.add(String.format("%s:%s", name, value));
                }
            }
        }
        Collections.sort(result);
        return result.toArray(new String[0]);
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

    public static Map<String, String> convertTagsToMapSingleValues(Map<String, Set<String>> tags) {
        if(tags == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
            if(entry.getValue() != null && entry.getValue().size() == 1) {
                result.put(entry.getKey(), entry.getValue().iterator().next());
            } else {
                LOGGER.warning("Unsupported multi-value tag in this context: Tag '"+ entry.getKey() + "' will be ignored.");
            }
        }

        return result;
    }
}