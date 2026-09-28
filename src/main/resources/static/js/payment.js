/**
 * payment.js — Payment page logic.
 *
 * Fetches the reservation details, displays a summary, and processes
 * simulated payment via POST /api/payments. On success, redirects to
 * the confirmation page.
 */

document.addEventListener('DOMContentLoaded', async function () {
    const reservationId = getQueryParam('reservationId');

    if (!reservationId) {
        showError('errorContainer', 'No reservation specified. Please complete a booking first.');
        return;
    }

    try {
        const reservation = await apiGet(`/reservations/${reservationId}`);
        renderSummary(reservation);
        document.getElementById('paymentFormCard').style.display = 'block';

        // Handle payment form submission
        document.getElementById('paymentForm').addEventListener('submit', async function (e) {
            e.preventDefault();
            clearMessages('errorContainer');

            const payBtn = document.getElementById('payBtn');
            payBtn.disabled = true;
            payBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing Payment...';

            const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

            try {
                const payment = await apiPost('/payments', {
                    reservationId: parseInt(reservationId),
                    paymentMethod: paymentMethod
                });
                // Redirect to confirmation page
                window.location.href = `confirmation.html?reservationId=${reservationId}`;
            } catch (err) {
                showError('errorContainer', err.message);
                payBtn.disabled = false;
                payBtn.innerHTML = '<i class="bi bi-lock-fill"></i> Pay Now';
            }
        });
    } catch (err) {
        showError('errorContainer', err.message);
    }
});

/**
 * Renders the reservation summary on the payment page.
 */
function renderSummary(reservation) {
    document.getElementById('reservationSummary').innerHTML = `
        <div class="info-card">
            <h5><i class="bi bi-receipt"></i> Booking Summary</h5>
            <div class="detail-row">
                <span class="detail-label">Reservation ID</span>
                <span class="detail-value">#${reservation.reservationId}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Room</span>
                <span class="detail-value">${escapeHtml(reservation.roomNumber)} (${reservation.roomType})</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Guest Name</span>
                <span class="detail-value">${escapeHtml(reservation.customerName)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Check-in</span>
                <span class="detail-value">${formatDate(reservation.checkIn)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Check-out</span>
                <span class="detail-value">${formatDate(reservation.checkOut)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Nights</span>
                <span class="detail-value">${reservation.numberOfNights}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Guests</span>
                <span class="detail-value">${reservation.numberOfGuests}</span>
            </div>
            <div class="detail-row" style="border-top: 2px solid var(--primary-light); padding-top: 0.75rem; margin-top: 0.5rem;">
                <span class="detail-label fw-bold fs-5" style="color: var(--text-dark);">Amount to Pay</span>
                <span class="detail-value" style="color: var(--primary-color); font-size: 1.4rem; font-weight: 700;">${formatPrice(reservation.totalAmount)}</span>
            </div>
        </div>
    `;
}
