package Objects;


/**
 * The Date class represents a specific day of a month in a property's
 * availability calendar. It stores the day number, a base price, an
 * environmental impact rate, and the reservation status.
 */
public class Date {

    private int day;
    private double price;
    private boolean isReserved;
    private double environmentalRate;

    /**
     * Constructs a Date object with a specific day.
     * The date is initially available (not reserved).
     * The base price is set to 1500.0 by default.
     * The environmental impact rate is set to 1.0 (100%).
     *
     * @param day the day of the month (1–30)
     */
    public Date(int day) {
        this.day = day;
        this.price = 1500.0;
        this.isReserved = false;
        this.environmentalRate = 1.0;
    }

    /**
     * Retrieves the day number of this date.
     *
     * @return the day of the month
     */
    public int getDay() {
        return day;
    }

    /**
     * Retrieves the base price assigned to this date.
     * This is the price before applying modifiers.
     *
     * @return the base price per night
     */
    public double getPrice() {
        return price;
    }

    /**
     * Updates the base price for this date only if it is at least 100.
     *
     * @param price the new base price to set
     */
    public void setPrice(double price) {
        boolean isInvalid = false;

        if (price < 100.0) {
            isInvalid = true;
            System.out.println("Error: Price cannot be less than 100.");
        }

        if (!isInvalid) {
            this.price = price;
        }
    }

    /**
     * Retrieves the environmental impact rate for this date.
     * This value ranges from 0.80 to 1.20.
     *
     * @return the environmental impact rate
     */
    public double getEnvironmentalRate() {
        return environmentalRate;
    }

    /**
     * Sets the environmental impact rate if within the allowed range
     * of 0.80 to 1.20.
     *
     * @param rate the environmental rate to set
     */
    public void setEnvironmentalRate(double rate) {
        boolean isInvalid = false;

        if (rate < 0.80 || rate > 1.20) {
            isInvalid = true;
            System.out.println("Error: Environmental rate must be between 0.80 and 1.20.");
        }

        if (!isInvalid) {
            this.environmentalRate = rate;
        }
    }

    /**
     * Computes the final nightly price for this date.
     * The formula is:
     *     final price = base price * type multiplier * environmental rate
     *
     * @param typeMultiplier the multiplier based on property type
     * @return the computed final nightly price
     */
    public double getFinalPrice(double typeMultiplier) {
        return price * typeMultiplier * environmentalRate;
    }

    /**
     * Checks whether this date is available for reservation.
     *
     * @return true if the date is not reserved; false otherwise
     */
    public boolean isAvailable() {
        return !isReserved;
    }

    /**
     * Marks this date as reserved.
     */
    public void book() {
        this.isReserved = true;
    }

    /**
     * Marks this date as available (not reserved).
     */
    public void unbook() {
        this.isReserved = false;
    }

    /**
     * Returns a formatted string containing the day, base price,
     * and reservation status.
     *
     * @return a string with date information
     */
    public String toString() {
        return "Day " + day + " - Base Price: " + price + " - " +
                (isReserved ? "Reserved" : "Available");
    }
}
