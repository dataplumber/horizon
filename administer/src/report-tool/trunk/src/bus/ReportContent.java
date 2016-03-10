package bus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ReportContent {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private String reportWeek;
    private List weekList;

    private List sections;
    private List sectionAppends;

    public void setReportWeek(String rpt) {
        reportWeek = rpt;
        logger.info(" in ReportContent - reportWeek set to " + reportWeek);
    }

    public String getReportWeek() {
    	logger.info(" in ReportContent - get reportWeek = " + reportWeek);
        return reportWeek;
    }
    
    public List getWeekList() {
        return weekList;
    }
      
    public void setWeekList(List lst) {
        this.weekList = lst;
    }

    public void setSections(List secs) {
        this.sections = secs;
        logger.info(" in ReportContent - sections set to " + sections);
    }

    public List getSections() {
    	logger.info(" in ReportContent - sections = " + sections);
        return this.sections;
    }
    
    public void setSectionAppends(List secApp) {
        this.sectionAppends = secApp;
        logger.info(" in ReportContent - sectionAppends set to " + sectionAppends);
    }

    public List getSectionAppends() {
        logger.info(" in ReportContent - sectionAppends = " + sectionAppends);
        return this.sectionAppends;
    }

}
