/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;

import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.data.*;
import org.appdynamics.appdrestapi.resources.s;
import org.appdynamics.licensecount.resources.LicenseS;
import org.appdynamics.licensecount.resources.LicenseOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.appdynamics.appdrestapi.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author soloink
 */
public class ApplicationLicenseCount extends LicenseCount{
    private static Logger logger=Logger.getLogger(ApplicationLicenseCount.class.getName());
    private String applicationName;
    private int applicationId;
    private int tierCount=0;
    private HashMap<Integer,TierLicenseCount> tierLicenses=new HashMap<Integer,TierLicenseCount>(); 
    private ArrayList<ApplicationLicenseRange> appLicenseRange= new ArrayList<ApplicationLicenseRange>();
    private ArrayList<AppHourLicenseRange> appHourLicenseRange=new ArrayList<AppHourLicenseRange>();
    private ApplicationLicenseRange totalRangeValue;
    
    
    

    public ApplicationLicenseCount(){super();}
    
    public ApplicationLicenseCount(String applicationName, int id){
        super();
        this.applicationId=id;
        this.applicationName=applicationName;
        
    }
    
    /*
     * This count is going to be a bit more involved, it will require more time because we are going to gather all of the timeranges and 
     * then request the availability for that time.
     */
    public void populateLicense(Nodes nodes, RESTAccess access, ArrayList<TimeRange> listOfTimes, TimeRange totalTimeRange){
        // In this scenario we are going to zero out the minutes, seconds, hours of 
        //ArrayList<LicenseRange> listOfTimes=getTimeRange(interval);
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("Application ").append(applicationName).append(" has ").append(nodes.getNodes().size()).append(" nodes.").toString());
        
        totalRangeValue=new ApplicationLicenseRange("Total Application Count");
        totalRangeValue.setStart(totalTimeRange.getStart());
        totalRangeValue.setEnd(totalTimeRange.getEnd());
        
        //If just in case we don't have any nodes
        if(nodes == null) return;
        
        
        /*
         *  First we are going to get all of the nodes, then match them with their tier.
         */
        for(Node node:nodes.getNodes()){
            if(!tierLicenses.containsKey(node.getTierId()))
                tierLicenses.put(node.getTierId(), new TierLicenseCount(node.getTierName()));
            
            
            //This is going to create the NodeLicense and return it.
            //NodeLicenseCount nodeL=tierLicenses.get(node.getTierId()).addNodeRange(node);
            tierLicenses.get(node.getTierId()).addNodeRange(node); 
        }
        
        Tiers tiers = access.getTiersForApplication(applicationId);
        if(tiers != null){
            for(Tier tier:tiers.getTiers()){
                if(tierLicenses.containsKey(tier.getId())){
                    tierLicenses.get(tier.getId()).setTierId(tier.getId());
                    tierLicenses.get(tier.getId()).setTierAgentType(tier.getAgentType());
                }
            }
        }
        
        /*
         * Now that we have all of the nodes we are going to get all of the tiers to count
         * the nodes. 
         */
        for(TierLicenseCount tCount: tierLicenses.values()){
            tCount.populateNodeLicenseRange(totalTimeRange, listOfTimes, access, applicationName);
        }
        
    }

    public void countTierLicenses(ArrayList<TimeRange> timeRanges){
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("Begin application level tier license count.").toString());

        
        /*
         * This is where we are going identify the countable agents
         */
        //logger.log(Level.INFO,"Starting the nodeLicense count.");
        for(TierLicenseCount tCount: tierLicenses.values()){
            tCount.countNodeLicenses(timeRanges);
        }
        
        for(int i=0; i < timeRanges.size(); i++){
            ApplicationLicenseRange aRange = new ApplicationLicenseRange();
            aRange.setStart(timeRanges.get(i).getStart());
            aRange.setEnd(timeRanges.get(i).getEnd());
            aRange.setName(aRange.createName());
            
            for(TierLicenseCount tCount:tierLicenses.values()){
                TierLicenseRange tRange= tCount.getTierLicenseRange().get(i);
                aRange.iisCount+=tRange.getIisCount();
                aRange.javaCount+=tRange.getJavaCount();
                aRange.nodeJSCount+=tRange.getNodeJSCount();
                aRange.machineCount+=tRange.getMachineCount();
                aRange.phpCount+=tRange.getPhpCount();
                aRange.totalCount+=tRange.getTotalCount();
                
            }
            
            appLicenseRange.add(aRange);
        }
        
        // This is going to get the tier counts:
        ArrayList<TimeRange> hourlyTimeRanges=TimeRangeHelper.getHourlyTimeRanges(totalRangeValue.getStart(), totalRangeValue.getEnd());
        for(int i = 0; i < hourlyTimeRanges.size(); i++){
            AppHourLicenseRange app = new AppHourLicenseRange(hourlyTimeRanges.get(i));
            for(TierLicenseCount tCount:tierLicenses.values()){
                for(TierHourLicenseRange tRange: tCount.getTierHourLicenseRange()){
                    if(app.withIn(tRange)){
                        app.appAgent+=tRange.appAgent;
                        app.machineAgent+=tRange.machineAgent;
                    }
                }
            }
            appHourLicenseRange.add(app);
        }
        
        
        for(ApplicationLicenseRange tRange:appLicenseRange){
            totalRangeValue.iisCount+=tRange.iisCount;
            totalRangeValue.javaCount+=tRange.javaCount;
            totalRangeValue.phpCount+=tRange.phpCount;
            totalRangeValue.nodeJSCount+=tRange.nodeJSCount;
            totalRangeValue.machineCount+=tRange.machineCount;
            totalRangeValue.totalCount+=tRange.totalCount;
        }
        
    }
                          

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public HashMap<Integer, TierLicenseCount> getTierLicenses() {
        return tierLicenses;
    }

    public void setTierLicenses(HashMap<Integer, TierLicenseCount> tierLicenses) {
        this.tierLicenses = tierLicenses;
    }

    public ArrayList<ApplicationLicenseRange> getAppLicenseRange() {
        return appLicenseRange;
    }

    public void setAppLicenseRange(ArrayList<ApplicationLicenseRange> appLicenseRange) {
        this.appLicenseRange = appLicenseRange;
    }

    public int getTierCount() {
        return tierCount;
    }

    public void setTierCount(int tierCount) {
        this.tierCount = tierCount;
    }

    public ArrayList<AppHourLicenseRange> getAppHourLicenseRange() {
        return appHourLicenseRange;
    }

    public void setAppHourLicenseRange(ArrayList<AppHourLicenseRange> appHourLicenseRange) {
        this.appHourLicenseRange = appHourLicenseRange;
    }

    public ApplicationLicenseRange getTotalRangeValue() {
        return totalRangeValue;
    }

    public void setTotalRangeValue(ApplicationLicenseRange totalRangeValue) {
        this.totalRangeValue = totalRangeValue;
    }

    

    @Override
    public String toString(){
        StringBuilder bud=new StringBuilder();
        bud.append("Application Name: ").append(applicationName).append("\n");
        bud.append("Total Tier Count: ").append(tierLicenses.size()).append("\n");
        bud.append("------------------- Application Time Ranges --------------------------------\n");
        for(ApplicationLicenseRange tRange: appLicenseRange){
            bud.append(tRange.toString());
        }
        bud.append("------------------- Tier Time Ranges --------------------------------\n");
        for(TierLicenseCount tLic:tierLicenses.values()){
            bud.append(tLic.toString());
        }
        return bud.toString();
    }
    
}
