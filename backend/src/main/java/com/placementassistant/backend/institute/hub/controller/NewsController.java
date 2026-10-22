package com.placementassistant.backend.institute.hub.controller;

import com.placementassistant.backend.institute.hub.dto.NewsCardResponse;
import com.placementassistant.backend.institute.records.service.StudentQueryService;
import com.placementassistant.backend.placement.drives.drive.Drive;
import com.placementassistant.backend.placement.drives.drive.DriveRepository;
import com.placementassistant.backend.placement.drives.opportunity.Opportunity;
import com.placementassistant.backend.placement.drives.opportunity.OpportunityRepository;
import com.placementassistant.backend.placement.drives.opportunity.OpportunityType;
import com.placementassistant.backend.placement.drives.talk.PlacementTalk;
import com.placementassistant.backend.placement.drives.talk.TalkRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NewsController {

    private final DriveRepository driveRepository;
    private final TalkRepository talkRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentQueryService studentQueryService;

    public NewsController(
            DriveRepository driveRepository,
            TalkRepository talkRepository,
            OpportunityRepository opportunityRepository,
            StudentQueryService studentQueryService) {
        this.driveRepository = driveRepository;
        this.talkRepository = talkRepository;
        this.opportunityRepository = opportunityRepository;
        this.studentQueryService = studentQueryService;
    }

    @GetMapping({"/news", "/public/news"})
    public ResponseEntity<List<NewsCardResponse>> getNewsFeed(
            @RequestParam(required = false) String type,
            Authentication authentication) {
        boolean isFinalYear = true;
        if (authentication != null && authentication.isAuthenticated()) {
            isFinalYear = studentQueryService.isFinalYear(authentication.getName());
        }

        List<NewsCardResponse> cards = new ArrayList<>();

        // 1. Placement Drives
        if (type == null || type.isBlank() || "DRIVE".equalsIgnoreCase(type)) {
            List<Drive> drives = driveRepository.findAll();
            for (Drive d : drives) {
                if (d.isDeleted()) continue;
                String role = d.getRoleTitle();
                String pkg = d.getPackageText() != null ? d.getPackageText() : "Package disclosed in drive";
                String venue = d.getVenue() != null ? d.getVenue() : "Campus";
                String headline = role + " Drive Announced";
                String summary = "Package: " + pkg + " · Location/Venue: " + venue;
                String deadline = d.getDeadline() != null ? d.getDeadline().toString() : "Soon";
                cards.add(new NewsCardResponse(
                        "drive-" + d.getId(),
                        "DRIVE",
                        headline,
                        summary,
                        d.getDriveDate() != null ? d.getDriveDate().toString() : "TBD",
                        deadline,
                        "/placements",
                        "badge-drive",
                        "Check my eligibility",
                        false
                ));
            }
        }

        // 2. Placement Talks
        if (type == null || type.isBlank() || "TALK".equalsIgnoreCase(type)) {
            List<PlacementTalk> talks = talkRepository.findAll();
            for (PlacementTalk t : talks) {
                if (t.isDeleted()) continue;
                String speaker = t.getSpeakerName() != null ? "Speaker: " + t.getSpeakerName() : "";
                String venue = t.getVenueOrLink() != null ? "Venue: " + t.getVenueOrLink() : "";
                cards.add(new NewsCardResponse(
                        "talk-" + t.getId(),
                        "TALK",
                        t.getTitle(),
                        (speaker + " · " + venue).trim(),
                        t.getTalkAt() != null ? t.getTalkAt().toString() : "TBD",
                        "Active",
                        "/placements",
                        "badge-talk",
                        "View talk details",
                        false
                ));
            }
        }

        // 3. Opportunities (Internship, Hackathon, Off-Campus)
        List<Opportunity> opps = opportunityRepository.findAll();
        for (Opportunity o : opps) {
            if (o.isDeleted()) continue;
            // FR-32: Off-campus hiring is shown only to final-year students (semester 7 or 8)
            if (o.isFinalYearOnly() && !isFinalYear) {
                continue;
            }
            if (type != null && !type.isBlank() && !o.getType().name().equalsIgnoreCase(type)) {
                continue;
            }
            String typeName = o.getType().name();
            String badge = typeName.equalsIgnoreCase("HACKATHON") ? "badge-hackathon" :
                    typeName.equalsIgnoreCase("INTERNSHIP") ? "badge-internship" : "badge-offcampus";

            cards.add(new NewsCardResponse(
                    "opp-" + o.getId(),
                    typeName,
                    o.getTitle() + " (" + o.getOrganisation() + ")",
                    o.getDescription() != null ? o.getDescription() : (o.getLocation() != null ? o.getLocation() : "Open to students"),
                    o.getStartDate() != null ? o.getStartDate().toString() : "Now",
                    o.getDeadline() != null ? o.getDeadline().toString() : "Open",
                    o.getLink() != null ? o.getLink() : "#",
                    badge,
                    "Open opportunity link",
                    o.isFinalYearOnly()
            ));
        }

        // Add a sample Live session round update card if no round update is present
        if (type == null || type.isBlank() || "ROUND_UPDATE".equalsIgnoreCase(type)) {
            cards.add(new NewsCardResponse(
                    "round-update-1",
                    "ROUND_UPDATE",
                    "Northstar Systems: Aptitude Round Completed",
                    "Round 1 stats: 50 entered, 32 cleared. Technical round begins at 2:00 PM in Lab 3.",
                    "Today",
                    "Immediate",
                    "/placements",
                    "badge-round",
                    "View round details",
                    false
            ));
        }

        return ResponseEntity.ok(cards);
    }
}
