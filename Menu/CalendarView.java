package Menu;

import java.util.Locale;

import Objects.Property;
import Objects.Date;

/**
 * Generates a grid-style calendar view for a property.
 * Each cell shows the status, final price, and environmental color category.
 */
public class CalendarView {

    /**
     * Displays a 30-day calendar in grid format.
     * Each row contains 7 days, except the last row.
     *
     * @param property the property whose calendar is displayed
     */
    public void printCalendar(Property property) {
        System.out.println("\n--- Calendar Grid View ---");
        System.out.println("Property: " + property.getName() +
                " (" + property.getType().getDisplayName() + ")");
        System.out.println("Format: [day status price color]");
        System.out.println("Status: A = Available, B = Booked, N/A = Not listed");
        System.out.println("Color: G = <100%, W = 100%, Y = >100%\n");

        double multiplier = property.getType().getMultiplier();
        int day = 1;
        int maxDays = 30;
        int columns = 7;

        while (day <= maxDays) {
            int c = 0;

            while (c < columns) {
                if (day <= maxDays) {
                    Date date = property.getDateByDay(day);
                    String cell;

                    if (date == null) {
                        cell = String.format("%2d %-8s", day, "N/A");
                    } else {
                        double finalPrice = date.getFinalPrice(multiplier);
                        int percent = (int)(date.getEnvironmentalRate() * 100);
                        String color = getColorCode(percent);
                        String status = date.isAvailable() ? "A" : "B";

                        cell = String.format(Locale.US,
                                "%2d %s %4.0f %s", day, status, finalPrice, color);
                    }

                    System.out.print(String.format("| %-11s", cell));
                    day = day + 1;
                }
                else {
                    System.out.print("|             ");
                }

                c = c + 1;
            }

            System.out.println("|");
        }
    }

    /**
     * Determines the color code based on environmental percentage.
     *
     * @param percent the environmental rate expressed as percent
     * @return "G", "W", or "Y"
     */
    private String getColorCode(int percent) {
        if (percent < 100)
            return "G";
        else if (percent > 100)
            return "Y";
        else
            return "W";
    }
}
