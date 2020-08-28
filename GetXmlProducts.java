package com.salesforce.automation.commonUI;

public class GetXmlProducts {

	String Qty;  
	String Term; 
	String PriceList;  
	String RT; 
	String isMonopolyProduct;
	String isramp;
	String Name;

	GetXmlProducts (String RT, String Term, String PriceList, String Qty, String isMonopolyProduct, String isramp, String Name){ 
		this.RT=RT;
		this.Term=Term;
		this.PriceList=PriceList;		 
		this.Qty=Qty; 
		this.isMonopolyProduct=isMonopolyProduct;
		this.isramp=isramp;
		this.Name=Name;		 
	}  
}