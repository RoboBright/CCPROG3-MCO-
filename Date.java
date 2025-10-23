import java.util.*;

public class Date{
    private int day;
    private double price;
    private boolean isReserved;

    public Date(int day, double price){
        this.day = day;
        this.price = price;
        this.isReserved = false;
    }

    public int getDay(){
        return day;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public boolean isAvailable(){
        return !isReserved;
    }

    public void book() {
        this.isReserved = true;
    }

    public void unbook() {
        this.isReserved = false;
    }

    public String toString() {
        return "Day " + day + " - Price: " + price + " - " + (isReserved ? "Reserved" : "Available");
    }
}