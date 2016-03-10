package db;

import bus.ReportContent;

import java.util.List;

public interface ReportManagerDao {

    public ReportContent getReportContentFromDB(String whichDate);
    public void setReportContentToDB(ReportContent rptC);
    public void appendReportContentToDB(ReportContent rptC);
    public List getReportWeekListFromDB();
    public int getNumSections();
    public int getNumWeeks();
}
