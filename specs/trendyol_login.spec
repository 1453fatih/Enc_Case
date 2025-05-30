# Trendyol Login Senaryosu

## Başarısız Email Girişi
* "https://www.trendyol.com/" adresine git
* "//button[@id='onetrust-accept-btn-handler']" elementine tıkla
* "//div[contains(@class, 'account-nav-item') and .//p[text()='Giriş Yap']]" elementine tıkla
* "//input[@id='login-email']" input alanına "test@example.com" metnini gir
* "//input[@id='login-password-input']" input alanına "enocta" metnini gir
* "//button[@type='submit']" elementine tıkla
* "E-posta adresiniz ve/veya şifreniz hatalı." mesajının göründüğünü doğrula
* Tarayıcıyı kapat


## Ekran Görüntüsü
* "https://www.trendyol.com/" adresine git
* "//button[@id='onetrust-accept-btn-handler1071']" elementine tıkla
* Tarayıcıyı kapat

## Arama ve Filtre Testi
* "https://www.trendyol.com/" adresine git
//Popup gitsin diye boş alana (fark etmez) tıklatıyorum
* "//button[@id='onetrust-accept-btn-handler']" elementine tıkla 
//Escape'li örnek
* "//input[@data-testid=\"suggestion\"]" input alanına "Cep Telefonu" metnini gir
* "//*[@data-testid=\"search-icon\"]" elementine tıkla
* "(//*[@class='fltr-cntnr-ttl-area'])[4]" elementine tıkla
* "//*[@class='fltr-srch-prc-rng-input min']" input alanına "15000" metnini gir
* "//*[@class='fltr-srch-prc-rng-input max']" input alanına "20000" metnini gir
* "//button[@class='fltr-srch-prc-rng-srch']" elementine tıkla
* "(//*[@class='product-down'])[10]" XPath'e kadar kaydırılır
* "5" saniye beklenir
* "(//*[@class='product-down'])[10]" elementine tıkla ve açılan yeni sekmeye geç
//* "(//*[@class='product-down'])[10]" elementine tıkla
* "10" saniye beklenir
* "//button[text()='Anladım']" elementine tıkla
* "//*[@class='add-to-basket-button-text']" elementine tıkla
* "5" saniye beklenir
* "//*[@class='link account-basket']" elementine tıkla
* "5" saniye beklenir
* "Sepeti Onayla" mesajının göründüğünü doğrula
* "1" saniye beklenir
* Tarayıcıyı kapat 