# Gauge Selenium Test Otomasyon Projesi

Bu proje, web uygulamalarını test etmek için Gauge, Selenium, Java ve Maven kullanan bir test otomasyon altyapısını göstermektedir. Farklı tarayıcılarda (Chrome, Firefox, Edge, Safari) parametrik olarak test çalıştırmayı destekler ve Selenium Grid ile entegre olabilir.

## Proje Yapısı

```
.
├── env
│   └── default
│       └── default.properties  # Ortama özel yapılandırmalar (BROWSER_TYPE, SELENIUM_GRID_URL)
├── pom.xml                     # Maven proje yapılandırması, bağımlılıklar ve eklentiler
├── specs                       # Gauge spesifikasyon dosyaları (.spec)
│   └── trendyol_login.spec     # Trendyol login ve arama için örnek spec dosyası
└── src
    └── test
        └── java
            └── StepImplementation.java # .spec dosyalarında tanımlanan adımların Java implementasyonları
```

## Ön Koşullar

1.  **Java Development Kit (JDK):** Sürüm 11 veya üzeri. `JAVA_HOME` ortam değişkeninin ayarlandığından ve Java'nın sistem PATH'inizde olduğundan emin olun.
2.  **Apache Maven:** Sürüm 3.6 veya üzeri. Maven'ın sistem PATH'inizde olduğundan emin olun.
3.  **Gauge:** Gauge ve Gauge Java eklentisini yükleyin.
    *   Kurulum talimatları için: [https://docs.gauge.org/getting_started/installing-gauge.html](https://docs.gauge.org/getting_started/installing-gauge.html)
    *   Java eklentisini yükleyin: `gauge install java`
4.  **Web Tarayıcıları:** Test etmek istediğiniz web tarayıcılarını yükleyin (örneğin, Chrome, Firefox).
5.  **(İsteğe Bağlı) Selenium Grid:** Testleri bir Selenium Grid üzerinde çalıştırmayı planlıyorsanız, Grid'in kurulu ve erişilebilir olduğundan emin olun.

## Kurulum

1.  **Projeyi klonlayın (eğer varsa) veya proje dosyalarına sahip olduğunuzdan emin olun.**
2.  **Projeyi derleyin ve bağımlılıkları indirin:**
    Proje kök dizininde bir terminal açın ve şunu çalıştırın:
    ```bash
    mvn clean compile -U
    ```
    Bu komut projeyi temizler, kaynak kodunu derler ve Maven bağımlılıklarını günceller.

## Yapılandırma

### Tarayıcı Yapılandırması

Yerel test çalıştırmaları için kullanılacak tarayıcı, testleri çalıştırırken komut satırı aracılığıyla veya `env/default/default.properties` dosyasında ayarlanabilen `BROWSER_TYPE` ortam değişkeni ile kontrol edilir.

`BROWSER_TYPE` için desteklenen değerler: `chrome`, `firefox`, `edge`, `safari`.
`BROWSER_TYPE` ayarlanmazsa, varsayılan olarak `chrome` kullanılır.

### Selenium Grid Yapılandırması (İsteğe Bağlı)

Testleri bir Selenium Grid üzerinde çalıştırmak için, `SELENIUM_GRID_URL` ortam değişkenini Selenium Hub'ınızın URL'sine ayarlayın.
Örnek:
```
SELENIUM_GRID_URL=http://localhost:4444/wd/hub
```
Bu, sisteminizin ortam değişkenlerinde veya `env/default/default.properties` dosyasında ayarlanabilir. `SELENIUM_GRID_URL` ayarlanırsa, testler Grid üzerinde çalışmayı dener; aksi takdirde yerel olarak çalışırlar.

### `env/default/default.properties`

Bu dosya, Gauge tarafından kullanılan ortam değişkenleri için varsayılan değerleri ayarlamak amacıyla kullanılabilir.
Örnek:
```properties
# Yerel çalıştırma için tarayıcı (chrome, firefox, edge, safari)
BROWSER_TYPE = chrome

# İsteğe Bağlı: Selenium Grid Hub URL'si
# SELENIUM_GRID_URL = http://localhost:4444/wd/hub

# İsteğe Bağlı: Gauge çalıştırması için JVM argümanları, dosya kodlaması için faydalı olabilir
# gauge_jvm_args = -Dfile.encoding=UTF-8
```

### Firefox Çalıştırılabilir Dosya Yolu (Gerekirse)

Selenium, Firefox tarayıcı kurulumunuzu bulmakta sorun yaşarsa (özellikle standart olmayan bir yolda kuruluysa), `FIREFOX_BINARY_PATH` ortam değişkenini `firefox.exe` (veya diğer işletim sistemleri için eşdeğeri) dosyanızın tam yoluna ayarlamanız gerekebilir.
Örnek (PowerShell):
```powershell
$env:FIREFOX_BINARY_PATH = "C:\Program Files\Mozilla Firefox\firefox.exe"
```
Ya da `gauge_jvm_args` aracılığıyla JVM argümanlarını yapılandırıyorsanız `env/default/default.properties` dosyasına ekleyebilirsiniz.

## Testleri Çalıştırma

Testler Gauge Maven eklentisi kullanılarak çalıştırılır.

### Tüm Spesifikasyonları Çalıştırma

`specs` dizinindeki tüm spesifikasyonları çalıştırmak için:

```bash
mvn gauge:execute
```
Bu komut, `BROWSER_TYPE` geçersiz kılınmadığı sürece varsayılan tarayıcıyı (Chrome) kullanacaktır.

### Belirli Bir Tarayıcı ile Çalıştırma

Maven komutunda bir sistem özelliği aracılığıyla tarayıcıyı doğrudan belirtebilirsiniz. Bu, o çalıştırma için `default.properties` dosyasındaki `BROWSER_TYPE` değerini geçersiz kılar.

*   **Chrome ile Çalıştırma:**
    ```bash
    mvn gauge:execute -Dbrowser=chrome
    ```
*   **Firefox ile Çalıştırma:**
    ```bash
    mvn gauge:execute -Dbrowser=firefox
    ```
*   **Edge ile Çalıştırma:**
    ```bash
    mvn gauge:execute -Dbrowser=edge
    ```

### Belirli Bir Spesifikasyon Dosyasını Çalıştırma

Belirli bir `.spec` dosyasını çalıştırmak için:
```bash
mvn gauge:execute -DspecsDir=specs/dosya_adinix.spec
```
Örnek:
```bash
mvn gauge:execute -DspecsDir=specs/trendyol_login.spec -Dbrowser=chrome
```

### Belirli Senaryoları Çalıştırma (Etiket Kullanarak)

`.spec` dosyalarınızdaki senaryoları etiketleyebilir ve ardından yalnızca bu etiketlere sahip senaryoları çalıştırabilirsiniz.
Örnek:
Bir senaryonun `@login` etiketi varsa:
```gauge
# Senaryom
Etiketler: login
* Adım 1
* Adım 2
```
`login` etiketine sahip senaryoları çalıştırın:
```bash
mvn gauge:execute -Dtags=login
```

## Karakter Kodlaması (UTF-8)

Bu projede, özellikle adım tanımları veya spesifikasyon metinleri ASCII olmayan karakterler (örneğin, Türkçe karakterler) içerdiğinde karakter kodlamasıyla ilgili sorunlar yaşanmıştır.

**Önemli Hususlar:**

1.  **Dosya Kodlamaları:** İlgili tüm dosyaların (`.spec`, `.java`, `pom.xml`) **UTF-8 (BOM'suz)** olarak kaydedildiğinden emin olun. Çoğu modern IDE bunu yapılandırmanıza olanak tanır.
2.  **Maven Compiler Plugin:** `pom.xml` dosyası, `maven-compiler-plugin` için `<encoding>UTF-8</encoding>` içerir.
3.  **Maven Surefire Plugin:** `pom.xml` dosyası, `maven-surefire-plugin` için `<argLine>-Dfile.encoding=UTF-8</argLine>` içerir.
4.  **Gauge JVM Argümanları:** Gauge'un kendisi için JVM'ye `-Dfile.encoding=UTF-8` parametresini geçmek gerekebilir. Bu şu şekillerde denenebilir:
    *   `MAVEN_OPTS` ortam değişkenini ayarlayarak:
        ```bash
        export MAVEN_OPTS="-Dfile.encoding=UTF-8" # Linux/macOS için
        set MAVEN_OPTS="-Dfile.encoding=UTF-8"    # Windows Komut İstemi için
        $env:MAVEN_OPTS = "-Dfile.encoding=UTF-8" # Windows PowerShell için
        ```
    *   `env/default/default.properties` dosyasına ekleyerek:
        ```properties
        gauge_jvm_args = -Dfile.encoding=UTF-8
        ```
    Kullanılan sürümdeki `gauge-maven-plugin` doğrudan bir `<jvmArgs>` yapılandırmasını desteklemediğinden, yukarıdaki yöntemler geçici çözümlerdir.

`Step implementation not found` (Adım implementasyonu bulunamadı) hatalarıyla karşılaşırsanız ve konsol çıktısında adım metinlerinde bozuk karakterler görüyorsanız, bu büyük olasılıkla bir kodlama sorunudur.

## Raporları Görüntüleme

Çalıştırmadan sonra Gauge, `reports/html-report` dizininde bir HTML raporu oluşturur. Test sonuçlarını görüntülemek için `index.html` dosyasını bir tarayıcıda açın.

## Sorun Giderme

*   **`SessionNotCreatedException` / `Expected browser binary location...` (Tarayıcı çalıştırılabilir dosya konumu bekleniyordu...):**
    *   Tarayıcının (örneğin, Firefox, Chrome) doğru şekilde kurulduğundan emin olun.
    *   Firefox için, varsayılan konumda değilse, `FIREFOX_BINARY_PATH` ortam değişkenini ayarlayın.
    *   WebDriverManager'ın tarayıcı sürümünüz için doğru sürücüyü indirebildiğinden emin olun. İnternet bağlantınızı ve varsa proxy ayarlarınızı kontrol edin.
*   **Özel karakterler içeren adımlar için `Step implementation not found` (Adım implementasyonu bulunamadı):**
    *   Bu neredeyse her zaman bir UTF-8 kodlama sorunudur. "Karakter Kodlaması (UTF-8)" bölümündeki adımları dikkatlice izleyin.
    *   `.spec` dosyalarınızın ve `StepImplementation.java` dosyanızın kodlamasını doğrulayın.
*   **Bağımlılıklar bulunamadı (`package ... does not exist` paketi mevcut değil):**
    *   Bağımlılıkların güncellenmesini ve yeniden indirilmesini zorlamak için `mvn clean compile -U` komutunu çalıştırın.
    *   `pom.xml` dosyanızdaki doğru bağımlılık sürümlerini kontrol edin.
    *   Bir IDE kullanıyorsanız, Maven projesiyle doğru şekilde senkronize olduğundan emin olun.

Bu README, bu test otomasyon projesiyle çalışan herkes için iyi bir başlangıç noktası sağlamalıdır. 