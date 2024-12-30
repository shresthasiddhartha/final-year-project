package Zest.gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Zest.gym.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{
	
	List<Schedule> findByDay(String day);
	
	
	@Query("SELECT DISTINCT s.timeSlot FROM Schedule s")
	List<String> findDistinctTimeSlots();
	
	
	@Query("SELECT s.trainer, s.activity FROM Schedule s WHERE s.day = :day AND s.timeSlot = :timeSlot")
	List<Object[]> findTrainerAndActivityByDayAndTimeSlot(String day, String timeSlot);

    @Query("SELECT s FROM Schedule s WHERE s.day IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') "
            + "AND s.timeSlot IN ('6:00 AM - 8:00 AM', '10:00 AM - 12:00 AM', '5:00 PM - 7:00 PM', '7:00 PM - 9:00 PM')")
    List<Schedule> findSchedulesByDaysAndTimeSlots();

    
    @Query("SELECT s FROM Schedule s WHERE s.day = :day AND s.timeSlot = :timeSlot")
    List<Schedule> findSchedulesByDayAndTimeSlot(@Param("day") String day, @Param("timeSlot") String timeSlot);
	

}
