package System;

import java.util.ArrayList;

import Objects.Property;
import Objects.Reservation;
import Objects.Date;
import Objects.PropertyType;

/**
 * The PropertySystem class stores all properties and their reservations.
 * It handles the core business logic but does not interact with the user.
 */
public class PropertySystem {

    private ArrayList<Property> properties;
    private ArrayList<ArrayList<Reservation>> reservationsPerProperty;

    /**
     * Constructs a PropertySystem and loads sample properties.
     */
    public PropertySystem() {
        properties = new ArrayList<Property>();
        reservationsPerProperty = new ArrayList<ArrayList<Reservation>>();
        seedSampleProperties();
    }

    /**
     * Creates two sample properties for demonstration and testing.
     */
    private void seedSampleProperties() {
        Property p1 = new Property("Grand Residences", PropertyType.ECO_APARTMENT);
        for (int d = 1; d <= 30; d++) p1.addDate(new Date(d));
        properties.add(p1);
        reservationsPerProperty.add(new ArrayList<Reservation>());


        Property p2 = new Property("Arasaka Tower", PropertyType.SUSTAINABLE_HOUSE);
        for (int d = 1; d <= 15; d++) p2.addDate(new Date(d));
        for (int d = 21; d <= 25; d++) p2.addDate(new Date(d));
        properties.add(p2);
        reservationsPerProperty.add(new ArrayList<Reservation>());


        Property p3 = new Property("Bolinao Reservations", PropertyType.GREEN_RESORT);
        for (int d = 10; d <= 20; d++) p3.addDate(new Date(d));
        properties.add(p3);
        reservationsPerProperty.add(new ArrayList<Reservation>());


        Property p4 = new Property("Sunset Retreat", PropertyType.ECO_GLAMPING);
        for (int d = 5; d <= 25; d++) p4.addDate(new Date(d));
        properties.add(p4);
        reservationsPerProperty.add(new ArrayList<Reservation>());


        Property p5 = new Property("Mountain Edge", PropertyType.SUSTAINABLE_HOUSE);
        for (int d = 1; d <= 10; d++) p5.addDate(new Date(d));
        for (int d = 18; d <= 28; d++) p5.addDate(new Date(d));
        properties.add(p5);
        reservationsPerProperty.add(new ArrayList<Reservation>());


// Bookings
        Reservation r1 = new Reservation("Paolo", 2, 4);
        Reservation r2 = new Reservation("Ammiel", 5, 9);
        Reservation r3 = new Reservation("Johnny SilverHand", 11, 13);
        Reservation r4 = new Reservation("Adam Smasher", 15, 20);
        Reservation r5 = new Reservation("Han Helldiver", 7, 14);
        Reservation r6 = new Reservation("Master Chief", 1, 3);
        Reservation r7 = new Reservation("Fireful FlyShine", 19, 24);


        p1.addReservation(r1); reservationsPerProperty.get(0).add(r1);
        p1.addReservation(r2); reservationsPerProperty.get(0).add(r2);
        p2.addReservation(r3); reservationsPerProperty.get(1).add(r3);
        p3.addReservation(r4); reservationsPerProperty.get(2).add(r4);
        p4.addReservation(r5); reservationsPerProperty.get(3).add(r5);
        p5.addReservation(r6); reservationsPerProperty.get(4).add(r6);
        p5.addReservation(r7); reservationsPerProperty.get(4).add(r7);


// Environmental Modifiers
        setEnvironmentalRateForRange(0, 1, 10, 0.90);
        setEnvironmentalRateForRange(0, 11, 20, 1.10);
        setEnvironmentalRateForRange(1, 1, 15, 1.00);
        setEnvironmentalRateForRange(1, 21, 25, 0.85);
        setEnvironmentalRateForRange(2, 10, 15, 1.20);
        setEnvironmentalRateForRange(2, 16, 20, 0.80);
        setEnvironmentalRateForRange(3, 5, 15, 0.95);
        setEnvironmentalRateForRange(3, 16, 25, 1.05);
        setEnvironmentalRateForRange(4, 1, 10, 0.88);
        setEnvironmentalRateForRange(4, 18, 28, 1.15);
    }

    /**
     * Returns the number of properties stored.
     *
     * @return the number of properties
     */
    public int getPropertyCount() {
        return properties.size();
    }

    /**
     * Retrieves the Property at a specific index.
     * If the index is invalid, null is returned.
     *
     * @param index the property index
     * @return the Property, or null if index is invalid
     */
    public Property getProperty(int index) {
        Property result = null;

        if (index >= 0 && index < properties.size()) {
            result = properties.get(index);
        }

        return result;
    }

    /**
     * Checks if a property name already exists.
     *
     * @param name the name to check
     * @return true if a property with this name exists; false otherwise
     */
    public boolean propertyNameExists(String name) {
        boolean exists = false;
        int i = 0;

        while (i < properties.size()) {
            if (properties.get(i).getName().equals(name)) {
                exists = true;
            }
            i = i + 1;
        }

        return exists;
    }

    /**
     * Creates a new property with a given name, type, and listed days.
     * Only valid days (1–30) are added. Duplicate days are ignored.
     * If creation succeeds, the index of the new property is returned.
     * If creation fails (for example, no valid days), -1 is returned.
     *
     * @param name the name of the property
     * @param type the type of the property
     * @param days an array of day numbers to list (1–30)
     * @return the index of the new property; -1 if creation fails
     */
    public int createProperty(String name, PropertyType type, int[] days) {
        int index = -1;

        if (name != null && type != null && days != null && days.length > 0 && !propertyNameExists(name)) {
            Property p = new Property(name, type);

            int i = 0;
            while (i < days.length) {
                int day = days[i];

                if (day >= 1 && day <= 30) {
                    p.addDate(new Date(day));
                }

                i = i + 1;
            }

            if (p.getAvailableDates().length > 0) {
                properties.add(p);
                reservationsPerProperty.add(new ArrayList<Reservation>());
                index = properties.size() - 1;
            }
        }

        return index;
    }

    /**
     * Changes the name of a property if the new name is not a duplicate.
     *
     * @param index the property index
     * @param newName the new name to set
     * @return true if the name is changed; false otherwise
     */
    public boolean changePropertyName(int index, String newName) {
        boolean changed = false;

        if (index >= 0 && index < properties.size() && newName != null && !propertyNameExists(newName)) {
            Property p = properties.get(index);
            p.setName(newName);
            changed = true;
        }

        return changed;
    }

    /**
     * Returns true if this property has at least one reservation.
     *
     * @param index the property index
     * @return true if there are reservations; false otherwise
     */
    public boolean hasReservations(int index) {
        boolean has = false;

        if (index >= 0 && index < reservationsPerProperty.size()) {
            ArrayList<Reservation> list = reservationsPerProperty.get(index);
            if (!list.isEmpty()) {
                has = true;
            }
        }

        return has;
    }

    /**
     * Updates the base price of all dates of a property.
     * This method does not check for reservations; this should be done
     * by the caller before calling this method.
     *
     * @param index the property index
     * @param newBase the new base price
     * @return true if the property exists and the price was updated; false otherwise
     */
    public boolean updateBasePrice(int index, double newBase) {
        boolean updated = false;

        if (index >= 0 && index < properties.size()) {
            Property p = properties.get(index);
            p.updateBasePrice(newBase);
            updated = true;
        }

        return updated;
    }

    /**
     * Changes the type of a property.
     *
     * @param index the property index
     * @param newType the new property type
     * @return true if updated; false otherwise
     */
    public boolean changePropertyType(int index, PropertyType newType) {
        boolean updated = false;

        if (index >= 0 && index < properties.size() && newType != null) {
            Property p = properties.get(index);
            p.setType(newType);
            updated = true;
        }

        return updated;
    }

    /**
     * Removes a property and its reservations if it has no reservations.
     *
     * @param index the property index
     * @return true if removed; false otherwise
     */
    public boolean removeProperty(int index) {
        boolean removed = false;

        if (index >= 0 && index < properties.size() && !hasReservations(index)) {
            properties.remove(index);
            reservationsPerProperty.remove(index);
            removed = true;
        }

        return removed;
    }

    /**
     * Returns the list of reservations for a specific property.
     * If the index is invalid, an empty list is returned.
     *
     * @param index the property index
     * @return the list of reservations (may be empty)
     */
    public ArrayList<Reservation> getReservationsForProperty(int index) {
        ArrayList<Reservation> result = new ArrayList<Reservation>();

        if (index >= 0 && index < reservationsPerProperty.size()) {
            ArrayList<Reservation> original = reservationsPerProperty.get(index);

            int i = 0;
            while (i < original.size()) {
                result.add(original.get(i));
                i = i + 1;
            }
        }

        return result;
    }

    /**
     * Removes a reservation by its position in the list of a property.
     *
     * @param propertyIndex the property index
     * @param reservationIndex the index of the reservation to remove
     * @return true if removed successfully; false otherwise
     */
    public boolean removeReservation(int propertyIndex, int reservationIndex) {
        boolean removed = false;

        if (propertyIndex >= 0 && propertyIndex < properties.size() &&
                reservationIndex >= 0 && reservationIndex < reservationsPerProperty.get(propertyIndex).size()) {

            ArrayList<Reservation> list = reservationsPerProperty.get(propertyIndex);
            Reservation target = list.get(reservationIndex);

            Property p = properties.get(propertyIndex);
            boolean unbooked = p.removeReservation(target);

            if (unbooked) {
                list.remove(reservationIndex);
                removed = true;
            }
        }

        return removed;
    }

    /**
     * Checks if all dates in a given range are listed and available.
     *
     * @param propertyIndex the property to check
     * @param checkIn the check-in day
     * @param checkOut the check-out day
     * @return true if all dates are available; false otherwise
     */
    public boolean areDatesAvailable(int propertyIndex, int checkIn, int checkOut) {
        boolean available = true;

        if (propertyIndex >= 0 && propertyIndex < properties.size() &&
                checkOut > checkIn) {

            Property p = properties.get(propertyIndex);
            int day = checkIn;

            while (day < checkOut) {
                Date d = p.getDateByDay(day);

                if (d == null || !d.isAvailable()) {
                    available = false;
                }

                day = day + 1;
            }
        }
        else {
            available = false;
        }

        return available;
    }

    /**
     * Adds a reservation for a property if possible.
     * The reservation is added to both the Property and the internal list.
     * If adding fails, null is returned.
     *
     * @param propertyIndex the property index
     * @param guest the guest name
     * @param checkIn the check-in day
     * @param checkOut the check-out day
     * @return the Reservation object if added; null otherwise
     */
    public Reservation addReservation(int propertyIndex, String guest, int checkIn, int checkOut) {
        Reservation created = null;

        if (propertyIndex >= 0 && propertyIndex < properties.size() &&
                checkOut > checkIn && guest != null) {

            Property p = properties.get(propertyIndex);
            Reservation r = new Reservation(guest, checkIn, checkOut);
            boolean ok = p.addReservation(r);

            if (ok) {
                ArrayList<Reservation> list = reservationsPerProperty.get(propertyIndex);
                list.add(r);
                created = r;
            }
        }

        return created;
    }

    /**
     * Sets the environmental rate for a specific date of a property.
     *
     * @param propertyIndex the property index
     * @param day the day number (1–30)
     * @param rate the environmental rate (0.80–1.20)
     * @return true if updated; false otherwise
     */
    public boolean setEnvironmentalRateForDate(int propertyIndex, int day, double rate) {
        boolean updated = false;

        if (propertyIndex >= 0 && propertyIndex < properties.size() &&
                day >= 1 && day <= 30) {

            Property p = properties.get(propertyIndex);
            Date d = p.getDateByDay(day);

            if (d != null) {
                d.setEnvironmentalRate(rate);
                updated = true;
            }
        }

        return updated;
    }

    /**
     * Sets the same environmental rate for all listed dates of a property.
     *
     * @param propertyIndex the property index
     * @param rate the environmental rate (0.80–1.20)
     * @return true if the property exists; false otherwise
     */
    public boolean setEnvironmentalRateForAllDates(int propertyIndex, double rate) {
        boolean updated = false;

        if (propertyIndex >= 0 && propertyIndex < properties.size()) {
            Property p = properties.get(propertyIndex);
            int day = 1;

            while (day <= 30) {
                Date d = p.getDateByDay(day);
                if (d != null) {
                    d.setEnvironmentalRate(rate);
                }
                day = day + 1;
            }

            updated = true;
        }

        return updated;
    }

    /**
     * Randomizes the environmental rate for all listed dates of a property.
     * Rates are between 0.80 and 1.20, rounded to 2 decimal places.
     *
     * @param propertyIndex the property index
     */
    public void randomizeEnvironmentalRates(int propertyIndex) {
        if (propertyIndex >= 0 && propertyIndex < properties.size()) {
            Property p = properties.get(propertyIndex);
            int day = 1;

            while (day <= 30) {
                Date d = p.getDateByDay(day);
                if (d != null) {
                    double raw = 0.80 + Math.random() * 0.40;
                    double rate = Math.round(raw * 100.0) / 100.0;
                    d.setEnvironmentalRate(rate);
                }
                day = day + 1;
            }
        }
    }

    /**
     * Sets the environmental rate for a range of days.
     *
     * @param index the property index
     * @param start the starting day (1–30)
     * @param end the ending day (1–30)
     * @param rate the environmental rate (0.80–1.20)
     * @return true if all days were updated
     */
    public boolean setEnvironmentalRateForRange(int index, int start, int end, double rate) {

        if (index < 0 || index >= properties.size())
            return false;

        if (start < 1 || end > 30 || start > end)
            return false;

        Property p = properties.get(index);

        int d = start;
        while (d <= end) {
            Date date = p.getDateByDay(d);
            if (date != null)
                date.setEnvironmentalRate(rate);
            d = d + 1;
        }

        return true;
    }
}


