/**
 * booking.js — Booking form page logic.
 *
 * Pre-fills form fields from query params, fetches room details for the
 * summary panel, provides live price preview, and submits the reservation
 * to the API.
 */

let currentRoom = null;

document.addEventListener('DOMContentLoaded', async function () {
    const roomId = getQueryParam('roomId');
    const checkIn = getQueryParam('checkIn');
    const checkOut = getQueryParam('checkOut');
    const guests = getQueryParam('guests');

    if (!roomId) {
        showError('errorContainer', 'No room selected. Please go back and choose a room.');
        document.getElementById('roomSummary').innerHTML = '';
        return;
    }

    // Set minimum date to today
    setMinDateToday('checkIn', 'checkOut');

    // Pre-fill dates and guests from query params
    if (checkIn) document.getElementById('checkIn').value = checkIn;
    if (checkOut) document.getElementById('checkOut').value = checkOut;
    if (guests) document.getElementById('guests').value = guests;

    // Fetch room details for the summary panel
    try {
        currentRoom = await apiGet(`/rooms/${roomId}`);
        updateSummary();
    } catch (err) {
        showError('errorContainer', err.message);
        document.getElementById('roomSummary').innerHTML = '';
        return;
    }

    // Live price update when dates change
    document.getElementById('checkIn').addEventListener('change', updateSummary);
    document.getElementById('checkOut').addEventListener('change', updateSummary);
    document.getElementById('guests').addEventListener('change', updateSummary);

    // Handle form submission
    document.getElementById('bookingForm').addEventListener('submit', handleBooking);
});

/**
 * Updates the room and price summary sidebar with current form values.
 */
function updateSummary() {
    if (!currentRoom) return;

    const checkIn = document.getElementById('checkIn').value;
    const checkOut = document.getElementById('checkOut').value;
    const guests = document.getElementById('guests').value;

    let nightsHtml = '';
    if (checkIn && checkOut && checkOut > checkIn) {
        const nights = Math.ceil((new Date(checkOut) - new Date(checkIn)) / (1000 * 60 * 60 * 24));
        const total = currentRoom.pricePerNight * nights;
        nightsHtml = `
            <div class="detail-row">
                <span class="detail-label">Check-in</span>
                <span class="detail-value">${formatDate(checkIn)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Check-out</span>
                <span class="detail-value">${formatDate(checkOut)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Nights</span>
                <span class="detail-value">${nights}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Guests</span>
                <span class="detail-value">${guests || 1}</span>
            </div>
            <div class="detail-row" style="border-top: 2px solid var(--primary-light); padding-top: 0.75rem; margin-top: 0.5rem;">
                <span class="detail-label fw-bold" style="color: var(--text-dark);">Total Amount</span>
                <span class="detail-value" style="color: var(--primary-color); font-size: 1.3rem; font-weight: 700;">${formatPrice(total)}</span>
            </div>
        `;
    }

    // Choose icon based on room type
    let icon = 'bi-house-door';
    if (currentRoom.roomType === 'DELUXE') icon = 'bi-star';
    if (currentRoom.roomType === 'SUITE') icon = 'bi-gem';

    document.getElementById('roomSummary').innerHTML = `
        <div class="info-card">
            <h5><i class="bi bi-door-open"></i> Room Summary</h5>
            <div class="text-center mb-3">
                <div class="room-card-img mx-auto" style="height: 120px; border-radius: var(--radius); max-width: 200px;">
                    <i class="bi ${icon}" style="font-size: 2.5rem;"></i>
                </div>
            </div>
            <div class="detail-row">
                <span class="detail-label">Room</span>
                <span class="detail-value">${escapeHtml(currentRoom.roomNumber)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Type</span>
                <span class="detail-value"><span class="badge ${getRoomTypeBadgeClass(currentRoom.roomType)}">${currentRoom.roomType}</span></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Price / Night</span>
                <span class="detail-value">${formatPrice(currentRoom.pricePerNight)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Max Capacity</span>
                <span class="detail-value">${currentRoom.capacity} guests</span>
            </div>
            ${nightsHtml}
        </div>
    `;
}

/**
 * Handles the booking form submission.
 * Sends a POST request to create the reservation, then redirects to payment.
 */
async function handleBooking(e) {
    e.preventDefault();
    clearMessages('errorContainer');

    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';

    const checkIn = document.getElementById('checkIn').value;
    const checkOut = document.getElementById('checkOut').value;

    // Client-side validation
    if (checkOut <= checkIn) {
        showError('errorContainer', 'Check-out date must be after check-in date.');
        resetButton();
        return;
    }

    const requestData = {
        roomId: currentRoom.roomId,
        customerName: document.getElementById('customerName').value.trim(),
        customerEmail: document.getElementById('customerEmail').value.trim(),
        customerPhone: document.getElementById('customerPhone').value.trim(),
        checkIn: checkIn,
        checkOut: checkOut,
        numberOfGuests: parseInt(document.getElementById('guests').value)
    };

    try {
        const reservation = await apiPost('/reservations', requestData);
        // Redirect to payment page with the new reservation ID
        window.location.href = `payment.html?reservationId=${reservation.reservationId}`;
    } catch (err) {
        showError('errorContainer', err.message);
        resetButton();
    }
}

function resetButton() {
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<i class="bi bi-check-circle"></i> Confirm Booking';
}
