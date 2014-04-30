/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appdynamics.licensecount.file;

import org.appdynamics.appdrestapi.resources.s;

import org.appdynamics.licensecount.resources.LicenseS;
import org.appdynamics.licensecount.data.TierHourLicenseRange;
import org.appdynamics.licensecount.data.TierLicenseRange;
import org.appdynamics.licensecount.data.ApplicationLicenseRange;
import org.appdynamics.licensecount.data.AppHourLicenseRange;
import org.appdynamics.licensecount.data.CustomerLicenseRange;
import org.appdynamics.licensecount.data.TierLicenseCount;
import org.appdynamics.licensecount.data.ApplicationLicenseCount;
import org.appdynamics.licensecount.data.CustomerLicenseCount;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author gilbert.solorzano
 */
public class WriteExcelDoc {
    private static Logger logger=Logger.getLogger(WriteExcelDoc.class.getName());
    private CustomerLicenseCount customer;
    private String licensePath;
    
    public WriteExcelDoc(){}
    
    public WriteExcelDoc(CustomerLicenseCount customer){this.customer=customer;}
    
    public void init(){
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet licenseSummary = workbook.createSheet(LicenseS.LICENSE_SUMMARY);
        XSSFSheet licenseTiers = workbook.createSheet(LicenseS.TIER_SUMMARY);
        XSSFSheet licenseHourlyTiers = workbook.createSheet(LicenseS.HOURLY_TIER_SUMMARY);
        
        // Lets create the first row which will be the header.
        int headerRowIndex=0;
        Row headerRow = licenseSummary.createRow(headerRowIndex);
        Row tierRow = licenseTiers.createRow(headerRowIndex);
        Row hourlyTierRow = licenseHourlyTiers.createRow(headerRowIndex);
        
        int i=0;
        Cell cell_1 = headerRow.createCell(i);cell_1.setCellValue(LicenseS.CUSTOMER_NAME);
        
        Cell cell_2 = tierRow.createCell(i);cell_2.setCellValue(LicenseS.APPLICATION_NAME);
        cell_2 = tierRow.createCell(i+1);cell_2.setCellValue(LicenseS.TIER_NAME);
        
        Cell cell_3 = hourlyTierRow.createCell(i);cell_3.setCellValue(LicenseS.APPLICATION_NAME);
        cell_3 = hourlyTierRow.createCell(i+1);cell_3.setCellValue(LicenseS.TIER_NAME);
        
        
        
        
        i+=2;
        
        int columnCount=2;
        int columnCount1=3;
        // Create the date headers
        for(CustomerLicenseRange cRange:customer.getCustomerRangeValues()){
            cell_1=headerRow.createCell(columnCount);cell_1.setCellValue(cRange.getColumnName());
            cell_2=tierRow.createCell(columnCount1);cell_2.setCellValue(cRange.getColumnName());
            columnCount++;columnCount1++;
        }
        
        
        
        i=addCustomer(licenseSummary, i);  
        //logger.log(Level.INFO,"Next row " + ++i);
        headerRow = licenseSummary.createRow(++i);
        cell_1 = headerRow.createCell(0);cell_1.setCellValue(LicenseS.APPLICATION_NAME);
        i++;
        int tierRowCount=2;
        int createdHourlyTierHeader=0;
        columnCount1=3;
        
        //logger.log(Level.INFO,new StringBuilder().append("\n\n\tNumber of applications ").append(customer.getApplications().size()).toString());
        for(ApplicationLicenseCount app: customer.getApplications().values()){
            i=addApplication(licenseSummary,i,app);
            int inCount=0;
            for(TierLicenseCount tier: app.getTierLicenses().values()){
                if(createdHourlyTierHeader == 0){
                    for(TierHourLicenseRange tr:tier.getTierHourLicenseRange()){
                        cell_3=hourlyTierRow.createCell(columnCount1);cell_3.setCellValue(tr.getHourColumnName());
                        columnCount1++;
                    }
                    createdHourlyTierHeader=1;
                }
                tierRowCount=addTier(licenseTiers,tierRowCount,tier,app.getApplicationName(), inCount);
                inCount++;
            }
            if(inCount != 0) tierRowCount++;
            i++;
        }
        
        tierRowCount=2;
        for(ApplicationLicenseCount app: customer.getApplications().values()){
            //i=addApplication(licenseSummary,i,app);
            int inCount=0;
            tierRowCount=addHourlyApp(licenseHourlyTiers,tierRowCount,app, inCount);
            
            for(TierLicenseCount tier: app.getTierLicenses().values()){
                tierRowCount=addHourlyTier(licenseHourlyTiers,tierRowCount,tier,app.getApplicationName(), inCount);
                inCount++;
            }
            tierRowCount++;
            i++;
        }
        
        try
        {
            //Write the workbook in file system
            //String fileName=new StringBuilder().append("/Users/gilbert.solorzano/Documents/").append(customer.getName()).append("LicenseFile.xlsx").toString();
            
            FileOutputStream out = new FileOutputStream(new File(LicenseS.FILENAME_V));
            workbook.write(out);
            out.close();
            StringBuilder bud=new StringBuilder();
            bud.append("Completed writing the file: ").append(LicenseS.FILENAME_V).append(".");
            logger.log(Level.INFO,bud.toString());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public int addCustomer(XSSFSheet curSheet, int rowIndex){
        // This going to add the customer information.
        int tempRowIndex=rowIndex;
        ArrayList<Row> rows=new ArrayList<Row>();
        for(int i=rowIndex; i < (rowIndex + 6); i++){
            rows.add(curSheet.createRow(i));
        }
        
        Cell cell=null;
        for(int i=0; i <  6;i++){
            switch(i){
                case 0:
                    cell = rows.get(i).createCell(0);
                    cell.setCellValue(customer.getName());
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.TOTAL_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 1:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.JAVA_AGENT_COUNT);
                    tempRowIndex++;
                    break;
               
                case 2:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.DOTNET_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                    
                case 3:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.PHP_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                
                case 4:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.NODEJS_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 5:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.MACHINE_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                default:
                    break;
            }

        }
        
        int columnCount=2;
        for(CustomerLicenseRange cRange:customer.getCustomerRangeValues()){
            
            for(int i=0; i < 6;i++){
                switch(i){
                    case 0: //Total Count
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getTotalCount());
                        break;
                    case 1: //Java Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getJavaCount());
                        break;

                    case 2: //DotNet Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getIisCount());
                        break;

                    case 3: //PHP Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getPhpCount());
                        break;

                    case 4: //NodeJS Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getNodeJSCount());
                        break;
                        
                    case 5: //Machine Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getMachineCount());
                        break;
                        
                    default:
                        break;
                }
            }
            columnCount++;
        }
        return tempRowIndex++;
    }
    
    public int addApplication(XSSFSheet curSheet, int rowIndex, ApplicationLicenseCount appLicenseCount){
        // This going to add the customer information.
        int tempRowIndex=rowIndex;
        ArrayList<Row> rows=new ArrayList<Row>();
        for(int i=rowIndex; i < (rowIndex + 6); i++){
            rows.add(curSheet.createRow(i));
        }
        
        Cell cell=null;
        for(int i=0; i <  6;i++){
            switch(i){
                case 0:
                    cell = rows.get(i).createCell(0);
                    cell.setCellValue(appLicenseCount.getApplicationName());
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.TOTAL_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 1:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.JAVA_AGENT_COUNT);
                    tempRowIndex++;
                    break;
               
                case 2:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.DOTNET_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                    
                case 3:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.PHP_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                
                case 4:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.NODEJS_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 5:
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(LicenseS.MACHINE_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                default:
                    break;
            }

        }
        
        int columnCount=2;
        for(ApplicationLicenseRange cRange:appLicenseCount.getAppLicenseRange()){
            
            for(int i=0; i < 6;i++){
                switch(i){
                    case 0: //Total Count
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getTotalCount());
                        break;
                    case 1: //Java Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getJavaCount());
                    
                        break;

                    case 2: //DotNet Agent
                        cell = rows.get(i).createCell(columnCount);
                        //logger.log(Level.INFO,new StringBuilder().append("Adding .Net ").append(cRange.getIisCount()).toString());
                        cell.setCellValue(new Double(cRange.getIisCount()));
      
                        break;

                    case 3: //PHP Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(new Double(cRange.getPhpCount()));

                        break;

                    case 4: //NodeJS Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getNodeJSCount());
     
                        break;
                        
                    case 5: //Machine Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getMachineCount());
   
                        break;
                        
                    default:
                        break;
                }
            }
            columnCount++;
        }
        return tempRowIndex++;
    }
    
    public int addTier(XSSFSheet curSheet, int rowIndex, TierLicenseCount tLicenseCount, String appName, int inCount){
        // This going to add the customer information.
        int tempRowIndex=rowIndex;
        ArrayList<Row> rows=new ArrayList<Row>();
        for(int i=rowIndex; i < (rowIndex + 6); i++){
            rows.add(curSheet.createRow(i));
        }
        
        Cell cell=null;
        for(int i=0; i <  6;i++){
            switch(i){
                case 0:
                    if(inCount == 0){
                       cell = rows.get(i).createCell(0);
                       cell.setCellValue(appName); 
                    }
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(tLicenseCount.getName());
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.TOTAL_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 1:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.TOTAL_AGENT_COUNT);
                    tempRowIndex++;
                    break;
               
                case 2:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.DOTNET_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                    
                case 3:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.PHP_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                
                case 4:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.NODEJS_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 5:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.MACHINE_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                default:
                    break;
            }

        }
        
        int columnCount=3;
        for(TierLicenseRange cRange:tLicenseCount.getTierLicenseRange()){
            
            for(int i=0; i < 6;i++){
                switch(i){
                    case 0: //Total Count
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getTotalCount());
                        break;
                    case 1: //Java Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getJavaCount());
                    
                        break;

                    case 2: //DotNet Agent
                        cell = rows.get(i).createCell(columnCount);
                        //logger.log(Level.INFO,new StringBuilder().append("Adding .Net ").append(cRange.getIisCount()).toString());
                        cell.setCellValue(new Double(cRange.getIisCount()));
      
                        break;

                    case 3: //PHP Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(new Double(cRange.getPhpCount()));

                        break;

                    case 4: //NodeJS Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getNodeJSCount());
     
                        break;
                        
                    case 5: //Machine Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getMachineCount());
   
                        break;
                        
                    default:
                        break;
                }
            }
            columnCount++;
        }
        tempRowIndex++;
        return tempRowIndex++;
    }
    
    public int addHourlyTier(XSSFSheet curSheet, int rowIndex, TierLicenseCount tLicenseCount, String appName, int inCount){
        // This going to add the customer information.
        int tempRowIndex=rowIndex;
        ArrayList<Row> rows=new ArrayList<Row>();
        for(int i=rowIndex; i < (rowIndex + 2); i++){
            rows.add(curSheet.createRow(i));
        }
        
        Cell cell=null;
        for(int i=0; i <  2;i++){
            switch(i){
                case 0:
                    if(inCount == 0){
                       //cell = rows.get(i).createCell(0);
                       //cell.setCellValue(appName); 
                    }
                    cell = rows.get(i).createCell(1);
                    cell.setCellValue(tLicenseCount.getName());
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.MACHINE_AGENT_COUNT);
                    tempRowIndex++;
                    break;
                case 1:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.APPLICATION_AGENT_COUNT);
                    tempRowIndex++;
                    break;
               
                default:
                    break;
            }

        }
        
        int columnCount=3;
        for(TierHourLicenseRange cRange:tLicenseCount.getTierHourLicenseRange()){
            
            for(int i=0; i < 2;i++){
                switch(i){
                    case 0: //Total Count
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getMachineAgent());
                        break;
                    case 1: //Java Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getAppAgent());
                    
                        break;

                        
                    default:
                        break;
                }
            }
            columnCount++;
        }
        tempRowIndex++;
        return tempRowIndex++;
    }
    
    public int addHourlyApp(XSSFSheet curSheet, int rowIndex, ApplicationLicenseCount appLicenseCount, int inCount){
        // This going to add the customer information.
        int tempRowIndex=rowIndex;
        ArrayList<Row> rows=new ArrayList<Row>();
        for(int i=rowIndex; i < (rowIndex + 2); i++){
            rows.add(curSheet.createRow(i));
        }
        
        Cell cell=null;
        for(int i=0; i <  2;i++){
            switch(i){
                case 0:
                    if(inCount == 0){
                        cell = rows.get(i).createCell(0);
                        cell.setCellValue(appLicenseCount.getApplicationName());
    
                        cell = rows.get(i).createCell(2);
                        cell.setCellValue(LicenseS.MACHINE_AGENT_COUNT);
                        tempRowIndex++;
                    }
                    break;
                case 1:
                    cell = rows.get(i).createCell(2);
                    cell.setCellValue(LicenseS.APPLICATION_AGENT_COUNT);
                    tempRowIndex++;
                    break;
               
                default:
                    break;
            }

        }
        
        int columnCount=3;
        for(AppHourLicenseRange cRange:appLicenseCount.getAppHourLicenseRange()){
            
            for(int i=0; i < 2;i++){
                switch(i){
                    case 0: //Total Count
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getMachineAgent());
                        break;
                    case 1: //Java Agent
                        cell = rows.get(i).createCell(columnCount);
                        cell.setCellValue(cRange.getAppAgent());
                    
                        break;

                        
                    default:
                        break;
                }
            }
            columnCount++;
        }
        tempRowIndex++;
        return tempRowIndex++;
    }
}
