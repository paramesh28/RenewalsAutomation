package com.salesforce.automation.beans;

public class OpportunityProductSummaryBean {
    String sfbase__PriorAnnualOrderValue__c;
    String forecastedOTV;
    String forecastedTotalOrderValue;
    String priorMonthlyOrderValue;
    String priorOTV;
    String priorTotalOrderValue;

    public String getSfbase__PriorAnnualOrderValue__c() {
        return sfbase__PriorAnnualOrderValue__c;
    }

    public void setSfbase__PriorAnnualOrderValue__c(String sfbase__PriorAnnualOrderValue__c) {
        this.sfbase__PriorAnnualOrderValue__c = sfbase__PriorAnnualOrderValue__c;
    }

    public String getForecastedOTV() {
        return forecastedOTV;
    }

    public void setForecastedOTV(String forecastedOTV) {
        this.forecastedOTV = forecastedOTV;
    }

    public String getForecastedTotalOrderValue() {
        return forecastedTotalOrderValue;
    }

    public void setForecastedTotalOrderValue(String forecastedTotalOrderValue) {
        this.forecastedTotalOrderValue = forecastedTotalOrderValue;
    }

    public String getPriorMonthlyOrderValue() {
        return priorMonthlyOrderValue;
    }

    public void setPriorMonthlyOrderValue(String priorMonthlyOrderValue) {
        this.priorMonthlyOrderValue = priorMonthlyOrderValue;
    }

    public String getPriorOTV() {
        return priorOTV;
    }

    public void setPriorOTV(String priorOTV) {
        this.priorOTV = priorOTV;
    }

    public String getPriorTotalOrderValue() {
        return priorTotalOrderValue;
    }

    public void setPriorTotalOrderValue(String priorTotalOrderValue) {
        this.priorTotalOrderValue = priorTotalOrderValue;
    }
}
