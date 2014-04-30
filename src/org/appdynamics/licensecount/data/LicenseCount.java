/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;

import org.appdynamics.appdrestapi.resources.s;
import org.appdynamics.appdrestapi.data.*;
import org.appdynamics.appdrestapi.util.TimeRange;


import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import org.appdynamics.licensecount.resources.LicenseS;

/**
 *
 * @author soloink
 */
public class LicenseCount {
    
    protected double php=0;
    protected double java=0;
    protected double machine=0;
    protected double iis=0;
    protected double nodeJS=0;
    protected TimeRange range;
    
    public LicenseCount(){}

    public double getPhp() { return php; }

    public void setPhp(int php) {
        this.php = php;
    }

    public double getJava() { return java; }

    public void setJava(int java) {
        this.java = java;
    }

    public double getMachine() {
        return machine;
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public double getIis() {  return iis;  }

    public void setIis(int iis) {
        this.iis = iis;
    }

    public double getNodeJS() { return nodeJS; }

    public void setNodeJS(int nodeJS) {
        this.nodeJS = nodeJS;
    }

    public TimeRange getRange() {  return range;  }

    public void setRange(TimeRange range) {
        this.range = range;
    }

    
    //Agent types: 0:Java, 1:IIS, 2:PHP, 3:NodeJS, 4 Machine Agent
    public String getAgentName(int agentId){
        String val = s.AGENT_NAME_JAVA;
        switch(agentId){
            case 1:
                val=s.AGENT_NAME_DOTNET;
                break;
            case 2:
                val=s.AGENT_NAME_PHP;
                break;
            case 3:
                val=s.AGENT_NAME_NODEJS;
                break;
            case 4:
                val=s.AGENT_NAME_MACHINE_AGENT;
                break;
            case 5:
                val=LicenseS.AGENT_NAME_OTHER_AGENT;
                break;
            default:
                break;
        }
        return val;
    }
    
    public MetricValues getMetricValues(MetricDatas mDatas){
        if(mDatas != null && mDatas.getMetric_data().size() > 0){
            MetricData mData = mDatas.getMetric_data().get(0);
            if(mData != null && mData.getMetricValues().size() > 0){
                return mData.getMetricValues().get(0);
            }
        }
        return new MetricValues();
    }
    
    public int getAgentTypeFromVersion(String version, String type){
        if(version.contains(s.AGENT_TYPE_CHK_PHP)){return 2;}
        if(type.contains(s.AGENT_TYPE_CHK_IIS) || version.contains(s.AGENT_TYPE_CHK_IIS)){return 1;}
        if(version.contains(s.AGENT_TYPE_CHK_NODEJS)){return 3;}
        if(type.contains(LicenseS.AGENT_TYPE_OTHER)){return 5;} // This might be Java or PHP
        return 0;
    }
    
    public double getUptimePercentageForNode(MetricDatas mds, NodeLicenseRange range){
        double value=0;
        int count=0;
        for(MetricData mdata:mds.getMetric_data()){
            for(MetricValues mValues:mdata.getMetricValues()){
                for(MetricValue mValue:mValues.getMetricValue()){
                    value+=mValue.getValue();
                    count++;
                }
            }
        }

        if(count > 0) value=value/count;
        range.setCount(count);
        range.setValue(value);
        
        return value;
    }
    
    public ArrayList<TimeRange> getTimeRanges(int interval){
        Calendar cal1=Calendar.getInstance();
        // First we are going to zero out the time 0000
        cal1.set(Calendar.HOUR_OF_DAY, 0);cal1.set(Calendar.SECOND,0);cal1.set(Calendar.MINUTE, 0);
        Calendar cal2=Calendar.getInstance();cal2.setTimeInMillis(cal1.getTimeInMillis());
        cal2.add(Calendar.HOUR, -24);
        
        ArrayList<TimeRange> value = new ArrayList<TimeRange>();
        
        value.add(new TimeRange(cal2.getTimeInMillis(),cal1.getTimeInMillis()));
        
        for(int i=1; i < interval;i++){
            cal1.add(Calendar.HOUR, -24);
            cal2.add(Calendar.HOUR, -24);
            value.add(new TimeRange(cal2.getTimeInMillis(),cal1.getTimeInMillis()));
        }
        Collections.reverse(value);
        return value;
    }
    
    public TimeRange getTimeRange(int interval){
        Calendar cal1=Calendar.getInstance();
        // First we are going to zero out the time 0000
        cal1.set(Calendar.HOUR_OF_DAY, 0);cal1.set(Calendar.SECOND,0);cal1.set(Calendar.MINUTE, 0);
        Calendar cal2=Calendar.getInstance();cal2.setTimeInMillis(cal1.getTimeInMillis());
        cal2.add(Calendar.HOUR, -24);
        
        for(int i=1; i < interval; i++)  cal2.add(Calendar.HOUR, -24);
        
        return new TimeRange(cal2.getTimeInMillis(),cal1.getTimeInMillis());
    }
    
    /*
     *  In this example cal2 points to the start and cal1 points to the end.
     */
    public TimeRange getTimeRange(long start, long end){
        return new TimeRange(start,end);
    }
    
    public ArrayList<TimeRange> getTimeRanges(long start, long end){
        Calendar cal1=Calendar.getInstance();
        Calendar cal2=Calendar.getInstance();
        cal1.setTimeInMillis(end);
        cal2.setTimeInMillis(start);
        boolean createOne=true;
        int count=0;
        ArrayList<TimeRange> value=new ArrayList<TimeRange>();
        while(createOne){
            Calendar cal3=Calendar.getInstance();
            cal3.setTimeInMillis(cal2.getTimeInMillis());
            cal3.add(Calendar.HOUR, 24);
            if(count==0 && (cal3.getTimeInMillis() > cal1.getTimeInMillis())){
                // This case will cover when the time stamp is smaller than 24 hours
                value.add(new TimeRange(cal2.getTimeInMillis(),cal1.getTimeInMillis()));
                createOne=false;
                count++;
            }else{
                if(cal3.getTimeInMillis() > cal1.getTimeInMillis()){
                    value.add(new TimeRange(cal2.getTimeInMillis(),cal1.getTimeInMillis()));
                    count++;
                    createOne=false;
                }else{
                    value.add(new TimeRange(cal2.getTimeInMillis(),cal3.getTimeInMillis()));
                    cal2.setTimeInMillis(cal3.getTimeInMillis());
                    count++;
                    
                }
                
            }
        }
        
        
        return value;
    }
    
    public static int licenseRound(double licenseC){
        String numString = new Double(licenseC).toString();
        if(numString.contains(".")){
            String decimalValue = numString.split("\\.")[1];
            String integerValue = numString.split("\\.")[0];
            try{
                Double decimalDouble = new Double(decimalValue);
                Integer value = new Integer(integerValue);
                if(decimalDouble > 0) return (value + 1);
            }catch(Exception e){
                
            }
        }
        
        return new Double(licenseC).intValue();
    }
}
