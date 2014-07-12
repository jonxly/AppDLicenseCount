/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;

import org.appdynamics.appdrestapi.RESTAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.appdynamics.appdrestapi.data.*;
import org.appdynamics.appdrestapi.resources.s;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author soloink
 */
public class CustomerLicenseCount extends LicenseCount{
    private static Logger logger=Logger.getLogger(CustomerLicenseCount.class.getName());
    
    private int totalCount=0;
    private String name;
    private HashMap<Integer,ApplicationLicenseCount> applications=new HashMap<Integer,ApplicationLicenseCount>();
    private ArrayList<CustomerLicenseRange> customerRangeValues=new ArrayList<CustomerLicenseRange>();
    private CustomerLicenseRange totalRangeValue;
    
    private ArrayList<org.appdynamics.appdrestapi.util.TimeRange> timeRanges=new ArrayList<org.appdynamics.appdrestapi.util.TimeRange>();
    private org.appdynamics.appdrestapi.util.TimeRange totalRange;
    
    public CustomerLicenseCount(){super();}
    
    public CustomerLicenseCount(String name){super();this.name=name;}
    
    
    public void addApplication(ApplicationLicenseCount app){
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("Adding application ").append(app.getApplicationName()).toString());
        applications.put(app.getApplicationId(),app);
        //if(s.debugLevel > 0) System.out.println("\tAdding " + app.getApplicationName() + " id " +app.getApplicationId()+" size " + applications.size());
    }
    
    /*
     *  This function will start the population of the nodes and tiers across the app.
     * 
     */
    public void populateApplications(RESTAccess access,int interval){
        
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("Creating time range for interval ").append(interval).toString());
        // Should we make the total timer range an avg of usage?
        timeRanges=getTimeRanges(interval);
        totalRange=getTimeRange(interval);
        
        if(s.debugLevel >= 2){
            StringBuilder msg1 = new StringBuilder();
            msg1.append("\nThe range is ").append(totalRange.toString());
            for(org.appdynamics.appdrestapi.util.TimeRange tr: timeRanges) msg1.append("\nThe ranges is ").append(tr.toString());
            logger.log(Level.INFO,msg1.toString());
        }
        
        totalRangeValue = new CustomerLicenseRange("Custumer Total");
        totalRangeValue.setStart(totalRange.getStart());
        totalRangeValue.setEnd(totalRange.getEnd());
        
        for(ApplicationLicenseCount app: applications.values()){
            logger.log(Level.INFO,new StringBuilder().append("\tPopulating application ").append(app.getApplicationName()).toString());
            app.populateLicense(access.getNodesForApplication(app.getApplicationId()), access, timeRanges,totalRange);
        }
        /*
        
        */
    }
    

    
    public void countTierLicenses(){
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("Initiating count of licenses.").toString());
        //This is going to count the licenses
        for(ApplicationLicenseCount tCount: applications.values()){
            tCount.countTierLicenses(timeRanges);
        }
        
        for(int i=0; i < timeRanges.size(); i++){
            CustomerLicenseRange aRange = new CustomerLicenseRange();
            aRange.setStart(timeRanges.get(i).getStart());
            aRange.setEnd(timeRanges.get(i).getEnd());
            aRange.setName(aRange.createName());
            
            for(ApplicationLicenseCount tCount:applications.values()){
                TierLicenseRange tRange= tCount.getAppLicenseRange().get(i);
                aRange.iisCount+=tRange.getIisCount();
                aRange.javaCount+=tRange.getJavaCount();
                aRange.nodeJSCount+=tRange.getNodeJSCount();
                aRange.machineCount+=tRange.getMachineCount();
                aRange.phpCount+=tRange.getPhpCount();
                aRange.totalCount+=tRange.getTotalCount();
                
            }
            //logger.log(Level.INFO, "Value of iisCount " + aRange.iisCount);
            // We need to round the license up.
            aRange.iisCount=licenseRound(aRange.iisCount);
            aRange.totalCount=licenseRound(aRange.totalCount);
            customerRangeValues.add(aRange);
        }
        
        for(CustomerLicenseRange tRange:customerRangeValues){
            totalRangeValue.iisCount+=tRange.iisCount;
            totalRangeValue.javaCount+=tRange.javaCount;
            totalRangeValue.phpCount+=tRange.phpCount;
            totalRangeValue.nodeJSCount+=tRange.nodeJSCount;
            totalRangeValue.machineCount+=tRange.machineCount;
            totalRangeValue.totalCount+=tRange.totalCount;
        }
        
        
        
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        CustomerLicenseCount.logger = logger;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Integer, ApplicationLicenseCount> getApplications() {
        return applications;
    }

    public void setApplications(HashMap<Integer, ApplicationLicenseCount> applications) {
        this.applications = applications;
    }

    public ArrayList<CustomerLicenseRange> getCustomerRangeValues() {
        return customerRangeValues;
    }

    public void setCustomerRangeValues(ArrayList<CustomerLicenseRange> customerRangeValues) {
        this.customerRangeValues = customerRangeValues;
    }

    public CustomerLicenseRange getTotalRangeValue() {
        return totalRangeValue;
    }

    public void setTotalRangeValue(CustomerLicenseRange totalRangeValue) {
        this.totalRangeValue = totalRangeValue;
    }

    public ArrayList<org.appdynamics.appdrestapi.util.TimeRange> getTimeRanges() {
        return timeRanges;
    }

    public void setTimeRanges(ArrayList<org.appdynamics.appdrestapi.util.TimeRange> timeRanges) {
        this.timeRanges = timeRanges;
    }

    public org.appdynamics.appdrestapi.util.TimeRange getTotalRange() {
        return totalRange;
    }

    public void setTotalRange(org.appdynamics.appdrestapi.util.TimeRange totalRange) {
        this.totalRange = totalRange;
    }
    
    
    @Override
    public String toString(){
        StringBuilder bud = new StringBuilder();
        bud.append("\nCustomer Name: ").append(name).append("\n");
        bud.append("Total Application Count: ").append(applications.size()).append("\n");
        //bud.append(totalRangeValue.toString());
        bud.append("----------------------- Customer Time Range ------------------------------\n");
        for(CustomerLicenseRange cRange: customerRangeValues){
            bud.append(cRange.toString());
        }
        
        for(ApplicationLicenseCount app : applications.values()){
            bud.append("------------------- Applications -------------------------------------\n");
            bud.append(app.toString());
        }

        
        return bud.toString();
    }
            
    
    
}
