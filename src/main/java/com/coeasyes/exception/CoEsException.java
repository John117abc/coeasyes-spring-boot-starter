package com.coeasyes.exception;

/**
 * cese类异常
 *
 * @author ruoyi
 */
public class CoEsException extends RuntimeException
{
    private static final long serialVersionUID = 8247610319171014183L;

    public CoEsException(Throwable e)
    {
        super(e.getMessage(), e);
    }

    public CoEsException(String message)
    {
        super(message);
    }

    public CoEsException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
