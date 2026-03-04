package com.oceanview.service;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.pattern.billing.*;

/**
 * BillingService — Generates bills using the Strategy pattern.
 * PATTERN: Strategy — different billing algorithms injected at runtime.
 * SOLID: Open-Closed — new pricing = new strategy class.
 * SOLID: Dependency Inversion — depends on DAO interfaces.
 */
public class BillingService {
    private final BillDAO billDAO;
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;

    public BillingService(BillDAO billDAO, ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.billDAO = billDAO;
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
    }

    /** Generates a bill for a reservation using the specified strategy. */
    public Bill generateBill(int reservationId, String strategyType, int generatedBy) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation == null) return null;

        Room room = roomDAO.getRoomById(reservation.getRoomId());
        if (room == null) return null;

        // Select billing strategy based on type
        BillingStrategy strategy = selectStrategy(strategyType);

        int nights = (int) reservation.getNumberOfNights();
        double subtotal = strategy.calculateTotal(nights, room.getPricePerNight());

        Bill bill = new Bill();
        bill.setReservationId(reservationId);
        bill.setNumberOfNights(nights);
        bill.setRoomRate(room.getPricePerNight());
        bill.setSubtotal(subtotal);
        bill.setBillingStrategy(strategy.getStrategyName());
        bill.setGeneratedBy(generatedBy);

        // Apply discount percent for loyalty strategy
        if ("LOYALTY".equals(strategy.getStrategyName())) {
            bill.setDiscountPercent(10.0);
        } else if ("SEASONAL".equals(strategy.getStrategyName())) {
            bill.setDiscountPercent(-20.0); // Surcharge shown as negative discount
        }

        bill.calculateTotal();
        billDAO.saveBill(bill);
        return bill;
    }

    public Bill getBillByReservationId(int reservationId) {
        return billDAO.getBillByReservationId(reservationId);
    }

    /** Factory for selecting the billing strategy — demonstrates Strategy pattern selection. */
    private BillingStrategy selectStrategy(String type) {
        if (type == null) return new StandardBillingStrategy();
        switch (type.toUpperCase()) {
            case "SEASONAL": return new SeasonalBillingStrategy();
            case "LOYALTY":  return new LoyaltyBillingStrategy();
            default:         return new StandardBillingStrategy();
        }
    }
}
