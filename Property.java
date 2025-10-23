import java.util.*;

public class Property{
    private String name;
    private double basePrice = 1500.0;
    private Date[] dates;
    private Reservation[] reservations;

    public Property(String name){
        this.name = name;
        this.dates = new Date[30];
        this.reservations = new Reservation[30];
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getBasePrice(){
        return basePrice;
    }

    public void setBasePrice(double basePrice){
        this.basePrice = basePrice;
    }

    // basic logic implementations

    public void addDate(Date date){
        if (date == null) return;
        // avoid duplicate day
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].getDay() == date.getDay()) return;
        }
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] == null) {
                dates[i] = date;
                return;
            }
        }
    }

    public void removeDate(int day){
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].getDay() == day) {
                dates[i] = null;
                return;
            }
        }
    }

    public boolean addReservation(Reservation res){
        if (res == null) return false;
        int in = res.getCheckIn();
        int out = res.getCheckOut();
        if (out <= in) return false;

        // check every day exists and is available
        for (int d = in; d < out; d++) {
            Date found = null;
            for (Date dt : dates) {
                if (dt != null && dt.getDay() == d) {
                    found = dt;
                    break;
                }
            }
            if (found == null || !found.isAvailable()) return false;
        }

        // store reservation
        for (int i = 0; i < reservations.length; i++) {
            if (reservations[i] == null) {
                reservations[i] = res;
                // mark dates as booked
                for (int d = in; d < out; d++) {
                    for (Date dt : dates) {
                        if (dt != null && dt.getDay() == d) dt.book();
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean removeReservation(Reservation res){
        if (res == null) return false;
        for (int i = 0; i < reservations.length; i++) {
            Reservation r = reservations[i];
            if (r != null &&
               r.getGuestName().equals(res.getGuestName()) &&
               r.getCheckIn() == res.getCheckIn() &&
               r.getCheckOut() == res.getCheckOut()) {

                // unbook associated dates
                for (int d = r.getCheckIn(); d < r.getCheckOut(); d++) {
                    for (int j = 0; j < dates.length; j++) {
                        Date dt = dates[j];
                        if (dt != null && dt.getDay() == d) {
                            dt.unbook();
                        }
                    }
                }

                reservations[i] = null;
                return true;
            }
        }
        return false;
    }

    public Date[] getAvailableDates(){
        int count = 0;
        for (Date d : dates) {
            if (d != null && d.isAvailable()) count++;
        }
        Date[] out = new Date[count];
        int idx = 0;
        for (Date d : dates) {
            if (d != null && d.isAvailable()) out[idx++] = d;
        }
        return out;
    }

    public double getEstimatedEarnings(){
        double sum = 0.0;
        for (Date d : dates) {
            if (d != null && !d.isAvailable()) sum += d.getPrice();
        }
        return sum;
    }

    public boolean removeProperty(){
        // only remove if no active reservations
        for (Reservation r : reservations) {
            if (r != null) return false;
        }
        name = null;
        dates = new Date[0];
        reservations = new Reservation[0];
        return true;
    }
}