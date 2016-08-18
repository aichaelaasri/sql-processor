package org.sqlproc.engine.plugin;

/**
 * The SQL Processor plugin devoted to the COUNT SQL construction.
 * 
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public interface SqlCountPlugin extends Modifiers {

    /**
     * Used to construct the COUNT SQL.
     * 
     * @param name
     *            Name of the META SQL query or statement
     * @param sql
     *            original META SQL
     * @return the COUNT SQL and COUNT column
     */
    public String[] sqlCount(String name, StringBuilder sql);
}
