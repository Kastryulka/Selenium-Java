import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.contains;

public class ThirdCaseTest {
    protected static WebDriver driver;
    WebDiverLogger listener;
    private Logger logger = LogManager.getLogger(FirstCaseTest.class);
    String outputDir = "temp\\\\ThirdCase\\\\";
    @BeforeEach
    public void setUp() {
        logger.info("Третий кейс");
        String env = System.getProperty("browser", "chrome");
        String loadStrategy = System.getProperty("loadstrategy", "normal");
        String parameters = System.getProperty("params", "");
        if(parameters.startsWith("\"")&parameters.endsWith("\"")){
            parameters = parameters.substring(1,parameters.length()-1);
        }

        logger.info("env = " + env);
        logger.info("loadStrategy = " + loadStrategy);
        logger.info("params = " + parameters);

        Map<String, Object> prefs = new HashMap<String, Object>();
        //TODO конфигурирование prefs из параметров командной строки

        driver = WebDriverFactory.getDriver(env.toLowerCase(),loadStrategy.toUpperCase(),parameters.toLowerCase(),prefs);

        listener = new WebDiverLogger(driver);
        listener.setOutputDir(outputDir);

        logger.info("Драйвер стартовал!");
    }

    @Test
    public void testFunction(){
        WebDriver decoratedDriver = new EventFiringDecorator<>(listener).decorate(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        Actions actions = new Actions(decoratedDriver);

        String vendorName = "ASUS";
        String ramSize = "32";
        String cityXpath = "//*[contains(@class,'city-select__text')]";

        decoratedDriver.get("https://www.dns-shop.ru/");
        logger.info("Открыта страница DNS - " + "https://www.dns-shop.ru/");
        //Сделать скриншот всей страницы (с прокруткой) после загрузки страницы
        listener.getScreenshotFull(driver,outputDir,"Начальная страница DNS");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();

        //если появляется окно выбора города, нажимаем на кнопку согласия
        String xpathConfirmCityBtn = "//button[contains(@class,'v-confirm-city__btn')]";
        try{
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathConfirmCityBtn)))
                    .click();
            logger.info("Согласились с выбором города");
            WebElement body = driver.findElement(By.xpath(
                    "//body[1]"));
            wait.until(ExpectedConditions.stalenessOf(body));
            logger.info("Страница обновлена");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Навестись на Компьютеры и периферия (пк, ноутбуки и периферия)
        String computersPeripheralsXpath = "//a[contains(@class,'menu-desktop__root-title') and contains(text(),'периферия')]";
        By computersPeripheralsBy = By.xpath(computersPeripheralsXpath);
        wait.until(ExpectedConditions.elementToBeClickable(computersPeripheralsBy));
        WebElement computersPeripherals = decoratedDriver.findElement(computersPeripheralsBy);
        actions.moveToElement(computersPeripherals).perform();
        logger.info("Навелись на Пк,ноутбуки и периферия");

        //Перейти по ссылке Ноутбуки
        String laptopsXpath = "//a[contains(@class,'menu-desktop__second-level') and contains(text(),'Ноутбуки')]";
        By laptopsBy = By.xpath(laptopsXpath);
        wait.until(ExpectedConditions.presenceOfElementLocated(laptopsBy));
        WebElement laptops = decoratedDriver.findElement(laptopsBy);
        actions.moveToElement(computersPeripherals).perform();
        logger.info("Навелись на Пк,ноутбуки и периферия повторно");
        wait.until(ExpectedConditions.elementToBeClickable(laptopsBy));
        laptops.click();
        logger.info("Перешли по ссылке Ноутбуки");
        //Сделать скриншот всей страницы (с прокруткой) после загрузки страницы
        listener.getScreenshotFull(driver,outputDir,"Ноутбуки");

        //Скрыть блок страницы (заголовок)
        By headerBy = By.xpath("//header[nav[@id='header-search']]");
        wait.until(ExpectedConditions.presenceOfElementLocated(headerBy));
        WebElement header = decoratedDriver.findElement(headerBy);
        JavascriptExecutor js = (JavascriptExecutor) decoratedDriver;
        String script = "arguments[0].style.display='none';";
        js.executeScript(script, header);
        logger.info("Заголовок страницы скрыт");
        //Сделать скриншот всей страницы (с прокруткой) после скрытия блока
        listener.getScreenshotFull(driver,outputDir,"Скрыт заголок");

        //Выбрать в фильтре Производитель ASUS
        String filtersXpath = "//div[@data-role='filters-container']";
        String chboxVendorXpath = filtersXpath + "//label[span[contains(text(), '" + vendorName + "')]]";
        By chboxVendorBy = By.xpath(chboxVendorXpath);
        wait.until(ExpectedConditions.elementToBeClickable(chboxVendorBy));
        WebElement vendor = decoratedDriver.findElement(chboxVendorBy);
        vendor.click();
        logger.info("Выбрали вендора в фильтрах: " + vendorName);

        //Выбрать в фильтре Объем оперативной памяти значение 32 ГБ
        //раскрыть подменю с фильтрами...
        String ramXpath = filtersXpath + "//span[contains(text(),'Объем оперативной памяти')]";
        By ramBy = By.xpath(ramXpath);
        wait.until(ExpectedConditions.elementToBeClickable(ramBy));
        WebElement ram = decoratedDriver.findElement(ramBy);
        ram.click();
        logger.info("Развернули фильтр ОЗУ");
        //...выбрать фильтр, ...
        String chboxRamXpath = filtersXpath + "//label[span[contains(text(),'" + ramSize + " ГБ')]]";
        By chboxRamBy = By.xpath(chboxRamXpath);
        wait.until(ExpectedConditions.elementToBeClickable(chboxRamBy));
        WebElement chboxRam = decoratedDriver.findElement(chboxRamBy);
        chboxRam.click();
        logger.info("Выбрали объем ОЗУ в фильтрах: " + ramSize + " Гб");
        //... применить фильтр
        String applyFiltersXpath = "//div[contains(@class,'apply-filters-float-btn')]";
        By applyFiltersBy = By.xpath(applyFiltersXpath);
        wait.until(ExpectedConditions.elementToBeClickable(applyFiltersBy));
        WebElement applyFilters = decoratedDriver.findElement(applyFiltersBy);
        applyFilters.click();
        logger.info("Применили фильтры");
        //Сделать скриншот всей страницы (с прокруткой) после применения фильтров
        listener.getScreenshotFull(driver,outputDir,"Фильтры");

        //Применить сортировку Сначала дорогие
        String topOrderXpath = "//div[@class='top-filters']//div[@data-id='order']";
        By topOrderBy = By.xpath(topOrderXpath);
        wait.until(ExpectedConditions.elementToBeClickable(topOrderBy));
        WebElement topOrder = decoratedDriver.findElement(topOrderBy);
        topOrder.click();
        logger.info("Открыли меню порядка отображения товаров");
        String topFilterExpensiveXpath = "//label[@class='ui-radio__item' and span[contains(text(),'Сначала дорогие')]]";
        By topFilterBy = By.xpath(topFilterExpensiveXpath);
        wait.until(ExpectedConditions.elementToBeClickable(topFilterBy));
        WebElement topFilter = decoratedDriver.findElement(topFilterBy);
        topFilter.click();
        logger.info("Применили сортировку Сначала дорогие");

        //Перейти на страницу первого продукта в списке в новом максимизированном окне
        String firstLaptopXpath = "//div[contains(@class,'catalog-products')][1]/div[contains(@class,'catalog-product')][1]";
        By firstLaptopBy = By.xpath(firstLaptopXpath);
        wait.until(ExpectedConditions.stalenessOf(decoratedDriver.findElement(firstLaptopBy)));
        //Сделать скриншот всей страницы (с прокруткой) после применения сортировки
        listener.getScreenshotFull(driver,outputDir,"Сортировка");
        wait.until(ExpectedConditions.elementToBeClickable(firstLaptopBy));
        WebElement firstLaptop = decoratedDriver.findElement(firstLaptopBy);
        //готовим строки для сравнения...
        String firstLaptopText = firstLaptop.findElement(
                By.xpath("./a[contains(@class,'catalog-product__name')]"))
                .getText();
        int indexEnd = firstLaptopText.indexOf("[");
        firstLaptopText = firstLaptopText.substring(0,indexEnd).trim();
        logger.info("Название первого продукта в списке: " + firstLaptopText);
        //...переходим на следующую страницу
        String firstLaptopLink = decoratedDriver.findElement(
                By.xpath(firstLaptopXpath+"/a[contains(@class,'catalog-product__name')]"))
                .getAttribute("href");
        decoratedDriver.switchTo().newWindow(WindowType.WINDOW);
        decoratedDriver.manage().window().maximize();
        decoratedDriver.get(firstLaptopLink);
        logger.info("В новом окне открыта ссылка: " + firstLaptopText);
        Set<String> windows = driver.getWindowHandles();

        //Проверить, что заголовок страницы соответствует ожидаемому
        String newWindowTitle = decoratedDriver.getTitle();
        logger.info("Заголовок новой страницы: " + newWindowTitle);
        logger.info("Название продукта в списке: " + firstLaptopText);
        Assertions.assertTrue(contains(newWindowTitle,firstLaptopText));
        logger.info("Заголовок страницы соответствует ожидаемому");
        //Сделать скриншот всей страницы (с прокруткой) после загрузки страницы
        listener.getScreenshotFull(driver,outputDir,"Страница выбранного товара");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();

        //Проверить, что в блоке Характеристики заголовок содержит ASUS
        String characteristicsTitleXpath = "//div[contains(@class,'product-card-description__title')]";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(characteristicsTitleXpath)));
        WebElement characteristicsTitle = decoratedDriver.findElement(By.xpath(characteristicsTitleXpath));
        Assertions.assertTrue(contains(characteristicsTitle.getText().toUpperCase(),vendorName));
        logger.info("Заголовок в блоке Характеристики корректный: " + vendorName);

        //Проверить, что в блоке Характеристики значение Объем оперативной памяти равно 32 ГБ
        String characteristicsBtnXpath = "//button[contains(@class,'product-characteristics__expand')]";
        By characteristicsBtnBy = By.xpath(characteristicsBtnXpath);
        wait.until(ExpectedConditions.elementToBeClickable(characteristicsBtnBy));
        WebElement characteristicsBtn = decoratedDriver.findElement(characteristicsBtnBy);
        characteristicsBtn.click();
        logger.info("Развернули список характеристик");
        String characteristicsRamXpath = "//div[contains(@class,'product-characteristics__ovh') " +
                "and div[contains(text(),'Объем оперативной памяти')]]" +
                "/*[contains(@class,'product-characteristics__spec-value')]";
        WebElement characteristicsRam = decoratedDriver.findElement(By.xpath(characteristicsRamXpath));
        logger.info(characteristicsRam.getText().toUpperCase());
        Assertions.assertTrue(contains(characteristicsRam.getText().toUpperCase(),ramSize));
        logger.info("Объем ОЗУ корректный: " + ramSize);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @AfterEach
    public void setDown() {
        if(driver != null) {
            driver.quit();
            logger.info("Драйвер остановлен!");
        }
    }
}
