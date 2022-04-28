package ru.notlebedev.webprak.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfigurator {
    @Bean
    public WebDriver webDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.setProperty("webdriver.firefox.driver", "geckodriver.exe");

        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(false);

        return new FirefoxDriver(options);
    }
}
