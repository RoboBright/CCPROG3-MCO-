/**
 * The Date class represents a specific day of a month in a property's availability calendar.
 * It stores the day number, the price for that day,m and whether it is currently reserved.
 */

public class Date {
    private int day;
    private double price;
    private boolean isReserved;

    /**
     * Constructs a Date object with a specific day and price.
     * The date is initially available (not reserved).
     * Price is automatically initialized as P 1500.0 per night.
     * @param day the day of the month (1–30)
     */
    public Date(int day) {
        this.day = day;
        this.price = 1500.0; // Default price
        this.isReserved = false;
    }

    /**
     * Retrieves the day number of this date.
     * @return the day of the month
     */
    public int getDay() {
        return day;
    }

    /**
     * Retrieves the price assigned to this date.
     * @return the price per night
     */
    public double getPrice() {
        return price;
    }

    /**
     * Updates the price for this date only if it is non-negative.
     * @param price the new price to set (must be >= 0)
     */
    public void setPrice(double price) {
        boolean isNegative = false;

        if (price < 0) {
            isNegative = true;
            System.out.println("Error: Price cannot be negative. Price remains unchanged.");
        }

        if(isNegative = false) {
            this.price = price;
        }
    }

    /**
     * Checks whether this date is available for reservation.
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
     * Marks this date as available by removing its reservation status.
     */
    public void unbook() {
        this.isReserved = false;
    }

    /**
     * Provides a string representation of this date, including its day,
     * price, and reservation status.
     * @return a formatted string with date information
     */
    public String toString() {
        return "Day " + day + " - Price: " + price + " - " + (isReserved ? "Reserved" : "Available");
    }
}
