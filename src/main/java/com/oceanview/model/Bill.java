package com.oceanview.model;

import java.time.LocalDateTime;

/**
 * Bill — Invoice generated for a reservation.
 * RELATIONSHIP: Composition with Reservation (cannot exist without it — ◆ in class diagram).
 * KEY METHOD: calculateTotal() computes subtotal, tax, discount, total.
 */
public class Bill {
    private int billId;
    private int reservationId;
    private int numberOfNights;
    private double roomRate;
    private double subtotal;
    private double taxRate;
    private double taxAmount;
    private double discountPercent;
    private double discountAmount;
    private double totalAmount;
    private String billingStrategy;
    private LocalDateTime generatedAt;
    private int generatedBy;

    public Bill() { this.taxRate = 10.0; this.discountPercent = 0.0; }

    /** Calculates: subtotal = nights × rate, then discount, then tax, then total. */
    public void calculateTotal() {
        this.subtotal = numberOfNights * roomRate;
        this.discountAmount = subtotal * (discountPercent / 100.0);
        double taxable = subtotal - discountAmount;
        this.taxAmount = taxable * (taxRate / 100.0);
        this.totalAmount = taxable + taxAmount;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }
    public double getRoomRate() { return roomRate; }
    public void setRoomRate(double roomRate) { this.roomRate = roomRate; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getBillingStrategy() { return billingStrategy; }
    public void setBillingStrategy(String billingStrategy) { this.billingStrategy = billingStrategy; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public int getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(int generatedBy) { this.generatedBy = generatedBy; }

    @Override
    public String toString() { return "Bill{id=" + billId + ", reservation=" + reservationId + ", total=" + totalAmount + "}"; }
}
