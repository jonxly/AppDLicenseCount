/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount;

import org.appdynamics.licensecount.data.ApplicationLicenseCount;
import org.appdynamics.licensecount.data.CustomerLicenseCount;
import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.data.*;
import org.appdynamics.appdrestapi.resources.s;
import org.appdynamics.licensecount.resources.LicenseS;
import org.appdynamics.licensecount.resources.LicenseOptions;
import org.appdynamics.licensecount.file.WriteExcelDoc;

import java.util.logging.Logger;
import java.util.logging.Level;


/**
 *
 * @author soloink
 * 
 * This is going to be the main class the for the solution. It will expect a set of options.
 * 
 */
public class CountLicenses {
    private static Logger logger=Logger.getLogger(CountLicenses.class.getName());
    private static boolean QAMODE=false;
    
    public static void main(String[] args){
        //String controller="appdyn02";
        
        LicenseOptions licOptions=new LicenseOptions(args);
        boolean continueExec = licOptions.parse();
        
        StringBuilder bud = new StringBuilder();
        bud.append("\nRunning license count with the following options:");
        bud.append("\n\tController: ").append(LicenseS.CONTROLLER_V);
        bud.append("\n\tController Port: ").append(LicenseS.PORT_V);
        bud.append("\n\tUser Name: ").append(LicenseS.USERNAME_V);
        //bud.append("\n\tUser Password: ").append(LicenseS.PASSWD_V);
        bud.append("\n\tUse SSL: ").append(LicenseS.SSL_V);
        bud.append("\n\tAccount name: ").append(LicenseS.ACCOUNT_V);
        bud.append("\n\tFile Name: ").append(LicenseS.FILENAME_V);
        bud.append("\n\tInterval: ").append(LicenseS.INTERVAL_V);
        bud.append("\n\tUptime: ").append(LicenseS.UPTIME_V);
        bud.append("\n\tDebug Level: ").append(LicenseS.DEBUG_V).append("\n");
        //
        
        if(!licOptions.validDebug(LicenseS.DEBUG_V)){licOptions.printHelp();System.exit(1);}
        else{s.debugLevel=LicenseS.DEBUG_V;}
        if(!licOptions.validInterval(LicenseS.INTERVAL_V)){licOptions.printHelp();System.exit(1);}
        if(!continueExec){licOptions.printHelp();System.exit(1);}
        if(LicenseS.UPTIME_V == -1.0){licOptions.printHelp();System.exit(1); }
        else{ s.percentageThreshold=LicenseS.UPTIME_V;}
        if(s.debugLevel > 0) logger.log(Level.INFO,bud.toString());
        
        
        /*
         *  At this point we have all of the standard options that we require to run properly
         * so we are going to create our access object.
         */
        RESTAccess access = new RESTAccess(LicenseS.CONTROLLER_V,LicenseS.PORT_V,LicenseS.SSL_V,LicenseS.USERNAME_V,LicenseS.PASSWD_V,LicenseS.ACCOUNT_V);
        
        // This grabs all of the apps.
        Applications apps=access.getApplications();
        if(apps == null){
            // If this is null then we had a problem connection so might as well stop.
            logger.log(Level.SEVERE,"No applications were returned when executing the request for applications, please insure the information provided was correct.\nExiting.");
            System.exit(1);
        }
        
        // Going to create the primary object.
        CustomerLicenseCount customer = new CustomerLicenseCount(LicenseS.ACCOUNT_V);
        
        logger.log(Level.INFO,"Getting applications.");
        for(Application appD: apps.getApplications()){
            if(QAMODE){
                if(appD.getName().equalsIgnoreCase("MPSUnified-Prod")){ //appD.getName().contains("PHP") || 
                    //Here we are creating the application into their own applications
                    ApplicationLicenseCount appCount = new ApplicationLicenseCount(appD.getName(),appD.getId());
                    //This is where we load the nodes into the application, no count is done yet
                    //appCount.populateLicense(access.getNodesForApplication(appD.getId()));
                    customer.addApplication(appCount);
                }
            }else{
               //Here we are creating the application into their own applications
               ApplicationLicenseCount appCount = new ApplicationLicenseCount(appD.getName(),appD.getId());
               //This is where we load the nodes into the application, no count is done yet
               //appCount.populateLicense(access.getNodesForApplication(appD.getId()));
               customer.addApplication(appCount);               
            }

        }
        
         
        /* 
         * Now that we have the list of ApplicationLicenseCount we can populate them in the populate method is where we 
         * can redefine the type of object.
        */
         
        logger.log(Level.INFO,new StringBuilder().append("Populating the applications for a total of ").append(customer.getApplications().size()).append(" of applications").toString());
        customer.populateApplications(access, LicenseS.INTERVAL_V);
        //This is were we start to count the licenses.
        customer.countTierLicenses();
        
        WriteExcelDoc excel=new WriteExcelDoc(customer);
        excel.init();
        
        //logger.log(Level.INFO,customer.toString());
       
        
        
        
    }
}
