package com.salesforce.automation.commonElements;

public class QuotePageElements {


	public  static String Create_Quote_Button_XPATH = null;
	public  static String Quote_Term_Text_XPATH = null;
	public  static String Quote_PriceList_Select_DropDown_XPATH=null;
	public  static String Quote_BillingCountry_Select_DropDown_XPATH=null;
	public  static String Quote_Product_Select_Button_XPATH =null;
	public  static String Product_Selection_SearchProducts_TextBox_XPATH =null;
	public  static String Product_Selection_Pricing_Button_XPATH =null;
	public  static String Pricing_Update_Button_XPATH =null;
	public  static String Pricing_Update_Button_UpdateCart_Spinner_XPATH =null;
	public static String Pricing_Billing_info_Button_XPATH=null;
	public  static String Pricing_BillingInfo_Button_UpdateCart_Spinner_XPATH =null;
	public static String Billing_Info_Contact_Text_Box_XPATH =null;
	public static String Billing_Info_Contact_Email_Text_Box_XPATH=null;
	public static String Billing_Info_Contact_Phone_Text_Box_XPATH =null;
	public static String Billing_Info_Contact_Search_Icon_Buttong_XPATH=null;
	public static String Contact_lookup_Window_SearchFrame_ID=null;
	public static String Contact_Lookup_Window_Search_Text_Box_XPATH=null;
	public static String Contact_Lookup_Window_Go_Button_XPATH =null;
	public static String Contact_Lookup_Search_lookup_Frame_ID=null;
	public static String Billing_Info_Special_Terms_Button_XPATH = null;
	public static String Special_Term_Quote_Summary_Button_XPATH=null;
	public static String Quote_Header_Complete_Button_XPATH=null;
	public static String Product_Selection_Suggested_header_XPATH=null;
	public static String Product_Selection_Suggested_ProductList_XPATH=null;
	public static String Product_Selection_Suggested_Products_Close_Button_XPATH=null;
	public static String Product_Selection_Suggested_Products_Error_XPATH=null;
	public static String Product_Selection_Suggested_Products_Error_GOTOPricing_Button_XPATH=null;
	public static String Quote_HeaderName_Xpath = null;
	public static String OneActionsDropDown_Xpath = null;
	public static String Quote_Details_Tab_Xpath = null;


	public QuotePageElements() {
		this.Create_Quote_Button_XPATH =null;
		this.Quote_Term_Text_XPATH =null;
		this.Quote_PriceList_Select_DropDown_XPATH =null;
		this.Quote_BillingCountry_Select_DropDown_XPATH=null;
		this.Quote_Product_Select_Button_XPATH =null;
		this.Product_Selection_SearchProducts_TextBox_XPATH =null;
		this.Product_Selection_Pricing_Button_XPATH =null;
		this.Pricing_Update_Button_XPATH=null;
		this.Pricing_Update_Button_UpdateCart_Spinner_XPATH =null;
		this.Pricing_Billing_info_Button_XPATH=null;
		this.Pricing_BillingInfo_Button_UpdateCart_Spinner_XPATH=null;
		this.Billing_Info_Contact_Text_Box_XPATH=null;
		this.Billing_Info_Contact_Email_Text_Box_XPATH=null;
		this.Billing_Info_Contact_Phone_Text_Box_XPATH=null;
		this.Billing_Info_Contact_Search_Icon_Buttong_XPATH=null;
		this.Contact_lookup_Window_SearchFrame_ID=null;
		this.Contact_Lookup_Window_Search_Text_Box_XPATH =null;
		this.Contact_Lookup_Window_Go_Button_XPATH =null;
		this.Contact_Lookup_Search_lookup_Frame_ID=null;
		this.Billing_Info_Special_Terms_Button_XPATH=null;
		this.Special_Term_Quote_Summary_Button_XPATH=null;
		this.Quote_Header_Complete_Button_XPATH=null;
		this.Product_Selection_Suggested_header_XPATH=null;
		this.Product_Selection_Suggested_ProductList_XPATH=null;
		this.Product_Selection_Suggested_Products_Close_Button_XPATH=null;
		this.Product_Selection_Suggested_Products_Error_XPATH=null;
		this.Product_Selection_Suggested_Products_Error_GOTOPricing_Button_XPATH=null;
		this.Quote_HeaderName_Xpath = null;
		this.OneActionsDropDown_Xpath = null;
		this.Quote_Details_Tab_Xpath = null;
	}


	public void setLightningElements(){
		Create_Quote_Button_XPATH                                            = "//li/a[@title='Create Quote']";
		Quote_Term_Text_XPATH                                                = "//label[text()='Term']//parent::div/div/input";
		Quote_PriceList_Select_DropDown_XPATH                                = "//label[text()='Price List']//parent::div/div/div/select";
		Quote_BillingCountry_Select_DropDown_XPATH                           = "//label[text()='Billing Country']//parent::div/div/div/select";
		Quote_Product_Select_Button_XPATH                                    = "//button[@id='cpq-qh-product-selection']";
		Product_Selection_SearchProducts_TextBox_XPATH                       = "//input[@name='searchProducts']";
		Product_Selection_Pricing_Button_XPATH                               = "//button[@class='cpq-autoid-ps-pricing slds-button slds-button--brand']";
		Pricing_Update_Button_XPATH                                          = "//button[text()='Update']";
		Pricing_Billing_info_Button_XPATH                                    = "//button[@class='cpqautoid-billinginfo-btn slds-button slds-button--brand']";
		Billing_Info_Contact_Text_Box_XPATH                                  = "//label[contains(text(),'Billing Contact')]//parent::th//following-sibling::td[1]//span/input";
		Billing_Info_Contact_Email_Text_Box_XPATH                            = "//label[contains(text(),'Billing Email')]//parent::th//following-sibling::td[1]/span/span/div/input";
		Billing_Info_Contact_Phone_Text_Box_XPATH                            = "//label[contains(text(),'Billing Phone')]//parent::th//following-sibling::td[1]/span/span/div/input";
		Billing_Info_Contact_Search_Icon_Buttong_XPATH                       = "//Img[@title='Billing Contact Lookup (New Window)']";
		Contact_lookup_Window_SearchFrame_ID                                 = "searchFrame";
		Contact_Lookup_Window_Search_Text_Box_XPATH                          = "//input[@name='lksrch']";
		Contact_Lookup_Window_Go_Button_XPATH                                = "//input[@title='Go!']";
		Contact_Lookup_Search_lookup_Frame_ID                                = "resultsFrame";
		Billing_Info_Special_Terms_Button_XPATH                              =  "//input[@id='SfdcUXEditSpecialTerms']";
//		 Special_Term_Quote_Summary_Button_XPATH                              =  "//input[@id='SfdcUXQuoteSummary']";
		//Special_Term_Quote_Summary_Button_XPATH                              =  "//input[@value='Quote Summary']";
		Special_Term_Quote_Summary_Button_XPATH                              =  "//button[text()='Quote Summary']";
		Quote_Header_Complete_Button_XPATH                                   =  "//div[@class='slds-grid slds-grid--vertical slds-col  cCpqBillingInfo']//button[text()='Complete']";//"//button[text()='Complete']";
		Product_Selection_Suggested_header_XPATH                             =  "//h2[@id='suggestedProductsHeader']//following::table/tbody/tr/td/div";
		Product_Selection_Suggested_ProductList_XPATH                        =  "//h2[@id='suggestedProductsHeader']//following::div[@class='slds-modal__content slds-p-around--medium']//table/tbody/tr/td/div";
		Product_Selection_Suggested_Products_Close_Button_XPATH              = "//h2[@id='suggestedProductsHeader']//parent::div/button";
		Product_Selection_Suggested_Products_Error_XPATH                     = "//button[text()='Stay Here and Fix']/preceding::div/div/table/thead/tr[@class='slds-theme--error']/th/div[contains(text(),Error)]";
		Product_Selection_Suggested_Products_Error_GOTOPricing_Button_XPATH  = "//button[text()='Go To Pricing']";
		Pricing_Update_Button_UpdateCart_Spinner_XPATH                       = "//div[@class='slds-spinner_container slds-hide cSparta_Spinner']";
		Pricing_BillingInfo_Button_UpdateCart_Spinner_XPATH                  = "//div[@class='message-container slds-p-top--large']/following::span[text()='Pricing Cart...']";
		Quote_HeaderName_Xpath												  = "";
		OneActionsDropDown_Xpath											  = "//li[@data-aura-class='oneActionsDropDown']//a";
		Quote_Details_Tab_Xpath 				                              = "//a[@class='tabHeader']/span[@class='title' and text()='Details']";
	}

	public void setClassisElements() {

		Create_Quote_Button_XPATH = "//input[@value='Create Quote']";
		Quote_HeaderName_Xpath    = "";
	}
}