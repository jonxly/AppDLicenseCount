/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;


import org.appdynamics.appdrestapi.data.MetricValue;
import org.appdynamics.appdrestapi.data.MetricValues;
import org.appdynamics.appdrestapi.util.TimeRange;

/**
 *
 * @author soloink
 */
public class NodeLicenseRange extends TimeRange{
    protected int count=0;
    protected double value;
    protected MetricValues metricValues=new MetricValues();
    protected boolean countAsLicense=false;
    
    public NodeLicenseRange(){super();}

    public NodeLicenseRange(String name){super(name);}
    
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isCountAsLicense() {
        return countAsLicense;
    }

    public void setCountAsLicense(boolean countAsLicense) {
        this.countAsLicense = countAsLicense;
    }

    public double getUptimePercentageForNode(){
        value=0;
        int rCount=0;
        for(MetricValue mValue: metricValues.getMetricValue()){
            value+=mValue.getValue();
            rCount++;
        }

        if(rCount > 0) value=value/rCount;        
        return value;
    }

    public MetricValues getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(MetricValues metricValues) {
        this.metricValues = metricValues;
    }
    
    

    @Override
    public String toString(){
        StringBuilder bud=new StringBuilder();
        bud.append("Time range\n");
        bud.append("\t\tStart time ").append(getDate(start)).append(" :: ").append(start).append("\n");
        bud.append("\t\tEnd time ").append(getDate(end)).append(" :: ").append(end).append("\n");
        bud.append("\t\tCount as license ").append(countAsLicense).append("\n");
        bud.append("\t\tPercentage of uptime ").append(value).append("\n");
        return bud.toString();
    }
           
}
