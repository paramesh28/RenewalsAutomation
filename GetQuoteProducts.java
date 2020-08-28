package com.salesforce.automation.commonUI;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class GetQuoteProducts {
	GetXmlProducts P1;
	ArrayList<GetXmlProducts> allProducts =new ArrayList<GetXmlProducts>();

	public ArrayList<GetXmlProducts> getProducts(String RT, String isMonopoly, String isramp){

		try {
			File fXmlFile = new File("testdata/NewQuoteProducts.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList ProductsList = doc.getElementsByTagName("Products");

			Node ProductNode;
			for (int i = 0; i < ProductsList.getLength(); i++) {
				Node ProductsNode = ProductsList.item(i);				
				Element XmlProducts = (Element) ProductsNode;
				NodeList ProductList = XmlProducts.getElementsByTagName("Product");

				if(XmlProducts.getAttribute("Recordtype").equals(RT)){					
					for (int j = 0; j < ProductList.getLength(); j++) {
						ProductNode = ProductList.item(j);
						Element XmlProduct = (Element) ProductNode;
						if(XmlProduct.getAttribute("isMonopolyProduct").equals(isMonopoly) 
								&& XmlProduct.getAttribute("isramp").equals(isramp)){
							P1 = null;
							P1 = new GetXmlProducts(XmlProducts.getAttribute("Recordtype"),
									XmlProducts.getAttribute("Term"),
									XmlProducts.getAttribute("PriceList"),
									XmlProduct.getAttribute("Qty"),
									XmlProduct.getAttribute("isMonopolyProduct"),
									XmlProduct.getAttribute("isramp"),
									XmlProduct.getAttribute("Name"));
							allProducts.add(P1);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return allProducts;
	}
}