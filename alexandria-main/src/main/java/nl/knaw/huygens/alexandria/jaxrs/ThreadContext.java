package nl.knaw.huygens.alexandria.jaxrs;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.huygens.Log;

public class ThreadContext {
  private static ThreadLocal<Map<String, Object>> threadLocalMap = new ThreadLocal<Map<String, Object>>();
  private static ThreadLocal<String> threadLocalUsername = new ThreadLocal<String>();

  private static Map<String, Object> getMap() {
    Map<String, Object> inner = threadLocalMap.get();
    if (inner == null) {
      inner = new HashMap<>();
      threadLocalMap.set(inner);
    }
    return inner;
  }

  public static void put(String key, Object value) {
    getMap().put(key, value);
  }

  public static Object get(String key) {
    return getMap().get(key);
  }

  @Override
  protected void finalize() throws Throwable {
    Log.info("Session.finalize()");
    super.finalize();
    threadLocalMap.remove();
    threadLocalUsername.remove();
  }

  public static String getUserName() {
    return threadLocalUsername.get();
  }

  public static void setUserName(String username) {
    threadLocalUsername.set(username);
  }
}
