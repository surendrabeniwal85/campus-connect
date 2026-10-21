package com.placementassistant.backend.institute.classroom.timetable;
import com.placementassistant.backend.institute.classroom.teacher.TeacherProfileRepository;


import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class TimetableService {
 private final TimetableSlotRepository slots; private final TeacherProfileRepository teachers; private final RoomRepository rooms;
 public TimetableService(TimetableSlotRepository slots,TeacherProfileRepository teachers,RoomRepository rooms){this.slots=slots;this.teachers=teachers;this.rooms=rooms;}
 public List<TimetableSlot> list(){return slots.findAllByDeletedFalse();}
 @Transactional public TimetableSlot create(Long teacher,Long room,String section,String subject,int day,int period){
  if(day<1||day>7||period<1||period>20||section==null||section.isBlank()||subject==null||subject.isBlank())throw new IllegalArgumentException("Invalid day, period, section or subject");
  teachers.findById(teacher).filter(x->!x.isDeleted()).orElseThrow(()->new NoSuchElementException("Teacher profile not found"));
  rooms.findById(room).filter(x->!x.isDeleted()).orElseThrow(()->new NoSuchElementException("Room not found"));
  if(slots.existsByTeacherProfileIdAndDayOfWeekAndPeriodNumberAndDeletedFalse(teacher,day,period))throw new IllegalStateException("Teacher already has a class in this period");
  if(slots.existsByRoomIdAndDayOfWeekAndPeriodNumberAndDeletedFalse(room,day,period))throw new IllegalStateException("Room already has a class in this period");
  if(slots.existsBySectionAndDayOfWeekAndPeriodNumberAndDeletedFalse(section,day,period))throw new IllegalStateException("Section already has a class in this period");
  return slots.save(new TimetableSlot(teacher,room,section.trim(),subject.trim(),day,period));
 }
}
