  package web;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bus.ReportManager;
import bus.ReportContent;
import util.cal;

import org.springframework.validation.BindException;

public class AppendReportFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private ReportManager rptMan;
    private String myPrompt = null;
    private String emptyPrompt = null;

    //----------- getMessages ------------
    // Get Strings from messages.properties
    public void getMessages() {
      if(myPrompt == null || emptyPrompt == null) {
        String catalina_home = System.getenv("CATALINA_HOME");
        //logger.info("****** CATALINA_HOME is: "+catalina_home);

        String propertyFile = catalina_home+"/webapps/report/WEB-INF/messages.properties";
        logger.info("****** propertyFile is: "+propertyFile);

        Properties p = new Properties();
        try{
          p.load(new FileInputStream(propertyFile));
        }
        catch (IOException e){
          logger.info("****** Error: "+propertyFile+" cannot be found!");
        }
        myPrompt = p.getProperty("myPrompt");
        emptyPrompt = p.getProperty("emptyPrompt");
      }
    }

    //------------- onSubmit ------------
    public ModelAndView onSubmit(Object command) throws ServletException {

        logger.info("CCCCCC====== AppendReportFormController.onSubmit called !");

        String report_week = ((ReportContent) command).getReportWeek();
        logger.info("XXXXXX onSubmit report_week: " + report_week);

        List sectList = ((ReportContent) command).getSections();
        List sectAppList = ((ReportContent) command).getSectionAppends();
        ListIterator secIter = sectList.listIterator();
        ListIterator secAppIter = sectAppList.listIterator();

        String sec, secapp;

        // Get Strings from messages.properties
        getMessages();

        ReportContent rc = new ReportContent();
        List sectList1 = new ArrayList();
        List sectAppList1 = new ArrayList();

        // Remove the prompt messages
        while(secIter.hasNext()) {
          sec = (String) secIter.next();
          if(sec.equals(emptyPrompt)) {
            sec = "";
          }

          secapp = (String) secAppIter.next();
          if(secapp.equals(myPrompt)) {
            secapp = "";
          }
          //logger.info("XXXXXX onSubmit sec: " + sec);
          //logger.info("XXXXXX onSubmit secapp: " + secapp);

          sectList1.add(sec);
          sectAppList1.add(secapp);
        }

        rc.setReportWeek(report_week);
        rc.setSections(sectList1);
        rc.setSectionAppends(sectAppList1);

        // Commit to DB
        rptMan.appendReportContent(rc);

        return new ModelAndView(new RedirectView(getSuccessView()));
    }

    //------------- formBackingObject ------------
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {

        logger.info("CCCCCC====== AppendReportFormController.formBackingObject called !");

        cal c = new cal();
        String prevMon = c.dateOfPreviousMonday();
        logger.info("****** prevMon: " + prevMon);
    	
    	ReportContent reportContent = getReportManager().getReportContent(prevMon);

        reportContent.setWeekList(rptMan.getReportWeekList());
    	
        logger.info("CCCCCC====== AppendReportFormController.formBackingObject before returning ...");
    	return reportContent;
    }

    //--------------- setReportManager -----------
    public void setReportManager(ReportManager rptMan) {
        this.rptMan = rptMan;
    }

    //--------------- getReportManager -----------
    public ReportManager getReportManager() {
        return rptMan;
    }

}
