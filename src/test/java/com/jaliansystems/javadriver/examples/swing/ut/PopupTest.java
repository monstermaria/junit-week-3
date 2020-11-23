package com.jaliansystems.javadriver.examples.swing.ut;

import static org.junit.Assert.*;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;

import java.lang.reflect.InvocationTargetException;

public class PopupTest {

    private static LoginDialog login;
    private static WebDriver driver;

    private WebDriverWait wait;

    @Before
    public void beforeEachTest() {
        System.out.println("beforeEachTest");

        // create the test object
        login = new LoginDialog();

        SwingUtilities.invokeLater(() -> login.setVisible(true));

        // default values for a new JavaProfile is correct for this application
        driver = new JavaDriver(new JavaProfile());
    }

    @After
    public void afterEachTest() throws InvocationTargetException, InterruptedException {
        System.out.println("afterEachTest");

        if (login != null) {
            SwingUtilities.invokeAndWait(() -> login.dispose());
        }

        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void onSuccess_HasBeenTriggered_InformationPopupIsShown() {
        System.out.println("onSuccess_HasBeenTriggered_InformationPopupIsShown");

        // arrange
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));

        user.sendKeys("bob");
        pass.sendKeys("secret");

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();

        // act
        driver.switchTo().window("Login Success");

        // find relevant elements
        WebElement message = driver.findElement(By.cssSelector("label[text='Hi bob! You have successfully logged in.']"));
        WebElement button = driver.findElement(By.cssSelector("button[text='OK']"));

        // assert
        assertNotNull(message);
        assertNotNull(button);
    }

    @Test
    public void onCancel_HasBeenTriggered_InformationPopupIsShown() {
        System.out.println("onCancel_HasBeenTriggered_InformationPopupIsShown");

        // arrange
        WebElement cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));

        cancelBtn.click();

        // act
        driver.switchTo().window("Login Cancel");

        // find relevant elements
        WebElement message = driver.findElement(By.cssSelector("label[text='Sorry to see you going.']"));
        WebElement button = driver.findElement(By.cssSelector("button[text='OK']"));

        // assert
        assertNotNull(message);
        assertNotNull(button);
    }

    @Test
    public void onInvalidCredentials_HasBeenTriggered_ErrorPopupIsShown() {
        System.out.println("onInvalidCredentials_HasBeenTriggered_ErrorPopupIsShown");

        // arrange
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));

        user.sendKeys("bob");
        pass.sendKeys("wrong");

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();

        // act
        driver.switchTo().window("Invalid Login");

        // find relevant elements
        WebElement message = driver.findElement(By.cssSelector("label[text='Invalid username or password']"));
        WebElement button = driver.findElement(By.cssSelector("button[text='OK']"));

        // assert
        assertNotNull(message);
        assertNotNull(button);
    }
}
