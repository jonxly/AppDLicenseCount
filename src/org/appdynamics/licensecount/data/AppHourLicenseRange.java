/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.data;

import org.appdynamics.appdrestapi.util.TimeRange;

/**
 *
 * @author gilbert.solorzano
 */
public class AppHourLicenseRange extends TierHourLicenseRange{
    public AppHourLicenseRange(){super();}

    public AppHourLicenseRange(String name){super(name);}
    
    public AppHourLicenseRange(TimeRange tr){
        this.start=tr.getStart();
        this.end=tr.getEnd();
    }
    
    
}
