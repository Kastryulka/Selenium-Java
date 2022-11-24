import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstCaseTest {
    protected static WebDriver driver;
    private Logger logger = LogManager.getLogger(FirstCaseTest.class);
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
        logger.info("Драйвер стартовал!");
    }
    @Test
    public void testFunction(){
        //driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(60000));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(50000));
        driver.get("https://www.dns-shop.ru/");
        logger.info("Открыта страница DNS - " + "https://www.dns-shop.ru/");
        logger.info("Заголовок страницы - " + driver.getTitle());
        logger.info("Текущий URL - " + driver.getCurrentUrl());
        logger.info(String.format("Ширина окна: %d", driver.manage().window().getSize().getWidth()));
        logger.info(String.format("Высота окна: %d", driver.manage().window().getSize().getHeight()));

        //если появляется окно выбора города (может скрывать Бытовую технику), нажимаем на кнопку согласия
        String xpathConfirmCityBtn = "//button[contains(@class,'v-confirm-city__btn')]";
        try{
            driver.findElement(By.xpath(xpathConfirmCityBtn)).click();
            logger.info("Согласились с выбором города");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Перейти по ссылке Бытовая техника, проверить, что отображается текст
        driver.findElement(By.xpath(
                "//a[contains(@class ,'menu-desktop__root-title') and text()='Бытовая техника']"))
                .click();
        logger.info("Перешли в раздел Бытовая техника");
        WebElement tHouseholdAppliances = driver.findElement(By.xpath(
                "//*[@class='subcategory__page-title']"));
        Assertions.assertEquals("Бытовая техника", tHouseholdAppliances.getText(), "В заголовке не Бытовая техника");
        Assertions.assertTrue(tHouseholdAppliances.isDisplayed(), "Надпись \"Бытовая техника\" в заголовке не отображена");
        logger.info("Надпись \"Бытовая техника\" в заголовке отображена");

        //Перейти по ссылке Техника для кухни, проверить, что отображается текст,...
        driver.findElement(By.xpath(
                "//*[text()='Техника для кухни']//ancestor::a[contains(@class ,'ui-link')]"))
                .click();
        logger.info("Перешли в раздел Техника для кухни");
        WebElement tKitchenAppliances = driver.findElement(By.xpath("//*[@class='subcategory__page-title']"));
        Assertions.assertEquals("Техника для кухни", tKitchenAppliances.getText(), "В заголовке не Техника для кухни");
        Assertions.assertTrue(tKitchenAppliances.isDisplayed(), "Надпись \"Техника для кухни\" в заголовке не отображена");
        logger.info("Надпись \"Техника для кухни\" в заголовке отображена");

        //... проверить, что отображается ссылка Собрать свою кухню,...
        String xpathConfBtn = "//a[contains(@class ,'configurator-links-block__links-link')]";
        WebElement ConfBtn = driver.findElement(By.xpath(xpathConfBtn));
        Assertions.assertTrue(ConfBtn.isDisplayed(), "ссылка \"Собрать свою кухню\" не отображена");
        logger.info("Ссылка \"Собрать свою кухню\" отображена");

        //... вывести в логи названия категорий, ...
        List<WebElement> subcategories = driver.findElements(By.xpath(
                "//*[@class='subcategory__title']"));
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
