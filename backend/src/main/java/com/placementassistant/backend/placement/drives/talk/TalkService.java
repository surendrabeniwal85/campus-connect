package com.placementassistant.backend.placement.drives.talk;

import com.placementassistant.backend.placement.drives.PlacementNotFoundException;
import com.placementassistant.backend.placement.drives.company.CompanyRepository;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TalkService {

        private final TalkRepository talks;
        private final CompanyRepository companies;

        public TalkService(TalkRepository talks, CompanyRepository companies) {
                this.talks = talks;
                this.companies = companies;
        }

        @Transactional(readOnly = true)
        public Page<TalkResponse> upcoming(Pageable pageable) {
                return talks.findByTalkAtGreaterThanEqualAndDeletedFalseOrderByTalkAtAsc(
                                LocalDateTime.now(), pageable).map(TalkResponse::from);
        }

        @Transactional(readOnly = true)
        public TalkResponse get(Long id) {
                return TalkResponse.from(find(id));
        }

        @Transactional
        public TalkResponse create(TalkRequest request) {
                checkCompany(request.companyId());
                return TalkResponse.from(talks.save(PlacementTalk.create(request)));
        }

        @Transactional
        public TalkResponse update(Long id, TalkRequest request) {
                checkCompany(request.companyId());
                PlacementTalk talk = find(id);
                talk.apply(request);
                return TalkResponse.from(talk);
        }

        private void checkCompany(Long companyId) {
                if (companyId != null && companies.findByIdAndDeletedFalse(companyId).isEmpty()) {
                        throw new PlacementNotFoundException("Company not found.");
                }
        }

        private PlacementTalk find(Long id) {
                return talks.findByIdAndDeletedFalse(id)
                                .orElseThrow(() -> new PlacementNotFoundException("Talk not found."));
        }
}
