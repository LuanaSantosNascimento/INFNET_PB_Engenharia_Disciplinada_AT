package org.banco.selenium.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    protected static WebDriver driver;

    @BeforeEach
    public void start() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=pt-BR");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("window-size=1920,1080");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "pt-BR,pt");
        options.setExperimentalOption("prefs", prefs);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }
}