package com.placementassistant.backend.placement.drives.talk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_talk")
public class PlacementTalk {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "title", nullable = false, length = 200)
        private String title;

        @Column(name = "speaker_name", length = 150)
        private String speakerName;

        @Column(name = "company_id")
        private Long companyId;

        @Column(name = "talk_at", nullable = false)
        private LocalDateTime talkAt;

        @Column(name = "venue_or_link", length = 300)
        private String venueOrLink;

        @Column(name = "description", columnDefinition = "TEXT")
        private String description;

        @Column(name = "audience", nullable = false, length = 150)
        private String audience = "ALL";

        @Column(name = "deleted", nullable = false)
        private boolean deleted;

        protected PlacementTalk() {
        }

        public static PlacementTalk create(TalkRequest r) {
                PlacementTalk t = new PlacementTalk();
                t.apply(r);
                return t;
        }

        public void apply(TalkRequest r) {
                this.title = r.title().trim();
                this.speakerName = r.speakerName();
                this.companyId = r.companyId();
                this.talkAt = r.talkAt();
                this.venueOrLink = r.venueOrLink();
                this.description = r.description();
                this.audience = (r.audience() == null || r.audience().isBlank()) ? "ALL" : r.audience().trim();
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getSpeakerName() { return speakerName; }
        public Long getCompanyId() { return companyId; }
        public LocalDateTime getTalkAt() { return talkAt; }
        public String getVenueOrLink() { return venueOrLink; }
        public String getDescription() { return description; }
        public String getAudience() { return audience; }
        public boolean isDeleted() { return deleted; }
}
