package com.oceanview.dao;

import com.oceanview.model.Bill;
import java.time.LocalDate;
import java.util.List;

/**
 * BillDAO — Data access interface for Bill operations.
 */
public interface BillDAO {
    boolean saveBill(Bill bill);
    Bill getBillByReservationId(int reservationId);
    List<Bill> getAllBills();
    List<Bill> getBillsByDateRange(LocalDate start, LocalDate end);
}
