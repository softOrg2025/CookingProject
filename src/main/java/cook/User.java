package cook;

public class User {
    protected String name;
    protected String email;
    protected String password;
    protected Role role;





    public User(String name , String email , String pass , Role role){
        this.name = name;
        this.email = email;
        password = pass;
        this.role = role;
    }

    public User(){
        this.name = "name";
        this.email = "email";
        password = "pass";
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }




}
