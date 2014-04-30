/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.resources;

/**
 *
 * @author gilbert.solorzano
 */
public class OPTS {
    public static final String CONTROLLER_L="controller";
    public static final String CONTROLLER_S="c";
    public static final String CONTROLLER_D="This is going to be the FQDN of the controller, for example: appdyn.saas.appdynamics.com";
    public static final boolean CONTROLLER_R=true;
    public static final boolean CONTROLLER_A=true;
    
    public static final String PORT_L="port";
    public static final String PORT_S="P";
    public static final String PORT_D="The is going to be the port that is used by the controller.";
    public static final boolean PORT_R=true;
    public static final boolean PORT_A=true;
    
    public static final String ACCOUNT_L="account";
    public static final String ACCOUNT_S="a";
    public static final String ACCOUNT_D="If controller is multi-tenant add the account";
    public static final boolean ACCOUNT_R=false;
    public static final boolean ACCOUNT_A=false;
    
    public static final String USERNAME_L="username";
    public static final String USERNAME_S="u";
    public static final String USERNAME_D="The user name to use for the connection";
    public static final boolean USERNAME_R=true;
    public static final boolean USERNAME_A=true;
    
    public static final String PASSWD_L="passwd";
    public static final String PASSWD_S="p";
    public static final String PASSWD_D="The password to use for the connection";
    public static final boolean PASSWD_R=true;
    public static final boolean PASSWD_A=true;
    
    public static final String SSL_L="ssl";
    public static final String SSL_S="s";
    public static final String SSL_D="Use SSL with connection";
    public static final boolean SSL_R=false;
    public static final boolean SSL_A=false;
    
    public static final String DEBUG_L="debug";
    public static final String DEBUG_S="d";
    public static final String DEBUG_D="Debug level to set the calls at.";
    public static final boolean DEBUG_A=true;
    public static final boolean DEBUG_R=false;
    
    public static final String GRANULAR_L="granular";
    public static final String GRANULAR_S="g";
    public static final String GRANULAR_D="How granular you want the text.";
    public static final boolean GRANULAR_A=false;
    public static final boolean GRANULAR_R=false;
    
    public static final String OPTION_ERROR_1="A required parameter was not found. Please view the help menu for required parameters.";
    
    
    public static String USERNAME_V=null;
    public static String PASSWD_V=null;
    public static String CONTROLLER_V=null;
    public static String ACCOUNT_V="Customer1";
    public static String PORT_V=null;
    public static int DEBUG_V=0;
    public static boolean SSL_V=false;
    public static boolean GRANULAR_V=false;
    public static double UPTIME_V=0.0;
    
    
}
