/**
 * Represents a property with a name, type, available dates, and reservations.
 * Each date has its own base pricing, environmental rate, and reservation status.
 */
public class Property {

    private String name;
    private PropertyType type;
    private Date[] dates;
    private Reservation[] reservations;

    /**
     * Constructs a Property with the given name.
     * The default property type is Eco-Apartment.
     * The property can store up to 30 dates and 30 reservations.
     *
     * @param name the name of the property
     */
    public Property(String name) {
        this(name, PropertyType.ECO_APARTMENT);
    }

    /**
     * Constructs a Property with a specific name and type.
     * Initializes storage for up to 30 dates and 30 reservations.
     *
     * @param name the name of the property
     * @param type the property type
     */
    public Property(String name, PropertyType type) {
        this.name = name;
        this.type = type;
        this.dates = new Date[30];
        this.reservations = new Reservation[30];
    }

    /**
     * Retrieves the property's name.
     *
     * @return the property name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the property.
     *
     * @param name the new name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the type of this property.
     *
     * @return the property type
     */
    public PropertyType getType() {
        return type;
    }

    /**
     * Sets the property type.
     *
     * @param type the new property type
     */
    public void setType(PropertyType type) {
        if (type != null) {
            this.type = type;
        }
    }

    /**
     * Adds a Date object to the property's list of available dates.
     * The date is added only if no duplicate day exists.
     *
     * @param date the Date to add
     */
    public void addDate(Date date) {
        boolean foundDuplicate = false;
        int i = 0;

        if (date != null) {
            while (i < dates.length) {
                if (dates[i] != null && dates[i].getDay() == date.getDay()) {
                    foundDuplicate = true;
                }
                i = i + 1;
            }

            if (!foundDuplicate) {
                int j = 0;
                boolean placed = false;

                while (j < dates.length && !placed) {
                    if (dates[j] == null) {
                        dates[j] = date;
                        placed = true;
                    }
                    j = j + 1;
                }
            }
        }
    }

    /**
     * Removes a date by its day number if it exists.
     *
     * @param day the day to remove
     */
    public void removeDate(int day) {
        boolean done = false;
        int i = 0;

        while (i < dates.length && !done) {
            if (dates[i] != null && dates[i].getDay() == day) {
                dates[i] = null;
                done = true;
            }
            i = i + 1;
        }
    }

    /**
     * Adds a reservation if all requested dates exist and are available.
     * When added, all corresponding Date objects are marked as reserved.
     *
     * @param res the reservation to add
     * @return true if added successfully; false otherwise
     */
    public boolean addReservation(Reservation res) {
        boolean success = false;

        if (res != null && res.getCheckOut() > res.getCheckIn()) {

            boolean allAvailable = true;
            int day = res.getCheckIn();

            while (day < res.getCheckOut()) {
                boolean found = false;
                int j = 0;

                while (j < dates.length) {
                    if (dates[j] != null && dates[j].getDay() == day) {
                        found = true;
                        if (!dates[j].isAvailable()) {
                            allAvailable = false;
                        }
                    }
                    j = j + 1;
                }

                if (!found) {
                    allAvailable = false;
                }

                day = day + 1;
            }

            if (allAvailable) {
                int k = 0;

                while (k < reservations.length && !success) {
                    if (reservations[k] == null) {
                        reservations[k] = res;

                        int d = res.getCheckIn();
                        while (d < res.getCheckOut()) {
                            int m = 0;
                            while (m < dates.length) {
                                if (dates[m] != null && dates[m].getDay() == d) {
                                    dates[m].book();
                                }
                                m = m + 1;
                            }
                            d = d + 1;
                        }

                        success = true;
                    }
                    k = k + 1;
                }
            }
        }

        return success;
    }

    /**
     * Removes a reservation and resets the reservation status of its dates.
     *
     * @param res the reservation to remove
     * @return true if removed; false otherwise
     */
    public boolean removeReservation(Reservation res) {
        boolean removed = false;
        int i = 0;

        if (res != null) {
            while (i < reservations.length && !removed) {
                Reservation r = reservations[i];

                if (r != null &&
                        r.getGuestName().equals(res.getGuestName()) &&
                        r.getCheckIn() == res.getCheckIn() &&
                        r.getCheckOut() == res.getCheckOut()) {

                    int d = r.getCheckIn();
                    while (d < r.getCheckOut()) {
                        int j = 0;
                        while (j < dates.length) {
                            if (dates[j] != null && dates[j].getDay() == d) {
                                dates[j].unbook();
                            }
                            j = j + 1;
                        }
                        d = d + 1;
                    }

                    reservations[i] = null;
                    removed = true;
                }
                i = i + 1;
            }
        }

        return removed;
    }

    /**
     * Retrieves all available (not reserved) dates.
     *
     * @return an array of available Date objects
     */
    public Date[] getAvailableDates() {
        int count = 0;
        int i = 0;

        while (i < dates.length) {
            if (dates[i] != null && dates[i].isAvailable()) {
                count = count + 1;
            }
            i = i + 1;
        }

        Date[] available = new Date[count];
        int idx = 0;
        int j = 0;

        while (j < dates.length) {
            if (dates[j] != null && dates[j].isAvailable()) {
                available[idx] = dates[j];
                idx = idx + 1;
            }
            j = j + 1;
        }

        return available;
    }

    /**
     * Retrieves the Date object for a specific day.
     *
     * @param day the day to search for
     * @return the Date object if found; null otherwise
     */
    public Date getDateByDay(int day) {
        Date found = null;
        int i = 0;

        while (i < dates.length) {
            if (dates[i] != null && dates[i].getDay() == day) {
                found = dates[i];
            }
            i = i + 1;
        }

        return found;
    }

    /**
     * Updates the base price for all listed dates.
     *
     * @param newBase the new base price to apply
     */
    public void updateBasePrice(double newBase) {
        int i = 0;

        while (i < dates.length) {
            if (dates[i] != null) {
                dates[i].setPrice(newBase);
            }
            i = i + 1;
        }
    }

    /**
     * Computes estimated earnings based on all reserved dates.
     * Final prices include the property type multiplier and environmental rate.
     *
     * @return the total earnings
     */
    public double getEstimatedEarnings() {
        double total = 0;
        double multiplier = type.getMultiplier();
        int i = 0;

        while (i < dates.length) {
            if (dates[i] != null && !dates[i].isAvailable()) {
                total = total + dates[i].getFinalPrice(multiplier);
            }
            i = i + 1;
        }

        return total;
    }

    /**
     * Removes this property only if it has no reservations.
     *
     * @return true if removed successfully; false otherwise
     */
    public boolean removeProperty() {
        boolean canRemove = true;
        int i = 0;

        while (i < reservations.length) {
            if (reservations[i] != null) {
                canRemove = false;
            }
            i = i + 1;
        }

        if (canRemove) {
            name = null;
            dates = new Date[0];
            reservations = new Reservation[0];
        }

        return canRemove;
    }
}
