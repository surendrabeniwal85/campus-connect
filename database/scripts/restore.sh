#!/bin/bash
# Puts a saved copy back. Usage: ./database/scripts/restore.sh aliza database/backups/FILE.sql
USER_NAME="${1:?Give your MySQL user name}"
FILE="${2:?Give the backup file to restore}"
/usr/bin/mysql -u "$USER_NAME" -p placement_assistant < "$FILE" && echo "Restored from: $FILE"
