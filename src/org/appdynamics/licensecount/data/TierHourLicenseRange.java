/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;;

import org.appdynamics.appdrestapi.util.TimeRange;
import org.appdynamics.appdrestapi.data.MetricValue;
import org.appdynamics.appdrestapi.data.MetricValues;
import org.appdynamics.appdrestapi.util.TimeRange;
import org.appdynamics.appdrestapi.data.MetricValue;
import org.appdynamics.appdrestapi.data.MetricValues;
/**
 *
 * @author gilbert.solorzano
 */
public class TierHourLicenseRange extends TimeRange{
    protected MetricValues appMetricValues=new MetricValues();
    protected MetricValues machineMetricValues=new MetricValues();
    protected int appAgent, machineAgent;
    
    public TierHourLicenseRange(){super();}

    public TierHourLicenseRange(String name){super(name);}
    
    public TierHourLicenseRange(TimeRange tr){
        this.start=tr.getStart();
        this.end=tr.getEnd();
    }

    public int getAppAgent() {
        return appAgent;
    }

    public void setAppAgent(int appAgent) {
        this.appAgent = appAgent;
    }

    public int getMachineAgent() {
        return machineAgent;
    }

    public void setMachineAgent(int machineAgent) {
        this.machineAgent = machineAgent;
    }

    public MetricValues getAppMetricValues() {
        return appMetricValues;
    }

    public void setAppMetricValues(MetricValues appMetricValues) {
        this.appMetricValues = appMetricValues;
    }

    public MetricValues getMachineMetricValues() {
        return machineMetricValues;
    }

    public void setMachineMetricValues(MetricValues machineMetricValues) {
        this.machineMetricValues = machineMetricValues;
    }
    
    
    
    public void countAgents(){
        int aCount=0;
        int mCount=0;
        for(MetricValue mv:appMetricValues.getMetricValue()){
             appAgent+=mv.getValue();
             aCount++;
        }
        for(MetricValue mv:appMetricValues.getMetricValue()){
            machineAgent+=mv.getValue();
            mCount++;
        }
        
        if(aCount > 0){
            Double dV = new Double(appAgent/mCount);
            appAgent=dV.intValue();
        }else{ appAgent=0;}
        
        if(mCount > 0){
            Double dV= new Double(machineAgent/mCount);
            machineAgent=dV.intValue();
        }else{machineAgent=0;}
    }
    
    @Override
    public String toString(){
        StringBuilder bud=new StringBuilder();
        bud.append("\tTime range\n");
        bud.append("\t\tStart time ").append(getDate(start)).append(" :: ").append(start).append("\n");
        bud.append("\t\tEnd time ").append(getDate(end)).append(" :: ").append(end).append("\n");
        bud.append("\t\tCount as application agent ").append(appAgent).append("\n");
        bud.append("\t\tCount of machine agent ").append(machineAgent).append("\n");
        return bud.toString();
    }
}
