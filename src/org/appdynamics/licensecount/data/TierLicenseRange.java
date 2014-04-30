/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;

import org.appdynamics.appdrestapi.util.TimeRange;

/**
 *
 * @author soloink
 */
public class TierLicenseRange extends TimeRange{
    protected double javaCount, phpCount, iisCount, nodeJSCount, machineCount, totalCount, tierAppAgentCount, tierMachineAgentCount;

    public TierLicenseRange(){super();}

    public TierLicenseRange(String name){super(name);}
    
    public double getJavaCount() {
        return javaCount;
    }

    public void setJavaCount(int javaCount) {
        this.javaCount = javaCount;
    }

    public double getPhpCount() {
        return phpCount;
    }

    public void setPhpCount(int phpCount) {
        this.phpCount = phpCount;
    }

    public double getIisCount() {
        return iisCount;
    }

    public void setIisCount(int iisCount) {
        this.iisCount = iisCount;
    }

    public double getNodeJSCount() {
        return nodeJSCount;
    }

    public void setNodeJSCount(int nodeJSCount) {
        this.nodeJSCount = nodeJSCount;
    }

    public double getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(int machineCount) {
        this.machineCount = machineCount;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getTierAppAgentCount() {
        return tierAppAgentCount;
    }

    public void setTierAppAgentCount(int tierAppAgentCount) {
        this.tierAppAgentCount = tierAppAgentCount;
    }

    public double getTierMachineAgentCount() {
        return tierMachineAgentCount;
    }

    public void setTierMachineAgentCount(int tierMachineAgentCount) {
        this.tierMachineAgentCount = tierMachineAgentCount;
    }
    
    
    
    @Override
    public String toString(){
        StringBuilder bud=new StringBuilder();
        bud.append(name).append("\n");
        bud.append("\t\tTotal count ").append(totalCount).append("\n");
        bud.append("\t\tJava agent count ").append(javaCount).append("\n");
        bud.append("\t\tDotNet agent count ").append(iisCount).append("\n");
        bud.append("\t\tPHP agent count ").append(phpCount).append("\n");
        bud.append("\t\tNodeJS agent count ").append(nodeJSCount).append("\n");
        bud.append("\t\tMachine agent count ").append(machineCount).append("\n");
        bud.append("\t\tStart time ").append(getDate(start)).append(" :: ").append(start).append("\n");
        bud.append("\t\tEnd time ").append(getDate(end)).append(" :: ").append(end).append("\n");
        return bud.toString();
    }
    
    
    
}
