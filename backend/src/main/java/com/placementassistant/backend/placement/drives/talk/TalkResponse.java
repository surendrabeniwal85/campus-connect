package com.placementassistant.backend.placement.drives.talk;

import java.time.LocalDateTime;

public record TalkResponse(Long id, String title, String speakerName, Long companyId,
                LocalDateTime talkAt, String venueOrLink, String description, String audience) {

        static TalkResponse from(PlacementTalk t) {
                return new TalkResponse(t.getId(), t.getTitle(), t.getSpeakerName(), t.getCompanyId(),
                                t.getTalkAt(), t.getVenueOrLink(), t.getDescription(), t.getAudience());
        }
}
