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


import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondCaseTest {
    protected static WebDriver driver;
    private Logger logger = LogManager.getLogger(FirstCaseTest.class);
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

        driver = WebDriverFactory.getDriver(env.toLowerCase(),loadStrategy.toUpperCase(), parameters.toLowerCase(),prefs);
        logger.info("Драйвер стартовал!");
    }
    @Test
    public void testFunction(){
        //driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(60000));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(50000));
        ArrayList<String> testRefs = new ArrayList<String>();
        testRefs.add("Техника для кухни");
        testRefs.add("Техника для дома");
        testRefs.add("Красота и здоровье");

        driver.get("https://www.dns-shop.ru/");
        logger.info("Открыта страница DNS - " + "https://www.dns-shop.ru/");
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

        //Навести курсор на ссылку Бытовая техника проверить, что отображаются ссылки
        String xpathAppliances ="//a[@class='ui-link menu-desktop__root-title' and text()='Бытовая техника']";
        WebElement element = driver.findElement(By.xpath(xpathAppliances));
        Actions actions = new Actions(driver);
        actions
                .moveToElement(element)
                .perform();
        String xpathSublist = ".//ancestor::div[@class='menu-desktop__root']//div[contains(@class ,'menu-desktop__submenu_top')]/a";
        List<WebElement> elements = element.findElements(By.xpath(xpathSublist));
        Assertions.assertEquals(elements.size(),testRefs.size(), "ссылок больше, чем должно быть");
        for(WebElement elem : elements){
            Assertions.assertTrue(testRefs.contains(elem.getText()), "элемент отсутствует среди ссылок");
            Assertions.assertTrue(elem.isDisplayed(), "не отображается элемент в разделе Бытовая техника");
        }
        logger.info("Необходимые ссылки отображаются");

        //Навестись на приготовление пищи... проверить количество ссылок в подменю
        String xpathCooking = "//a[@class='ui-link menu-desktop__second-level' and text()='Приготовление пищи']";
        element = driver.findElement(By.xpath(xpathCooking));
        actions.moveToElement(element).perform();
        elements = element.findElements(By.xpath("./span[@class='menu-desktop__popup']/a"));
        logger.info("Открыто подменю Приготовление пищи");
        logger.info("Количество элементов подменю: " + elements.size());
        //... проверить, что ссылок больше пяти
        Assertions.assertTrue(elements.size()>5, "приготовление пищи меньше пяти");

        //Навестись и перейти в плиты, ...
        String xpathStoves ="./span[@class='menu-desktop__popup']/a[contains(text(),'Плиты')]";
        actions
                .moveToElement(element.findElement(By.xpath(xpathStoves)))
                .click()
                .perform();
        logger.info("Перешли в раздел Плиты");
        //...перейти в электрические плиты...
        driver.findElement(By.xpath("//a[@class='subcategory__item ui-link ui-link_blue']//span[text()='Плиты электрические']")).click();
        logger.info("Перешли в раздел Плиты электрические");
        //... проверить, что в заголовке количество товаров больше ста
        element = driver.findElement(By.xpath("//span[@class='products-count']"));
        int value = Integer.parseInt(element.getText().replaceAll("[^0-9]", ""));
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
