package cook;

import java.util.ArrayList;
import java.util.List;
public class Customer extends User {
    private List<String> preferences = new ArrayList<String>();
    private List<String> allergies = new ArrayList<String>();
    public Customer(String name , String email , String password ){
        super(name , email , password , Role.Customer);

    }

    public boolean savePreferences(String option) {
        if(preferences.contains(option)){
            return false;
        }
        preferences.add(option);
        return true;
    }

    public boolean saveAllergy(String allergy) {
        if(allergies.contains(allergy)){
            return false;
        }
        allergies.add(allergy);
        return true;
    }

    public boolean allergyExist(String string) {
        return allergies.contains(string) ;

    }

    public List<String> getAllergies() {
        return allergies;
    }
}

