package com.codealpha.hotelreservation.repository;

import com.codealpha.hotelreservation.entity.Room;
import com.codealpha.hotelreservation.entity.enums.RoomStatus;
import com.codealpha.hotelreservation.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Room} entities.
 *
 * <p>Provides basic CRUD operations (inherited from JpaRepository) plus
 * custom query methods derived from method names by Spring Data.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Find all rooms with the given administrative status.
     * Used to list only ACTIVE rooms in public-facing search results.
     *
     * @param status the room status to filter by (ACTIVE or INACTIVE)
     * @return list of rooms matching the status
     */
    List<Room> findByStatus(RoomStatus status);

    /**
     * Find all active rooms of a specific type.
     * Used when the user filters search results by room category.
     *
     * @param status   typically ACTIVE
     * @param roomType the category to filter by (STANDARD, DELUXE, SUITE)
     * @return list of matching rooms
     */
    List<Room> findByStatusAndRoomType(RoomStatus status, RoomType roomType);
}
