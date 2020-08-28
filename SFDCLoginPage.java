package com.sforce.cd.Refactor.page;

/**
 * Login page for sfdc overlaying LoadableComponent DP
 * @author vgandham
 */

import com.sforce.cd.Refactor.utils.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import static org.junit.Assert.fail;

public class SFDCLoginPage extends LoadableComponent<SFDCLoginPage> {

  private final WebDriver driver;
  private final LoadableComponent<?> parent;
  private final String username;
  private final String password;
  private final String loginEndPoint;
  private final WebDriverWait wait;
  private static final Logger LOG = LoggerFactory.getLogger(SFDCLoginPage.class);

  public SFDCLoginPage(WebDriver driver, LoadableComponent<?> parent, String loginEndPoint, String username, String password, long pageLoadWaitDuration) {
    this.driver = driver;
    this.parent = parent;
    this.username = username;
    this.password = password;
    this.loginEndPoint = loginEndPoint;
    this.wait = new WebDriverWait(driver, pageLoadWaitDuration);
  }

  @Override
  protected void load() {
	  
    if (parent !=null){
    	parent.get();
    }
    // Sign in
    driver.get(loginEndPoint);
    LOG.info("Page title is: " + driver.getTitle());
    
    //optional wait
    WebDriverUtils.waitAndFindByName("username", wait).sendKeys(username);
    driver.findElement(By.name("pw")).sendKeys(password);
    

    driver.findElement(By.name("Login")).click();

    //optional wait
    wait.until(new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver d) {
            return d.getTitle().toLowerCase().startsWith("salesforce");
        }
    });
    
    LOG.info("New page title is: " + driver.getTitle());
    LOG.info("Page URL is: " + driver.getCurrentUrl());
    
  }

  @Override
  protected void isLoaded() throws Error {
    // If you're signed in, you have the option of picking a different login.
    // Let's check for the presence of that.

    try {
    	Assert.assertEquals(driver.getTitle().contains("salesforce.com"), true);
        Assert.assertEquals(driver.getCurrentUrl().contains("salesforce.com/home/home.jsp"), true);
    } catch (NoSuchElementException e) {
      fail("Page load failed");
    }
  }
  
  
}