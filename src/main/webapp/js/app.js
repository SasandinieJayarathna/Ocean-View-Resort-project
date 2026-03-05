/* ============================================================
   Ocean View Resort — Main JavaScript Application File
   Pure vanilla JS with fetch API. NO jQuery, NO frameworks.
   ============================================================ */

// --- Base URL for API calls ---
var BASE_URL = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';

/**
 * Helper function for all API calls.
 * Wraps fetch() with proper headers, error handling, and JSON parsing.
 * @param {string} endpoint - API path (e.g., '/api/reservations')
 * @param {string} method - HTTP method (GET, POST, PUT, DELETE)
 * @param {string|null} data - URL-encoded form data for POST/PUT
 * @returns {Promise<Object>} Parsed JSON response
 */
function apiCall(endpoint, method, data) {
    var options = {
        method: method,
        credentials: 'same-origin'
    };

    if (data && (method === 'POST' || method === 'PUT')) {
        options.headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        options.body = data;
    }

    return fetch(BASE_URL + endpoint, options)
        .then(function(response) {
            // If unauthorized, redirect to login
            if (response.status === 401) {
                window.location.href = BASE_URL + '/index.html';
                throw new Error('Not authenticated');
            }
            return response.json();
        });
}

/**
 * Session check — verifies user is logged in.
 * Redirects to login page if session is invalid.
 */
function checkSession() {
    apiCall('/api/dashboard', 'GET')
        .then(function(data) {
            // Update nav user name if element exists
            var navUser = document.getElementById('navUserName');
            if (navUser) navUser.textContent = data.fullName || '';
        })
        .catch(function() {
            // Redirect to login if not authenticated
            window.location.href = BASE_URL + '/index.html';
        });
}

// --- Dashboard Functions ---

/** Loads dashboard statistics and populates the page. */
function loadDashboardStats() {
    return apiCall('/api/dashboard', 'GET');
}

/** Loads recent reservations for the dashboard table. */
function loadRecentReservations() {
    return apiCall('/api/reservations', 'GET');
}

// --- Reservation Functions ---

/**
 * Loads available rooms for the given date range and type.
 * Used by the add-reservation page.
 */
function loadAvailableRooms(checkIn, checkOut, type) {
    var params = '?checkIn=' + checkIn + '&checkOut=' + checkOut;
    if (type) params += '&type=' + type;
    return apiCall('/api/rooms' + params, 'GET');
}

/**
 * Submits a new reservation via POST.
 * @param {string} formData - URL-encoded form data
 */
function submitReservation(formData) {
    return apiCall('/api/reservations', 'POST', formData);
}

/**
 * Searches reservations by keyword.
 * @param {string} keyword - Search term
 */
function searchReservationsApi(keyword) {
    return apiCall('/api/reservations?search=' + encodeURIComponent(keyword), 'GET');
}

/**
 * Cancels a reservation by ID.
 * @param {number} id - Reservation ID
 */
function cancelReservationApi(id) {
    return apiCall('/api/reservations?id=' + id, 'DELETE');
}

// --- Billing Functions ---

/**
 * Looks up a reservation by number.
 * @param {string} number - Reservation number (e.g., RES-100001)
 */
function lookupReservationApi(number) {
    return apiCall('/api/reservations?number=' + encodeURIComponent(number), 'GET');
}

/**
 * Generates a bill for a reservation.
 * @param {number} reservationId - Reservation ID
 * @param {string} strategyType - STANDARD, SEASONAL, or LOYALTY
 */
function generateBillApi(reservationId, strategyType) {
    var data = 'reservationId=' + reservationId + '&strategyType=' + strategyType;
    return apiCall('/api/billing', 'POST', data);
}

// --- Report Functions ---

/**
 * Generates a report.
 * @param {string} type - 'occupancy' or 'revenue'
 * @param {string} startDate - Start date (YYYY-MM-DD)
 * @param {string} endDate - End date (YYYY-MM-DD)
 */
function generateReportApi(type, startDate, endDate) {
    return apiCall('/api/reports?type=' + type + '&startDate=' + startDate + '&endDate=' + endDate, 'GET');
}

// --- Utility Functions ---

/**
 * Formats a date string for display.
 * @param {string} dateStr - Date string (YYYY-MM-DD)
 * @returns {string} Formatted date
 */
function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    var d = new Date(dateStr);
    return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
}

/**
 * Formats a number as currency (LKR).
 * @param {number} amount - Amount to format
 * @returns {string} Formatted amount with commas and 2 decimal places
 */
function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '0.00';
    return Number(amount).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

/**
 * Shows an alert message on the page.
 * @param {string} type - 'success', 'error', 'warning', 'info'
 * @param {string} message - Alert text
 */
function showAlert(type, message) {
    var container = document.getElementById('alertContainer');
    if (!container) return;

    var alert = document.createElement('div');
    alert.className = 'alert alert-' + type;
    alert.textContent = message;

    // Add close button
    var closeBtn = document.createElement('span');
    closeBtn.textContent = ' ×';
    closeBtn.style.cursor = 'pointer';
    closeBtn.style.float = 'right';
    closeBtn.style.fontWeight = 'bold';
    closeBtn.onclick = function() { alert.remove(); };
    alert.appendChild(closeBtn);

    container.innerHTML = '';
    container.appendChild(alert);

    // Auto-dismiss after 5 seconds
    setTimeout(function() {
        if (alert.parentNode) alert.remove();
    }, 5000);
}

/** Shows the loading spinner. */
function showLoading() {
    var el = document.getElementById('loading');
    if (el) el.classList.add('active');
}

/** Hides the loading spinner. */
function hideLoading() {
    var el = document.getElementById('loading');
    if (el) el.classList.remove('active');
}

// --- Form Validation Helpers ---

/** Validates phone number format. */
function isValidPhone(phone) {
    return /^\+?[0-9]{7,15}$/.test(phone);
}

/** Validates email format. */
function isValidEmail(email) {
    return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(email);
}

/** Validates name format. */
function isValidName(name) {
    return /^[A-Za-z\s'\-]{2,100}$/.test(name);
}

/** Validates that checkout is after checkin. */
function isValidDateRange(checkIn, checkOut) {
    return checkOut > checkIn;
}

// --- Page Initialization ---
// Check session on every page load (except login page)
document.addEventListener('DOMContentLoaded', function() {
    var page = document.body.getAttribute('data-page');
    if (page && page !== 'login') {
        checkSession();
    }
});
