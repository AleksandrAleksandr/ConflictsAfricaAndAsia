package com.example.armedconflicts;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Event... event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Delete
    void delete(Event event);

    @Query("DELETE FROM Event")
    void deleteAll();

    @Query("DELETE FROM Event WHERE event_date NOT IN (:dateList)")
    int deleteByDate(List<String> dateList);

    @Query("SELECT * FROM Event")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM Event WHERE event_type = :type")
    LiveData<List<Event>> getEventsByType(String type);

    @Query("SELECT * FROM Event WHERE event_date = :date AND event_type LIKE :type")
    LiveData<List<Event>> getEventsByDateAndType(String date, String type);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND fatalities >= :fatalities")
    LiveData<List<Event>> getEventsByWeek(List<String> dateList, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND event_type LIKE :type AND fatalities >= :fatalities")
    LiveData<List<Event>> getEventsByWeekAndType(List<String> dateList, String type, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND region LIKE :region AND fatalities >= :fatalities")
    LiveData<List<Event>> getEventsByWeekAndRegion(List<String> dateList, String region, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND event_type LIKE :type AND region LIKE :region AND fatalities >= :fatalities")
    LiveData<List<Event>> getEventsByWeekAndTypeAndRegion(List<String> dateList, String type, String region, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND fatalities <= :fatalities")
    LiveData<List<Event>> getEventsByWeek_(List<String> dateList, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND event_type LIKE :type AND fatalities <= :fatalities")
    LiveData<List<Event>> getEventsByWeekAndType_(List<String> dateList, String type, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND region LIKE :region AND fatalities <= :fatalities")
    LiveData<List<Event>> getEventsByWeekAndRegion_(List<String> dateList, String region, int fatalities);

    @Query("SELECT * FROM Event WHERE event_date IN (:dateList) AND event_type LIKE :type AND region LIKE :region AND fatalities <= :fatalities")
    LiveData<List<Event>> getEventsByWeekAndTypeAndRegion_(List<String> dateList, String type, String region, int fatalities);
}
