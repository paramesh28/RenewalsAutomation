package com.salesforce.automation.SalesforceEngageAutomation;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.soap.partner.PartnerConnection;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EngageDataCreation {
	private static final Logger LOG = LoggerFactory.getLogger(EngageDataCreation.class);
	final static private String TEST_DATA_ENGAGE_FIELDS_SETUP_FILE = "./testdata/TestData_Engage_Setup.xml";

protected  Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA_ENGAGE_FIELDS_SETUP_FILE);
	
	private final WebDriver driver;
	private final WebDriverWait wait;
	private final WebDriverWait wait1;
	private final UIUtilities uIUtilities;
	
	
	public EngageDataCreation(WebDriver driver, long wait) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, wait);
		this.wait1 = new WebDriverWait(driver,150);
		this.uIUtilities = new UIUtilities(driver, wait);
	}
	

    public String createLead(PartnerConnection connection, String LeadName) throws Exception{
	    
    	
	    Map<String,Object> createlead=dataMap.get("GlobalLead");
	    createlead.put("LastName", LeadName);
	    createlead.put("Status","Open");
	    createlead.put("Email", "pmarina@salesforce.com");
		createlead.put("Country", "GB");
		createlead.put("Email", "pmarina@salesforce.com");
		createlead.put("Email", "pmarina@salesforce.com");
	    String LeadIdCallNow = ApiUtilities.createObject(connection,"Lead", createlead);
		LOG.info("LeadId for Call Now : "+LeadIdCallNow);
		return LeadIdCallNow;
    }
    
    public String createLead_OptOutEmail(PartnerConnection connection, String LeadName) throws Exception{    
    	
	    Map<String,Object> createlead=dataMap.get("OptOutLead");
	    createlead.put("LastName", LeadName);
	    createlead.put("Status","Open");
	    createlead.put("Email","agudivada@salesforce.com");
	    createlead.put("HasOptedOutOfEmail", true);
	    String LeadIdCallNow = ApiUtilities.createObject(connection,"Lead", createlead);
		LOG.info("LeadId for Call Now : "+LeadIdCallNow);
		return LeadIdCallNow;
    }
    
    public String createLead_NoEmailId (PartnerConnection connection, String LeadName) throws Exception{    
    	
	    Map<String,Object> createlead=dataMap.get("NoEmailIdLead");
	    createlead.put("LastName", LeadName);
	    createlead.put("Status","Open");
	    String LeadIdCallNow = ApiUtilities.createObject(connection,"Lead", createlead);
		LOG.info("LeadId for Call Now : "+LeadIdCallNow);
		return LeadIdCallNow;
    }
    
    
    public String createContact(PartnerConnection connection, String ContactName) throws Exception{
	    String ContactId = null;
	    Map<String,Object> createcontact=dataMap.get("GlobalContact");
	    createcontact.put("LastName", ContactName);
	    createcontact.put("Email", "pmarinan@salesforce.com");
	    
	    ContactId = ApiUtilities.createObject(connection,"Contact", createcontact);
		LOG.info("ContactId : "+ContactId);
	    return ContactId;
}
    
}

