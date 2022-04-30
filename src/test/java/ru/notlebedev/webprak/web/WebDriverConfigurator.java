package ru.notlebedev.webprak.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfigurator {
    @Value("${test_driver_firefox:geckodriver}")
    private String geckodriver;
    @Value("${test_driver_chrome:chromedriver}")
    private String chromedriver;
    @Value("${test_driver:geckodriver}")
    private String driver;

    @Bean
    public WebDriver webDriver() {
        System.setProperty("webdriver.chrome.driver", chromedriver);
        System.setProperty("webdriver.firefox.driver", geckodriver);

        switch (driver) {
            case "geckodriver": {
                FirefoxOptions options = new FirefoxOptions();
                options.setHeadless(false);
                return new FirefoxDriver(options);
            }
            case "chromedriver": {
                ChromeOptions options = new ChromeOptions();
                options.setHeadless(true);
                return new ChromeDriver(options);
            }
            default:
                throw new IllegalStateException("Unknown web driver type");
        }
    }
}
