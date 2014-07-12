/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;

import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.resources.s;
import org.appdynamics.appdrestapi.data.*;
import org.appdynamics.appdrestapi.util.TimeRange;



import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author soloink
 * 
 * The node will determine whether it is an app agent or machine agent.
 * 
 * if 'PHP' in agent_type:
        agent_type = 'PHP'
    if 'IIS' in agent_type:
        agent_type = 'DOT_NET'
        license_count = 1
    elif agent_type == 'Machine Agent':
        agent_type = 'MACHINE'
        license_count = 1
        assert len(nodes_on_machine) == 1
    else:
        agent_type = 'JAVA'
 * Agent types: 0:Java, 1:IIS, 2:PHP, 3:NodeJS, 4 Machine Agent
 */
public class NodeLicenseCount extends LicenseCount{
    private static Logger logger=Logger.getLogger(NodeLicenseCount.class.getName());
    private int type=0;
    private int queryType=0;
    private Node node;
    private ArrayList<NodeLicenseRange> rangeValues=new ArrayList<NodeLicenseRange>();
    private NodeLicenseRange totalRangeValue;
    private double licWeight=0;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public NodeLicenseCount(Node node){
        this.node=node;
        this.setInformation();
    }
    
    public void setInformation(){

        if(node.isMachineAgentPresent() && !node.isAppAgentPresent()){
            type=4;
            queryType=1;
        }else{
            type=getAgentTypeFromVersion(node.getAppAgentVersion(),node.getType());
        }
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("\t\tNode ").append(node.getName()).append(" type is ").append(type).toString());
        
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public String getName() {
        return node.getName();
    }


    public String getMachineName() {
        return node.getMachineName();
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public ArrayList<NodeLicenseRange> getRangeValues() {
        return rangeValues;
    }

    public void setRangeValues(ArrayList<NodeLicenseRange> rangeValues) {
        this.rangeValues = rangeValues;
    }
    
    public void setLicenseCounts(double percentage, int index){
        
        if(rangeValues.get(index).getValue() > percentage) rangeValues.get(index).setCountAsLicense(true);
    }

    public NodeLicenseRange getTotalRangeValue() {
        return totalRangeValue;
    }

    public void setTotalRangeValue(NodeLicenseRange totalRangeValue) {
        this.totalRangeValue = totalRangeValue;
    }
    
    public void populateNodeLicenseRange(TimeRange totalTimeRange, ArrayList<TimeRange> timeRanges, RESTAccess access, 
            String applicationName, String tierAgentType){
        MetricDatas mDatas= 
                access.getRESTMetricQuery(getQueryType(), applicationName, node.getTierName(), node.getName(), 
                totalTimeRange.getStart(), totalTimeRange.getEnd());
        //What happens whe this is null ?
        totalRangeValue=new NodeLicenseRange("Total Node Count");
        totalRangeValue.setStart(totalTimeRange.getStart());
        totalRangeValue.setEnd(totalTimeRange.getEnd());
        totalRangeValue.setMetricValues(getMetricValues(mDatas));
        
        int count=0;
        for(TimeRange tRange:timeRanges){
            
            if(type == 5 && count == 0){
                count++;
                if( checkIfNotJava(access, applicationName, tRange.getStart(), tRange.getEnd()) ){ 
                    if(s.debugLevel >= 2) 
                            logger.log(Level.INFO,new StringBuilder().append("\n\tIt is not java").toString());
                    boolean fnd=false;
                    if(checkIfPHP(tierAgentType)){ type=2;fnd=true;}
                    if(checkIfNodeJs(tierAgentType)){ type=3;fnd=true;}
                    if(checkIfDotNet(tierAgentType)){type=1;fnd=true;}
                    if(!fnd)type=2;
                }else{
                    type = 0;
                }
            }
            NodeLicenseRange nodeR = new NodeLicenseRange();
            nodeR.setStart(tRange.getStart());nodeR.setEnd(tRange.getEnd());
            nodeR.setName(nodeR.createName());
            for(MetricValue val: totalRangeValue.getMetricValues().getMetricValue()){
                if(nodeR.withIn(val.getStartTimeInMillis())) nodeR.getMetricValues().getMetricValue().add(val);
            }
            rangeValues.add(nodeR);
        }
        
    }
    
    /*
     *  This is where we are going to actually execute the count, for Java this is simple
     * but for .Net and Php we need to something different.
     */
    public void countNodeLicenseRange(double minUsage){
        int tempTotalCount=0;
        for(NodeLicenseRange nodeR:rangeValues){
            if(nodeR.getUptimePercentageForNode() > minUsage){
                nodeR.setCountAsLicense(true);
                tempTotalCount++;
            }
        }
        totalRangeValue.setCount(tempTotalCount);
        totalRangeValue.setCountAsLicense(true);
    }

    public double getLicWeight() {
        return licWeight;
    }

    public void setLicWeight(double licWeight) {
        this.licWeight = licWeight;
    }
    
    public boolean checkIfNotJava(RESTAccess access, String app, long start, long end){
        /*
         * So when the metric exists but is not populated then you get the 
         * <metric-datas><metric-data>
  <metricPath>Application Infrastructure Performance|1stTier|JVM|Classes|Current Loaded Class Count</metricPath>
  <frequency>ONE_MIN</frequency>
  <metricValues/>
</metric-data>
</metric-datas>osxltgsolo:appBuild gilbert.solorzano$ curl -k -u gsadmin@customer1 'http://gsappdyn01:8090/controller/rest/applications/BDR%20Big%20Deal%20Retail/mec-path=Application%20Infrastructure%20Performance%7C1stTier%7CJVM1%7CClasses%7CCurrent%20Loaded%20Class%20Count&time-range-type=BEFORE_NOW&duration-in-mins=15'
Enter host password for user 'gsadmin@customer1':
<metric-datas></metric-datas>
         * 
         */
        if(s.debugLevel >= 2) 
            logger.log(Level.INFO,new StringBuilder().append("\t\tNode ").append(node.getName()).append(" is being check to see if its php, with tiername ").append(node.getTierName()).toString());
        MetricDatas mds = access.getRESTMetricQuery(25, app, node.getTierName(), node.getName(), start, end);
        
        if(mds != null && mds.getMetric_data() != null && mds.getMetric_data().size() == 0) return true;
        
        return false;
    }
    
    public boolean checkIfPHP(String tierAgentType){
        if(tierAgentType.matches("(?i)php")) return true;
        return false;
    }
    
    public boolean checkIfNodeJs(String tierAgentType){
        if(tierAgentType.matches("(?i)node")) return true;
        return false;
    }
    
    public boolean checkIfDotNet(String tierAgentType){
        if(tierAgentType.contains("DOT_NET")) return true;
        
        return false;
    }
    
    @Override
    public String toString(){
        StringBuilder bud=new StringBuilder();
        bud.append("Node Name=").append(node.getName()).append(s.P);
        bud.append("Machine Name=").append(node.getMachineName()).append(s.P);
        bud.append("Agent Type=").append(getAgentName(type)).append(s.P).append("\n");
        
        return bud.toString();
    }
    
    
}
