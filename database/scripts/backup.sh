#!/bin/bash
# Saves a copy of the database. Usage: ./database/scripts/backup.sh aliza
USER_NAME="${1:?Give your MySQL user name, for example: ./database/scripts/backup.sh aliza}"
FILE="database/backups/placement_assistant_$(date +%Y%m%d_%H%M%S).sql"
/usr/bin/mysqldump -u "$USER_NAME" -p --single-transaction --no-tablespaces placement_assistant > "$FILE" && echo "Saved: $FILE"
