package com.jaliansystems.javadriver.examples.swing.ut;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;

import static org.junit.Assert.*;


public class LoginDialogTest {

    private LoginDialog login;
    private WebDriver driver;

    @Before
    public void setUp() {
        login = new LoginDialog() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess() {
            }

            @Override
            protected void onCancel() {
            }
        };
        SwingUtilities.invokeLater(() -> login.setVisible(true));
        driver = new JavaDriver(new JavaProfile());
    }

    @After
    public void tearDown() throws Exception {
        if (login != null)
            SwingUtilities.invokeAndWait(() -> login.dispose());
        if (driver != null)
            driver.quit();
    }

    @Test
    public void loginSuccess() {
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        user.sendKeys("bob");
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        pass.sendKeys("secret");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();
        assertTrue(login.isSucceeded());
        assertNotNull(login.getSize());
    }

    @Test
    public void loginCancel() {
        WebElement cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));
        cancelBtn.click();
        assertFalse(login.isSucceeded());
    }

    @Test
    public void loginInvalid() {
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        user.sendKeys("bob");
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        pass.sendKeys("wrong");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();
        driver.switchTo().window("Invalid Login");
        driver.findElement(By.cssSelector("button[text='OK']")).click();
        driver.switchTo().window("Login");
        assertEquals("", user.getText());
        assertEquals("", pass.getText());
    }

    @Test
    public void checkTooltipText() {
        // Check that all the text components (like text fields, password
        // fields, text areas) are associated
        // with a tooltip
        List<WebElement> textComponents = driver.findElements(By.className(JTextComponent.class.getName()));
        for (WebElement tc : textComponents) {
            assertNotEquals(null, tc.getAttribute("toolTipText"));
        }
    }
}
