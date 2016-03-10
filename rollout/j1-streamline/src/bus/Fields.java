package bus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Date;

public class Fields implements Serializable {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    // These correspond to the schema in db.
    // Because the Oracle db does not support the boolean type,
    // we use String in the db and bridge the difference
    // in the code that accesses the db (CatalogDaoJdbc.java).
    //
    private int id;
    private String cycle;
    private String version;
    private String dataAuthor;
    private boolean NASAapproved;
    private boolean CNESapproved;
    private String GDRreleaseDate;
    private boolean SGDRstaged;
    private String SGDRreleaseDate;
    private boolean GDRnetCDFstaged;
    private String GDRnetCDFreleaseDate;
    private boolean SSHAnetCDFstaged;
    private String SSHAnetCDFreleaseDate;
    private boolean SGDRnetCDFstaged;
    private String SGDRnetCDFreleaseDate;
    private String emailId;

    public void setId(int i) {
        id = i;
    }

    public int getId() {
        return id;
    }

    public void setCycle(String c) {
        cycle = c;
    }

    public String getCycle() {
        return cycle;
    }

    public void setVersion(String v) {
        version = v;
        //logger.info("XXX in setVersion, version is: "+version);
    }

    public String getVersion() {
        //logger.info("XXX in getVersion, version is: "+version);
        return version;
    }

    public void setDataAuthor(String d) {
        dataAuthor = d;
    }

    public String getDataAuthor() {
        return dataAuthor;
    }

    public void setCNESapproved(boolean ca) {
        CNESapproved = ca;
        logger.info("===>>> in setCNESapproved, CNESapproved is: "+CNESapproved);
    }

    public boolean getCNESapproved() {
        logger.info("<<<=== in getCNESapproved, CNESapproved is: "+CNESapproved);
        return CNESapproved;
    }

    public void setNASAapproved(boolean na) {
        NASAapproved = na;
        logger.info("===>>> in setNASAapproved, NASAapproved is: "+NASAapproved);
    }

    public boolean getNASAapproved() {
        logger.info("<<<=== in getNASAapproved, NASAapproved is: "+NASAapproved);
        return NASAapproved;
    }

    public void setGDRreleaseDate(String grd) {
        GDRreleaseDate = grd;
    }

    public String getGDRreleaseDate() {
        return GDRreleaseDate;
    }

    public void setSGDRstaged(boolean s) {
        SGDRstaged = s;
        logger.info("===>>> in setSGDRstaged, SGDRstaged is: "+SGDRstaged);
    }

    public boolean getSGDRstaged() {
        logger.info("<<<=== in getSGDRstaged, SGDRstaged is: "+SGDRstaged);
        return SGDRstaged;
    }

    public void setSGDRreleaseDate(String srd) {
        SGDRreleaseDate = srd;
    }

    public String getSGDRreleaseDate() {
        return SGDRreleaseDate;
    }

    public void setGDRnetCDFstaged(boolean s) {
        GDRnetCDFstaged = s;
        logger.info("===>>> in setGDRnetCDFstaged, GDRnetCDFstaged is: "+GDRnetCDFstaged);
    }

    public boolean getGDRnetCDFstaged() {
        logger.info("<<<=== in getGDRnetCDFstaged, GDRnetCDFstaged is: "+GDRnetCDFstaged);
        return GDRnetCDFstaged;
    }

    public void setGDRnetCDFreleaseDate(String srd) {
        GDRnetCDFreleaseDate = srd;
    }

    public String getGDRnetCDFreleaseDate() {
        return GDRnetCDFreleaseDate;
    }

    public void setSGDRnetCDFstaged(boolean s) {
        SGDRnetCDFstaged = s;
        logger.info("===>>> in setSGDRnetCDFstaged, SGDRnetCDFstaged is: "+SGDRnetCDFstaged);
    }

    public boolean getSGDRnetCDFstaged() {
        logger.info("<<<=== in getSGDRnetCDFstaged, SGDRnetCDFstaged is: "+SGDRnetCDFstaged);
        return SGDRnetCDFstaged;
    }

    public void setSGDRnetCDFreleaseDate(String srd) {
        SGDRnetCDFreleaseDate = srd;
    }

    public String getSGDRnetCDFreleaseDate() {
        return SGDRnetCDFreleaseDate;
    }

    public void setSSHAnetCDFstaged(boolean s) {
        SSHAnetCDFstaged = s;
        logger.info("===>>> in setSSHAnetCDFstaged, SSHAnetCDFstaged is: "+SSHAnetCDFstaged);
    }

    public boolean getSSHAnetCDFstaged() {
        logger.info("<<<=== in getSSHAnetCDFstaged, SSHAnetCDFstaged is: "+SSHAnetCDFstaged);
        return SSHAnetCDFstaged;
    }

    public void setSSHAnetCDFreleaseDate(String srd) {
        SSHAnetCDFreleaseDate = srd;
    }

    public String getSSHAnetCDFreleaseDate() {
        return SSHAnetCDFreleaseDate;
    }

    public void setEmailId(String ei) {
        emailId = ei;
    }

    public String getEmailId() {
        return emailId;
    }

}
