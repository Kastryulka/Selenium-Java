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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstCaseTest {
    protected static WebDriver driver;
    WebDiverLogger listener;
    private Logger logger = LogManager.getLogger(FirstCaseTest.class);
    String outputDir = "temp\\\\FirstCase\\\\";

    @BeforeEach
    public void setUp() {
        logger.info("Первый кейс");
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

        driver = WebDriverFactory.getDriver(env.toLowerCase(),loadStrategy.toUpperCase(), parameters.toLowerCase(),prefs);

        listener = new WebDiverLogger(driver);
        listener.setOutputDir(outputDir);

        logger.info("Драйвер стартовал!");
    }

    @Test
    public void testFunction(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        WebDriver decoratedDriver = new EventFiringDecorator<>(listener).decorate(driver);
        Actions actions = new Actions(driver);
        decoratedDriver.get("https://www.dns-shop.ru/");
        logger.info("Открыта страница DNS - " + "https://www.dns-shop.ru/");
        logger.info("Заголовок страницы - " + decoratedDriver.getTitle());
        logger.info("Текущий URL - " + decoratedDriver.getCurrentUrl());
        logger.info(String.format("Ширина окна: %d", decoratedDriver.manage().window().getSize().getWidth()));
        logger.info(String.format("Высота окна: %d", decoratedDriver.manage().window().getSize().getHeight()));
        listener.getScreenshotFull(driver,outputDir,"Начальная страница DNS");
        actions.scrollToElement(driver.findElement(By.xpath("//*[contains(@class,'city-select__text')]"))).perform();

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
            listener.getScreenshotFull(driver,outputDir,"Обновленная начальная страница");
            actions.scrollToElement(driver.findElement(By.xpath("//*[contains(@class,'city-select__text')]"))).perform();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Перейти по ссылке Бытовая техника, проверить, что отображается текст
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@class ,'menu-desktop__root-title') and text()='Бытовая техника']")))
                .click();
        logger.info("Перешли в раздел Бытовая техника");
        listener.getScreenshotFull(driver,outputDir,"Страница Бытовая техника");
        WebElement titleHouseholdAppliances = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@class='subcategory__page-title']")));
        Assertions.assertEquals("Бытовая техника", titleHouseholdAppliances.getText(), "В заголовке не Бытовая техника");
        Assertions.assertTrue(titleHouseholdAppliances.isDisplayed(), "Надпись \"Бытовая техника\" в заголовке не отображена");
        logger.info("Надпись \"Бытовая техника\" в заголовке отображена");

        //Перейти по ссылке Техника для кухни, проверить, что отображается текст,...
        driver.findElement(By.xpath(
                "//*[text()='Техника для кухни']//ancestor::a[contains(@class ,'ui-link')]"))
                .click();
        logger.info("Перешли в раздел Техника для кухни");
        listener.getScreenshotFull(driver,outputDir,"Страница Техника для кухни");
        WebElement titleKitchenAppliances = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@class='subcategory__page-title']")));
        Assertions.assertEquals("Техника для кухни",
                titleKitchenAppliances.getText(), "В заголовке не Техника для кухни");
        Assertions.assertTrue(titleKitchenAppliances.isDisplayed(),
                "Надпись \"Техника для кухни\" в заголовке не отображена");
        logger.info("Надпись \"Техника для кухни\" в заголовке отображена");

        //... проверить, что отображается ссылка Собрать свою кухню,...
        String xpathConfBtn = "//a[contains(@class ,'configurator-links-block__links-link')]";
        WebElement ConfBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathConfBtn)));
        Assertions.assertTrue(ConfBtn.isDisplayed(), "ссылка \"Собрать свою кухню\" не отображена");
        logger.info("Ссылка \"Собрать свою кухню\" отображена");

        //... вывести в логи названия категорий, ...
        List<WebElement> subcategories = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath("//*[@class='subcategory__title']")));
        logger.info("Количество категорий: " + subcategories.size());
        for(WebElement elem : subcategories){
            logger.info("-" + elem.getText());
        }

        //...проверить, что категорий больше пяти
        Assertions.assertTrue(subcategories.size()>5, "категорий меньше пяти");
        logger.info("Категорий больше пяти");
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
