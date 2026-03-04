package com.oceanview.service;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.pattern.billing.*;

/**
 * BillingService - This class handles generating bills for hotel reservations.
 * It uses the Strategy pattern to apply different pricing methods (standard, seasonal, loyalty).
 *
 * PATTERN: Strategy - different billing algorithms can be swapped in at runtime.
 * SOLID: Open-Closed - to add a new pricing type, just create a new strategy class (no need to change this code).
 * SOLID: Dependency Inversion - depends on DAO interfaces, not implementations.
 */
public class BillingService {

    // These are the DAOs we use to access bills, reservations, and rooms from the database
    private final BillDAO billDAO;
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;

    // We pass in all three DAOs through the constructor - this is called Dependency Injection
    // This makes the class easier to test because we can pass in fake (mock) DAOs
    public BillingService(BillDAO billDAO, ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.billDAO = billDAO;
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
    }

    /**
     * This method generates a bill for a reservation.
     * It looks up the reservation and room, picks a billing strategy, calculates the total, and saves it.
     *
     * @param reservationId - the ID of the reservation to bill
     * @param strategyType  - which pricing strategy to use ("STANDARD", "SEASONAL", or "LOYALTY")
     * @param generatedBy   - the user ID of the staff member generating the bill
     * @return the generated Bill object, or null if something went wrong
     */
    public Bill generateBill(int reservationId, String strategyType, int generatedBy) {
        // First, look up the reservation from the database
        Reservation reservation = reservationDAO.getReservationById(reservationId);

        // If the reservation doesn't exist, we can't generate a bill
        if (reservation == null) {
            return null;
        }

        // Look up the room details so we know the price per night
        Room room = roomDAO.getRoomById(reservation.getRoomId());

        // If the room doesn't exist, we can't generate a bill
        if (room == null) {
            return null;
        }

        // Select the billing strategy based on the type string
        // This is the Strategy pattern - we pick the right algorithm at runtime
        BillingStrategy strategy = selectStrategy(strategyType);

        // Calculate the number of nights and the subtotal using the chosen strategy
        int nights = (int) reservation.getNumberOfNights();
        double subtotal = strategy.calculateTotal(nights, room.getPricePerNight());

        // Create a new Bill object and fill in all the details
        Bill bill = new Bill();
        bill.setReservationId(reservationId);
        bill.setNumberOfNights(nights);
        bill.setRoomRate(room.getPricePerNight());
        bill.setSubtotal(subtotal);
        bill.setBillingStrategy(strategy.getStrategyName());
        bill.setGeneratedBy(generatedBy);

        // Apply special discount/surcharge depending on the strategy
        if ("LOYALTY".equals(strategy.getStrategyName())) {
            // Loyalty customers get a 10% discount
            bill.setDiscountPercent(10.0);
        } else if ("SEASONAL".equals(strategy.getStrategyName())) {
            // Seasonal pricing adds a 20% surcharge (shown as negative discount)
            bill.setDiscountPercent(-20.0);
        }

        // Calculate the final total (applies tax and discount)
        bill.calculateTotal();

        // Save the bill to the database
        billDAO.saveBill(bill);

        // Return the completed bill
        return bill;
    }

    // This method retrieves an existing bill by its reservation ID
    public Bill getBillByReservationId(int reservationId) {
        return billDAO.getBillByReservationId(reservationId);
    }

    /**
     * This method selects the correct billing strategy based on the type string.
     * It uses a switch statement to pick between Standard, Seasonal, and Loyalty strategies.
     * This is how the Strategy pattern works - we choose the right algorithm at runtime.
     *
     * If the type is null or doesn't match any known strategy, we default to Standard.
     */
    private BillingStrategy selectStrategy(String type) {
        // If no type was provided, use the standard (default) strategy
        if (type == null) {
            return new StandardBillingStrategy();
        }

        // Use a switch to pick the right strategy based on the type
        switch (type.toUpperCase()) {
            case "SEASONAL":
                // Seasonal strategy applies a surcharge during peak season
                return new SeasonalBillingStrategy();
            case "LOYALTY":
                // Loyalty strategy gives a discount to returning customers
                return new LoyaltyBillingStrategy();
            default:
                // If the type doesn't match anything, use the standard strategy
                return new StandardBillingStrategy();
        }
    }
}
