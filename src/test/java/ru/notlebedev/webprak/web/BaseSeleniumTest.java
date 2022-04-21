package ru.notlebedev.webprak.web;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application.properties")
public class BaseSeleniumTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebDriver driver;

    @Test
    public void testTest() {
        driver.get("localhost:" + port + "/");
        WebElement element = driver.findElement(By.tagName("h1"));
        assertEquals("Добро пожаловать.", element.getText());
    }
}
