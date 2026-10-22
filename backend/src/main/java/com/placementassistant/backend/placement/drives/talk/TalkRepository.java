package com.placementassistant.backend.placement.drives.talk;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalkRepository extends JpaRepository<PlacementTalk, Long> {

        Optional<PlacementTalk> findByIdAndDeletedFalse(Long id);

        Page<PlacementTalk> findByTalkAtGreaterThanEqualAndDeletedFalseOrderByTalkAtAsc(
                        LocalDateTime from, Pageable pageable);
}
