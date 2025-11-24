/**
 * Enumerates the available property types and their price multipliers.
 * Each multiplier affects the final nightly price of a reservation.
 */
public enum PropertyType {

    ECO_APARTMENT(1.00),
    SUSTAINABLE_HOUSE(1.20),
    GREEN_RESORT(1.35),
    ECO_GLAMPING(1.50);

    private double multiplier;

    /**
     * Constructs a PropertyType with a specific multiplier.
     *
     * @param multiplier the value used to multiply the base price
     */
    PropertyType(double multiplier) {
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
    public String getDisplayName() {
        if (this == ECO_APARTMENT)
            return "Eco-Apartment";
        else if (this == SUSTAINABLE_HOUSE)
            return "Sustainable House";
        else if (this == GREEN_RESORT)
            return "Green Resort";
        else
            return "Eco-Glamping";
    }

    /**
     * Converts a numeric menu choice (1–4) into a PropertyType value.
     *
     * @param choice the menu choice
     * @return the corresponding PropertyType, or null if invalid
     */
    public static PropertyType fromChoice(int choice) {
        if (choice == 1)
            return ECO_APARTMENT;
        else if (choice == 2)
            return SUSTAINABLE_HOUSE;
        else if (choice == 3)
            return GREEN_RESORT;
        else if (choice == 4)
            return ECO_GLAMPING;

        return null;
    }
}
