# 🚦 Perfect Traffic Light System - Mustafa - Sprint 4 - 5

## 📋 İçindekiler
- [Genel Bakış](#genel-bakış)
- [Yeni Özellikler](#yeni-özellikler)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [API Dokümantasyonu](#api-dokümantasyonu)
- [Frontend Entegrasyon Rehberi](#frontend-entegrasyon-rehberi)
- [Test Senaryoları](#test-senaryoları)
- [Veritabanı Şeması](#veritabanı-şeması)

---

## 🎯 Genel Bakış

Perfect Traffic Light System artık **JWT Authentication**, **Acil Araç Öncelik Sistemi**, **Kural Tabanlı Optimizasyon**, **Sensör Entegrasyonu** ve **İstatistik Raporlama** özellikleriyle donatılmış kapsamlı bir trafik yönetim platformu.

### Teknoloji Stack
- **Backend:** Spring Boot 3.2.0
- **Database:** PostgreSQL 15
- **Authentication:** JWT (JSON Web Token)
- **Migration:** Flyway
- **API Documentation:** Swagger/OpenAPI 3.0
- **Deployment:** Docker + Docker Compose

---

## ✨ Yeni Özellikler

### 1. 🔐 JWT Authentication System
**Endpoint'ler:**
- `POST /api/auth/register` - Kullanıcı kaydı
- `POST /api/auth/login` - Giriş yapma ve token alma
- `GET /api/auth/me` - Mevcut kullanıcı bilgisi
- `GET /api/auth/validate` - Token doğrulama

**Özellikler:**
- BCrypt ile şifrelenmiş parolalar
- Token tabanlı authentication
- Role-based access control (USER, ADMIN)
- 24 saat geçerli JWT token'lar

**Varsayılan Kullanıcılar:**
```
Username: admin    | Password: admin123  | Role: ADMIN
Username: user     | Password: user123   | Role: USER
```

---

### 2. 🚨 Emergency Vehicle Priority System
**Endpoint'ler:**
- `POST /api/emergency/trigger` - Acil araç tespit et
- `POST /api/emergency/clear/{id}` - Acil durumu sonlandır
- `GET /api/emergency/active` - Aktif acil durumlar
- `GET /api/emergency/history/{id}` - Geçmiş kayıtları
- `POST /api/emergency/test/ambulance` - Test: Ambulans
- `POST /api/emergency/test/firetruck` - Test: İtfaiye
- `POST /api/emergency/test/police` - Test: Polis

**Desteklenen Araç Tipleri:**
- 🚑 Ambulans (Priority: 1)
- 🚒 İtfaiye (Priority: 2)
- 🚓 Polis (Priority: 2)

**Nasıl Çalışır:**
1. Acil araç tespit edilir
2. İlgili kavşak anında **YEŞIL** yapılır (60 saniye)
3. Diğer kavşaklar **KIRMIZI** yapılır (güvenlik protokolü)
4. Detaylı event log'u kaydedilir
5. Araç geçtikten sonra normal moda dönülür

**Response Örneği:**
```json
{
  "success": true,
  "message": "🚑 Acil araç tespit edildi ve öncelik verildi",
  "vehicle": {
    "vehicleId": "AMB-001",
    "type": "🚑 Ambulans",
    "status": "Tespit Edildi",
    "location": "Kavşak-1 (Atatürk Bulvarı)",
    "direction": "Kuzey",
    "priority": 1
  },
  "actions": [
    "✅ Kavşak-1: Anında yeşile çevrildi (60 saniye)",
    "🔴 Kavşak-2: Güvenlik için kırmızıya alındı",
    "🔴 Kavşak-3: Güvenlik için kırmızıya alındı"
  ],
  "impact": {
    "affectedIntersections": 3,
    "totalWaitTime": 120,
    "estimatedDelay": "Minimal (10-15 saniye)"
  }
}
```

---

### 3. 🎯 Traffic Optimization System (Rule Engine)
**Endpoint'ler:**
- `POST /api/optimization/apply` - Optimizasyon uygula
- `GET /api/optimization/rules` - Tüm kuralları listele
- `GET /api/optimization/rules/active` - Aktif kurallar
- `POST /api/optimization/rules/create-defaults` - Varsayılan kuralları oluştur
- `POST /api/optimization/test/high-traffic` - Test: Yoğun trafik
- `POST /api/optimization/test/night-mode` - Test: Gece modu

**Varsayılan Kurallar:**
1. **PEAK_HOUR_EXTENSION**
   - Zaman: 07:00-09:00
   - Koşul: 25+ araç
   - Ayarlama: +15 saniye
   - Açıklama: Sabah yoğunluğunda yeşil süreyi artırır

2. **HIGH_DENSITY_BOOST**
   - Koşul: 40+ araç
   - Ayarlama: +20 saniye
   - Açıklama: Yüksek yoğunlukta ekstra süre verir

3. **NIGHT_MODE_QUICK**
   - Zaman: 00:00-06:00
   - Koşul: 15 veya daha az araç
   - Ayarlama: -10 saniye
   - Açıklama: Gece saatlerinde hızlı geçiş

**Nasıl Çalışır:**
1. Kavşakta araç sayısı ve hız ölçülür
2. Sistem uygun kuralı bulur (öncelik sırasına göre)
3. Yeşil ışık süresi dinamik olarak ayarlanır
4. Performans metrikleri hesaplanır
5. Uygulama kaydedilir

**Request Örneği:**
```json
POST /api/optimization/apply
{
  "intersectionId": 1,
  "vehicleCount": 45,
  "averageSpeed": 25.5
}
```

**Response Örneği:**
```json
{
  "success": true,
  "message": "🎯 Trafik kuralı başarıyla uygulandı: PEAK_HOUR_EXTENSION",
  "intersection": {
    "intersectionId": 1,
    "name": "Kavşak-1 (Atatürk Bulvarı)",
    "vehicleCount": 45,
    "densityLevel": "🟠 Yüksek (30-49 araç)"
  },
  "details": {
    "previousGreenDuration": 30,
    "newGreenDuration": 45,
    "adjustment": "+15 saniye",
    "visual": "⏱️ 30s → 45s (+15s)"
  },
  "performance": {
    "waitTimeReduction": "-30%",
    "flowImprovement": "+45%",
    "efficiencyScore": "85/100"
  }
}
```

---

### 4. 📡 Sensor Integration System
**Endpoint'ler:**
- `POST /api/optimization/sensor/data` - Sensör verisi gönder
- `GET /api/optimization/sensor/intersection/{id}` - Kavşak sensör verileri
- `GET /api/optimization/sensor/recent/{id}` - Son 1 saatteki veriler

**Sensör Verisi Gönderme:**
```json
POST /api/optimization/sensor/data
{
  "sensorId": "SENSOR-001",
  "intersectionId": 1,
  "direction": "NORTH",
  "vehicleCount": 45,
  "averageSpeed": 35.5
}
```

**Yoğunluk Seviyeleri:**
- 🟢 **LOW:** 0-9 araç
- 🟡 **MEDIUM:** 10-29 araç
- 🟠 **HIGH:** 30-49 araç
- 🔴 **CRITICAL:** 50+ araç

---

### 5. 📊 Statistics & Reporting System
**Endpoint'ler:**
- `GET /api/statistics/daily-summary` - Günlük özet rapor
- `GET /api/statistics/weekly-performance` - Haftalık performans
- `GET /api/statistics/system-status` - Gerçek zamanlı durum
- `GET /api/statistics/dashboard` - Dashboard (hepsi bir arada)
- `GET /api/statistics/compare-intersections` - Kavşak karşılaştırması
- `GET /api/statistics/chart-data` - Grafik verileri
- `GET /api/statistics/emergency-stats` - Acil durum istatistikleri

**Günlük Özet Response:**
```json
{
  "reportDate": "2025-12-28T01:30:00",
  "reportType": "📊 Günlük Özet Rapor",
  "emergencyVehicles": {
    "total": 8,
    "description": "🚨 Toplam acil araç geçişi",
    "breakdown": {
      "ambulance": 5,
      "fireTruck": 2,
      "police": 1,
      "total": 8
    }
  },
  "ruleApplications": {
    "total": 127,
    "description": "🎯 Toplam kural uygulaması"
  }
}
```

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Docker Desktop
- Git
- Postman veya web browser (Swagger için)

### Adım 1: Projeyi Klonla
```bash
git clone <repo-url>
cd Backend
```

### Adım 2: Docker ile Başlat
```bash
# İlk kurulum (database'i temizle)
docker-compose down -v

# Build ve başlat
docker-compose up --build -d

# Logları izle
docker-compose logs -f app
```

### Adım 3: Migration Kontrolü
Logları kontrol et, şunu göreceksin:
```
Flyway: Migrating schema "public" to version "4 - create emergency system tables"
Flyway: Migrating schema "public" to version "5 - create optimization system tables"
Flyway: Successfully applied 2 migrations
```

### Adım 4: İlk Kurulum
```bash
# Swagger'a git
http://localhost:8080/swagger-ui.html

# Varsayılan kuralları oluştur
POST /api/optimization/rules/create-defaults
```

### Adım 5: Test Et
```bash
# Health check
curl http://localhost:8080/api/health

# Login test
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 📚 API Dokümantasyonu

### Swagger UI
**URL:** http://localhost:8080/swagger-ui.html

### API Kategorileri

#### 1. Authentication (Public)
- ✅ `/api/auth/**` - Authentication gerekmiyor
- ✅ `/api/health/**` - Health check

#### 2. Emergency System (Protected)
- 🔒 `/api/emergency/**` - Token gerekli
- 👑 `/api/emergency/clear/**` - Admin gerekli (opsiyonel)

#### 3. Traffic Optimization (Protected)
- 🔒 `/api/optimization/**` - Token gerekli

#### 4. Statistics (Protected)
- 🔒 `/api/statistics/**` - Token gerekli

### Authentication Flow
```
1. POST /api/auth/login
   → Response: { "token": "eyJhbGc..." }

2. Diğer endpoint'lere istek at:
   Headers: { "Authorization": "Bearer eyJhbGc..." }

3. Token 24 saat geçerli
   → Süre dolarsa tekrar login ol
```

---

## 🎨 Frontend Entegrasyon Rehberi

### 1. Authentication Entegrasyonu

#### Login Sayfası
```javascript
// Login API call
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  
  if (data.token) {
    // Token'ı sakla
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({
      id: data.userId,
      username: data.username,
      isAdmin: data.isAdmin
    }));
    
    return { success: true, user: data };
  }
  
  return { success: false, error: 'Login failed' };
};

// Kullanım
const result = await login('admin', 'admin123');
if (result.success) {
  // Dashboard'a yönlendir
  navigate('/dashboard');
}
```

#### Protected API Calls
```javascript
// Axios interceptor ile token ekle
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Kullanım
const fetchDashboard = async () => {
  const response = await api.get('/statistics/dashboard');
  return response.data;
};
```

#### Logout
```javascript
const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  navigate('/login');
};
```

---

### 2. Emergency System UI

#### Acil Araç Tespit Butonu
```jsx
import React, { useState } from 'react';

const EmergencyTrigger = () => {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const triggerEmergency = async (vehicleType) => {
    setLoading(true);
    
    try {
      const response = await api.post('/emergency/trigger', {
        vehicleId: `${vehicleType}-${Date.now()}`,
        type: vehicleType, // "AMBULANCE", "FIRE_TRUCK", "POLICE"
        intersectionId: 1,
        direction: "NORTH",
        notes: "Emergency detected from UI"
      });
      
      setResult(response.data);
      
      // Başarı bildirimi göster
      toast.success(response.data.message);
      
      // 5 saniye sonra temizle
      setTimeout(() => setResult(null), 5000);
      
    } catch (error) {
      toast.error('Acil durum tetiklenemedi');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="emergency-panel">
      <h3>🚨 Acil Araç Tespiti</h3>
      
      <div className="button-group">
        <button 
          onClick={() => triggerEmergency('AMBULANCE')}
          disabled={loading}
          className="btn-emergency ambulance"
        >
          🚑 Ambulans
        </button>
        
        <button 
          onClick={() => triggerEmergency('FIRE_TRUCK')}
          disabled={loading}
          className="btn-emergency firetruck"
        >
          🚒 İtfaiye
        </button>
        
        <button 
          onClick={() => triggerEmergency('POLICE')}
          disabled={loading}
          className="btn-emergency police"
        >
          🚓 Polis
        </button>
      </div>

      {result && (
        <div className="emergency-result">
          <h4>{result.message}</h4>
          
          <div className="affected-intersections">
            {result.affectedIntersections.map(intersection => (
              <div key={intersection.intersectionId} className="intersection-status">
                <span className="visual">{intersection.visual}</span>
                <span className="name">{intersection.name}</span>
                <span className="duration">{intersection.duration}s</span>
              </div>
            ))}
          </div>
          
          <div className="impact-info">
            <p>Etkilenen kavşak: {result.impact.affectedIntersections}</p>
            <p>Gecikme: {result.impact.estimatedDelay}</p>
          </div>
        </div>
      )}
    </div>
  );
};
```

#### Aktif Acil Durumlar Listesi
```jsx
const ActiveEmergencies = () => {
  const [emergencies, setEmergencies] = useState([]);

  useEffect(() => {
    const fetchActive = async () => {
      const response = await api.get('/emergency/active');
      setEmergencies(response.data);
    };

    // Her 5 saniyede bir güncelle
    const interval = setInterval(fetchActive, 5000);
    fetchActive();

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="active-emergencies">
      <h3>🚨 Aktif Acil Durumlar ({emergencies.length})</h3>
      
      {emergencies.length === 0 ? (
        <p>✅ Aktif acil durum yok</p>
      ) : (
        <ul>
          {emergencies.map(emergency => (
            <li key={emergency.id} className="emergency-item">
              <span className="icon">{emergency.type.displayName}</span>
              <span className="vehicle-id">{emergency.vehicleId}</span>
              <span className="location">Kavşak-{emergency.currentIntersectionId}</span>
              <span className="status">{emergency.status.displayName}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};
```

---

### 3. Traffic Optimization UI

#### Sensör Veri Gönderimi
```jsx
const SensorSimulator = ({ intersectionId }) => {
  const [vehicleCount, setVehicleCount] = useState(0);
  const [speed, setSpeed] = useState(30);

  const sendSensorData = async () => {
    try {
      const response = await api.post('/optimization/sensor/data', {
        sensorId: `SENSOR-UI-${intersectionId}`,
        intersectionId,
        direction: "NORTH",
        vehicleCount,
        averageSpeed: speed
      });
      
      toast.success(`📡 Sensör verisi gönderildi - ${response.data.densityLevel}`);
      
    } catch (error) {
      toast.error('Sensör verisi gönderilemedi');
    }
  };

  return (
    <div className="sensor-simulator">
      <h4>📡 Sensör Simülatörü - Kavşak {intersectionId}</h4>
      
      <div className="input-group">
        <label>Araç Sayısı:</label>
        <input 
          type="range" 
          min="0" 
          max="80" 
          value={vehicleCount}
          onChange={(e) => setVehicleCount(e.target.value)}
        />
        <span>{vehicleCount} araç</span>
      </div>

      <div className="input-group">
        <label>Ortalama Hız:</label>
        <input 
          type="range" 
          min="0" 
          max="60" 
          value={speed}
          onChange={(e) => setSpeed(e.target.value)}
        />
        <span>{speed} km/h</span>
      </div>

      <button onClick={sendSensorData} className="btn-primary">
        📡 Veri Gönder
      </button>
    </div>
  );
};
```

#### Optimizasyon Uygula
```jsx
const OptimizationPanel = ({ intersectionId }) => {
  const [result, setResult] = useState(null);

  const applyOptimization = async () => {
    try {
      const response = await api.post('/optimization/apply', {
        intersectionId,
        vehicleCount: 45,
        averageSpeed: 25.5
      });
      
      setResult(response.data);
      
    } catch (error) {
      toast.error('Optimizasyon uygulanamadı');
    }
  };

  return (
    <div className="optimization-panel">
      <button onClick={applyOptimization} className="btn-optimize">
        🎯 Optimizasyon Uygula
      </button>

      {result && result.success && (
        <div className="optimization-result">
          <h4>{result.message}</h4>
          
          <div className="details">
            <div className="metric">
              <label>Önceki Süre:</label>
              <span>{result.details.previousGreenDuration}s</span>
            </div>
            <div className="metric">
              <label>Yeni Süre:</label>
              <span className="highlight">{result.details.newGreenDuration}s</span>
            </div>
            <div className="metric">
              <label>Ayarlama:</label>
              <span className="adjustment">{result.details.adjustment}</span>
            </div>
          </div>

          <div className="performance">
            <p>⏱️ {result.details.visual}</p>
            <p>📉 Bekleme azalması: {result.performance.waitTimeReduction}</p>
            <p>📈 Akış iyileşmesi: {result.performance.flowImprovement}</p>
            <p>🎯 Verimlilik: {result.performance.efficiencyScore}</p>
          </div>
        </div>
      )}
    </div>
  );
};
```

---

### 4. Statistics Dashboard

#### Ana Dashboard
```jsx
import { useState, useEffect } from 'react';
import { Line, Bar, Pie } from 'react-chartjs-2';

const MainDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      // Dashboard verilerini al
      const dashResponse = await api.get('/statistics/dashboard');
      setDashboard(dashResponse.data);

      // Grafik verilerini al
      const chartResponse = await api.get('/statistics/chart-data');
      setChartData(chartResponse.data);
    };

    fetchData();
    const interval = setInterval(fetchData, 30000); // 30 saniyede bir güncelle

    return () => clearInterval(interval);
  }, []);

  if (!dashboard) return <div>Yükleniyor...</div>;

  const { systemStatus, dailySummary, topIntersections } = dashboard;

  return (
    <div className="dashboard">
      {/* System Status */}
      <div className="status-card">
        <h2>{systemStatus.systemStatus}</h2>
        <div className="active-emergencies">
          <h3>Aktif Acil Durumlar: {systemStatus.activeEmergencies.count}</h3>
          <span className={`badge ${systemStatus.activeEmergencies.status}`}>
            {systemStatus.activeEmergencies.status}
          </span>
        </div>
      </div>

      {/* Daily Summary */}
      <div className="summary-grid">
        <div className="summary-card">
          <h3>🚨 Acil Araç Geçişleri</h3>
          <div className="number">{dailySummary.emergencyVehicles.total}</div>
          <div className="breakdown">
            <span>🚑 {dailySummary.emergencyVehicles.breakdown.ambulance}</span>
            <span>🚒 {dailySummary.emergencyVehicles.breakdown.fireTruck}</span>
            <span>🚓 {dailySummary.emergencyVehicles.breakdown.police}</span>
          </div>
        </div>

        <div className="summary-card">
          <h3>🎯 Kural Uygulamaları</h3>
          <div className="number">{dailySummary.ruleApplications.total}</div>
        </div>
      </div>

      {/* Charts */}
      {chartData && (
        <div className="charts-grid">
          <div className="chart-card">
            <h3>📈 Saatlik Trafik</h3>
            <Line
              data={{
                labels: chartData.hourlyTraffic.labels,
                datasets: [{
                  label: 'Araç Sayısı',
                  data: chartData.hourlyTraffic.data,
                  borderColor: 'rgb(75, 192, 192)',
                  tension: 0.1
                }]
              }}
            />
          </div>

          <div className="chart-card">
            <h3>📊 Kural Dağılımı</h3>
            <Pie
              data={{
                labels: chartData.ruleDistribution.labels,
                datasets: [{
                  data: chartData.ruleDistribution.data,
                  backgroundColor: [
                    'rgba(255, 99, 132, 0.8)',
                    'rgba(54, 162, 235, 0.8)',
                    'rgba(255, 206, 86, 0.8)'
                  ]
                }]
              }}
            />
          </div>

          <div className="chart-card">
            <h3>🏆 Kavşak Karşılaştırması</h3>
            <Bar
              data={{
                labels: chartData.intersectionComparison.labels,
                datasets: [
                  {
                    label: 'Acil Durumlar',
                    data: chartData.intersectionComparison.emergencies,
                    backgroundColor: 'rgba(255, 99, 132, 0.8)'
                  },
                  {
                    label: 'Verimlilik',
                    data: chartData.intersectionComparison.efficiency,
                    backgroundColor: 'rgba(75, 192, 192, 0.8)'
                  }
                ]
              }}
            />
          </div>
        </div>
      )}

      {/* Top Intersections */}
      <div className="intersections-table">
        <h3>🚦 Kavşak Performansı</h3>
        <table>
          <thead>
            <tr>
              <th>Kavşak</th>
              <th>Acil Durumlar</th>
              <th>Kural Uygulamaları</th>
              <th>Verimlilik</th>
              <th>Rating</th>
            </tr>
          </thead>
          <tbody>
            {topIntersections.intersections.map(intersection => (
              <tr key={intersection.id}>
                <td>{intersection.name}</td>
                <td>{intersection.emergencyCount}</td>
                <td>{intersection.ruleApplications}</td>
                <td>{intersection.efficiency}</td>
                <td>{intersection.rating}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default MainDashboard;
```

#### Gerçek Zamanlı Sistem Durumu
```jsx
const SystemStatus = () => {
  const [status, setStatus] = useState(null);

  useEffect(() => {
    const fetchStatus = async () => {
      const response = await api.get('/statistics/system-status');
      setStatus(response.data);
    };

    fetchStatus();
    const interval = setInterval(fetchStatus, 5000); // 5 saniyede bir

    return () => clearInterval(interval);
  }, []);

  if (!status) return null;

  const getStatusColor = (statusText) => {
    if (statusText.includes('🟢')) return 'green';
    if (statusText.includes('🟡')) return 'yellow';
    if (statusText.includes('🔴')) return 'red';
    return 'gray';
  };

  return (
    <div className={`system-status ${getStatusColor(status.systemStatus)}`}>
      <div className="status-indicator">
        <h2>{status.systemStatus}</h2>
        <span className="timestamp">
          {new Date(status.timestamp).toLocaleTimeString()}
        </span>
      </div>

      <div className="active-emergencies">
        <span className="label">Aktif Acil Durumlar:</span>
        <span className="count">{status.activeEmergencies.count}</span>
        <span className={`priority ${status.activeEmergencies.priority}`}>
          {status.activeEmergencies.priority}
        </span>
      </div>

      <div className="recent-activity">
        <h4>Son 5 Dakika</h4>
        <p>Kural Uygulamaları: {status.recentActivity.ruleApplications}</p>
        <p>Durum: {status.recentActivity.status}</p>
      </div>
    </div>
  );
};
```

---

### 5. Real-time Updates with Polling

```jsx
// Custom hook for polling
const usePolling = (fetchFunction, interval = 5000) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const result = await fetchFunction();
        setData(result);
        setLoading(false);
      } catch (err) {
        setError(err);
        setLoading(false);
      }
    };

    fetchData();
    const intervalId = setInterval(fetchData, interval);

    return () => clearInterval(intervalId);
  }, [fetchFunction, interval]);

  return { data, loading, error };
};

// Kullanım
const Dashboard = () => {
  const { data: emergencies } = usePolling(
    () => api.get('/emergency/active').then(res => res.data),
    5000 // 5 saniyede bir
  );

  const { data: systemStatus } = usePolling(
    () => api.get('/statistics/system-status').then(res => res.data),
    3000 // 3 saniyede bir
  );

  return (
    <div>
      <SystemStatus status={systemStatus} />
      <EmergencyList emergencies={emergencies} />
    </div>
  );
};
```

---

## 🧪 Test Senaryoları

### Senaryo 1: Complete Emergency Flow
```bash
# 1. Login
POST /api/auth/login
Body: { "username": "admin", "password": "admin123" }
→ Copy token

# 2. Ambulans tespit et
POST /api/emergency/test/ambulance
Header: Authorization: Bearer <token>
→ Ambulans ID'sini not al (response'dan)

# 3. Aktif acil durumları kontrol et
GET /api/emergency/active
Header: Authorization: Bearer <token>
→ Ambulans listede görünmeli

# 4. Geçmişi kontrol et
GET /api/emergency/history/{vehicleId}
Header: Authorization: Bearer <token>
→ Event timeline'ı göreceksin

# 5. Acil durumu sonlandır
POST /api/emergency/clear/{vehicleId}
Header: Authorization: Bearer <token>
→ Durum "CLEARED" olmalı

# 6. İstatistikleri kontrol et
GET /api/statistics/daily-summary
Header: Authorization: Bearer <token>
→ Emergency count artmış olmalı
```

### Senaryo 2: Traffic Optimization Flow
```bash
# 1. Varsayılan kuralları oluştur (ilk seferlik)
POST /api/optimization/rules/create-defaults
→ 3 kural oluşturulmalı

# 2. Kuralları listele
GET /api/optimization/rules/active
→ PEAK_HOUR, HIGH_DENSITY, NIGHT_MODE

# 3. Sensör verisi gönder
POST /api/optimization/sensor/data
Body: {
  "sensorId": "SENSOR-001",
  "intersectionId": 1,
  "direction": "NORTH",
  "vehicleCount": 45,
  "averageSpeed": 35.5
}
→ Density level hesaplanmalı

# 4. Optimizasyon uygula
POST /api/optimization/apply
Body: {
  "intersectionId": 1,
  "vehicleCount": 45,
  "averageSpeed": 25.5
}
→ Kural uygulanmalı, süre artırılmalı

# 5. Kural geçmişini kontrol et
GET /api/optimization/rules/{ruleId}/history
→ Uygulama kayıtlarını göreceksin
```

### Senaryo 3: Dashboard Data Flow
```bash
# 1. Birkaç test verisi oluştur
POST /api/emergency/test/ambulance (2 kez)
POST /api/emergency/test/firetruck (1 kez)
POST /api/emergency/test/police (1 kez)
POST /api/optimization/test/high-traffic (3 kez)

# 2. Dashboard'u çek
GET /api/statistics/dashboard
→ Tüm veriler bir arada

# 3. Günlük özeti kontrol et
GET /api/statistics/daily-summary
→ Emergency breakdown: ambulance=2, firetruck=1, police=1

# 4. Grafik verilerini al
GET /api/statistics/chart-data
→ Frontend grafikleri için hazır data

# 5. Kavşak karşılaştırması
GET /api/statistics/compare-intersections
→ Hangi kavşak daha iyi performans gösteriyor
```

---

## 💾 Veritabanı Şeması

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT TRUE
);
```

### Emergency Vehicles Table
```sql
CREATE TABLE emergency_vehicles (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL, -- AMBULANCE, FIRE_TRUCK, POLICE
    status VARCHAR(20) NOT NULL, -- DETECTED, IN_PROGRESS, CLEARED
    current_intersection_id BIGINT NOT NULL,
    direction VARCHAR(20) NOT NULL, -- NORTH, SOUTH, EAST, WEST
    detected_at TIMESTAMP NOT NULL,
    cleared_at TIMESTAMP,
    priority_level INTEGER,
    notes VARCHAR(500)
);
```

### Emergency Events Table
```sql
CREATE TABLE emergency_events (
    id BIGSERIAL PRIMARY KEY,
    emergency_vehicle_id BIGINT NOT NULL,
    intersection_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    previous_phase VARCHAR(20),
    new_phase VARCHAR(20),
    duration_seconds INTEGER,
    created_at TIMESTAMP NOT NULL
);
```

### Traffic Rules Table
```sql
CREATE TABLE traffic_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(100) UNIQUE NOT NULL,
    rule_type VARCHAR(30) NOT NULL,
    priority INTEGER NOT NULL,
    min_vehicle_count INTEGER,
    max_vehicle_count INTEGER,
    time_start TIME,
    time_end TIME,
    green_duration_adjustment INTEGER,
    base_green_duration INTEGER DEFAULT 30,
    times_applied BIGINT DEFAULT 0
);
```

### Traffic Sensors Table
```sql
CREATE TABLE traffic_sensors (
    id BIGSERIAL PRIMARY KEY,
    sensor_id VARCHAR(50) UNIQUE NOT NULL,
    intersection_id BIGINT NOT NULL,
    direction VARCHAR(20) NOT NULL,
    vehicle_count INTEGER NOT NULL,
    average_speed DOUBLE PRECISION,
    density_level VARCHAR(20) NOT NULL,
    recorded_at TIMESTAMP NOT NULL
);
```

---

## 🔧 Troubleshooting

### Docker Container Başlamıyor
```bash
# Container loglarını kontrol et
docker-compose logs app

# Database bağlantısı kontrolü
docker-compose logs postgres

# Tüm container'ları sil ve yeniden başlat
docker-compose down -v
docker-compose up --build -d
```

### Migration Hataları
```bash
# Migration sırasını kontrol et
ls -la src/main/resources/db/migration/

# Flyway'i temizle (dikkatli kullan!)
docker-compose exec postgres psql -U trafficlight -d trafficlight_db
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\q

# Yeniden başlat
docker-compose restart app
```

### JWT Token Geçersiz
```bash
# Token süresi dolmuş olabilir (24 saat)
# Yeniden login ol:
POST /api/auth/login

# Token formatını kontrol et:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
# "Bearer " öneki önemli!
```

### CORS Hataları
```java
// SecurityConfig.java'da CORS yapılandırması mevcut
// Frontend farklı port'ta çalışıyorsa (örn: 3000)
// localhost:3000 otomatik olarak izin verilir
```

---

## 📞 Destek

Sorular için:
- GitHub Issues: <repo-url>/issues
- Team Lead: [İsim]
- Documentation: http://localhost:8080/swagger-ui.html

---

## 📝 Notlar

### Önemli Hatırlatmalar
1. **İlk kurulumda** varsayılan kuralları oluşturmayı unutma: `POST /api/optimization/rules/create-defaults`
2. **JWT token** 24 saat geçerli, sonrasında yeniden login gerekli
3. **Database** docker-compose down -v ile silinir, önemli verileri yedekle
4. **Swagger** her zaman güncel API dokümantasyonu için kaynak

### Performans İpuçları
- Frontend'de polling interval'ı ihtiyaca göre ayarla (önerilen: 5-10 saniye)
- Chart verilerini cache'le, her render'da API çağırma
- Büyük listelerde pagination kullan (backend'de destekleniyor)
- WebSocket yerine HTTP polling kullanıyoruz (basitlik için)

### Güvenlik
- Production'da JWT secret'ı değiştir (environment variable)
- HTTPS kullan
- Rate limiting ekle (isteğe bağlı)
- Admin endpoint'lerine özellikle dikkat et

---

**Son Güncelleme:** 28 Aralık 2025
**Versiyon:** Sprint 4 - Complete System
**Durum:** ✅ Production Ready