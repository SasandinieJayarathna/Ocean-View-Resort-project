package com.oceanview.dao;

import com.oceanview.model.Bill;
import java.time.LocalDate;
import java.util.List;

/**
 * BillDAO - This is an interface for Bill data access.
 * An interface defines what methods the implementation class must have,
 * but does not contain the actual code for those methods.
 *
 * PATTERN: DAO (Data Access Object) - separates database code from business logic.
 */
public interface BillDAO {

    // This method saves a new bill to the database and returns true if successful
    boolean saveBill(Bill bill);

    // This method finds and returns a bill by its associated reservation ID
    Bill getBillByReservationId(int reservationId);

    // This method returns a list of all bills from the database
    List<Bill> getAllBills();

    // This method returns bills that were generated within a given date range
    List<Bill> getBillsByDateRange(LocalDate start, LocalDate end);
}
