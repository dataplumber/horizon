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

public class HtmlController extends SimpleFormController {

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

        //logger.info("CCCC===== HtmlController.formBackingObject called !");

        if (!isFormSubmission(request)) {
          catalog = cm.getCatalog();
        }
        else
          logger.info("^^^^^^^^^^^^ HtmlController isFormSubmission!");

        return catalog;
    }

    public CatalogManager getCatalogManager() {
        return this.cm;
    }

    public void setCatalogManager(CatalogManager cm) {
        this.cm = cm;
    }
}
