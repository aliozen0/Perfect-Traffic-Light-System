# 🔗 Frontend + Backend Entegrasyon Talimatları

## ✅ Yapılan İşlemler

### Backend (Spring Boot)
- ✅ **CORS konfigürasyonu eklendi** - Frontend ile iletişim için
- ✅ **Docker ile çalışıyor** - http://localhost:8080
- ✅ **API endpoint'leri hazır** - Swagger: http://localhost:8080/swagger-ui.html
- ✅ **Test datası yüklendi** - 55 kesişim, metrikler, vb.

### Frontend (React)
- ✅ **GitHub'dan indirildi** - Perfect-Traffic-Light-System
- ✅ **API service oluşturuldu** - `src/services/api.js`
- ✅ **Örnek component eklendi** - `src/components/IntersectionList.js`
- ✅ **Dokümantasyon hazırlandı** - `BACKEND_INTEGRATION.md`

---

## 🚀 BAŞLATMA TALİMATLARI

### 📍 Backend Zaten Çalışıyor!

```powershell
# Durum kontrolü:
docker-compose ps

# Eğer durmadıysa, çalışıyor demektir ✅
```

**Test et:**
- Health: http://localhost:8080/api/health
- Swagger: http://localhost:8080/swagger-ui.html

---

### 📍 Frontend'i Başlatma

#### 1. Node.js Kur (Eğer Yoksa)

**İndir:** https://nodejs.org/

- **LTS versiyonu** indirin (örn: v20.x.x)
- Kurulumu tamamlayın
- Bilgisayarı yeniden başlatın

#### 2. Terminal'de Kontrol

```powershell
node --version
npm --version
```

Versiyon numaraları görmelisiniz.

#### 3. Frontend Projesini Başlat

**YENİ BİR TERMINAL AÇIN** (Ctrl + Shift + ` VSCode'da):

```powershell
# Frontend klasörüne git
cd C:\Users\muham\Desktop\dersler\securecoding\Perfect-Traffic-Light-System

# Bağımlılıkları yükle (ilk kez, 2-3 dakika sürer)
npm install

# React uygulamasını başlat
npm start
```

**Otomatik olarak açılır:** http://localhost:3000

---

## 🔌 İki Projeyi Birlikte Çalıştırma

### Terminal Yapısı:

```
Terminal 1 (Backend):
C:\...\securecoding\proje> docker-compose up -d
✅ Backend: http://localhost:8080

Terminal 2 (Frontend):
C:\...\securecoding\Perfect-Traffic-Light-System> npm start
✅ Frontend: http://localhost:3000
```

### Nasıl Çalışır?

```
React (Port 3000)  →  API İsteği  →  Spring Boot (Port 8080)
                   ←  JSON Yanıt  ←
```

---

## 🧪 Test Adımları

### 1. Backend Çalışıyor mu?

```powershell
curl http://localhost:8080/api/health
```

**Beklenen:**
```json
{
  "success": true,
  "message": "System is healthy",
  "data": { "status": "UP" }
}
```

### 2. Frontend'den Backend'e İstek

Tarayıcıda **Developer Console** açın (F12):

```javascript
fetch('http://localhost:8080/api/intersections')
  .then(r => r.json())
  .then(data => console.log(data))
```

**CORS hatası almazsanız:** ✅ Entegrasyon başarılı!

### 3. Örnek Component'i Kullan

`src/App.js` dosyasını düzenleyin:

```javascript
import IntersectionList from './components/IntersectionList';

function App() {
  return (
    <div className="App">
      <IntersectionList />
    </div>
  );
}

export default App;
```

Tarayıcıda **kesişim listesini** göreceksiniz!

---

## 📁 Proje Yapısı

```
Desktop/dersler/securecoding/
├── proje/                              # BACKEND (Spring Boot)
│   ├── src/
│   ├── pom.xml
│   ├── docker-compose.yml
│   └── ...
│
└── Perfect-Traffic-Light-System/      # FRONTEND (React)
    ├── src/
    │   ├── services/
    │   │   └── api.js                 # ✅ Backend API client
    │   └── components/
    │       └── IntersectionList.js   # ✅ Örnek component
    ├── package.json
    ├── BACKEND_INTEGRATION.md         # ✅ Entegrasyon dokümantasyonu
    └── ...
```

---

## 🎯 API Kullanım Örnekleri

### Tüm Kesişimleri Getir

```javascript
import { getIntersections } from './services/api';

const response = await getIntersections();
const intersections = response.data;
```

### Şehre Göre Filtrele

```javascript
import { getIntersectionsByCity } from './services/api';

const response = await getIntersectionsByCity('Istanbul');
const istanbulIntersections = response.data;
```

### Yeni Kesişim Ekle

```javascript
import { createIntersection } from './services/api';

const newIntersection = {
  name: "Yeni Kavşak",
  code: "YK-001",
  latitude: 41.0082,
  longitude: 28.9784,
  city: "Istanbul",
  intersectionType: "TRAFFIC_LIGHT",
  status: "ACTIVE",
  lanesCount: 4
};

const response = await createIntersection(newIntersection);
if (response.success) {
  console.log('Kesişim oluşturuldu!', response.data);
}
```

---

## 🎨 Mevcut Dosyalar

### ✅ Oluşturulan Dosyalar:

1. **Backend:**
   - `src/main/java/com/trafficlight/config/CorsConfig.java`

2. **Frontend:**
   - `src/services/api.js` - Tüm API çağrıları
   - `src/components/IntersectionList.js` - Örnek component
   - `BACKEND_INTEGRATION.md` - Detaylı dokümantasyon

---

## 🔄 Günlük Kullanım

### Sabah (Başlatma):

**Terminal 1:**
```powershell
cd C:\Users\muham\Desktop\dersler\securecoding\proje
docker-compose up -d
```

**Terminal 2:**
```powershell
cd C:\Users\muham\Desktop\dersler\securecoding\Perfect-Traffic-Light-System
npm start
```

### Akşam (Kapatma):

**Terminal 1:**
```powershell
docker-compose down
```

**Terminal 2:**
```
Ctrl + C
```

---

## 📚 Daha Fazla Bilgi

- **Backend Swagger:** http://localhost:8080/swagger-ui.html
- **Backend Dokümantasyon:** `proje/QUICKSTART.md`
- **Frontend Entegrasyon:** `Perfect-Traffic-Light-System/BACKEND_INTEGRATION.md`
- **API Örnekleri:** `proje/API_EXAMPLES.md`

---

## ⚡ HIZLI TEST

### Şu Anda Yapabilecekleriniz:

1. **Backend Çalışıyor mu kontrol:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

2. **Frontend için Node.js kur:**
   ```
   https://nodejs.org/
   ```

3. **Frontend başlat:**
   ```powershell
   cd C:\Users\muham\Desktop\dersler\securecoding\Perfect-Traffic-Light-System
   npm install
   npm start
   ```

---

## 🎉 SONUÇ

✅ **Backend:** ÇALIŞIYOR (Port 8080)  
✅ **CORS:** YAPILANDI  
✅ **Frontend:** İNDİRİLDİ  
✅ **API Service:** OLUŞTURULDU  
✅ **Örnek Component:** HAZIR  
⏳ **Node.js:** KURULMALI (kullanıcı tarafından)  

**Sonraki adım:** Node.js'i kurun ve `npm start` ile frontend'i başlatın!


