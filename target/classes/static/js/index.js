/**
 * index.js — Home page logic.
 *
 * Handles the search form submission: validates dates client-side,
 * then redirects to rooms.html with the search parameters as query params.
 */

document.addEventListener('DOMContentLoaded', function () {
    // Set minimum date to today for date pickers
    setMinDateToday('checkIn', 'checkOut');

    // When check-in date changes, update check-out minimum
    document.getElementById('checkIn').addEventListener('change', function () {
        const checkOutInput = document.getElementById('checkOut');
        if (this.value) {
            // Check-out must be at least the day after check-in
            const nextDay = new Date(this.value);
            nextDay.setDate(nextDay.getDate() + 1);
            checkOutInput.setAttribute('min', nextDay.toISOString().split('T')[0]);

            // If current check-out is before new min, clear it
            if (checkOutInput.value && checkOutInput.value <= this.value) {
                checkOutInput.value = '';
            }
        }
    });

    // Handle search form submission
    document.getElementById('searchForm').addEventListener('submit', function (e) {
        e.preventDefault();
        clearMessages('errorContainer');

        const checkIn = document.getElementById('checkIn').value;
        const checkOut = document.getElementById('checkOut').value;
        const guests = document.getElementById('guests').value;
        const roomType = document.getElementById('roomType').value;

        // Client-side date validation
        if (!checkIn || !checkOut) {
            showError('errorContainer', 'Please select both check-in and check-out dates.');
            return;
        }

        if (checkOut <= checkIn) {
            showError('errorContainer', 'Check-out date must be after check-in date.');
            return;
        }

        if (guests < 1) {
            showError('errorContainer', 'Number of guests must be at least 1.');
            return;
        }

        // Build query string and redirect to rooms page
        let params = `?checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}`;
        if (roomType) {
            params += `&roomType=${roomType}`;
        }

        window.location.href = 'rooms.html' + params;
    });
});
