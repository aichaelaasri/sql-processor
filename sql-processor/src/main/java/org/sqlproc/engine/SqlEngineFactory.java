package org.sqlproc.engine;

/**
 * The factory definition, which can be used to construct the {@link SqlEngine} instances.
 * 
 * <p>
 * The factory can be based on Spring DI framework for example.
 * 
 * <p>
 * For more info please see the <a href="https://github.com/hudec/sql-processor/wiki">Tutorials</a>.
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public interface SqlEngineFactory {

    /**
     * Returns the named SQL Query Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Query Engine instance
     * @return the SQL Engine instance or null value in the case the related statement is missing
     */
    SqlQueryEngine getQueryEngine(String name);

    /**
     * Returns the named SQL CRUD Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL CRUD Engine instance
     * @return the SQL Engine instance or null value in the case the related statement is missing
     */
    SqlCrudEngine getCrudEngine(String name);

    /**
     * Returns the named SQL Procedure Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Procedure Engine instance
     * @return the SQL Engine instance or null value in the case the related statement is missing
     */
    SqlProcedureEngine getProcedureEngine(String name);

    /**
     * Returns the named SQL Query Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Query Engine instance
     * @return the SQL Engine instance
     * @throws SqlEngineException
     *             in the case the related statement is missing
     */
    SqlQueryEngine getCheckedQueryEngine(String name) throws SqlEngineException;

    /**
     * Returns the named SQL CRUD Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL CRUD Engine instance
     * @return the SQL Engine instance
     * @throws SqlEngineException
     *             in the case the related statement is missing
     */
    SqlCrudEngine getCheckedCrudEngine(String name);

    /**
     * Returns the named SQL Procedure Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Procedure Engine instance
     * @return the SQL Engine instance
     * @throws SqlEngineException
     *             in the case the related statement is missing
     */
    SqlProcedureEngine getCheckedProcedureEngine(String name);

    /**
     * Returns the named dynamic SQL Query Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Query Engine instance
     * @param sqlStatement
     *            the new SQL statement, which is going to replace the original one
     * @return the SQL Engine instance
     * @throws SqlEngineException
     *             in the case the original statement is missing
     */
    SqlQueryEngine getDynamicQueryEngine(String name, String sqlStatement) throws SqlEngineException;

    /**
     * Returns the named dynamic SQL CRUD Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL CRUD Engine instance
     * @param sqlStatement
     *            the new SQL statement, which is going to replace the original one
     * @return the SQL Engine instance
     * @throws SqlEngineException
     *             in the case the original statement is missing
     */
    SqlCrudEngine getDynamicCrudEngine(String name, String sqlStatement);

    /**
     * Returns the named dynamic SQL Procedure Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Procedure Engine instance
     * @param sqlStatement
     *            the new SQL statement, which is going to replace the original one
     * @return the SQL Engine instance
     * @throws SqlEngineException
     *             in the case the original statement is missing
     */
    SqlProcedureEngine getDynamicProcedureEngine(String name, String sqlStatement);
}
