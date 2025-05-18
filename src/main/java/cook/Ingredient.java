package cook;

// package com.example.yourpackage; // Add your package declaration

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class Ingredient {
    private String name;
    private boolean available;
    private Set<String> dietaryTags; // e.g., "vegan", "gluten-free", "dairy"
    private List<Ingredient> potentialAlternatives; // Alternatives specific to this ingredient if it's unavailable

    public Ingredient(String name) {
        this.name = name;
        this.available = true; // Default to available
        this.dietaryTags = new HashSet<>();
        this.potentialAlternatives = new ArrayList<>();
    }



    // --- Getters ---
    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public Set<String> getDietaryTags() {
        return dietaryTags;
    }

    public List<Ingredient> getPotentialAlternatives() {
        return potentialAlternatives;
    }

    // --- Setters / Modifiers ---
    public void setName(String name) {
        this.name = name;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void addDietaryTag(String tag) {
        this.dietaryTags.add(tag.toLowerCase()); // Store tags consistently
    }



    public void addPotentialAlternative(Ingredient alternative) {
        this.potentialAlternatives.add(alternative);
    }

    // --- Overrides for collection handling (especially for Map keys) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(name, that.name); // Primarily identify by name
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); // Primarily identify by name
    }

    @Override
    public String toString() {
        return name + (available ? "" : " (Unavailable)");
    }
}
