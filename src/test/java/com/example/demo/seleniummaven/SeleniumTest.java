package com.example.demo.seleniummaven;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = SeleniumTest.Initializer.class)
public class SeleniumTest {

    @LocalServerPort
    int port;

    @Rule
    public BrowserWebDriverContainer chrome
            = new BrowserWebDriverContainer()
            .withCapabilities(new ChromeOptions())
            .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("./target/"));

    @Test
    public void whenNavigatedToPage_thenHeadingIsInThePage() {
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get("http://example.com");
        String heading = driver.findElement(By.xpath("/html/body/div/h1"))
                .getText();
        assertEquals("Example Domain", heading);
    }

    @Test
    public void greeting() {


        RemoteWebDriver driver = chrome.getWebDriver();

        driver.get("http://host.testcontainers.internal:" + port + "/greeting");

        String greeting = driver.findElement(By.xpath("/html/body/p"))
                .getText();

        assertEquals("Hello, World!", greeting);

        driver.get("http://host.testcontainers.internal:" + port + "/greeting?name=Hector");

        greeting = driver.findElement(By.xpath("/html/body/p"))
                .getText();

        assertEquals("Hello, Hector!", greeting);

    }


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.addApplicationListener((ApplicationListener<WebServerInitializedEvent>) event -> {
                Testcontainers.exposeHostPorts(event.getWebServer().getPort());
            });
        }
    }
}
