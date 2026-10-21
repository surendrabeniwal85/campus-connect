package com.placementassistant.backend.institute.classroom.timetable;


import com.placementassistant.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*;
@Entity @Table(name="room") public class Room extends AuditableEntity {
 @Column(nullable=false,unique=true,length=40) private String code; @Column(name="capacity") private Integer capacity;
 protected Room(){} public Room(String code,Integer capacity){this.code=code;this.capacity=capacity;} public String getCode(){return code;} public Integer getCapacity(){return capacity;}
}
