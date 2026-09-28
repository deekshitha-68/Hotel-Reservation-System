/**
 * confirmation.js — Booking confirmation page logic.
 *
 * Fetches the completed reservation details and renders a confirmation
 * card with all booking, payment, and customer information.
 */

document.addEventListener('DOMContentLoaded', async function () {
    const reservationId = getQueryParam('reservationId');

    if (!reservationId) {
        showError('errorContainer', 'No reservation specified.');
        document.getElementById('confirmationContainer').innerHTML = '';
        return;
    }

    try {
        const reservation = await apiGet(`/reservations/${reservationId}`);
        renderConfirmation(reservation);
    } catch (err) {
        showError('errorContainer', err.message);
        document.getElementById('confirmationContainer').innerHTML = '';
    }
});

/**
 * Renders the booking confirmation card.
 */
function renderConfirmation(res) {
    const paymentInfo = res.paymentId ? `
        <div class="detail-row">
            <span class="detail-label">Payment Method</span>
            <span class="detail-value">${res.paymentMethod}</span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Payment Status</span>
            <span class="detail-value"><span class="badge badge-success-payment">${res.paymentStatus}</span></span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Payment Date</span>
            <span class="detail-value">${formatDateTime(res.paymentDate)}</span>
        </div>
    ` : '';

    document.getElementById('confirmationContainer').innerHTML = `
        <div class="confirmation-card">
            <!-- Success Header -->
            <div class="confirmation-header">
                <i class="bi bi-check-circle-fill d-block"></i>
                <h2 class="mb-1">Booking Confirmed!</h2>
                <p class="mb-0 opacity-75">Your reservation has been successfully processed.</p>
            </div>

            <!-- Details -->
            <div class="p-4">
                <!-- Reservation ID Badge -->
                <div class="text-center mb-4">
                    <span class="badge bg-dark fs-5 px-4 py-2">
                        Reservation #${res.reservationId}
                    </span>
                </div>

                <!-- Guest Info -->
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

                <!-- Room & Stay Info -->
                <div class="info-card mb-3">
                    <h5><i class="bi bi-door-open"></i> Room & Stay</h5>
                    <div class="detail-row">
                        <span class="detail-label">Room</span>
                        <span class="detail-value">${escapeHtml(res.roomNumber)} 
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
                </div>

                <!-- Payment Info -->
                <div class="info-card mb-3">
                    <h5><i class="bi bi-credit-card"></i> Payment Details</h5>
                    <div class="detail-row">
                        <span class="detail-label">Price / Night</span>
                        <span class="detail-value">${formatPrice(res.pricePerNight)}</span>
                    </div>
                    <div class="detail-row" style="border-top: 2px solid var(--primary-light); padding-top: 0.75rem;">
                        <span class="detail-label fw-bold" style="color: var(--text-dark);">Total Amount</span>
                        <span class="detail-value" style="color: var(--primary-color); font-size: 1.2rem; font-weight: 700;">${formatPrice(res.totalAmount)}</span>
                    </div>
                    ${paymentInfo}
                    <div class="detail-row">
                        <span class="detail-label">Booking Status</span>
                        <span class="detail-value"><span class="badge ${getStatusBadgeClass(res.bookingStatus)}">${res.bookingStatus}</span></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Booking Date</span>
                        <span class="detail-value">${formatDateTime(res.bookingDate)}</span>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="d-flex gap-2 mt-4">
                    <a href="my-bookings.html" class="btn btn-primary flex-fill">
                        <i class="bi bi-list-check"></i> View My Bookings
                    </a>
                    <a href="index.html" class="btn btn-outline-primary flex-fill">
                        <i class="bi bi-house"></i> Back to Home
                    </a>
                </div>
            </div>
        </div>
    `;
}
