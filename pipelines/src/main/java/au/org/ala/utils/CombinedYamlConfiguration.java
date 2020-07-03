package au.org.ala.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CombinedYamlConfiguration {

  private final LinkedHashMap<String, Object> combined;

  public CombinedYamlConfiguration(String...configs) throws FileNotFoundException {
    combined = new LinkedHashMap<String, Object>();
    for (String config : configs) {
      InputStream input = new FileInputStream(new File(config));
      Yaml yaml = new Yaml();
      LinkedHashMap<String, Object> loaded = (LinkedHashMap<String, Object>) yaml.load(input);
      combined.putAll(loaded);
    }
  }
  public LinkedHashMap<String, Object> subSet(String...keys) {
    LinkedHashMap<String, Object> partial = new LinkedHashMap<String, Object>();
    if (keys.length == 0) {
      return combined;
    }
    for (String key : keys) {
      LinkedHashMap<String, Object> subList = toList(key, combined.get(key));
      if (subList.size() > 0) {
        partial.putAll(subList);
      } else {
        // Try to find in the tree if is a var.with.dots
        if (key.contains(".")) {
          LinkedHashMap<String, Object> current = traverse(key);
          partial.putAll(current);
        }
      }
    }
    return partial;
  }

  private LinkedHashMap<String, Object> traverse(String key) {
    String[] keySplitted = key.split("\\.");
    LinkedHashMap<String, Object> current = combined;
    for (String keyS : keySplitted) {
      if (current.size() > 0)
        current = toList(keyS, current.get(keyS));
    }
    return current;
  }

  private LinkedHashMap<String, Object> toList(String key, Object obj) {
    if (obj instanceof LinkedHashMap) {
      return (LinkedHashMap<String, Object>) obj;
    }
    LinkedHashMap<String, Object> list = new LinkedHashMap<String, Object>();
    if (obj != null) {
      list.put(key, obj);
    }
    return list;
  }

  public String[] toArgs(String... keys) {
    List<String> argList = new ArrayList<String>();
      for (Map.Entry<String, Object> conf : subSet(keys).entrySet()) {
        argList.add(
            new StringBuffer()
                .append("--")
                .append(conf.getKey())
                .append("=")
                .append(conf.getValue())
                .toString());
      }
    return argList.toArray(new String[0]);
  }

  public Object get(String key) {
    Object value = combined.get(key);
    if (value == null && key.contains(".")) {
      // we try to traverse the tree looking for that var
      LinkedHashMap<String, Object> traversed = traverse(key);
      // If is an object, return the value, if not, the list of values
      return traversed.size() == 1? traversed.values().toArray()[0]: traversed;
    }
    return value;
  }
}
