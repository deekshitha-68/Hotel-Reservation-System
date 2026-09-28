package com.codealpha.hotelreservation.entity.enums;

/**
 * Administrative status of a hotel room.
 *
 * <p>This is for admin-level room management (e.g., taking a room offline
 * for maintenance), NOT for booking availability. A room with status ACTIVE
 * can still be fully booked for specific dates — availability is determined
 * by checking reservation date overlaps, not by this flag.
 *
 * <ul>
 *   <li>ACTIVE — room is available for booking (subject to date availability)</li>
 *   <li>INACTIVE — room is taken offline by admin (hidden from search results)</li>
 * </ul>
 */
public enum RoomStatus {
    ACTIVE,
    INACTIVE
}
