package web;

import bus.CatalogManager;
import bus.Catalog;
import bus.Fields;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException; 
import java.util.List; 
import java.util.ListIterator; 
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamliningController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogManager cm; // = new CatalogManager();
    //private Catalog catalog = null;

    //--------- formBackingObject -----------
    // Forms a catalog object (content from db) before
    // the jsp is rendered.
    //
    protected Object formBackingObject(HttpServletRequest request) 
                     throws ServletException {

        Catalog catalog = null;

        logger.info("CCCC===== StreamliningController.formBackingObject called !");

        if (!isFormSubmission(request)) {
           catalog = cm.getCatalog();
        }
        else
          logger.info("^^^^^^^^^^^^ StreamliningController isFormSubmission!");

        return catalog;
    }

    //--------- onSubmit -----------
    // This is called when the "Submit" button is pushed.
    // The contents of the GUI widgets are committed to the db.
    //
    public ModelAndView onSubmit(Object command)
            throws ServletException {

        logger.info("CCCCCC====== StreamliningController.onSubmit called !");

        Catalog cat = (Catalog) command;
        cm.setCatalog(cat);

        // print out the fields to verify
       /*
        List fds = cat.getFields();

        ListIterator li = fds.listIterator();
        while (li.hasNext()) {
            Fields f = (Fields) li.next();
            boolean approved = f.getNASAapproved();
            logger.info("XXXXXX onSubmit NASAapproved: " + approved);
            approved = f.getCNESapproved();
            logger.info("XXXXXX onSubmit CNESapproved: " + approved);
        }
        logger.info("CCCC==== returning to " + getSuccessView());
       */

        return new ModelAndView(new RedirectView(getSuccessView()));
    }


    public CatalogManager getCatalogManager() {
        return this.cm;
    }

    public void setCatalogManager(CatalogManager cm) {
        this.cm = cm;
    }
}
