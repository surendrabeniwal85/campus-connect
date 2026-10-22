# Database rules (owner: Aliza)

- MySQL 8, utf8mb4, names in snake_case.
- Every table has: id BIGINT AUTO_INCREMENT, created_at, updated_at, created_by, deleted.
- Marks, percentages and CGPA use DECIMAL, never float.
- Keys, unique rules and indexes are written in the migration file.
- Tables change only through Flyway files named V{number}__{group}_{what}.sql.
- Number ranges: V1-V9 base (Aliza), V10-V29 accounts and students (Surendra),
  V30-V39 classroom (Naman), V40-V49 drives (Aliza), V50-V59 selection (Ashish),
  V60-V69 news and resume (Pallavi), V90+ views (Aliza).
- Never edit a merged migration. Add a new one.
- Each owner sends their migration in a Pull Request. Aliza checks it before it merges.
