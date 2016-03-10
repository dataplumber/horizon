  package web;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
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

public class EditReportFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private ReportManager RptMan;
    private String emptyPrompt = null;

    //----------- getMessage ------------
    // Get Strings from messages.properties
    public void getMessage() {
      if(emptyPrompt == null) {
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
        emptyPrompt = p.getProperty("emptyPrompt");
      }
    }

    //------------- onSubmit ------------
    public ModelAndView onSubmit(Object command) throws ServletException {

        logger.info("CCCCCCCCCCC===== In EditReportFormController.onSubmit() ");

	String report_week = ((ReportContent) command).getReportWeek();
        logger.info("xxxxxxxx onSubmit() - report_week :  " + report_week);

        List sectList = ((ReportContent) command).getSections();
        ListIterator secIter = sectList.listIterator();
        String sec;

        // Get Strings from messages.properties
        getMessage();

        List sectList1 = new ArrayList();
        ReportContent rc = new ReportContent();

        // If what's submitted is the prompt message, remove it.
        while(secIter.hasNext()) {
          sec = (String) secIter.next();
          //logger.info("XXXXXX onSubmit sec: " + sec);

          if(sec.equals(emptyPrompt)) {
            sec = "";
          }

          sectList1.add(sec);
        }

	rc.setReportWeek(report_week);
	rc.setSections(sectList1);

        RptMan.setReportContent(rc);

        return new ModelAndView(new RedirectView(getSuccessView()));
    }


    //------------- formBackingObject ------------
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {

      //logger.info("CCCCCCCC======= In web.EditReportFormController.formBackingObject() ");
    	
      String whichDate = request.getParameter("whichDate");
      if(whichDate == null) {
        whichDate = (new cal()).dateOfPreviousMonday();
      }
      //logger.info("In formBackingObject, whichDate is : " + whichDate);

      ReportContent reportContent = getReportManager().getReportContent(whichDate);

      return reportContent;
    }

    //--------------- setReportManager -----------
    public void setReportManager(ReportManager rm) {
        RptMan = rm;
    }

    //--------------- getReportManager -----------
    public ReportManager getReportManager() {
        return RptMan;
    }

}
