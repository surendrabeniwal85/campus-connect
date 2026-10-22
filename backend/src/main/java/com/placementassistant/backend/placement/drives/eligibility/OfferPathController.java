package com.placementassistant.backend.placement.drives.eligibility;

import com.placementassistant.backend.placement.drives.eligibility.OfferPathDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drives")
public class OfferPathController {

    private final OfferPathService offerPathService;

    public OfferPathController(OfferPathService offerPathService) {
        this.offerPathService = offerPathService;
    }

    @GetMapping("/{driveId}/eligibility")
    public ResponseEntity<EligibilityResult> getEligibility(
            @PathVariable Long driveId,
            @RequestParam(required = false) String studentEmail,
            Authentication authentication) {
        String email = (studentEmail != null && !studentEmail.isBlank())
                ? studentEmail
                : (authentication != null ? authentication.getName() : null);

        return ResponseEntity.ok(offerPathService.explainDriveEligibility(driveId, email));
    }

    @PostMapping("/simulate")
    public ResponseEntity<SimulationResponse> simulateOfferPath(
            @RequestBody SimulationRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(offerPathService.simulate(request, email));
    }

    @GetMapping("/{driveId}/one-rule-away")
    @PreAuthorize("hasAnyRole('TPO', 'D_TPO', 'DIRECTOR_HR', 'PLACEMENT_COORDINATOR', 'HOD', 'TEACHER')")
    public ResponseEntity<OneRuleAwayResponse> getOneRuleAwaySummary(@PathVariable Long driveId) {
        return ResponseEntity.ok(offerPathService.getOneRuleAway(driveId));
    }

    @PostMapping("/{driveId}/nudge")
    @PreAuthorize("hasAnyRole('TPO', 'D_TPO', 'DIRECTOR_HR', 'PLACEMENT_COORDINATOR')")
    public ResponseEntity<NudgeResponse> sendNudge(
            @PathVariable Long driveId,
            @RequestBody(required = false) NudgeRequest request,
            Authentication authentication) {
        String sender = authentication != null ? authentication.getName() : "TPO Desk";
        return ResponseEntity.ok(offerPathService.sendTargetedNudge(driveId, request, sender));
    }
}
