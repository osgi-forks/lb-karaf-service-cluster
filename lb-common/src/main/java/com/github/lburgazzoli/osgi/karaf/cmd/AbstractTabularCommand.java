package com.github.lburgazzoli.osgi.karaf.cmd;

/**
 *
 */
public abstract class AbstractTabularCommand<T> extends AbstractServiceCommand<T> {

    private ShellTable m_table;

    /**
     * c-tor
     */
    public AbstractTabularCommand(String... columns) {
        m_table = new ShellTable(columns);
    }

    /**
     *
     * @param service
     * @throws Exception
     */
    protected void doExecute(T service) throws Exception {
        doExecute(service,m_table);
        m_table.print();;
    }

    /**
     *
     * @param service
     * @throws Exception
     */
    protected abstract void doExecute(T service,ShellTable table) throws Exception;
}
