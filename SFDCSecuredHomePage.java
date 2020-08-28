package com.sforce.cd.Refactor.page;

/**
 * sfdc home page - overlaying LoadableComponent DP
 * @author vgandham
 */

import com.sforce.cd.Refactor.utils.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import static junit.framework.Assert.assertTrue;

public class SFDCSecuredHomePage extends LoadableComponent<SFDCSecuredHomePage> {
	
  // By default the PageFactory will locate elements with the same name or id
  // as the field. Since the summary element has a name attribute of "summary"
  // we don't need any additional annotations.
  private WebElement phSearchInput;
  
  private final WebDriver driver;
  private final LoadableComponent<?> parent;
  private final WebDriverWait wait;
  private static final Logger LOG = LoggerFactory.getLogger(SFDCSecuredHomePage.class);

  public SFDCSecuredHomePage(WebDriver driver, LoadableComponent<?> parent,  long pageLoadWaitDuration) {
    this.driver = driver;
    this.parent = parent;
    this.wait = new WebDriverWait(driver, pageLoadWaitDuration);;
    
    // This call sets the WebElement fields.
    PageFactory.initElements(driver, this);
  }
 
  @Override
  protected void load() {
    if (parent != null) {
    	parent.get();
    }
    this.get();
  }

  @Override
  protected void isLoaded() throws Error {
    String title = driver.getTitle().toLowerCase();
    assertTrue("Page load failed: " + title, title.startsWith("salesforce"));
  }
  
  public void universalSearchTest(String searchKeyWord) {
	  WebDriverUtils.clearAndType(phSearchInput, searchKeyWord);
    phSearchInput.submit();
    wait.until(ExpectedConditions.titleContains("Search Results"));

  	String firstHit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"User_body\"]/table/tbody/tr[2]/th/div/div"))).getAttribute("title");
  	LOG.info("Name of first hit is " + firstHit);
  	//driver.close();
  	Assert.assertTrue(firstHit.toLowerCase().contains(searchKeyWord));
  }
  
  public void chatterPostTest(){
	  
	//If on home page, just use chatter input box there.
  	if(!driver.getCurrentUrl().toLowerCase().contains("home"))
  		//wait until chatter tab has loaded and then click on it.
  		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Chatter_Tab"))).click();
  	
      //focus on "what are you working on?" box and enter text
      WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("publishereditablearea")));//driver.findElement(By.name("publisherprompttext"));
      commentBox.sendKeys("Fixture 2.0 Rocks!");

      //Click on the share button.
      WebElement shareBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("publishersharebutton")));
      LOG.info(shareBtn.getAttribute("value"));
      shareBtn.click();

      
      LOG.info("New page title is: " + driver.getTitle());
      LOG.info("Page URL is: " + driver.getCurrentUrl());

      driver.close();
  }
  
  public void createAccountTest(){
	  
	  //find and click on accounts tab
	  WebDriverUtils.waitAndFindByID("Account_Tab", wait).click();
	  LOG.info("TAB FOUND?");
      //find and click new
	  WebDriverUtils.waitAndFindByName("new", wait).click();
      //find and fill Account Name
	  WebDriverUtils.clearAndType(WebDriverUtils.waitAndFindByID("acc2", wait),("wow.com"));
      //click save
      driver.findElement(By.name("save")).click();
      wait.until(new ExpectedCondition<Boolean>() {
                  public Boolean apply(WebDriver d) {
                      return d.getTitle().toLowerCase().startsWith("account: wow.com");
                  }
              });
      String title = driver.getTitle();
      LOG.info("New page title is: " + title);
      driver.close();
      Assert.assertEquals(title.contains("wow.com"), true);
      LOG.info("New wow.com account created successfully");
	      
 }
  
}