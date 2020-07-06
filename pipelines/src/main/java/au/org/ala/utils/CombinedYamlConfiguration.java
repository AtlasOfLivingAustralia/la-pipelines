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

  private final LinkedHashMap<String, Object> combined = new LinkedHashMap<String, Object>();
  private final LinkedHashMap<String, String> mainArgs = new LinkedHashMap<String, String>();
  private final String[][] mainArgsAsList;

  public CombinedYamlConfiguration(String[] mainArgs)
      throws FileNotFoundException {
    for (String arg : mainArgs) {
      // For each arg of type --varName=value we remove the -- and split by = in varName and value
      String[] argPair = arg.replaceFirst("--", "").split("=", 2);
      // And we combine the result
      this.mainArgs.put(argPair[0], argPair[1]);
    }
    String[] yamlConfigPaths = this.mainArgs.get("config").split(",");
    this.mainArgs.remove("config"); // we remove config, because is not an pipeline configuration
    mainArgsAsList =
      new String[][] {
        this.mainArgs.keySet().toArray(new String[0]),
        this.mainArgs.values().toArray(new String[0])
      };

    for (String config : yamlConfigPaths) {
      InputStream input = new FileInputStream(new File(config));
      Yaml yaml = new Yaml();
      LinkedHashMap<String, Object> loaded = yaml.load(input);
      combined.putAll(loaded);
    }
  }

  public LinkedHashMap<String, Object> subSet(String... keys) {
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
    partial.putAll(mainArgs);
    return partial;
  }

  private LinkedHashMap<String, Object> traverse(String key) {
    String[] keySplitted = key.split("\\.");
    LinkedHashMap<String, Object> current = combined;
    for (String keyS : keySplitted) {
      if (current.size() > 0) current = toList(keyS, current.get(keyS));
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
    return toArgs(mainArgsAsList, keys);
  }

  public String[] toArgs(String[][] params, String... keys) {
    List<String> argList = new ArrayList<String>();
    for (Map.Entry<String, Object> conf : subSet(keys).entrySet()) {
      Object value = format(conf.getValue(), params);
      argList.add(
          new StringBuffer()
              .append("--")
              .append(conf.getKey())
              .append("=")
              .append(value)
              .toString());
    }
    return argList.toArray(new String[0]);
  }

  private Object format(Object value, String[][] params) {
    if (value instanceof String) {
      String formatted = (String) value;
      for (int i = 0; i < params[0].length; i++) {
        formatted = formatted.replace("{" + params[0][i] + "}", params[1][i]);
      }
      return formatted;
    } else {
      return value;
    }
  }

  public Object get(String key) {
    Object value = combined.get(key);
    if (value == null && key.contains(".")) {
      // we try to traverse the tree looking for that var
      LinkedHashMap<String, Object> traversed = traverse(key);
      // If is an object, return the value, if not, the list of values
      return traversed.size() == 1 ? traversed.values().toArray()[0] : traversed;
    }
    return value;
  }
}
