package org.sqlproc.engine.impl;

import java.util.Map;

import org.sqlproc.engine.SqlControl;
import org.sqlproc.engine.SqlOrder;

/**
 * The compound parameters controlling the META SQL execution.
 * 
 * <p>
 * For more info please see the <a href="https://github.com/hudec/sql-processor/wiki">Tutorials</a>.
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public class SqlStandardControl implements SqlControl {

    /**
     * The max SQL execution time. This parameter can help to protect production system against ineffective SQL query
     * commands. The value is in milliseconds.
     */
    private int maxTimeout;
    /**
     * The first SQL execution output row to be returned in the case we need to skip some rows in the result set. The
     * primary usage is to support the pagination.
     */
    private int firstResult;
    /**
     * The max number of SQL execution output rows, which can be returned in the result list. The primary usage is to
     * support the pagination.
     */
    private int maxResults;
    /**
     * The ordering directive list. Using the class SqlOrder the ordering rules can be chained. Every ordering rule in
     * this chain should correspond to one META SQL ordering statement.
     */
    private SqlOrder order;
    /**
     * More result classes used for the return values, like the collections classes or the collections items. They are
     * used mainly for the one-to-one, one-to-many and many-to-many associations.
     */
    private Map<String, Class<?>> moreResultClasses;

    /**
     * Standard constructor.
     */
    public SqlStandardControl() {
    }

    /**
     * Merging constructor.
     */
    public SqlStandardControl(SqlControl sqlControl) {
        if (sqlControl != null) {
            setFirstResult(sqlControl.getFirstResult());
            setMaxResults(sqlControl.getMaxResults());
            setMaxTimeout(sqlControl.getMaxTimeout());
            setMoreResultClasses(sqlControl.getMoreResultClasses());
            setOrder(sqlControl.getOrder());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxTimeout() {
        return maxTimeout;
    }

    /**
     * Sets the max SQL execution time. This parameter can help to protect production system against ineffective SQL
     * query commands. The value is in milliseconds.
     * 
     * @param maxTimeout
     *            the max SQL execution time
     * @return this instance
     */
    public SqlStandardControl setMaxTimeout(int maxTimeout) {
        this.maxTimeout = maxTimeout;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * Sets the first SQL execution output row to be returned in the case we need to skip some rows in the result set.
     * The primary usage is to support the pagination.
     * 
     * @param firstResult
     *            the first SQL execution output row
     * @return this instance
     */
    public SqlStandardControl setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the max number of SQL execution output rows, which can be returned in the result list. The primary usage is
     * to support the pagination.
     * 
     * @param maxResults
     *            the max number of SQL execution output rows
     * @return this instance
     */
    public SqlStandardControl setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlOrder getOrder() {
        return order;
    }

    /**
     * Sets the ordering directive list. Using the class SqlOrder the ordering rules can be chained. Every ordering rule
     * in this chain should correspond to one META SQL ordering statement.
     * 
     * @param order
     *            the ordering directive list
     * @return this instance
     */
    public SqlStandardControl setOrder(SqlOrder order) {
        this.order = order;
        return this;
    }

    /**
     * Sets the ordering directive list. Using the class SqlOrder the ordering rules can be chained. Every ordering rule
     * in this chain should correspond to one META SQL ordering statement.
     * 
     * @param order
     *            the ordering directive
     * @return this instance
     */
    public SqlStandardControl setAscOrder(int order) {
        this.order = SqlOrder.getAscOrder(order);
        return this;
    }

    /**
     * Sets the ordering directive list. Using the class SqlOrder the ordering rules can be chained. Every ordering rule
     * in this chain should correspond to one META SQL ordering statement.
     * 
     * @param order
     *            the ordering directive
     * @return this instance
     */
    public SqlStandardControl setDescOrder(int order) {
        this.order = SqlOrder.getDescOrder(order);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getMoreResultClasses() {
        return moreResultClasses;
    }

    /**
     * Sets more result classes used for the return values, like the collections classes or the collections items. They
     * are used mainly for the one-to-one, one-to-many and many-to-many associations.
     * 
     * @param moreResultClasses
     *            more result classes used for the return values
     * @return this instance
     */
    public SqlStandardControl setMoreResultClasses(Map<String, Class<?>> moreResultClasses) {
        this.moreResultClasses = moreResultClasses;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SqlStandardControl [maxTimeout=" + maxTimeout + ", firstResult=" + firstResult + ", maxResults="
                + maxResults + ", order=" + order + ", moreResultClasses=" + moreResultClasses + "]";
    }
}
