package Objects;


/**
 * Abstract class representing a property type and its price multiplier.
 * Each multiplier affects the final nightly price of a reservation.
 */
public abstract class PropertyType {

    private double multiplier;

    /**
     * Constructs a PropertyType with a specific multiplier.
     *
     * @param multiplier the value used to multiply the base price
     */
    protected PropertyType(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Returns the multiplier for this property type.
     *
     * @return the multiplier value
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Returns a readable name for display.
     *
     * @return the display name of the property type
     */
    public abstract String getDisplayName();

    /**
     * Converts a numeric menu choice (1–4) into a PropertyType value.
     *
     * @param choice the menu choice
     * @return the corresponding PropertyType, or null if invalid
     */
    public static PropertyType fromChoice(int choice) {
        if (choice == 1)
            return new EcoApartment();
        else if (choice == 2)
            return new SustainableHouse();
        else if (choice == 3)
            return new GreenResort();
        else if (choice == 4)
            return new EcoGlamping();

        return null;
    }
}

/**
 * Represents an Eco-Apartment property type.
 */
class EcoApartment extends PropertyType {
    public EcoApartment() {
        super(1.00);
    }

    @Override
    public String getDisplayName() {
        return "Eco-Apartment";
    }
}

/**
 * Represents a Sustainable House property type.
 */
class SustainableHouse extends PropertyType {
    public SustainableHouse() {
        super(1.20);
    }

    @Override
    public String getDisplayName() {
        return "Sustainable House";
    }
}

/**
 * Represents a Green Resort property type.
 */
class GreenResort extends PropertyType {
    public GreenResort() {
        super(1.35);
    }

    @Override
    public String getDisplayName() {
        return "Green Resort";
    }
}

/**
 * Represents an Eco-Glamping property type.
 */
class EcoGlamping extends PropertyType {
    public EcoGlamping() {
        super(1.50);
    }

    @Override
    public String getDisplayName() {
        return "Eco-Glamping";
    }
}