package com.oceanview.dao;

import com.oceanview.model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BillDAOImplTest — Mockito-based tests for BillDAOImpl.
 * Mocks JDBC to test DAO logic without a real database.
 */
@ExtendWith(MockitoExtension.class)
class BillDAOImplTest {
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private ResultSet generatedKeys;
    @Mock private Statement statement;

    private BillDAOImpl billDAO;

    @BeforeEach
    void setUp() throws SQLException {
        billDAO = new BillDAOImpl(connection);
        lenient().when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        lenient().when(connection.prepareStatement(any(String.class), anyInt())).thenReturn(ps);
        lenient().when(connection.createStatement()).thenReturn(statement);
    }

    private void mockBillRow() throws SQLException {
        when(rs.getInt("bill_id")).thenReturn(1);
        when(rs.getInt("reservation_id")).thenReturn(10);
        when(rs.getInt("number_of_nights")).thenReturn(3);
        when(rs.getDouble("room_rate")).thenReturn(5000.0);
        when(rs.getDouble("subtotal")).thenReturn(15000.0);
        when(rs.getDouble("tax_rate")).thenReturn(10.0);
        when(rs.getDouble("tax_amount")).thenReturn(1500.0);
        when(rs.getDouble("discount_percent")).thenReturn(0.0);
        when(rs.getDouble("discount_amount")).thenReturn(0.0);
        when(rs.getDouble("total_amount")).thenReturn(16500.0);
        when(rs.getString("billing_strategy")).thenReturn("STANDARD");
        when(rs.getTimestamp("generated_at")).thenReturn(null);
        when(rs.getInt("generated_by")).thenReturn(1);
    }

    @Test @DisplayName("TC-BD001: saveBill returns true on success")
    void saveBillSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(5);

        Bill bill = new Bill();
        bill.setReservationId(10);
        bill.setNumberOfNights(3);
        bill.setRoomRate(5000.0);
        bill.setSubtotal(15000.0);
        bill.setTaxRate(10.0);
        bill.setTaxAmount(1500.0);
        bill.setTotalAmount(16500.0);
        bill.setBillingStrategy("STANDARD");
        bill.setGeneratedBy(1);

        assertTrue(billDAO.saveBill(bill));
        assertEquals(5, bill.getBillId());
    }

    @Test @DisplayName("TC-BD002: saveBill returns false on failure")
    void saveBillFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        Bill bill = new Bill();
        assertFalse(billDAO.saveBill(bill));
    }

    @Test @DisplayName("TC-BD003: getBillByReservationId returns bill when found")
    void getBillByReservationIdFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockBillRow();

        Bill bill = billDAO.getBillByReservationId(10);
        assertNotNull(bill);
        assertEquals(10, bill.getReservationId());
        assertEquals("STANDARD", bill.getBillingStrategy());
        assertEquals(16500.0, bill.getTotalAmount());
    }

    @Test @DisplayName("TC-BD004: getBillByReservationId returns null when not found")
    void getBillByReservationIdNotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertNull(billDAO.getBillByReservationId(999));
    }

    @Test @DisplayName("TC-BD005: getAllBills returns list")
    void getAllBills() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockBillRow();

        List<Bill> bills = billDAO.getAllBills();
        assertEquals(1, bills.size());
        assertEquals(1, bills.get(0).getBillId());
    }

    @Test @DisplayName("TC-BD006: getAllBills returns empty list when none")
    void getAllBillsEmpty() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertTrue(billDAO.getAllBills().isEmpty());
    }

    @Test @DisplayName("TC-BD007: getBillsByDateRange returns matching bills")
    void getBillsByDateRange() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockBillRow();

        List<Bill> bills = billDAO.getBillsByDateRange(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        assertEquals(1, bills.size());
    }

    @Test @DisplayName("TC-BD008: getBillsByDateRange returns empty list")
    void getBillsByDateRangeEmpty() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<Bill> bills = billDAO.getBillsByDateRange(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2));
        assertTrue(bills.isEmpty());
    }

    @Test @DisplayName("TC-BD009: saveBill handles SQL exception")
    void saveBillSQLException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        Bill bill = new Bill();
        assertFalse(billDAO.saveBill(bill));
    }

    @Test @DisplayName("TC-BD010: getBillByReservationId handles SQL exception")
    void getBillByReservationIdSQLException() throws SQLException {
        when(ps.executeQuery()).thenThrow(new SQLException("DB error"));
        assertNull(billDAO.getBillByReservationId(1));
    }

    @Test @DisplayName("TC-BD011: Bill with generated_at timestamp is mapped correctly")
    void billWithTimestamp() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("bill_id")).thenReturn(2);
        when(rs.getInt("reservation_id")).thenReturn(20);
        when(rs.getInt("number_of_nights")).thenReturn(2);
        when(rs.getDouble("room_rate")).thenReturn(8000.0);
        when(rs.getDouble("subtotal")).thenReturn(16000.0);
        when(rs.getDouble("tax_rate")).thenReturn(10.0);
        when(rs.getDouble("tax_amount")).thenReturn(1600.0);
        when(rs.getDouble("discount_percent")).thenReturn(5.0);
        when(rs.getDouble("discount_amount")).thenReturn(800.0);
        when(rs.getDouble("total_amount")).thenReturn(16800.0);
        when(rs.getString("billing_strategy")).thenReturn("LOYALTY");
        Timestamp ts = Timestamp.valueOf("2025-08-01 10:30:00");
        when(rs.getTimestamp("generated_at")).thenReturn(ts);
        when(rs.getInt("generated_by")).thenReturn(2);

        Bill bill = billDAO.getBillByReservationId(20);
        assertNotNull(bill);
        assertNotNull(bill.getGeneratedAt());
        assertEquals(2, bill.getGeneratedBy());
    }
}
