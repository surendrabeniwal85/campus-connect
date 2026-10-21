package com.campusconnect.backend.institute.classroom.crt;

import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; public interface CrtSessionRepository extends JpaRepository<CrtSession,Long>{List<CrtSession> findAllByDeletedFalse();}