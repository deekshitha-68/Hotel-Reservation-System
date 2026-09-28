/**
 * my-bookings.js — My Bookings page logic.
 *
 * Lets the user enter their email to look up all their reservations.
 * Renders booking cards with status badges and links to details.
 */

document.addEventListener('DOMContentLoaded', function () {
    // Check if email is passed as query param (e.g., from confirmation page)
    const emailParam = getQueryParam('email');
    if (emailParam) {
        document.getElementById('emailInput').value = emailParam;
        lookupBookings(emailParam);
    }

    // Handle form submission
    document.getElementById('lookupForm').addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('emailInput').value.trim();
        if (email) {
            lookupBookings(email);
        }
    });
});

/**
 * Fetches and renders bookings for the given email.
 */
async function lookupBookings(email) {
    clearMessages('errorContainer');
    showLoading('bookingsContainer');

    try {
        const bookings = await apiGet(`/reservations?email=${encodeURIComponent(email)}`);
        renderBookings(bookings);
    } catch (err) {
        showError('errorContainer', err.message);
        document.getElementById('bookingsContainer').innerHTML = '';
    }
}

/**
 * Renders booking cards into the container.
 */
function renderBookings(bookings) {
    const container = document.getElementById('bookingsContainer');

    if (bookings.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <h4>No bookings found</h4>
                <p>No reservations were found for this email address.</p>
                <a href="index.html" class="btn btn-outline-primary mt-2">
                    <i class="bi bi-search"></i> Search Rooms
                </a>
            </div>
        `;
        return;
    }

    let html = '<div class="row g-3">';
    bookings.forEach(booking => {
        html += `
            <div class="col-lg-6">
                <div class="info-card h-100">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <h5 class="mb-1" style="border: none; padding: 0;">
                                Reservation #${booking.reservationId}
                            </h5>
                            <small class="text-muted">Booked on ${formatDateTime(booking.bookingDate)}</small>
                        </div>
                        <span class="badge ${getStatusBadgeClass(booking.bookingStatus)} fs-6">
                            ${booking.bookingStatus}
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Room</span>
                        <span class="detail-value">
                            ${escapeHtml(booking.roomNumber)}
                            <span class="badge ${getRoomTypeBadgeClass(booking.roomType)} ms-1">${booking.roomType}</span>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Dates</span>
                        <span class="detail-value">${formatDate(booking.checkIn)} → ${formatDate(booking.checkOut)}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Nights / Guests</span>
                        <span class="detail-value">${booking.numberOfNights} nights · ${booking.numberOfGuests} guests</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Total</span>
                        <span class="detail-value fw-bold" style="color: var(--primary-color);">${formatPrice(booking.totalAmount)}</span>
                    </div>
                    <div class="mt-3">
                        <a href="booking-details.html?reservationId=${booking.reservationId}" class="btn btn-outline-primary btn-sm w-100">
                            <i class="bi bi-eye"></i> View Details
                        </a>
                    </div>
                </div>
            </div>
        `;
    });
    html += '</div>';

    container.innerHTML = html;
}
