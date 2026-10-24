package dslabs.kvstore;

import dslabs.framework.Application;
import dslabs.framework.Command;
import dslabs.framework.Result;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class KVStore implements Application {

  public interface KVStoreCommand extends Command {}

  public interface SingleKeyCommand extends KVStoreCommand {
    String key();
  }

  @Data
  public static final class Get implements SingleKeyCommand {
    @NonNull private final String key;

    @Override
    public boolean readOnly() {
      return true;
    }
  }

  @Data
  public static final class Put implements SingleKeyCommand {
    @NonNull private final String key, value;
  }

  @Data
  public static final class Append implements SingleKeyCommand {
    @NonNull private final String key, value;
  }

  public interface KVStoreResult extends Result {}

  @Data
  public static final class GetResult implements KVStoreResult {
    @NonNull private final String value;
  }

  @Data
  public static final class KeyNotFound implements KVStoreResult {}

  @Data
  public static final class PutOk implements KVStoreResult {}

  @Data
  public static final class AppendResult implements KVStoreResult {
    @NonNull private final String value;
  }

  // Your code here...
  private final Map<String, String> map = new HashMap<>();

  @Override
  public KVStoreResult execute(Command command) {
    if (command instanceof Get) {
      Get g = (Get) command;
      // Your code here...
      String key = g.key();
      String value = map.get(key);
      if (value != null) {
        return new GetResult(value);
      } else {
        return new KeyNotFound();
      }
    }

    if (command instanceof Put) {
      Put p = (Put) command;
      // Your code here...
      String key = p.key();
      String value = p.value();
      map.put(key, value);
      return new PutOk();
    }

    if (command instanceof Append) {
      Append a = (Append) command;
      // Your code here...
      String appendKey = a.key();
      String appendValue = a.value();
      String value = map.get(appendKey);
      String result;
      if (value != null) {
        result = value + appendValue;
        map.put(appendKey, result);
      } else {
        result = appendValue;
        map.put(appendKey, result);
      }
      return new AppendResult(result);
    }

    throw new IllegalArgumentException();
  }
}
