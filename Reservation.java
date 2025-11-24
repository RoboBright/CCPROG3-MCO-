/**
 * Represents a reservation made by a guest.
 * Stores the guest name, check-in day, and check-out day.
 */
public class Reservation {

    private String guestName;
    private int checkIn;
    private int checkOut;

    /**
     * Constructs a Reservation object.
     *
     * @param guest the name of the guest making the reservation
     * @param in the check-in day (1–29)
     * @param out the check-out day (2–30, must be greater than check-in)
     */
    public Reservation(String guest, int in, int out) {
        this.guestName = guest;
        this.checkIn = in;
        this.checkOut = out;
    }

    /**
     * Returns the name of the guest who made the reservation.
     *
     * @return the guest name
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * Returns the check-in day.
     *
     * @return the check-in day
     */
    public int getCheckIn() {
        return checkIn;
    }

    /**
     * Returns the check-out day.
     *
     * @return the check-out day
     */
    public int getCheckOut() {
        return checkOut;
    }

    /**
     * Calculates the total price for this reservation.
     * The price is computed using each Date's final price, which already
     * includes the property type multiplier and the environmental rate.
     * If any date does not exist, the total becomes 0.
     *
     * @param property the property containing the dates
     * @return the total price of the reservation
     */
    public double getTotalPrice(Property property) {
        double total = 0;
        int day = checkIn;
        double multiplier = property.getType().getMultiplier();

        while (day < checkOut) {
            Date date = property.getDateByDay(day);

            if (date != null) {
                total = total + date.getFinalPrice(multiplier);
            } else {
                total = 0;
                day = checkOut;
            }

            day = day + 1;
        }

        return total;
    }

    /**
     * Creates a breakdown of nightly prices for this reservation.
     * Each price already includes all modifiers.
     * Missing dates produce a value of 0.
     *
     * @param property the property to use for price lookup
     * @return an array containing the nightly prices
     */
    public double[] getBreakdown(Property property) {
        int nights = checkOut - checkIn;
        double[] breakdown = new double[nights];

        int index = 0;
        int day = checkIn;
        double multiplier = property.getType().getMultiplier();

        while (index < nights) {
            Date date = property.getDateByDay(day);

            if (date != null) {
                breakdown[index] = date.getFinalPrice(multiplier);
            } else {
                breakdown[index] = 0;
            }

            index = index + 1;
            day = day + 1;
        }

        return breakdown;
    }
}
