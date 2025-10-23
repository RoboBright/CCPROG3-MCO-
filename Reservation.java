import java.util.*;

public class Reservation{

    private String guestName;
    private int checkIn;
    private int checkOut;

    public Reservation(String guest, int in, int out){
        this.guestName = guest;
        this.checkIn = in;
        this.checkOut = out;
    }

    public String getGuestName(){
        return guestName;
    }

    public int getCheckIn() {
        return checkIn;
    }

    public int getCheckOut() {
        return checkOut;
    }

    public double getTotalPrice(double basePrice){
        int nights = checkOut - checkIn;
        if (nights < 0) nights = 0;
        return nights * basePrice;
    }

    public double[] getBreakdown(double basePrice){
        int nights = checkOut - checkIn;
        if (nights < 0) nights = 0;
        double[] breakdown = new double[nights];
        for (int i = 0; i < nights; i++) {
            breakdown[i] = basePrice;
        }
        return breakdown;
    }
}