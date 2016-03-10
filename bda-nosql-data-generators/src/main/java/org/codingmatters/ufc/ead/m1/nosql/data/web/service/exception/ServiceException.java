package org.codingmatters.ufc.ead.m1.nosql.data.web.service.exception;

/**
 * Created by vagrant on 2/17/16.
 */
public class ServiceException extends Exception {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
