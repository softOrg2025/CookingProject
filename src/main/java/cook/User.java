package cook;

public class User {
    protected String name;
    protected String email;
    protected String password;
    protected Role role;


    public User(String name, String email, String pass, Role role) {
        this.name = name;
        this.email = email;
        this.password = pass; // تم تعديل اسم المتغير هنا ليتطابق مع الاستخدام
        this.role = role;
    }

    // --- هذا هو الميثود المطلوب ---
    public String getName() {
        return name;
    }
    // -----------------------------

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