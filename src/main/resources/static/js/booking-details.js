/**
 * booking-details.js — Booking Details page logic.
 *
 * Fetches a single reservation's full details and renders them.
 * Provides a "Cancel Booking" button with a confirmation dialog
 * that calls PUT /api/reservations/{id}/cancel.
 */

document.addEventListener('DOMContentLoaded', async function () {
    const reservationId = getQueryParam('reservationId');

    if (!reservationId) {
        showError('errorContainer', 'No reservation specified.');
        document.getElementById('detailsContainer').innerHTML = '';
        return;
    }

    await loadReservation(reservationId);
});

/**
 * Fetches and renders reservation details.
 */
async function loadReservation(reservationId) {
    try {
        const res = await apiGet(`/reservations/${reservationId}`);
        renderDetails(res);
    } catch (err) {
        showError('errorContainer', err.message);
        document.getElementById('detailsContainer').innerHTML = '';
    }
}

/**
 * Renders the full reservation details card.
 */
function renderDetails(res) {
    const paymentInfo = res.paymentId ? `
        <div class="info-card mb-3">
            <h5><i class="bi bi-credit-card"></i> Payment</h5>
            <div class="detail-row">
                <span class="detail-label">Payment ID</span>
                <span class="detail-value">#${res.paymentId}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Method</span>
                <span class="detail-value">${res.paymentMethod}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Status</span>
                <span class="detail-value"><span class="badge badge-success-payment">${res.paymentStatus}</span></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Date</span>
                <span class="detail-value">${formatDateTime(res.paymentDate)}</span>
            </div>
        </div>
    ` : '';

    // Show cancel button only if booking is not already cancelled
    const cancelBtn = res.bookingStatus !== 'CANCELLED' ? `
        <button class="btn btn-danger w-100 mt-3" id="cancelBtn" onclick="cancelBooking(${res.reservationId})">
            <i class="bi bi-x-circle"></i> Cancel Booking
        </button>
    ` : `
        <div class="alert alert-secondary text-center mt-3 mb-0">
            <i class="bi bi-info-circle"></i> This booking has been cancelled.
        </div>
    `;

    document.getElementById('detailsContainer').innerHTML = `
        <!-- Status Banner -->
        <div class="text-center mb-4">
            <span class="badge ${getStatusBadgeClass(res.bookingStatus)} fs-5 px-4 py-2">
                ${res.bookingStatus}
            </span>
            <h4 class="mt-2">Reservation #${res.reservationId}</h4>
            <small class="text-muted">Booked on ${formatDateTime(res.bookingDate)}</small>
        </div>

        <!-- Guest Information -->
        <div class="info-card mb-3">
            <h5><i class="bi bi-person-fill"></i> Guest Information</h5>
            <div class="detail-row">
                <span class="detail-label">Name</span>
                <span class="detail-value">${escapeHtml(res.customerName)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Email</span>
                <span class="detail-value">${escapeHtml(res.customerEmail)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Phone</span>
                <span class="detail-value">${escapeHtml(res.customerPhone)}</span>
            </div>
        </div>

        <!-- Room & Stay Details -->
        <div class="info-card mb-3">
            <h5><i class="bi bi-door-open"></i> Room & Stay</h5>
            <div class="detail-row">
                <span class="detail-label">Room</span>
                <span class="detail-value">
                    ${escapeHtml(res.roomNumber)}
                    <span class="badge ${getRoomTypeBadgeClass(res.roomType)} ms-1">${res.roomType}</span>
                </span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Check-in</span>
                <span class="detail-value">${formatDate(res.checkIn)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Check-out</span>
                <span class="detail-value">${formatDate(res.checkOut)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Nights</span>
                <span class="detail-value">${res.numberOfNights}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Guests</span>
                <span class="detail-value">${res.numberOfGuests}</span>
            </div>
            <div class="detail-row" style="border-top: 2px solid var(--primary-light); padding-top: 0.75rem; margin-top: 0.5rem;">
                <span class="detail-label fw-bold" style="color: var(--text-dark);">Total Amount</span>
                <span class="detail-value" style="color: var(--primary-color); font-size: 1.2rem; font-weight: 700;">${formatPrice(res.totalAmount)}</span>
            </div>
        </div>

        ${paymentInfo}

        <!-- Action Buttons -->
        <div class="d-flex gap-2">
            <a href="my-bookings.html" class="btn btn-outline-primary flex-fill">
                <i class="bi bi-arrow-left"></i> Back to My Bookings
            </a>
        </div>
        ${cancelBtn}
    `;
}

/**
 * Handles the Cancel Booking button click.
 * Shows a confirmation dialog before calling the API.
 */
async function cancelBooking(reservationId) {
    if (!confirm('Are you sure you want to cancel this booking? This action cannot be undone.')) {
        return;
    }

    const cancelBtn = document.getElementById('cancelBtn');
    cancelBtn.disabled = true;
    cancelBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Cancelling...';

    try {
        await apiPut(`/reservations/${reservationId}/cancel`);
        showSuccess('errorContainer', 'Booking cancelled successfully.');
        // Reload the reservation details to reflect the updated status
        await loadReservation(reservationId);
    } catch (err) {
        showError('errorContainer', err.message);
        cancelBtn.disabled = false;
        cancelBtn.innerHTML = '<i class="bi bi-x-circle"></i> Cancel Booking';
    }
}
