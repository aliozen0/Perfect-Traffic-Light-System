# Java 25 Uyumluluk Sorunu

## Sorun
Proje Java 17 için tasarlanmış ancak sisteminizde Java 25 kurulu. Maven compiler plugin Java 25 ile uyumlu değil ve şu hatayı veriyor:
```
Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Çözüm Önerileri

### Çözüm 1: Java 17 Kurulumu (Önerilen)
1. Java 17 JDK indirin: https://adoptium.net/temurin/releases/?version=17
2. Java 17'yi kurun
3. JAVA_HOME ortam değişkenini Java 17 yoluna ayarlayın:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```
4. Projeyi derleyin:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Çözüm 2: Docker Kullanımı (En Kolay)
Docker Desktop'ı başlatın ve:
```bash
docker-compose up -d
```
Uygulama http://localhost:8080 adresinde çalışacaktır.

### Çözüm 3: H2 ile Test (PostgreSQL Olmadan)
PostgreSQL gerekmeden test etmek için:
```bash
mvn spring-boot:run -Dspring.profiles.active=h2
```

### Çözüm 4: IDE Kullanımı
IntelliJ IDEA veya Eclipse kullanarak:
1. Projeyi IDE'de açın
2. Project SDK'yı Java 17 olarak ayarlayın
3. TrafficLightApplication sınıfını Run edin

## Test İçin Önemli Notlar
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/api/health
- H2 Console (h2 profile ile): http://localhost:8080/h2-console

## Daha Fazla Bilgi
Proje detayları için QUICKSTART.md dosyasına bakın.

