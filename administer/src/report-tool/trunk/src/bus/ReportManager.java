package bus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ListIterator;
import java.util.List;
import java.util.Set;

import db.ReportManagerDao;
import java.util.ArrayList;


public class ReportManager implements Serializable {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private ReportManagerDao rd;
    private ReportContent reportContent;

    public void setReportManagerDao(ReportManagerDao rd) {
        this.rd = rd;
    }

    public ReportContent getReportContent(String whichWeek) {
    	reportContent = rd.getReportContentFromDB(whichWeek);

    	return reportContent;
    }

    public void setReportContent(ReportContent rptC) {
        reportContent = rptC;
    	rd.setReportContentToDB(reportContent);
    }

    public void appendReportContent(ReportContent rptC) {
        reportContent = rptC;
    	rd.appendReportContentToDB(reportContent);
    }

    public List getReportWeekList() {
        return (rd.getReportWeekListFromDB());
    }
}

