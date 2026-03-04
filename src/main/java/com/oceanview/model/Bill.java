package com.oceanview.model;

import java.time.LocalDateTime;

// ============================================================
// Bill.java
// This file defines the Bill class, which represents an invoice
// generated for a guest's reservation at Ocean View Resort.
// A Bill is linked to a Reservation through composition —
// meaning a bill can't exist without a reservation.
// The key method is calculateTotal(), which works out the
// subtotal, applies any discount, adds tax, and gives the
// final total the guest needs to pay.
// ============================================================

/**
 * Bill — Invoice generated for a reservation.
 * RELATIONSHIP: Composition with Reservation (cannot exist without it).
 * KEY METHOD: calculateTotal() computes subtotal, tax, discount, total.
 */
public class Bill {

    // These private fields store all the billing information.
    // They are private for encapsulation — we use getters and setters to access them.
    private int billId;              // unique ID for this bill in the database
    private int reservationId;       // the ID of the reservation this bill belongs to
    private int numberOfNights;      // how many nights the guest stayed
    private double roomRate;         // the price per night for the room
    private double subtotal;         // numberOfNights * roomRate (before tax and discount)
    private double taxRate;          // the tax percentage (e.g. 10.0 means 10%)
    private double taxAmount;        // the actual tax amount in currency
    private double discountPercent;  // the discount percentage (e.g. 5.0 means 5% off)
    private double discountAmount;   // the actual discount amount in currency
    private double totalAmount;      // the final amount the guest has to pay
    private String billingStrategy;  // the billing strategy used (for the Strategy design pattern)
    private LocalDateTime generatedAt; // when this bill was generated
    private int generatedBy;         // the user ID of the staff member who generated this bill

    // Default constructor — sets the tax rate to 10% and discount to 0% by default.
    // These are sensible defaults so every new bill starts with standard tax and no discount.
    public Bill() {
        this.taxRate = 10.0;
        this.discountPercent = 0.0;
    }

    // This method calculates the total amount the guest needs to pay.
    // Step 1: subtotal = number of nights * room rate per night
    // Step 2: work out the discount amount from the subtotal
    // Step 3: subtract the discount to get the taxable amount
    // Step 4: calculate the tax on the taxable amount
    // Step 5: total = taxable amount + tax
    public void calculateTotal() {
        this.subtotal = numberOfNights * roomRate;
        this.discountAmount = subtotal * (discountPercent / 100.0);
        double taxable = subtotal - discountAmount;
        this.taxAmount = taxable * (taxRate / 100.0);
        this.totalAmount = taxable + taxAmount;
    }

    // =====================================================
    // Getters and setters
    // These let other classes read and change the private
    // fields above. Each field has a get (read) and set
    // (write) method — this is standard Java encapsulation.
    // =====================================================

    // getter for billId — returns the bill's unique database ID
    public int getBillId() {
        return billId;
    }

    // setter for billId — sets the bill's database ID
    public void setBillId(int billId) {
        this.billId = billId;
    }

    // getter for reservationId — returns which reservation this bill is for
    public int getReservationId() {
        return reservationId;
    }

    // setter for reservationId — links this bill to a reservation
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    // getter for numberOfNights — returns how many nights the guest stayed
    public int getNumberOfNights() {
        return numberOfNights;
    }

    // setter for numberOfNights — sets the number of nights
    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    // getter for roomRate — returns the price per night
    public double getRoomRate() {
        return roomRate;
    }

    // setter for roomRate — sets the price per night
    public void setRoomRate(double roomRate) {
        this.roomRate = roomRate;
    }

    // getter for subtotal — returns nights * rate (before tax/discount)
    public double getSubtotal() {
        return subtotal;
    }

    // setter for subtotal — lets us set the subtotal directly
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // getter for taxRate — returns the tax percentage
    public double getTaxRate() {
        return taxRate;
    }

    // setter for taxRate — lets us change the tax percentage
    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    // getter for taxAmount — returns the calculated tax in currency
    public double getTaxAmount() {
        return taxAmount;
    }

    // setter for taxAmount — lets us set the tax amount directly
    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    // getter for discountPercent — returns the discount percentage
    public double getDiscountPercent() {
        return discountPercent;
    }

    // setter for discountPercent — lets us set the discount percentage
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    // getter for discountAmount — returns the calculated discount in currency
    public double getDiscountAmount() {
        return discountAmount;
    }

    // setter for discountAmount — lets us set the discount amount directly
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    // getter for totalAmount — returns the final amount the guest pays
    public double getTotalAmount() {
        return totalAmount;
    }

    // setter for totalAmount — lets us set the total directly
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // getter for billingStrategy — returns which billing strategy was used
    public String getBillingStrategy() {
        return billingStrategy;
    }

    // setter for billingStrategy — sets the billing strategy name
    public void setBillingStrategy(String billingStrategy) {
        this.billingStrategy = billingStrategy;
    }

    // getter for generatedAt — returns when the bill was created
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    // setter for generatedAt — sets the bill generation timestamp
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    // getter for generatedBy — returns which staff member generated this bill
    public int getGeneratedBy() {
        return generatedBy;
    }

    // setter for generatedBy — records which staff member generated this bill
    public void setGeneratedBy(int generatedBy) {
        this.generatedBy = generatedBy;
    }

    // toString method — gives a readable summary when we print a Bill object.
    // It shows the bill ID, the linked reservation ID, and the total amount.
    @Override
    public String toString() {
        return "Bill{id=" + billId + ", reservation=" + reservationId + ", total=" + totalAmount + "}";
    }
}
