-- ============================================================================
-- Hotel Reservation System — Seed Data
-- ============================================================================
-- Inserts 10 sample rooms across all 3 types (4 Standard, 3 Deluxe, 3 Suite)
-- so the app is demo-ready immediately after startup.
--
-- Uses MERGE INTO for H2 compatibility (equivalent to MySQL's INSERT IGNORE).
-- ============================================================================

-- Standard Rooms (budget-friendly, basic amenities)
MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (1, '101', 'STANDARD', 2500.00, 2,
        'Cozy standard room with a queen-size bed, work desk, free Wi-Fi, and an en-suite bathroom with hot shower. Perfect for solo travelers or couples on a budget.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (2, '102', 'STANDARD', 2500.00, 2,
        'Comfortable standard room featuring modern furnishings, flat-screen TV, mini-fridge, and a city-view window. Ideal for short business stays.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (3, '103', 'STANDARD', 3000.00, 3,
        'Spacious standard room with one double bed and one single bed, accommodating up to 3 guests. Includes complimentary breakfast and parking.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (4, '104', 'STANDARD', 2800.00, 2,
        'Well-appointed standard room with contemporary decor, blackout curtains, tea/coffee maker, and a rainfall shower. Ground floor with garden access.',
        'ACTIVE');

-- Deluxe Rooms (premium comfort, extra space)
MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (5, '201', 'DELUXE', 4500.00, 3,
        'Elegant deluxe room with a king-size bed, plush seating area, 55-inch smart TV, premium minibar, and a marble bathroom with bathtub and rain shower.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (6, '202', 'DELUXE', 4500.00, 3,
        'Luxurious deluxe room offering panoramic city views, a king bed with premium linens, Nespresso machine, and complimentary access to the rooftop pool.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (7, '203', 'DELUXE', 5000.00, 4,
        'Family-friendly deluxe room with two queen beds, a large work desk, 65-inch TV, and a spacious bathroom. Includes breakfast for all guests.',
        'ACTIVE');

-- Suite Rooms (luxury experience, separate living area)
MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (8, '301', 'SUITE', 8000.00, 4,
        'Grand suite with a separate living room, king bedroom, dining area for 4, walk-in closet, jacuzzi bathtub, and a private balcony with sunset views.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (9, '302', 'SUITE', 8500.00, 4,
        'Presidential suite featuring two bedrooms, a full kitchen, living and dining areas, home theater system, and 24/7 personal butler service.',
        'ACTIVE');

MERGE INTO rooms (room_id, room_number, room_type, price_per_night, capacity, description, status) KEY (room_id)
VALUES (10, '303', 'SUITE', 10000.00, 6,
        'The Royal Penthouse — our most luxurious offering. Three bedrooms, panoramic floor-to-ceiling windows, private terrace with plunge pool, and exclusive concierge.',
        'ACTIVE');
