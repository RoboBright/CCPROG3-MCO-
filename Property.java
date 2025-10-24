/**
 * Represents a property with a name, available dates, and reservations.
 * Each date has its own pricing (default 1500 in Date class).
  */
public class Property {

    private String name;
    private Date[] dates;
    private Reservation[] reservations;

    /**
     * Constructs a Property with the given name.
     * Initializes storage for up to 30 dates and 30 reservations.
     * @param name the name of the property
     */
    public Property(String name) {
        this.name = name;
        this.dates = new Date[30];
        this.reservations = new Reservation[30];
    }

    /**
     * Retrieves the property's name.
     * @return name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the property.
     * @param name new name to apply
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a Date to the property's calendar if it does not already exist.
     * @param date the Date to add
     */
    public void addDate(Date date) {
        boolean canAdd = true;

        if (date != null) {
            // check if date already exists
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].getDay() == date.getDay()) {
                    canAdd = false;
                }
            }

            // add to first available slot
            if (canAdd) {
                for (int i = 0; i < dates.length; i++) {
                    if (dates[i] == null) {
                        dates[i] = date;
                        i = dates.length;

                    }
                }
            }
        }
    }

    /**
     * Removes a date by its day number if present.
     * @param day the day to remove
     */
    public void removeDate(int day) {
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].getDay() == day) {
                dates[i] = null;
                i = dates.length;
            }
        }
    }

    /**
     * Adds a reservation if all dates from check-in to check-out exist and are available.
     * @param res the reservation to add
     * @return true if successfully added, false otherwise
     */
    public boolean addReservation(Reservation res) {
        boolean success = false;

        if (res != null && res.getCheckOut() > res.getCheckIn()) {

            boolean allAvailable = true;

            for (int d = res.getCheckIn(); d < res.getCheckOut(); d++) {
                boolean found = false;

                for (int j = 0; j < dates.length; j++) {
                    if (dates[j] != null && dates[j].getDay() == d) {
                        if (!dates[j].isAvailable()) {
                            allAvailable = false;
                        }
                        found = true;
                    }
                }
                if (!found) {
                    allAvailable = false;
                }
            }

            if (allAvailable) {
                for (int i = 0; i < reservations.length; i++) {
                    if (!success && reservations[i] == null) {
                        reservations[i] = res;

                        for (int d = res.getCheckIn(); d < res.getCheckOut(); d++) {
                            for (int j = 0; j < dates.length; j++) {
                                if (dates[j] != null && dates[j].getDay() == d) {
                                    dates[j].book();
                                }
                            }
                        }

                        success = true;
                    }
                }
            }
        }
        return success;
    }

    /**
     * Removes a reservation and unbooks its dates.
     * @param res the reservation to remove
     * @return true if found and removed, false otherwise
     */
    public boolean removeReservation(Reservation res) {
        boolean removed = false;

        if (res != null) {
            for (int i = 0; i < reservations.length; i++) {
                Reservation r = reservations[i];

                if (!removed && r != null &&
                        r.getGuestName().equals(res.getGuestName()) &&
                        r.getCheckIn() == res.getCheckIn() &&
                        r.getCheckOut() == res.getCheckOut()) {

                    for (int d = r.getCheckIn(); d < r.getCheckOut(); d++) {
                        for (int j = 0; j < dates.length; j++) {
                            if (dates[j] != null && dates[j].getDay() == d) {
                                dates[j].unbook();
                            }
                        }
                    }

                    reservations[i] = null;
                    removed = true;
                }
            }
        }
        return removed;
    }

    /**
     * Retrieves all currently available dates.
     * @return an array of available Date objects
     */
    public Date[] getAvailableDates() {
        int count = 0;
        int idx = 0;

        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].isAvailable()) {
                count++;
            }
        }

        Date[] availableDates = new Date[count];

        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].isAvailable()) {
                availableDates[idx] = dates[i];
                idx++;
            }
        }
        return availableDates;
    }

    /**
     * Retrieves the Date object for a specific day.
     * @param day the calendar day to find (1–30)
     * @return the Date object if it exists, otherwise null
     */
    public Date getDateByDay(int day) {
        Date found = null;
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].getDay() == day) {
                found = dates[i];
            }
        }
        return found;
    }

    /**
     * Computes the total earnings from all reserved dates by summing the date price.
     * @return total earnings from booked dates
     */
    public double getEstimatedEarnings() {
        double sum = 0;

        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && !dates[i].isAvailable()) {
                sum = sum + dates[i].getPrice();
            }
        }
        return sum;
    }

    /**
     * Removes the property only if it has no active reservations.
     * @return true if removal succeeds, false otherwise
     */
    public boolean removeProperty() {
        boolean canRemove = true;

        for (int i = 0; i < reservations.length; i++) {
            if (reservations[i] != null) {
                canRemove = false;
            }
        }

        if (canRemove) {
            name = null;
            dates = new Date[0];
            reservations = new Reservation[0];
        }

        return canRemove;
    }
}
