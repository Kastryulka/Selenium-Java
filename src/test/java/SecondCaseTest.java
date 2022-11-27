import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.*;

public class SecondCaseTest {
    protected static WebDriver driver;
    WebDiverLogger listener;
    private Logger logger = LogManager.getLogger(FirstCaseTest.class);
    String outputDir = "temp\\\\SecondCase\\\\";
    @BeforeEach
    public void setUp() {
        logger.info("Второй кейс");
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
        ArrayList<String> testRefs = new ArrayList<String>();
        testRefs.add("Техника для кухни");
        testRefs.add("Техника для дома");
        testRefs.add("Встраиваемая техника");
        String cityXpath = "//*[contains(@class,'city-select__text')]";

        decoratedDriver.get("https://www.dns-shop.ru/");
        logger.info("Открыта страница DNS - " + "https://www.dns-shop.ru/");
        listener.getScreenshotFull(driver,outputDir,"Начальная страница DNS");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();

        //если появляется окно выбора города (может скрывать Бытовую технику), нажимаем на кнопку согласия
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

        //Навести курсор на ссылку Бытовая техника, проверить, что отображаются ссылки
        String xpathAppliances ="//a[contains(@class,'menu-desktop__root-title') and text()='Бытовая техника']";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathAppliances)));
        listener.getScreenshotFull(driver,outputDir,"Обновленная начальная страница");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();
        WebElement linkAppliances = driver.findElement(By.xpath(xpathAppliances));
        actions
                .moveToElement(linkAppliances)
                .perform();
        String xpathSublist = ".//ancestor::div[@class='menu-desktop__root']//div[contains(@class ,'menu-desktop__submenu_top')]/a";
        List<WebElement> sublistAppliances = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath(xpathSublist)));
        Assertions.assertEquals(sublistAppliances.size(),testRefs.size(), "ссылок больше, чем должно быть");
        for(WebElement elem : sublistAppliances){
            Assertions.assertTrue(testRefs.contains(elem.getText()), "элемент отсутствует среди ссылок");
            Assertions.assertTrue(elem.isDisplayed(), "не отображается элемент в разделе Бытовая техника");
        }
        listener.getScreenshotFull(driver,outputDir,"Элементы меню Бытовая техника");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();
        logger.info("Необходимые ссылки отображаются");

        //Навестись на приготовление пищи... проверить количество ссылок в подменю
        String xpathCooking = "//a[@class='ui-link menu-desktop__second-level' and text()='Плиты и печи']";
        WebElement linkCooking = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathCooking)));
        actions
                .moveToElement(linkCooking)
                .perform();
        List<WebElement> popupCooking = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath(xpathCooking+"/span[@class='menu-desktop__popup']/a")));
        listener.getScreenshotFull(driver,outputDir,"Элементы меню Плиты и печи");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();
        logger.info("Открыто подменю Плиты и печи");
        logger.info("Количество элементов подменю: " + popupCooking.size());
        //... проверить, что ссылок больше пяти
        Assertions.assertTrue(popupCooking.size()>5, "Плиты и печи меньше пяти");

        //Навестись и перейти в плиты, ...
        actions
                .moveToElement(linkAppliances)
                .perform();
        WebElement stoves = wait.until(ExpectedConditions.elementToBeClickable(linkCooking));
        actions
                .moveToElement(stoves)
                .click()
                .perform();
        logger.info("Перешли в раздел Плиты");
        listener.getScreenshotFull(driver,outputDir,"Плиты");
        actions.scrollToElement(driver.findElement(By.xpath(cityXpath))).perform();
        //...перейти в электрические плиты...
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@class,'subcategory__item')]//span[text()='Плиты электрические']")))
                .click();
        logger.info("Перешли в раздел Плиты электрические");
        listener.getScreenshotFull(driver,outputDir,"Электрические плиты");
        //... проверить, что в заголовке количество товаров больше ста
        WebElement productsCount = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[@class='products-count']")));
        int value = Integer.parseInt(productsCount.getText().replaceAll("[^0-9]", ""));
        logger.info("Количество товаров: " + value);
        Assertions.assertTrue(value>100, "товаров меньше ста");
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
