package com.github.lburgazzoli.osgi.karaf.cmd;

import org.apache.karaf.shell.console.Completer;

import java.util.List;

/**
 *
 */
public abstract class AbstractServiceCompleter <T> implements Completer {

    private T m_service;

    /**
     * c-tor
     */
    public AbstractServiceCompleter() {
        m_service = null;
    }

    /**
     *
     * @param service
     */
    public void setService(T service) {
        m_service = service;
    }

    /**
     *
     * @return
     */
    public T getService() {
        return m_service;
    }

    public int complete(String buffer, int cursor, List<String> candidates) {
        return doComplete(m_service,buffer,cursor,candidates);
    }

    /**
     *
     * @param service
     * @param buffer
     * @param cursor
     * @param candidates
     * @return
     */
    protected abstract int doComplete(T service,String buffer, int cursor, List<String> candidates);
}
