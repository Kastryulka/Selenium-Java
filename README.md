# Selenium-Java
## Первый кейс
### Chrome
mvn clean test -Dtest=FirstCaseTest

---
mvn clean test -Dtest=FirstCaseTest -Dparams="--incognito --start-maximized"

---
mvn clean test -Dtest=FirstCaseTest -Dloadstrategy=eager -Dparams="--incognito --start-maximized"

---
mvn clean test -Dtest=FirstCaseTest -Dloadstrategy=none -Dparams="--incognito --start-maximized"

---
### Firefox
mvn clean test -Dtest=FirstCaseTest -Dbrowser=firefox

---
mvn clean test -Dtest=FirstCaseTest -Dbrowser=firefox -Dparams="-private --kiosk"

---
mvn clean test -Dtest=FirstCaseTest -Dloadstrategy=eager -Dbrowser=firefox -Dparams="-private --kiosk"

---
mvn clean test -Dtest=FirstCaseTest -Dbrowser=firefox -Dloadstrategy=none -Dparams="-private"

---

## Второй кейс
### Chrome
mvn clean test -Dtest=SecondCaseTest

---
mvn clean test -Dtest=SecondCaseTest -Dparams="--incognito --start-maximized"

---
mvn clean test -Dtest=SecondCaseTest -Dloadstrategy=eager -Dparams="--incognito --start-maximized"

---
mvn clean test -Dtest=SecondCaseTest -Dloadstrategy=none -Dparams="--incognito --start-maximized"

---
### Firefox
mvn clean test -Dtest=SecondCaseTest -Dbrowser=firefox

---
mvn clean test -Dtest=SecondCaseTest -Dbrowser=firefox -Dparams="-private --kiosk"

---
mvn clean test -Dtest=SecondCaseTest -Dloadstrategy=eager -Dbrowser=firefox -Dparams="-private --kiosk"

---
mvn clean test -Dtest=SecondCaseTest -Dbrowser=firefox -Dloadstrategy=none -Dparams="-private"

---