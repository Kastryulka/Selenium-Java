import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.Map;
import java.util.Objects;

public class WebDriverFactory {
    private static Logger logger = LogManager.getLogger(WebDriverFactory.class);
    public static WebDriver getDriver(String browserName, String loadStrategy, String params, Map prefs)
    {
        //TODO выделить отдельные классы и интерфейсы
        switch (browserName)
        {
            // Создание драйвера для браузера Google Chrome
            case "chrome":
                WebDriverManager.chromedriver().setup();
                logger.info("Драйвер для браузера Google Chrome");
                ChromeOptions chromeOptions = new ChromeOptions();
                if(Objects.equals(params, "")){
                    //применение дефолтных настроек
                    chromeOptions.addArguments("--start-fullscreen");
                    chromeOptions.addArguments("--incognito");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    logger.info("Применены стандартные настройки");
                }
                else{
                    chromeOptions.addArguments(params);
                }
                chromeOptions.setPageLoadStrategy(PageLoadStrategy.fromString(loadStrategy));
                if(prefs.isEmpty()){
                    //отказ в уведомлениях и геолокации
                    prefs.put("profile.default_content_setting_values.notifications", 2);
                    prefs.put("profile.default_content_setting_values.geolocation", 2);
                    logger.info("Применены стандартные настройки prefs");
                }

                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                return new ChromeDriver(chromeOptions);
            // Создание драйвера для браузера Mozilla Firefox
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                logger.info("Драйвер для браузера Mozilla Firefox");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if(Objects.equals(params, "")) {
                    //применение дефолтных настроек
                    firefoxOptions.addArguments("--kiosk");
                    firefoxOptions.addArguments("-private");
                    logger.info("Применены стандартные настройки");
                }
                else{
                    firefoxOptions.addArguments(params.split(" "));
                }
                firefoxOptions.setPageLoadStrategy(PageLoadStrategy.fromString(loadStrategy));
                firefoxOptions.setProfile(new FirefoxProfile());
                if(prefs.isEmpty()){
                    //отказ в уведомлениях и геолокации
                    firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                    firefoxOptions.addPreference("dom.disable_beforeunload", true);
                    firefoxOptions.addPreference("geo.enabled", false);
                    logger.info("Применены стандартные настройки prefs");
                }
                firefoxOptions.setCapability("moz:firefoxOptions", firefoxOptions);
                return new FirefoxDriver(firefoxOptions);
            default:
                throw new RuntimeException("введено неверное название браузера");


        }
    }
}
