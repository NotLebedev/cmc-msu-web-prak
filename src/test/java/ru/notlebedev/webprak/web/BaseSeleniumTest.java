package ru.notlebedev.webprak.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations= "classpath:application.properties")
public class BaseSeleniumTest {
    @LocalServerPort
    private int port;
    private WebDriver driver;

    private String base;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.setProperty("webdriver.firefox.driver", "geckodriver.exe");

//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

        driver = new FirefoxDriver();
        this.base = "http://localhost:" + port;
    }

    @Test
    public void testTest() {
        driver.get(base);
        WebElement element = driver.findElement(By.tagName("h1"));
        assertEquals("Добро пожаловать.", element.getText());
    }
}
