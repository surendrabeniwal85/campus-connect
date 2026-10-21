package com.placementassistant.backend.institute.classroom.timetable;


import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot,Long>{
 List<TimetableSlot> findAllByDeletedFalse();
 boolean existsByTeacherProfileIdAndDayOfWeekAndPeriodNumberAndDeletedFalse(Long teacher,int day,int period);
 boolean existsByRoomIdAndDayOfWeekAndPeriodNumberAndDeletedFalse(Long room,int day,int period);
 boolean existsBySectionAndDayOfWeekAndPeriodNumberAndDeletedFalse(String section,int day,int period);
}
