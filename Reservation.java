/**
 * Represents a reservation made by a guest.
 * Stores guest name, check-in, and check-out dates.
 */
public class Reservation {

    private String guestName;
    private int checkIn;
    private int checkOut;

    /**
     * Constructs a Reservation object.
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
     * @return guest name
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * Returns the check-in day.
     * @return check-in day
     */
    public int getCheckIn() {
        return checkIn;
    }

    /**
     * Returns the check-out day.
     * @return check-out day
     */
    public int getCheckOut() {
        return checkOut;
    }

    /**
     * Calculates total price for this reservation based on actual Date prices
     * stored in the Property. If any day in the range is missing (i.e., no Date
     * object exists), the total will become 0 (invalid reservation).
     *
     * @param property the Property whose dates are used for pricing
     * @return total price of the reservation, or 0 if any date is missing
     */
    public double getTotalPrice(Property property) {
        double total = 0;
        int day = checkIn;

        while (day < checkOut) {
            Date date = property.getDateByDay(day);

            if (date != null) {
                total = total + date.getPrice();
            } else {
                total = 0;
                day = checkOut;
            }
            day = day + 1;
        }
        return total;
    }

    /**
     * Creates a per-night breakdown of prices for this reservation.
     * Each element corresponds to one night. If a date is missing, its price is set to 0.
     *
     * @param property the Property to retrieve individual nightly prices
     * @return an array of nightly prices
     */
    public double[] getBreakdown(Property property) {
        int nights = checkOut - checkIn;
        double[] breakdown = new double[nights];

        int index = 0;
        int day = checkIn;

        while (index < nights) {
            Date date = property.getDateByDay(day);

            if (date != null) {
                breakdown[index] = date.getPrice();
            } else {
                breakdown[index] = 0;
            }

            index = index + 1;
            day = day + 1;
        }
        return breakdown;
    }
}
