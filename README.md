Java ustawiona jest na 11, ale raczej się nic nie stanie jeśli masz 1.8.
Trzeba tylko w pomie wersje zmienić.

Puszczamy ```mvn spring-boot:run``` w katalogu z pom.xml.

Dane getujemy z ```localhost:8080/usd/ROK/MIESIAC/DZIEN```, gdzie
- rok - od 2002 do 2019 (nbp api tylko takie lata obsługuje)
- miesiac - 1-12 przy czym styczeń to "1" a nie "01"
- dzień - 1-31 analogicznie do miesiąca.

np. Dane od 7 lipca 2017 do dnia obecnego pobieramy:
- ```localhost:8080/usd/2017/7/7``` w przeglądarce(polecam firefoxa, ładnie  koloruje i formatuje JSON'a)
- ```curl localhost:8080/usd/2017/7/7``` w bashu
- Oczywiście w postmanach itd. też zadziała.

Api nie jest zbytnio "idiotoodporne" podobnie do api NBP, jak damy datę z przyszłości lub przed 2002 to da 500-tke.

Api tak samo jak api NBP da błąd jeśli danego dnia nie został wrzucony kurs waluty. Tzn. nie działa w święta i niedziele oraz w dni robocze do godziny wrzucenia kursu.

Trzeba mieć datę systemową dobrze ustawioną. 

Polecenie brzmi do "bieżącej daty" i jeśli bieżąca data nie ma wartości to niemożliwym jest danie odpowiedzi. 

Jeśli miałoby to działać do ostatniej znanej nam wartości to ewentualnym fixem byłoby troche infowania i cofania sie dzień po dniu, aż do ostatniej niepustej daty.

Zdecydowałem się także, nie kopiować poprzednich wartości do pustch dni(np. do 11 listopada dane z 10), gdyż to może zrobić ewentualny front. Nie widze sensu w powiększaniu przesyłanego JSON'a.

