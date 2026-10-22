# ER diagram: Drives and Opportunities (owner: Aliza)

```mermaid
erDiagram
    company ||--o{ company_contact : has
    company ||--o{ placement_drive : hosts
    company ||--o{ placement_talk : gives
    placement_drive ||--o| eligibility_rule : "has limits"
    placement_drive ||--o{ drive_application : receives

    company {
        bigint id PK
        varchar name UK
        varchar industry
        varchar website
    }
    company_contact {
        bigint id PK
        bigint company_id FK
        varchar name
        varchar email
    }
    placement_drive {
        bigint id PK
        bigint company_id FK
        varchar role_title
        enum employment_type
        decimal ctc_lpa
        boolean has_ppo
        int bond_months
        date drive_date
        datetime deadline
        enum status
    }
    eligibility_rule {
        bigint id PK
        bigint drive_id FK
        decimal min_cgpa
        int max_backlogs
        decimal min_attendance
        varchar allowed_branches
        int passing_year
    }
    drive_application {
        bigint id PK
        bigint drive_id FK
        bigint student_id
        enum status
    }
    placement_talk {
        bigint id PK
        bigint company_id FK
        varchar title
        datetime talk_at
    }
    opportunity {
        bigint id PK
        enum type
        varchar title
        varchar organisation
        datetime deadline
        boolean final_year_only
    }
```

Notes:
- `student_id` in drive_application points to the student table owned by Surendra. It becomes a real link when his tables land.
- Rounds (`drive_round`) and candidate status belong to Ashish and will join this diagram when his tables land.
