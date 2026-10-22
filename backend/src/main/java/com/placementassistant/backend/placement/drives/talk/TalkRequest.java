package com.placementassistant.backend.placement.drives.talk;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TalkRequest(
                @NotBlank @Size(max = 200) String title,
                @Size(max = 150) String speakerName,
                Long companyId,
                @NotNull LocalDateTime talkAt,
                @Size(max = 300) String venueOrLink,
                String description,
                @Size(max = 150) String audience) {
}
