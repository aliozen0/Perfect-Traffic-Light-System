# 🚀 Quick Start Guide - Traffic Light Management System

Bu rehber, projeyi hızlıca çalıştırmanız için adım adım talimatlar içerir.

## ⚡ Hızlı Başlangıç (Docker ile)

En kolay yöntem Docker kullanmaktır:

```bash
# 1. Repoyu klonla (veya indir)
cd traffic-light-system

# 2. Docker servislerini başlat
docker-compose up -d

# 3. Logları izle (opsiyonel)
docker-compose logs -f app

# 4. Tarayıcıda aç
# Swagger UI: http://localhost:8080/swagger-ui.html
# PgAdmin: http://localhost:5050
```

✅ **Tamamdır!** Uygulama çalışıyor ve test datası otomatik yüklendi.

---

## 📋 Detaylı Kurulum (Yerel Geliştirme)

### 1. Gereksinimleri Kontrol Et

```bash
# Java 17 kurulu mu?
java -version
# Output: openjdk 17.x.x

# Maven kurulu mu?
mvn -version
# Output: Apache Maven 3.9.x

# PostgreSQL kurulu mu?
psql --version
# Output: psql (PostgreSQL) 15.x
```

### 2. Database Oluştur

```bash
# PostgreSQL'e bağlan
psql -U postgres

# Database oluştur
CREATE DATABASE trafficlight_db;

# Çıkış
\q
```

### 3. Konfigürasyonu Ayarla

```bash
# application.properties'i düzenle
nano src/main/resources/application.properties

# Veya environment variables kullan:
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=trafficlight_db
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
```

### 4. Projeyi Derle ve Çalıştır

```bash
# Bağımlılıkları indir ve derle
mvn clean install

# Testleri çalıştır
mvn test

# Uygulamayı başlat
mvn spring-boot:run
```

### 5. Test Data Yükle

Data seeding otomatik çalışır (`dev` profile ile). Manuel yükleme için:

```bash
# dev profile ile çalıştır
mvn spring-boot:run -Dspring.profiles.active=dev
```

---

## 🔍 İlk Testler

### Health Check
```bash
curl http://localhost:8080/api/health
```

**Beklenen Response:**
```json
{
  "status": "success",
  "data": {
    "status": "UP",
    "service": "Traffic Light Management System",
    "timestamp": "2025-12-20T10:30:00"
  }
}
```

### Tüm Kesişimleri Listele
```bash
curl http://localhost:8080/api/intersections?page=0&limit=10
```

### Şehre Göre Filtrele
```bash
curl http://localhost:8080/api/intersections?city=Istanbul
```

### Yakındaki Kesişimleri Bul
```bash
curl "http://localhost:8080/api/intersections/nearby?lat=41.0369&lng=28.9857&radius=5"
```

### Dashboard Özeti
```bash
curl http://localhost:8080/api/dashboard/summary
```

---

## 🌐 Web Arayüzleri

### Swagger UI (API Dokümantasyonu)
**URL:** http://localhost:8080/swagger-ui.html

Tüm API endpoint'lerini test edebilirsiniz:
- CRUD operations
- Filtering & pagination
- Metrics & analytics
- Dashboard & map APIs

### PgAdmin (Database Yönetimi)
**URL:** http://localhost:5050

**Login:**
- Email: `admin@trafficlight.com`
- Password: `admin`

**Database Bağlantısı:**
- Host: `postgres` (Docker) veya `localhost`
- Port: `5432`
- Database: `trafficlight_db`
- Username: `postgres`
- Password: `postgres`

---

## 📊 Test Data Özeti

Otomatik yüklenen data:
- **55 kesişim noktası:**
  - İstanbul: 30
  - Ankara: 15
  - İzmir: 10
- **Her kesişim için:**
  - 1 konfigürasyon
  - 3-4 faz bilgisi
  - 150 metrik (son 30 gün)

---

## 🧪 Test Senaryoları

### Senaryo 1: Kesişim Oluştur
```bash
curl -X POST http://localhost:8080/api/intersections \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Yeni Kavşak",
    "code": "YK-001",
    "latitude": 41.0500,
    "longitude": 29.0100,
    "city": "Istanbul",
    "intersectionType": "TRAFFIC_LIGHT",
    "status": "ACTIVE"
  }'
```

### Senaryo 2: Metrik Ekle
```bash
curl -X POST http://localhost:8080/api/intersections/1/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "measurementDate": "2025-12-20",
    "measurementHour": 14,
    "totalVehicleCount": 1000,
    "averageWaitTime": 45.5,
    "dataQualityScore": 0.95
  }'
```

### Senaryo 3: Analytics Sorgula
```bash
curl "http://localhost:8080/api/intersections/1/metrics/analytics?startDate=2025-12-01&endDate=2025-12-20"
```

### Senaryo 4: Harita Datası
```bash
# Tüm kesişimler
curl http://localhost:8080/api/map/intersections

# Bounding box içindeki kesişimler
curl "http://localhost:8080/api/map/bounds?minLat=41.0&maxLat=41.1&minLng=28.9&maxLng=29.1"

# Heatmap datası
curl "http://localhost:8080/api/map/heatmap?city=Istanbul&days=7"

# GeoJSON format
curl http://localhost:8080/api/map/geojson?city=Istanbul
```

---

## 🛠️ Yaygın Sorunlar ve Çözümler

### Problem: Port zaten kullanımda
```bash
# 8080 portu başka bir uygulama tarafından kullanılıyor

# Çözüm 1: Farklı port kullan
SERVER_PORT=8081 mvn spring-boot:run

# Çözüm 2: Kullanılan portu kapat (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Problem: Database bağlantı hatası
```bash
# Hata: Connection refused

# Çözüm 1: PostgreSQL'in çalıştığından emin ol
sudo service postgresql status
sudo service postgresql start

# Çözüm 2: Bağlantı bilgilerini kontrol et
psql -U postgres -h localhost -p 5432 -d trafficlight_db
```

### Problem: Maven build hatası
```bash
# Çözüm 1: Clean install
mvn clean install -U

# Çözüm 2: .m2 cache'i temizle
rm -rf ~/.m2/repository
mvn clean install
```

### Problem: Docker container başlamıyor
```bash
# Logları kontrol et
docker-compose logs app

# Container'ı yeniden başlat
docker-compose restart app

# Tüm servisleri yeniden başlat
docker-compose down
docker-compose up -d
```

---

## 📱 Monitoring & Maintenance

### Health Check
```bash
# Basic health
curl http://localhost:8080/api/health

# Database health
curl http://localhost:8080/api/health/database

# Detailed metrics
curl http://localhost:8080/api/health/detailed
```

### Actuator Endpoints
```bash
# Health
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Database Backup
```bash
# Manuel backup
./backup.sh

# Cron job ile otomatik backup
# Her gün 02:00'da
crontab -e
# Ekle: 0 2 * * * /path/to/backup.sh
```

---

## 🎯 Sonraki Adımlar

1. **API'ları Keşfet:**
   - Swagger UI'da tüm endpoint'leri dene
   - Dashboard API'larını test et
   - Map API'larını incele

2. **Frontend Geliştir:**
   - Dashboard API'larını kullan
   - Map API'ları ile harita oluştur
   - Real-time updates ekle

3. **Advanced Features:**
   - Authentication ekle
   - Real-time WebSocket
   - ML-based predictions

---

## 📚 Ek Kaynaklar

- **Detaylı Dokümantasyon:** `WEEK_SUMMARY.md`
- **API Referansı:** http://localhost:8080/swagger-ui.html
- **Database Schema:** `src/main/resources/db/migration/V1__Create_Intersection_Schema.sql`

---

## ✅ Checklist

- [ ] Java 17 kuruldu
- [ ] Maven kuruldu
- [ ] PostgreSQL kuruldu
- [ ] Database oluşturuldu
- [ ] Proje derlendi
- [ ] Testler geçti
- [ ] Uygulama başlatıldı
- [ ] Swagger UI açıldı
- [ ] Health check çalışıyor
- [ ] Test data yüklendi

---

**Hazırsınız!** 🎉

API'nızı kullanmaya başlayabilirsiniz. Sorunla karşılaşırsanız `WEEK_SUMMARY.md` dosyasına bakın veya issue açın.

**İyi geliştirmeler!** 🚀
