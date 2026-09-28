/**
 * room-details.js — Room Details page logic.
 *
 * Fetches a single room's details from the API and renders them.
 * Carries forward the selected dates so the "Proceed to Book" button
 * can pass them to the booking page.
 */

document.addEventListener('DOMContentLoaded', async function () {
    const roomId = getQueryParam('roomId');
    const checkIn = getQueryParam('checkIn');
    const checkOut = getQueryParam('checkOut');
    const guests = getQueryParam('guests');

    if (!roomId) {
        showError('errorContainer', 'No room specified. Please go back and select a room.');
        document.getElementById('roomDetailContainer').innerHTML = '';
        return;
    }

    try {
        const room = await apiGet(`/rooms/${roomId}`);
        renderRoomDetails(room, checkIn, checkOut, guests);
    } catch (err) {
        showError('errorContainer', err.message);
        document.getElementById('roomDetailContainer').innerHTML = '';
    }
});

/**
 * Renders full room details into the page.
 */
function renderRoomDetails(room, checkIn, checkOut, guests) {
    const container = document.getElementById('roomDetailContainer');

    // Choose icon based on room type
    let icon = 'bi-house-door';
    if (room.roomType === 'DELUXE') icon = 'bi-star';
    if (room.roomType === 'SUITE') icon = 'bi-gem';

    // Calculate nights and total if dates are provided
    let nightsInfo = '';
    if (checkIn && checkOut) {
        const nights = Math.ceil((new Date(checkOut) - new Date(checkIn)) / (1000 * 60 * 60 * 24));
        const total = room.pricePerNight * nights;
        nightsInfo = `
            <div class="info-card mt-4">
                <h5><i class="bi bi-calculator"></i> Price Estimate</h5>
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
                    <span class="detail-label fw-bold" style="color: var(--text-dark);">Estimated Total</span>
                    <span class="detail-value" style="color: var(--primary-color); font-size: 1.2rem;">${formatPrice(total)}</span>
                </div>
            </div>
        `;
    }

    // Build the "Proceed to Book" link
    let bookLink = `booking.html?roomId=${room.roomId}`;
    if (checkIn && checkOut) {
        bookLink += `&checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests || 1}`;
    }

    container.innerHTML = `
        <div class="row g-4">
            <div class="col-lg-7">
                <!-- Room Image Placeholder -->
                <div class="room-card-img" style="height: 300px; border-radius: var(--radius-lg);">
                    <i class="bi ${icon}" style="font-size: 5rem;"></i>
                </div>
            </div>
            <div class="col-lg-5">
                <div class="info-card">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <h3 class="mb-0" style="border: none;">Room ${escapeHtml(room.roomNumber)}</h3>
                        <span class="badge ${getRoomTypeBadgeClass(room.roomType)} fs-6">${room.roomType}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Price per Night</span>
                        <span class="detail-value room-price">${formatPrice(room.pricePerNight)}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Capacity</span>
                        <span class="detail-value"><i class="bi bi-people-fill"></i> Up to ${room.capacity} guests</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Status</span>
                        <span class="detail-value"><span class="badge bg-success">Available</span></span>
                    </div>
                </div>

                ${nightsInfo}

                <div class="mt-4">
                    <a href="${bookLink}" class="btn btn-primary btn-lg w-100">
                        <i class="bi bi-bookmark-plus"></i> Proceed to Book
                    </a>
                    <a href="javascript:history.back()" class="btn btn-outline-secondary w-100 mt-2">
                        <i class="bi bi-arrow-left"></i> Back to Rooms
                    </a>
                </div>
            </div>
        </div>

        <!-- Full Description -->
        <div class="info-card mt-4">
            <h5><i class="bi bi-info-circle"></i> Room Description</h5>
            <p class="mb-0">${escapeHtml(room.description)}</p>
        </div>
    `;
}
