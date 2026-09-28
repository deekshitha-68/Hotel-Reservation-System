/**
 * rooms.js — Available Rooms page logic.
 *
 * Reads search parameters from the URL query string, fetches matching
 * rooms from the API, and renders them as Bootstrap cards.
 */

document.addEventListener('DOMContentLoaded', async function () {
    const checkIn = getQueryParam('checkIn');
    const checkOut = getQueryParam('checkOut');
    const guests = getQueryParam('guests') || '1';
    const roomType = getQueryParam('roomType');

    const container = document.getElementById('roomsContainer');
    const summaryEl = document.getElementById('searchSummary');

    // If no search params, show all active rooms
    if (!checkIn || !checkOut) {
        summaryEl.textContent = 'Showing all available rooms';
        try {
            showLoading('roomsContainer');
            const rooms = await apiGet('/rooms');
            renderRooms(rooms, container, null, null, null);
        } catch (err) {
            showError('errorContainer', err.message);
        }
        return;
    }

    // Show search summary
    summaryEl.textContent = `${formatDate(checkIn)} → ${formatDate(checkOut)} · ${guests} guest(s)` +
        (roomType ? ` · ${roomType}` : '');

    try {
        showLoading('roomsContainer');
        let endpoint = `/rooms/search?checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}`;
        if (roomType) {
            endpoint += `&roomType=${roomType}`;
        }
        const rooms = await apiGet(endpoint);
        renderRooms(rooms, container, checkIn, checkOut, guests);
    } catch (err) {
        showError('errorContainer', err.message);
    }
});

/**
 * Renders room cards into the container element.
 *
 * @param {Array} rooms - Array of room response objects
 * @param {HTMLElement} container - The container to render into
 * @param {string|null} checkIn - Selected check-in date (for "Book Now" link)
 * @param {string|null} checkOut - Selected check-out date (for "Book Now" link)
 * @param {string|null} guests - Selected guest count (for "Book Now" link)
 */
function renderRooms(rooms, container, checkIn, checkOut, guests) {
    if (rooms.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-emoji-frown"></i>
                <h4>No rooms available</h4>
                <p>Try different dates, fewer guests, or a different room type.</p>
                <a href="index.html" class="btn btn-outline-primary mt-2">
                    <i class="bi bi-arrow-left"></i> New Search
                </a>
            </div>
        `;
        return;
    }

    let html = '<div class="row g-4">';
    rooms.forEach(room => {
        // Build the "Book Now" or "View Details" link with date params
        let detailLink = `room-details.html?roomId=${room.roomId}`;
        if (checkIn && checkOut) {
            detailLink += `&checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}`;
        }

        // Choose icon based on room type
        let icon = 'bi-house-door';
        if (room.roomType === 'DELUXE') icon = 'bi-star';
        if (room.roomType === 'SUITE') icon = 'bi-gem';

        html += `
            <div class="col-md-6 col-lg-4">
                <div class="room-card">
                    <div class="room-card-img">
                        <i class="bi ${icon}"></i>
                    </div>
                    <div class="room-card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h5 class="mb-0">Room ${escapeHtml(room.roomNumber)}</h5>
                            <span class="badge ${getRoomTypeBadgeClass(room.roomType)}">${room.roomType}</span>
                        </div>
                        <p class="text-muted small mb-2">
                            <i class="bi bi-people-fill"></i> Up to ${room.capacity} guests
                        </p>
                        <p class="text-muted small flex-grow-1">${escapeHtml(room.description).substring(0, 120)}...</p>
                        <div class="d-flex justify-content-between align-items-center mt-auto">
                            <div class="room-price">
                                ${formatPrice(room.pricePerNight)} <span>/ night</span>
                            </div>
                            <a href="${detailLink}" class="btn btn-primary btn-sm">
                                ${checkIn ? '<i class="bi bi-bookmark-plus"></i> Book Now' : '<i class="bi bi-eye"></i> View'}
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });
    html += '</div>';

    container.innerHTML = html;
}
