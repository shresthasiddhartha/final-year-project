package Zest.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Zest.gym.model.AttendanceSheet;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


public interface AttendanceRepository extends JpaRepository<AttendanceSheet, Integer> {

	  Optional<AttendanceSheet> findByDateAndEmail(LocalDate date, String email);

	  List<AttendanceSheet> findByEmail(String email); // To fetch attendance by email
	
	  @Query("SELECT COUNT(a) FROM AttendanceSheet a WHERE a.email = :email")
	   long countByEmail(@Param("email") String email);
	  
	  @Query("SELECT COUNT(a) FROM AttendanceSheet a WHERE a.email = :email AND MONTH(a.date) = MONTH(CURRENT_DATE) AND YEAR(a.date) = YEAR(CURRENT_DATE)")
	    long countCurrentMonthByEmail(@Param("email") String email);
	  
	  @Query("SELECT DISTINCT DAY(a.date) FROM AttendanceSheet a WHERE a.email = :email")
	  List<Integer> findDistinctDaysByEmail(@Param("email") String email);


	  @Query("SELECT a.email, COUNT(a) FROM AttendanceSheet a GROUP BY a.email")
	  List<Object[]> findAttendanceByDistinctEmail();

}
