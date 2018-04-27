public class User {

    private String id, pass, firstName, lastName, email;

    public User(String id, String pass, String firstName, String lastName, String email) {
        this.id = id;
        this.pass = pass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getPass() {
        return pass;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

}