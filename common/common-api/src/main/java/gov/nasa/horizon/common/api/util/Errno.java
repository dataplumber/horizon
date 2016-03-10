/******************************************************************************
 * Copyright (C) 1999 California Institute of Technology. All rights re served
 * US Government Sponsorship under NASA contract NAS7-918 is acknowledged
 *****************************************************************************/
package gov.nasa.horizon.common.api.util;


/**
 * Contains value and message associated with an errno
 * @author
 * @version $Id:$
 */
public class Errno {
    private int _id;
    private String _msg;

    /**
     * Simple constructor
     */
    public Errno() {
        // no-op
    }

    /**
     * Constructor with input error ID and associated message.
     *
     * @param id error number
     * @param msg error message
     */
    public Errno(int id, String msg) {
        this._id = id;
        this._msg = msg;
    }

    /**
     * Accessor method to return the error id
     *
     * @return error id
     */
    public final int getId() {
        return this._id;
    }

    /**
     * Accessor method to return the error message
     *
     * @return error message
     */
    public final String getMessage() {
        return this._msg;
    }
}
