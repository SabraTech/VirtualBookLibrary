public class ClientCredentials {

  /** Value of the "API key" shown under "Simple API Access". */
  static final String API_KEY = "AIzaSyCg0fiC1WQUj7j09xO0bobAdLbRIeVo7W8";

  static void errorIfNotSpecified() {
    if (API_KEY.startsWith("Enter ")) {
      System.err.println(API_KEY);
      System.exit(1);
    }
  }
}