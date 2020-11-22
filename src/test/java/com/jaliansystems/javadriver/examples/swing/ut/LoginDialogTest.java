package com.jaliansystems.javadriver.examples.swing.ut;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

    private static LoginDialog login;
    private static WebDriver driver;

    @BeforeClass
    public static void beforeTestingBegins() {
        System.out.println("beforeTestingBegins");
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

    @Before
    public void beforeEachTest() {
        System.out.println("beforeEachTest");
        login.setVisible(true);
    }

    @AfterClass
    public static void afterTestingHasFinished() throws Exception {
        System.out.println("afterTestingHasFinished");
        if (login != null) {
            SwingUtilities.invokeAndWait(() -> login.dispose());
        }
        if (driver != null)
            driver.quit();
    }

    @Test
    public void login_CorrectCredentials_LoginSucceeds() {
        System.out.println("login_CorrectCredentials_LoginSucceeds");

        // arrange
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        user.sendKeys("bob");
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        pass.sendKeys("secret");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));

        // act
        loginBtn.click();

        //assert
        assertTrue(login.isSucceeded());
        assertNotNull(login.getSize());
    }

    @Test
    public void login_Canceled_LoginFails() {
        System.out.println("login_Canceled_LoginFails");

        //arrange
        WebElement cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));

        //act
        cancelBtn.click();

        // assert
        assertFalse(login.isSucceeded());
    }

    @Test
    public void login_InvalidCredentials_LoginFails() {
        System.out.println("login_InvalidCredentials_LoginFails");

        // arrange
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        user.sendKeys("bob");
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        pass.sendKeys("wrong");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));

        // act
        loginBtn.click();

        // cleanup
        driver.switchTo().window("Invalid Login");
        driver.findElement(By.cssSelector("button[text='OK']")).click();
        driver.switchTo().window("Login");

        //assert
        assertEquals("", user.getText());
        assertEquals("", pass.getText());
    }

    @Test
    public void checkTooltipText_TooltipTextExists() {
        // Check that all the text components (like text fields, password
        // fields, text areas) are associated
        // with a tooltip
        System.out.println("checkTooltipText_TooltipTextExists");

        // arrange
        List<WebElement> textComponents = driver.findElements(By.className(JTextComponent.class.getName()));

        // assert
        for (WebElement tc : textComponents) {
            assertNotEquals(null, tc.getAttribute("toolTipText"));
            assertNotEquals("", tc.getAttribute("toolTipText"));
        }
    }
}
