#!/bin/bash

# HAFTA 5 - Database Backup Script
# Automated backup script for PostgreSQL database

# Configuration
DB_NAME="trafficlight_db"
DB_USER="postgres"
DB_HOST="localhost"
DB_PORT="5432"
BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/backup_${DB_NAME}_${TIMESTAMP}.sql"
RETENTION_DAYS=30

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Traffic Light DB Backup Script${NC}"
echo -e "${GREEN}======================================${NC}"

# Create backup directory if it doesn't exist
if [ ! -d "$BACKUP_DIR" ]; then
    echo -e "${YELLOW}Creating backup directory...${NC}"
    mkdir -p "$BACKUP_DIR"
fi

# Perform backup
echo -e "${YELLOW}Starting backup at $(date)${NC}"
echo -e "Database: ${DB_NAME}"
echo -e "Backup file: ${BACKUP_FILE}"

# Full backup
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -F p -b -v -f "$BACKUP_FILE" $DB_NAME

# Check if backup was successful
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Backup completed successfully!${NC}"
    
    # Compress the backup
    echo -e "${YELLOW}Compressing backup...${NC}"
    gzip "$BACKUP_FILE"
    
    COMPRESSED_FILE="${BACKUP_FILE}.gz"
    BACKUP_SIZE=$(du -h "$COMPRESSED_FILE" | cut -f1)
    
    echo -e "${GREEN}Compressed backup size: ${BACKUP_SIZE}${NC}"
    echo -e "${GREEN}Backup location: ${COMPRESSED_FILE}${NC}"
    
    # Clean up old backups (older than RETENTION_DAYS)
    echo -e "${YELLOW}Cleaning up old backups (older than ${RETENTION_DAYS} days)...${NC}"
    find "$BACKUP_DIR" -name "backup_*.sql.gz" -type f -mtime +$RETENTION_DAYS -delete
    
    OLD_BACKUPS_COUNT=$(find "$BACKUP_DIR" -name "backup_*.sql.gz" -type f | wc -l)
    echo -e "${GREEN}Total backups retained: ${OLD_BACKUPS_COUNT}${NC}"
    
    # Log backup info
    echo "$(date): Backup completed - ${COMPRESSED_FILE} (${BACKUP_SIZE})" >> "${BACKUP_DIR}/backup.log"
    
else
    echo -e "${RED}Backup failed!${NC}"
    echo "$(date): Backup failed" >> "${BACKUP_DIR}/backup.log"
    exit 1
fi

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Backup process completed at $(date)${NC}"
echo -e "${GREEN}======================================${NC}"
