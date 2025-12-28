#!/bin/bash

# HAFTA 5 - Database Restore Script
# Restore PostgreSQL database from backup

# Configuration
DB_NAME="trafficlight_db"
DB_USER="postgres"
DB_HOST="localhost"
DB_PORT="5432"
BACKUP_DIR="./backups"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Traffic Light DB Restore Script${NC}"
echo -e "${GREEN}======================================${NC}"

# Check if backup directory exists
if [ ! -d "$BACKUP_DIR" ]; then
    echo -e "${RED}Backup directory not found: ${BACKUP_DIR}${NC}"
    exit 1
fi

# List available backups
echo -e "${YELLOW}Available backups:${NC}"
ls -lh "${BACKUP_DIR}"/backup_*.sql.gz 2>/dev/null | nl

BACKUP_COUNT=$(ls -1 "${BACKUP_DIR}"/backup_*.sql.gz 2>/dev/null | wc -l)

if [ $BACKUP_COUNT -eq 0 ]; then
    echo -e "${RED}No backup files found in ${BACKUP_DIR}${NC}"
    exit 1
fi

# Get backup file from user or use latest
if [ -z "$1" ]; then
    # Use latest backup
    BACKUP_FILE=$(ls -t "${BACKUP_DIR}"/backup_*.sql.gz 2>/dev/null | head -n1)
    echo -e "${YELLOW}No backup file specified. Using latest backup:${NC}"
    echo -e "${GREEN}${BACKUP_FILE}${NC}"
else
    BACKUP_FILE="$1"
    
    if [ ! -f "$BACKUP_FILE" ]; then
        echo -e "${RED}Backup file not found: ${BACKUP_FILE}${NC}"
        exit 1
    fi
fi

# Confirm restore
echo -e "${YELLOW}WARNING: This will restore the database from backup!${NC}"
echo -e "${YELLOW}Database: ${DB_NAME}${NC}"
echo -e "${YELLOW}Backup: ${BACKUP_FILE}${NC}"
echo -e "${RED}All current data will be replaced!${NC}"
read -p "Do you want to continue? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo -e "${YELLOW}Restore cancelled.${NC}"
    exit 0
fi

# Create a backup of current database before restore
SAFETY_BACKUP="${BACKUP_DIR}/pre_restore_backup_$(date +"%Y%m%d_%H%M%S").sql"
echo -e "${YELLOW}Creating safety backup of current database...${NC}"
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -F p -f "$SAFETY_BACKUP" $DB_NAME
gzip "$SAFETY_BACKUP"
echo -e "${GREEN}Safety backup created: ${SAFETY_BACKUP}.gz${NC}"

# Decompress backup if needed
if [[ $BACKUP_FILE == *.gz ]]; then
    echo -e "${YELLOW}Decompressing backup...${NC}"
    gunzip -c "$BACKUP_FILE" > "/tmp/restore_temp.sql"
    RESTORE_FILE="/tmp/restore_temp.sql"
else
    RESTORE_FILE="$BACKUP_FILE"
fi

# Drop and recreate database
echo -e "${YELLOW}Dropping existing database...${NC}"
dropdb -h $DB_HOST -p $DB_PORT -U $DB_USER --if-exists $DB_NAME

echo -e "${YELLOW}Creating new database...${NC}"
createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME

# Restore from backup
echo -e "${YELLOW}Restoring database from backup...${NC}"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$RESTORE_FILE"

# Check if restore was successful
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Database restored successfully!${NC}"
    
    # Clean up temp file
    if [ -f "/tmp/restore_temp.sql" ]; then
        rm "/tmp/restore_temp.sql"
    fi
    
    echo "$(date): Database restored from ${BACKUP_FILE}" >> "${BACKUP_DIR}/restore.log"
    
else
    echo -e "${RED}Restore failed!${NC}"
    echo -e "${YELLOW}Attempting to restore from safety backup...${NC}"
    
    gunzip -c "${SAFETY_BACKUP}.gz" > "/tmp/safety_restore.sql"
    dropdb -h $DB_HOST -p $DB_PORT -U $DB_USER --if-exists $DB_NAME
    createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "/tmp/safety_restore.sql"
    
    rm "/tmp/safety_restore.sql"
    
    echo "$(date): Restore failed, reverted to safety backup" >> "${BACKUP_DIR}/restore.log"
    exit 1
fi

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Restore process completed at $(date)${NC}"
echo -e "${GREEN}======================================${NC}"
