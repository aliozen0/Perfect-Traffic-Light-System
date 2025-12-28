# 🚦 Traffic Light Management System - Çalıştırma Talimatları

## ⚠️ ÖNEMLİ: Java Sürüm Uyumsuzluğu

Sisteminizde **Java 25** kurulu ancak bu proje **Java 17** için geliştirilmiş. Lombok kütüphanesi Java 25 ile uyumlu değil ve şu hatayı veriyor:

```
java.lang.NoSuchFieldException: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## 🎯 Çözümler (Öncelik Sırasına Göre)

### ✅ Çözüm 1: Docker Kullan (EN KOLAY - ÖNERİLİR)

Docker Desktop'ı başlatın ve şu komutları çalıştırın:

```powershell
docker-compose up -d
```

Bu komut otomatik olarak:
- PostgreSQL veritabanını
- PgAdmin arayüzünü  
- Spring Boot uygulamasını

başlatacak ve yapılandıracaktır.

**Erişim Adresleri:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/api/health
- PgAdmin: http://localhost:5050 (admin@trafficlight.com / admin)

---

### ✅ Çözüm 2: Java 17 Kur (KALICI ÇÖZÜM)

#### Adım 1: Java 17 İndir ve Kur
https://adoptium.net/temurin/releases/?version=17

veya

https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

#### Adım 2: JAVA_HOME'u Ayarla

**PowerShell'de (Geçici):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
java -version  # Kontrol et
```

**Kalıcı Olarak (Sistem Özellikleri):**
1. "Sistem Özellikleri" > "Gelişmiş Sistem Ayarları"
2. "Ortam Değişkenleri"
3. "JAVA_HOME" değişkenini Java 17 yoluna ayarla
4. PATH'e "%JAVA_HOME%\bin" ekle

#### Adım 3: Projeyi Çalıştır

```powershell
cd "C:\Users\muham\Desktop\dersler\securecoding\proje"

# PostgreSQL'in çalıştığından emin ol
# Yoksa Docker'dan sadece PostgreSQL başlat:
docker run -d -p 5432:5432 -e POSTGRES_DB=trafficlight_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:15-alpine

# Veritabanını oluştur
# psql -U postgres
# CREATE DATABASE trafficlight_db;

# Projeyi derle ve çalıştır
mvn clean install
mvn spring-boot:run
```

---

### ✅ Çözüm 3: IDE Kullan (Visual Studio Code, IntelliJ IDEA)

#### IntelliJ IDEA:
1. Projeyi aç
2. File → Project Structure → Project SDK → Java 17 seç
3. `TrafficLightApplication` sınıfına git
4. Run düğmesine tıkla

#### Visual Studio Code:
1. Java Extension Pack kur
2. Settings → Java: Configuration → Runtime → Java 17 ekle
3. `TrafficLightApplication.java`'yı aç ve Run

---

### ✅ Çözüm 4: H2 Database ile Test (PostgreSQL Gerekmez)

Eğer sadece test etmek istiyorsanız, PostgreSQL olmadan H2 in-memory database ile çalıştırabilirsiniz:

```powershell
# Önce Java 17'yi aktif edin (Çözüm 2'deki gibi)
mvn spring-boot:run -Dspring.profiles.active=h2
```

**Not:** H2 profili veritabanını bellekte tutar, her yeniden başlatmada data sıfırlanır.

---

## 📊 Test Endpoint'leri

Uygulama çalıştıktan sonra bu adresleri test edin:

### Basic Health Check
```bash
curl http://localhost:8080/api/health
```

### Tüm Kesişimler
```bash
curl http://localhost:8080/api/intersections
```

### Swagger UI (Tüm API'ler)
Tarayıcıda açın: http://localhost:8080/swagger-ui.html

### Dashboard
```bash
curl http://localhost:8080/api/dashboard/summary
```

---

## 🐛 Sorun Giderme

### "Port 8080 zaten kullanımda" Hatası

```powershell
# Windows'ta portu kontrol et
netstat -ano | findstr :8080

# İşlemi kapat
taskkill /PID <PID_NUMARASI> /F
```

### "Database connection refused" Hatası

```powershell
# PostgreSQL çalışıyor mu kontrol et
docker ps

# Docker ile PostgreSQL başlat
docker run -d --name postgres_traffic -p 5432:5432 -e POSTGRES_DB=trafficlight_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:15-alpine
```

### Maven Dependency Hatası

```powershell
# Maven cache'i temizle
rmdir /s /q %USERPROFILE%\.m2\repository
mvn clean install -U
```

---

## 📚 Ek Kaynaklar

- **Hızlı Başlangıç:** `QUICKSTART.md`
- **API Örnekleri:** `API_EXAMPLES.md`  
- **Proje Detayları:** `PROJE_OZET.md`
- **README:** `README.md`

---

## ✅ Özet

En kolay ve önerilen yöntem: **Docker kullanmak**

Kalıcı çözüm için: **Java 17 kurmak**

Test için: **H2 profili kullanmak**

---

**Başarılar!** 🚀

Sorularınız için: Projeyi daha detaylı incelemek için yukarıdaki dokümanları okuyun.

