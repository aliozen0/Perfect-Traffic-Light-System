# 🔗 Backend Integration Guide

Bu dosya, React frontend'in Spring Boot backend ile nasıl entegre edildiğini açıklar.

## 📋 Genel Bakış

- **Backend:** Spring Boot (Port 8080)
- **Frontend:** React (Port 3000)
- **Database:** PostgreSQL (Port 5432)
- **API Format:** REST JSON

## 🚀 Çalıştırma

### 1. Backend'i Başlat

```bash
cd ../proje
docker-compose up -d

# Veya Docker olmadan:
mvn spring-boot:run
```

**Backend hazır:** http://localhost:8080

### 2. Frontend'i Başlat

```bash
# İlk kez çalıştırıyorsanız:
npm install

# Başlat:
npm start
```

**Frontend hazır:** http://localhost:3000

## 🔌 API Kullanımı

### API Service Kullanımı

```javascript
import { getIntersections, getIntersectionById } from './services/api';

// Tüm kesişimleri getir
const response = await getIntersections();
const intersections = response.data;

// Tek bir kesişim getir
const response = await getIntersectionById(1);
const intersection = response.data;

// Şehre göre filtrele
const response = await getIntersectionsByCity('Istanbul');
const intersections = response.data;
```

### Response Formatı

Backend her zaman şu formatta cevap döner:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-12-21T...",
  "statusCode": 200
}
```

**Kullanım:**
```javascript
const response = await api.getIntersections();
if (response.success) {
  const data = response.data;
  // data kullan...
}
```

## 📡 Mevcut API Endpoint'leri

### Health & Status
- `GET /api/health` - Sistem durumu

### Intersections
- `GET /api/intersections` - Tüm kesişimler (pagination: page, limit)
- `GET /api/intersections/{id}` - Tek kesişim
- `GET /api/intersections?city={city}` - Şehre göre
- `GET /api/intersections/nearby?lat={lat}&lng={lng}&radius={km}` - Yakındakiler
- `POST /api/intersections` - Yeni kesişim
- `PUT /api/intersections/{id}` - Güncelle
- `DELETE /api/intersections/{id}` - Sil

### Dashboard
- `GET /api/dashboard/summary` - Genel özet
- `GET /api/dashboard/city-statistics` - Şehir istatistikleri
- `GET /api/dashboard/status-distribution` - Durum dağılımı

### Map
- `GET /api/map/intersections` - Harita için tüm kesişimler
- `GET /api/map/bounds?minLat=...&maxLat=...&minLng=...&maxLng=...` - Sınırlar içinde
- `GET /api/map/heatmap?city={city}&days={days}` - Heatmap datası
- `GET /api/map/geojson?city={city}` - GeoJSON format

### Metrics
- `GET /api/intersections/{id}/metrics` - Metrikler
- `POST /api/intersections/{id}/metrics` - Yeni metrik
- `GET /api/intersections/{id}/metrics/analytics?startDate=...&endDate=...` - Analitik

## 🛠️ Component Örneği

`src/components/IntersectionList.js` dosyasına bakın. Tam çalışan bir örnek.

### App.js'e Entegre Etme

```javascript
import IntersectionList from './components/IntersectionList';

function App() {
  return (
    <div className="App">
      <IntersectionList />
    </div>
  );
}
```

## 🔧 Konfigürasyon

### Environment Variables

`.env` dosyası oluşturun:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

### CORS

Backend'de CORS zaten yapılandırılmış:
- `CorsConfig.java` dosyası otomatik olarak React'tan gelen isteklere izin veriyor
- Allowed origins: localhost:3000, localhost:3001

## 🧪 Test

### 1. Backend Çalışıyor mu?

Tarayıcıda açın:
```
http://localhost:8080/api/health
```

Yanıt:
```json
{
  "success": true,
  "message": "System is healthy",
  "data": { "status": "UP", ... }
}
```

### 2. Frontend Backend'e Bağlanabiliyor mu?

Browser Console'da:
```javascript
fetch('http://localhost:8080/api/health')
  .then(r => r.json())
  .then(console.log)
```

### 3. Swagger UI

Backend API dokümantasyonu:
```
http://localhost:8080/swagger-ui.html
```

## 🐛 Sorun Giderme

### CORS Hatası

```
Access to fetch at 'http://localhost:8080/api/...' from origin 'http://localhost:3000' 
has been blocked by CORS policy
```

**Çözüm:** Backend'in CorsConfig.java dosyasını kontrol edin ve Docker'ı yeniden başlatın:
```bash
cd ../proje
docker-compose down
docker-compose up -d --build
```

### Connection Refused

```
Failed to fetch
```

**Çözüm:** Backend çalışmıyor olabilir:
```bash
# Backend durumunu kontrol et
cd ../proje
docker-compose ps

# Veya
curl http://localhost:8080/api/health
```

### Empty Data

Veri gelmiyor, boş liste dönüyor.

**Çözüm:** 
1. Backend'de DataSeeder çalışmış mı kontrol edin
2. Database'de veri var mı kontrol edin
3. Swagger UI'dan manuel test yapın

## 📦 Proje Yapısı

```
Perfect-Traffic-Light-System/
├── src/
│   ├── services/
│   │   └── api.js           # Backend API client
│   ├── components/
│   │   └── IntersectionList.js  # Örnek component
│   └── App.js
├── .env                      # Environment variables
└── BACKEND_INTEGRATION.md    # Bu dosya
```

## 🎯 Sonraki Adımlar

1. ✅ IntersectionList component'ini test edin
2. 📝 Kendi component'lerinizi oluşturun
3. 🗺️ Harita entegrasyonu ekleyin
4. 📊 Dashboard sayfası oluşturun
5. 🔐 Authentication ekleyin (opsiyonel)

## 📚 Kaynaklar

- **Backend Swagger:** http://localhost:8080/swagger-ui.html
- **Backend Health:** http://localhost:8080/api/health
- **Frontend:** http://localhost:3000
- **PgAdmin:** http://localhost:5050

---

**🎉 Başarıyla entegre edildi! İyi kodlamalar!**


