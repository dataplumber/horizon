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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bus.ReportManager;
import bus.ReportContent;
import util.cal;

import org.springframework.validation.BindException;

public class htmlReportFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private ReportManager rptMan;

    //------------- formBackingObject ------------
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
      List sectList1 = new ArrayList();
      String sec;
    	
      String whichDate = request.getParameter("whichDate");
      if(whichDate == null) {
        whichDate = (new cal()).dateOfPreviousMonday();
      }
      logger.info("In formBackingObject, whichDate is : " + whichDate);

      ReportContent reportContent = getReportManager().getReportContent(whichDate);

      List sectList = reportContent.getSections();
      ListIterator secIter = sectList.listIterator();

      while(secIter.hasNext()) {
        sec = (String) secIter.next();
        sec = addPRE(sec);
        sectList1.add(sec);
      }

      reportContent.setSections(sectList1);
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

    //--------------- addPRE -----------
    public String addPRE(String sec) {
      StringBuffer sb = new StringBuffer();
      String returnSec = null;
      char c;

      // use pre-formatted text
      sb.append("<PRE>");
      sb.append("<p class=\"wrap\">");
      sb.append(sec);
      sb.append("</p>");
      sb.append("</PRE>");

      returnSec = sb.toString();

      return returnSec;
    }
}
