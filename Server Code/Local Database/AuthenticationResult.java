public class AuthenticationResult {

    private String error;
    private boolean authenticated;

    // Constructor for a valid authentication operation.
    public AuthenticationResult() {
        this.authenticated = true;
        this.error = null;
    }

    // Constructor for a failed authentication operation.
    public AuthenticationResult(String error) {
        this.authenticated = false;
        this.error = error;
    }

    // Get the results of the authentication operation.
    public boolean isValid() {
        return authenticated;
    }

    // Get the error in authentication -in case any exists-.
    public String error() {
        return error;
    }

    public String toString() {
        if (authenticated)
            return "authenticated";
        return error;
    }

}
