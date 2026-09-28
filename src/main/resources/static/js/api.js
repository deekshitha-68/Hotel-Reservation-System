/**
 * api.js — Shared API helper functions for the Hotel Reservation System.
 *
 * Provides reusable functions for making fetch requests to the REST API
 * and handling errors consistently across all pages.
 */

const API_BASE = '/api';

/**
 * Makes a GET request to the given API endpoint.
 *
 * @param {string} endpoint - The API path (e.g., '/rooms' or '/reservations?email=test@test.com')
 * @returns {Promise<any>} Parsed JSON response
 * @throws {Error} with the server's error message if the request fails
 */
async function apiGet(endpoint) {
    const response = await fetch(API_BASE + endpoint);
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'An unexpected error occurred' }));
        throw new Error(error.message || `Request failed with status ${response.status}`);
    }
    return response.json();
}

/**
 * Makes a POST request to the given API endpoint with a JSON body.
 *
 * @param {string} endpoint - The API path (e.g., '/reservations')
 * @param {object} data - The request body (will be JSON-stringified)
 * @returns {Promise<any>} Parsed JSON response
 * @throws {Error} with the server's error message if the request fails
 */
async function apiPost(endpoint, data) {
    const response = await fetch(API_BASE + endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'An unexpected error occurred' }));
        throw new Error(error.message || `Request failed with status ${response.status}`);
    }
    return response.json();
}

/**
 * Makes a PUT request to the given API endpoint.
 *
 * @param {string} endpoint - The API path (e.g., '/reservations/1/cancel')
 * @param {object} [data] - Optional request body
 * @returns {Promise<any>} Parsed JSON response
 * @throws {Error} with the server's error message if the request fails
 */
async function apiPut(endpoint, data) {
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' }
    };
    if (data) {
        options.body = JSON.stringify(data);
    }
    const response = await fetch(API_BASE + endpoint, options);
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'An unexpected error occurred' }));
        throw new Error(error.message || `Request failed with status ${response.status}`);
    }
    return response.json();
}

/**
 * Displays an error message in the specified container element.
 * Creates a Bootstrap-style alert with a dismiss button.
 *
 * @param {string} containerId - The ID of the HTML element to show the error in
 * @param {string} message - The error message to display
 */
function showError(containerId, message) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                ${escapeHtml(message)}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }
}

/**
 * Displays a success message in the specified container element.
 *
 * @param {string} containerId - The ID of the HTML element to show the message in
 * @param {string} message - The success message to display
 */
function showSuccess(containerId, message) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                ${escapeHtml(message)}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }
}

/**
 * Clears any messages in the specified container.
 *
 * @param {string} containerId - The ID of the HTML element to clear
 */
function clearMessages(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '';
    }
}

/**
 * Shows a loading spinner in the specified container.
 *
 * @param {string} containerId - The ID of the HTML element to show the spinner in
 */
function showLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="loading-spinner">
                <div class="spinner-border text-success" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        `;
    }
}

/**
 * Escapes HTML special characters to prevent XSS.
 *
 * @param {string} text - The text to escape
 * @returns {string} HTML-safe text
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Formats a price as Indian Rupees (₹).
 *
 * @param {number|string} amount - The amount to format
 * @returns {string} Formatted price string
 */
function formatPrice(amount) {
    return '₹' + parseFloat(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

/**
 * Formats a date string (yyyy-MM-dd) to a human-readable format.
 *
 * @param {string} dateStr - ISO date string
 * @returns {string} Formatted date (e.g., "20 Dec 2024")
 */
function formatDate(dateStr) {
    if (!dateStr) return '—';
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
    });
}

/**
 * Formats a datetime string to a human-readable format.
 *
 * @param {string} dateTimeStr - ISO datetime string
 * @returns {string} Formatted datetime
 */
function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '—';
    const date = new Date(dateTimeStr);
    return date.toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Returns the appropriate Bootstrap badge CSS class for a room type.
 *
 * @param {string} roomType - STANDARD, DELUXE, or SUITE
 * @returns {string} CSS class name
 */
function getRoomTypeBadgeClass(roomType) {
    switch (roomType) {
        case 'STANDARD': return 'badge-standard';
        case 'DELUXE':   return 'badge-deluxe';
        case 'SUITE':    return 'badge-suite';
        default:         return 'bg-secondary';
    }
}

/**
 * Returns the appropriate Bootstrap badge CSS class for a booking status.
 *
 * @param {string} status - PENDING, CONFIRMED, or CANCELLED
 * @returns {string} CSS class name
 */
function getStatusBadgeClass(status) {
    switch (status) {
        case 'PENDING':   return 'badge-pending';
        case 'CONFIRMED': return 'badge-confirmed';
        case 'CANCELLED': return 'badge-cancelled';
        default:          return 'bg-secondary';
    }
}

/**
 * Gets a URL query parameter value.
 *
 * @param {string} name - Parameter name
 * @returns {string|null} Parameter value or null
 */
function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

/**
 * Sets today's date as the minimum for date input fields.
 * Prevents selecting past dates for check-in.
 *
 * @param  {...string} inputIds - IDs of date input elements
 */
function setMinDateToday(...inputIds) {
    const today = new Date().toISOString().split('T')[0];
    inputIds.forEach(id => {
        const input = document.getElementById(id);
        if (input) {
            input.setAttribute('min', today);
        }
    });
}
