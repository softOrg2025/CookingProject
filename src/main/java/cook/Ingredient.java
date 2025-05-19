package cook;

import java.util.*;

public class Ingredient {
    private String name;
    private boolean available;
    private final Set<String> dietaryTags;
    private final List<Ingredient> potentialAlternatives;

    public Ingredient(String name) {
        this.name = Objects.requireNonNull(name, "Ingredient name cannot be null");
        this.available = true;
        this.dietaryTags = new HashSet<>();
        this.potentialAlternatives = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public Set<String> getDietaryTags() {
        return Collections.unmodifiableSet(dietaryTags);
    }

    public List<Ingredient> getPotentialAlternatives() {
        return Collections.unmodifiableList(potentialAlternatives);
    }


    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Ingredient name cannot be null");
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Methods to add tags and alternatives
    public void addDietaryTag(String tag) {
        dietaryTags.add(tag.toLowerCase());
    }

    public void addPotentialAlternative(Ingredient alternative) {
        if (alternative == null || this.equals(alternative)) {
            return;
        }
        potentialAlternatives.add(alternative);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient that)) return false;
        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name + (available ? "" : " (Unavailable)");
    }

}