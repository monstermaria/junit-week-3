package com.jaliansystems.javadriver.examples.swing.ut;

import java.util.List;

import javax.swing.*;
import javax.swing.text.JTextComponent;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;


public class LoginDialogTest {

    private static LoginDialog login;
    private static WebDriver driver;

    private final WebDriverWait wait = new WebDriverWait(driver, 10);

    @BeforeClass
    public static void beforeTestingBegins() {
        System.out.println("beforeTestingBegins");

        // create an object of an anonymous class that extends LoginDialog
        // some methods are overridden, and must be tested separately
        login = new LoginDialog() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess() {
            }

            @Override
            protected void onCancel() {
            }

            @Override
            protected void onInvalidCredentials() {
            }
        };

        SwingUtilities.invokeLater(() -> login.setVisible(true));

        // default values for a new JavaProfile is correct for this application
        driver = new JavaDriver(new JavaProfile());
    }

    @Before
    public void beforeEachTest() {
        System.out.println("beforeEachTest");

        WebElement userTextField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.cssSelector("password-field"));
        WebElement loginButton = driver.findElement(By.name("login_button"));

        // reset login succeeded
        if (login.isSucceeded()) {
            System.out.println("Login is already accepted, resetting...");

            // swing app must be visible to be able to interact with elements
            login.setVisible(true);

            // set incorrect user credentials and try to log in
            userTextField.sendKeys("b");
            passwordField.sendKeys("s");
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginButton.click();

            // make sure that login succeeded really has been reset
            assertFalse(login.isSucceeded());
        }

        // make the login window visible
        login.setVisible(true);

        // clear input fields
        userTextField.clear();
        passwordField.clear();
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
    public void loginDialogCreated_LoginDialogExistsAndHasAllElements() {
        System.out.println("loginDialogCreated_LoginDialogExistsAndHasAllElements");

        // arrange
        WebElement userLabel = driver.findElement(By.cssSelector("label[text='Username: ']"));
        WebElement userTextField = driver.findElement(By.name("username"));
        WebElement passwordLabel = driver.findElement(By.cssSelector("label[text='Password: ']"));
        WebElement passwordField = driver.findElement(By.cssSelector("password-field"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[text='Login']"));
        WebElement cancelButton = driver.findElement(By.cssSelector("button[text='Cancel']"));

        // assert
        assertNotNull(login);
        assertEquals(JFrame.EXIT_ON_CLOSE, login.getDefaultCloseOperation());
        assertNotNull(userLabel);
        assertNotNull(userTextField);
        assertEquals("Enter user name", userTextField.getAttribute("toolTipText"));
        assertNotNull(passwordLabel);
        assertNotNull(passwordField);
        assertEquals("Enter password", passwordField.getAttribute("toolTipText"));
        assertNotNull(loginButton);
        assertNotNull(loginButton.getAttribute("actionListener"));
        assertNotNull(cancelButton);
        assertNotNull(cancelButton.getAttribute("actionListener"));
    }

    @Test
    public void getUserName_UserNameInput_UserNameReturned() {
        System.out.println("getUserName_UserNameInput_UserNameReturned");

        // arrange
        WebElement userTextField = driver.findElement(By.name("username"));
        userTextField.sendKeys("bob");

        // act
        String user = login.getUsername();

        // assert
        assertEquals("bob", user);
    }

    @Test
    public void getUserName_UserNameWithWhitespace_WhitespaceRemoved() {
        System.out.println("getUserName_UserNameWithWhitespace_WhitespaceRemoved");

        // arrange
        WebElement userTextField = driver.findElement(By.name("username"));
        userTextField.sendKeys(" bob ");

        // act
        String user = login.getUsername();

        // assert
        assertEquals("bob", user);
    }

    @Test
    public void getPassword_PasswordInput_PasswordReturned() {
        System.out.println("getPassword_PasswordInput_PasswordReturned");

        // arrange
        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.sendKeys("secret");

        // act
        String password = login.getPassword();

        // assert
        assertEquals("secret", password);
    }

    @Test
    public void authenticate_CorrectCredentials_True() {
        System.out.println("authenticate_CorrectCredentials_LoginSucceeds");

        // arrange
        // act
        boolean result = login.authenticate("bob", "secret");

        // assert
        assertTrue(result);
    }

    @Test
    public void authenticate_IncorrectCredentials_False() {
        System.out.println("authenticate_IncorrectCredentials_False");

        // arrange
        // act
        boolean result = login.authenticate("bo", "secret");

        // assert
        assertFalse(result);
    }

    @Test
    public void login_CorrectCredentials_LoginSucceeds() {
        System.out.println("login_CorrectCredentials_LoginSucceeds");

        // arrange
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));

        user.sendKeys("bob");
        pass.sendKeys("secret");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));

        // act
        loginBtn.click();

        //assert
        assertTrue(login.isSucceeded());
    }

    @Test
    public void login_InvalidCredentials_LoginFails() {
        System.out.println("login_InvalidCredentials_LoginFails");

        // arrange
        WebElement user = driver.findElement(By.cssSelector("text-field"));
        WebElement pass = driver.findElement(By.cssSelector("password-field"));
        WebElement loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));

        user.sendKeys("bob");
        pass.sendKeys("wrong");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));

        // act
        loginBtn.click();

        //assert
        assertFalse(login.isSucceeded());
        assertEquals("", user.getText());
        assertEquals("", pass.getText());
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

    /*
    this test is not necessary anymore, as more specific testing of tool tips are made in an other test
     */
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
