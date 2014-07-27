package org.sqlproc.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlproc.engine.impl.SqlMappingRule;
import org.sqlproc.engine.impl.SqlMetaStatement;
import org.sqlproc.engine.impl.SqlProcessor;
import org.sqlproc.engine.impl.SqlUtils;
import org.sqlproc.engine.plugin.SqlPluginFactory;
import org.sqlproc.engine.type.SqlComposedTypeFactory;
import org.sqlproc.engine.type.SqlInternalType;
import org.sqlproc.engine.type.SqlTypeFactory;
import org.sqlproc.engine.validation.SqlValidator;
import org.sqlproc.engine.validation.SqlValidatorFactory;

/**
 * The helper class for the META SQL statements and mapping rules parsing. These statements and rules are taken from the
 * file repository based on the provided ANTLR grammar.<br>
 * 
 * The purpose of this class is to load and analyze (=parse) the SQL Processor artifacts:
 * <ul>
 * <li>the META SQL statements</li>
 * <li>the mapping rules</li>
 * <li>the optional features</li>
 * </ul>
 * 
 * <p>
 * This is rather a low level helper class. For more simple approach please see {@link SqlSimpleFactory}
 * 
 * <p>
 * All the statements with the name <code>name(QRY)</code> are parsed as the META SQL queries using the ANTLR based
 * grammar. <br>
 * All the statements with the name <code>name(CRUD)</code> are parsed as the META SQL CRUD statements using the ANTLR
 * based grammar. <br>
 * All the statements with the name <code>name(CALL)</code> are parsed as the META SQL CALL statements using the ANTLR
 * based grammar. These are used to call the stored procedures.<br>
 * All the statements with the name <code>name(OUT)</code> are parsed as the mapping rules using the ANTLR based
 * grammar.<br>
 * A pair of the META SQL statement and the (optional) mapping rule forms one named SQL Engine instance.<br>
 * All the statements with the name <code>name(OPT)</code> are taken as the optional features used in the process of the
 * SQL query/statement generation and execution.<br>
 * <p>
 * In the process of ANTLR based parsing different kinds of incorrect stuff can cause the {@link SqlEngineException} to
 * be raised.
 * 
 * <p>
 * In the process of the META SQL statements and mapping rules parsing the input/output values META types have to be
 * established. For this purpose a factory class responsible for these META types construction has to be supplied.<br>
 * 
 * <p>
 * To initialize the SqlProcessorLoader, Spring DI configuration can be utilized, like the next one:<br>
 * 
 * <pre>
 * &lt;beans ...&gt;
 *   ...
 *     &lt;bean id="sqlFactory" class="org.sqlproc.engine.jdbc.JdbcEngineFactory" init-method="init"&gt;
 *         &lt;property name="metaFilesNames"&gt;
 *             &lt;list&gt;
 *                 &lt;value&gt;statements.qry&lt;/value&gt;
 *             &lt;/list&gt;
 *         &lt;/property&gt;
 *     &lt;/bean&gt;
 * &lt;/beans&gt;
 * </pre>
 * 
 * and use the next code to obtain an instance of the SQL Query engine
 * 
 * <pre>
 * SqlEngineFactory sqlFactory = context.getBean(&quot;sqlFactory&quot;, SqlEngineFactory.class);
 * SqlQueryEngine sqlEngine = sqlFactory.getQueryEngine(&quot;ALL&quot;);
 * </pre>
 * 
 * or the next code to obtain an instance of the SQL CRUD engine
 * 
 * <pre>
 * SqlEngineFactory sqlFactory = context.getBean(&quot;sqlFactory&quot;, SqlEngineFactory.class);
 * SqlCrudEngine sqlEngine = sqlFactory.getCrudEngine(&quot;ALL&quot;);
 * </pre>
 * <p>
 * Another possibility is to utilize {@link SqlFilesLoader}.
 * 
 * <pre>
 * SqlFilesLoader loader = new SqlFilesLoader(&quot;statements.qry&quot;, this.getClass());
 * SqlProcessorLoader sqlLoader = new SqlProcessorLoader(loader.getStatements(), JdbcTypeFactory.getInstance());
 * SqlQueryEngine sqlEngine = sqlLoader.getQueryEngine(&quot;ALL&quot;);
 * </pre>
 * 
 * <p>
 * For more info please see the <a href="https://github.com/hudec/sql-processor/wiki">Tutorials</a>.
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public class SqlProcessorLoader implements SqlEngineFactory {

    /**
     * The internal slf4j logger.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The combined factory for the META types construction.
     */
    private SqlTypeFactory composedTypeFactory;
    /**
     * The factory for the SQL Processor plugins. This is the basic facility to alter the SQL Processor processing.
     */
    private SqlPluginFactory pluginFactory;
    /**
     * The monitor factory used in the process of the SQL Monitor instances creation
     */
    private SqlMonitorFactory monitorFactory;
    /**
     * The validator factory used in the process of the SQL Monitor instances creation
     */
    private SqlValidatorFactory validatorFactory;
    /**
     * The collection of named SQL Engines (the primary SQL Processor class) instances.
     */
    private Map<String, SqlEngine> engines = new HashMap<String, SqlEngine>();
    /**
     * The collection of named META SQL queries.
     */
    private Map<String, SqlMetaStatement> sqls;
    /**
     * The collection of named META SQL CRUD statements.
     */
    private Map<String, SqlMetaStatement> cruds;
    /**
     * The collection of named META SQL CALLABLE statements.
     */
    private Map<String, SqlMetaStatement> calls;
    /**
     * The collection of named explicitly defined mapping rules.
     */
    private Map<String, SqlMappingRule> outs;
    /**
     * The collection of the SQL Processor optional features.
     */
    private Map<String, Object> features;
    /**
     * The collection of the SQL Processor optional features in the statement context.
     */
    private Map<String, Map<String, Object>> statementsFeatures;
    /**
     * The collection of the SQL Processor optional features to be cleared in the statement context.
     */
    private Map<String, Set<String>> statementsFeaturesUnset;
    /**
     * This flag indicates to speed up the initialization process.
     */
    private boolean lazyInit;

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory)
            throws SqlEngineException {
        this(sbStatements, typeFactory, pluginFactory, null, null, null, (String[]) null);
    }

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @param filter
     *            the properties name prefix to filter the META SQL statements, mapping rules and optional features
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory,
            String filter) throws SqlEngineException {
        this(sbStatements, typeFactory, pluginFactory, filter, null, null, (String[]) null);
    }

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied. Every instance of the SQL
     * Engine is accompanied with the SQL Monitor for the runtime statistics gathering. For the creation of these
     * monitors the SQL Monitor Factory can be used.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @param filter
     *            the properties name prefix to filter the META SQL statements, mapping rules and optional features
     * @param monitorFactory
     *            the monitor factory used in the process of the SQL Monitor instances creation
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory,
            String filter, SqlMonitorFactory monitorFactory) throws SqlEngineException {
        this(sbStatements, typeFactory, pluginFactory, filter, monitorFactory, null, (String[]) null);
    }

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied. Every instance of the SQL
     * Engine is accompanied with the SQL Monitor for the runtime statistics gathering. For the creation of these
     * monitors the SQL Monitor Factory can be used.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @param filter
     *            the properties name prefix to filter the META SQL statements, mapping rules and optional features
     * @param monitorFactory
     *            the monitor factory used in the process of the SQL Monitor instances creation
     * @param onlyStatements
     *            only statements and rules with the names in this container are picked up from the properties
     *            repository
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory,
            String filter, SqlMonitorFactory monitorFactory, String... onlyStatements) {
        this(sbStatements, typeFactory, pluginFactory, filter, monitorFactory, null, onlyStatements);
    }

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied. Every instance of the SQL
     * Engine is accompanied with the SQL Monitor for the runtime statistics gathering. For the creation of these
     * monitors the SQL Monitor Factory can be used.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @param filter
     *            the properties name prefix to filter the META SQL statements, mapping rules and optional features
     * @param monitorFactory
     *            the monitor factory used in the process of the SQL Monitor instances creation
     * @param customTypes
     *            the custom META types
     * @param onlyStatements
     *            only statements and rules with the names in this container are picked up from the properties
     *            repository
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory,
            String filter, SqlMonitorFactory monitorFactory, List<SqlInternalType> customTypes,
            String... onlyStatements) throws SqlEngineException {
        this(sbStatements, typeFactory, pluginFactory, filter, monitorFactory, null, customTypes, onlyStatements);
    }

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied. Every instance of the SQL
     * Engine is accompanied with the SQL Monitor for the runtime statistics gathering. For the creation of these
     * monitors the SQL Monitor Factory can be used.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @param filter
     *            the properties name prefix to filter the META SQL statements, mapping rules and optional features
     * @param monitorFactory
     *            the monitor factory used in the process of the SQL Monitor instances creation
     * @param validatorFactory
     *            the validator factory used in the process of the SQL Monitor instances creation
     * @param customTypes
     *            the custom META types
     * @param onlyStatements
     *            only statements and rules with the names in this container are picked up from the properties
     *            repository
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory,
            String filter, SqlMonitorFactory monitorFactory, SqlValidatorFactory validatorFactory,
            List<SqlInternalType> customTypes, String... onlyStatements) throws SqlEngineException {
        this(sbStatements, typeFactory, pluginFactory, filter, monitorFactory, validatorFactory, customTypes, false,
                onlyStatements);
    }

    /**
     * Creates a new instance of the SqlProcessorLoader from the String content repository (which is in fact a
     * collection of the META SQL statements, mapping rules and optional features. During the instance construction all
     * the statements are parsed and the collection of named SQL Engine instances is established. Later these instances
     * are used for the SQL queries/statements execution. For the purpose of the META types construction (located inside
     * the META SQL statements and mapping rules) a factory instance has to be supplied. Every instance of the SQL
     * Engine is accompanied with the SQL Monitor for the runtime statistics gathering. For the creation of these
     * monitors the SQL Monitor Factory can be used.
     * 
     * @param sbStatements
     *            the String representation of the META SQL queries/statements/output mappings
     * @param typeFactory
     *            the factory for the META types construction
     * @param pluginFactory
     *            the factory for the SQL Processor plugins
     * @param filter
     *            the properties name prefix to filter the META SQL statements, mapping rules and optional features
     * @param monitorFactory
     *            the monitor factory used in the process of the SQL Monitor instances creation
     * @param validatorFactory
     *            the validator factory used in the process of the SQL Monitor instances creation
     * @param customTypes
     *            the custom META types
     * @param lazyInit
     *            this flag indicates to speed up the initialization process.
     * @param onlyStatements
     *            only statements and rules with the names in this container are picked up from the properties
     *            repository
     * @throws SqlEngineException
     *             mainly in the case the provided statements or rules are not compliant with the ANTLR based grammar
     */
    public SqlProcessorLoader(StringBuilder sbStatements, SqlTypeFactory typeFactory, SqlPluginFactory pluginFactory,
            String filter, SqlMonitorFactory monitorFactory, SqlValidatorFactory validatorFactory,
            List<SqlInternalType> customTypes, boolean lazyInit, String... onlyStatements) throws SqlEngineException {
        if (logger.isTraceEnabled()) {
            logger.trace(">> SqlProcessorLoader, sbStatements=" + sbStatements + ", typeFactory=" + typeFactory
                    + ", pluginFactory=" + pluginFactory + ", monitorFactory=" + monitorFactory + ", validatorFactory="
                    + validatorFactory + ", filter=" + filter + ", customTypes=" + customTypes + ", lazyInit="
                    + lazyInit + ", onlyStatements=" + onlyStatements);
        }

        long start = System.currentTimeMillis();

        if (sbStatements == null)
            throw new SqlEngineException("Missing SQL META queries/statements/output mappings");
        if (typeFactory == null)
            throw new SqlEngineException("Missing the META types factory");

        this.composedTypeFactory = new SqlComposedTypeFactory(typeFactory, customTypes);
        this.pluginFactory = pluginFactory;
        this.validatorFactory = validatorFactory;
        this.monitorFactory = monitorFactory;
        this.lazyInit = lazyInit;

        try {
            Set<String> setSelectQueries = (onlyStatements != null && onlyStatements.length > 0) ? new HashSet<String>(
                    Arrays.asList(onlyStatements)) : null;

            filter = (filter != null) ? filter.toUpperCase() : null;
            Map<String, Object> defaultFeatures = SqlUtils.getDefaultFeatures(filter);

            StringBuilder errors = new StringBuilder();
            SqlProcessor processor = null;

            try {
                processor = (lazyInit) ? SqlProcessor.getLazyInstance(sbStatements, composedTypeFactory,
                        defaultFeatures, setSelectQueries, filter) : SqlProcessor.getInstance(sbStatements,
                        composedTypeFactory, defaultFeatures, setSelectQueries, filter);
            } catch (SqlEngineException see) {
                errors.append(see.getMessage());
            }

            if (errors.length() > 0)
                throw new SqlEngineException(errors.toString());

            if (!processor.getWarnings().isEmpty()) {
                logger.warn("There're warnings in the process of statements parsing: " + processor.getWarnings());
            }

            sqls = processor.getMetaStatements(SqlProcessor.StatementType.QRY);
            cruds = processor.getMetaStatements(SqlProcessor.StatementType.CRUD);
            calls = processor.getMetaStatements(SqlProcessor.StatementType.CALL);
            outs = processor.getMappingRules(SqlProcessor.MappingType.OUT);
            features = processor.getFeatures();
            statementsFeatures = processor.getStatementsFeatures();
            statementsFeaturesUnset = processor.getStatementsFeaturesUnset();

            for (String name : outs.keySet()) {
                if (!sqls.containsKey(name) && !calls.containsKey(name) && !cruds.containsKey(name))
                    errors.append("For the OUT/FIELDS there's no QRY: ").append(name).append("\n");
            }

            if (errors.length() > 0)
                throw new SqlEngineException(errors.toString());

            if (!lazyInit) {
                for (String name : sqls.keySet()) {
                    getQueryEngine(name, errors);
                }

                for (String name : cruds.keySet()) {
                    getCrudEngine(name, errors);
                }

                for (String name : calls.keySet()) {
                    getProcedureEngine(name, errors);
                }

                if (errors.length() > 0)
                    throw new SqlEngineException(errors.toString());
            }
        } finally {
            long end = System.currentTimeMillis();
            logger.warn("== SqlProcessorLoader, lazyInit=" + lazyInit + ", duration in ms=" + (end - start));

            if (logger.isDebugEnabled()) {
                logger.debug("<< SqlProcessorLoader, engines=" + engines + ", sqls=" + sqls + ", cruds=" + cruds
                        + ", fields=" + outs + ", features=" + features);
            }
        }
    }

    /**
     * Some filters can be the optional features in the statement context.
     * 
     * @param name
     *            the name of the META SQL statement
     */
    private void loadStatementFeatures(String name) {
        SqlEngine sqlEngine = engines.get(name);
        Map<String, Object> statementFeatures = statementsFeatures.get(name);
        if (statementFeatures != null) {
            for (Map.Entry<String, Object> entry : statementFeatures.entrySet()) {
                sqlEngine.setFeature(entry.getKey(), entry.getValue());
            }
        }
        Set<String> statementFeaturesUnset = statementsFeaturesUnset.get(name);
        if (statementFeaturesUnset != null) {
            sqlEngine.unsetFeatures(statementFeaturesUnset);
        }
    }

    /**
     * Returns the collection of names of all initialized/constructed SQL Engine instances.
     * 
     * @return The collection of all initialized SQL Engine instances' names
     */
    public Collection<String> getNames() {
        return engines.keySet();
    }

    /**
     * Returns the named SQL Query Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Query Engine instance
     * @param errors
     *            the container for the error messages
     * @return the SQL Engine instance or null value in the case the related statement is missing
     */
    private SqlQueryEngine getQueryEngine(String name, StringBuilder errors) {
        SqlEngine o = engines.get(name);
        if (o == null && sqls.containsKey(name)) {
            SqlMetaStatement stmt = sqls.get(name);

            synchronized (stmt) {
                o = engines.get(name);
                if (o == null) {
                    if (lazyInit)
                        stmt.compile(name, composedTypeFactory);
                    SqlMappingRule mapping = null;
                    if (!stmt.isHasOutputMapping() && !outs.containsKey(name)) {
                        if (errors != null)
                            errors.append("For the QRY there's no OUT: ").append(name).append("\n");
                    } else if (outs.containsKey(name)) {
                        mapping = outs.get(name);
                        if (lazyInit)
                            mapping.compile(name, composedTypeFactory);
                    } else {
                        mapping = new SqlMappingRule();
                    }
                    SqlMonitor monitor = (monitorFactory != null) ? monitorFactory.getSqlMonitor(name, features) : null;
                    engines.put(name, o = new SqlQueryEngine(name, stmt, mapping, monitor, features,
                            this.composedTypeFactory, this.pluginFactory));
                    loadStatementFeatures(name);
                }
            }
        }
        if (o != null && o instanceof SqlQueryEngine)
            return (SqlQueryEngine) o;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlQueryEngine getQueryEngine(String name) {
        return getQueryEngine(name, null);
    }

    /**
     * Returns the named SQL CRUD Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL CRUD Engine instance
     * @param errors
     *            the container for the error messages
     * @return the SQL Engine instance or null value in the case the related statement is missing
     */
    private SqlCrudEngine getCrudEngine(String name, StringBuilder errors) {
        SqlEngine o = engines.get(name);
        if (o == null && cruds.containsKey(name)) {
            SqlMetaStatement stmt = cruds.get(name);

            synchronized (stmt) {
                o = engines.get(name);
                if (o == null) {
                    if (lazyInit)
                        stmt.compile(name, composedTypeFactory);
                    SqlValidator validator = (validatorFactory != null) ? validatorFactory.getSqlValidator() : null;
                    SqlMappingRule mapping = null;
                    if (outs.containsKey(name)) {
                        mapping = outs.get(name);
                    }
                    SqlMonitor monitor = (monitorFactory != null) ? monitorFactory.getSqlMonitor(name, features) : null;
                    engines.put(name, o = new SqlCrudEngine(name, stmt, mapping, monitor, features,
                            this.composedTypeFactory, this.pluginFactory));
                    engines.get(name).setValidator(validator);
                    loadStatementFeatures(name);
                }
            }
        }
        if (o != null && o instanceof SqlCrudEngine)
            return (SqlCrudEngine) o;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlCrudEngine getCrudEngine(String name) {
        return getCrudEngine(name, null);
    }

    /**
     * Returns the named SQL Procedure Engine instance (the primary SQL Processor class).
     * 
     * @param name
     *            the name of the required SQL Procedure Engine instance
     * @param errors
     *            the container for the error messages
     * @return the SQL Engine instance or null value in the case the related statement is missing
     */
    private SqlProcedureEngine getProcedureEngine(String name, StringBuilder errors) {
        SqlEngine o = engines.get(name);
        if (o == null && calls.containsKey(name)) {
            SqlMetaStatement stmt = calls.get(name);

            synchronized (stmt) {
                o = engines.get(name);
                if (o == null) {
                    if (lazyInit)
                        stmt.compile(name, composedTypeFactory);
                    SqlMappingRule mapping = null;
                    if (outs.containsKey(name)) {
                        mapping = outs.get(name);
                        if (lazyInit)
                            mapping.compile(name, composedTypeFactory);
                    } else {
                        mapping = new SqlMappingRule();
                    }
                    SqlMonitor monitor = (monitorFactory != null) ? monitorFactory.getSqlMonitor(name, features) : null;
                    engines.put(name, o = new SqlProcedureEngine(name, stmt, mapping, monitor, features,
                            this.composedTypeFactory, this.pluginFactory));
                    loadStatementFeatures(name);
                }
            }
        }
        if (o != null && o instanceof SqlProcedureEngine)
            return (SqlProcedureEngine) o;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlProcedureEngine getProcedureEngine(String name) {
        return getProcedureEngine(name, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlQueryEngine getCheckedQueryEngine(String name) throws SqlEngineException {
        SqlQueryEngine queryEngine = getQueryEngine(name);
        if (queryEngine == null)
            throw new SqlEngineException("Missing SqlQueryEngine " + name);
        return queryEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlCrudEngine getCheckedCrudEngine(String name) {
        SqlCrudEngine queryEngine = getCrudEngine(name);
        if (queryEngine == null)
            throw new SqlEngineException("Missing SqlQueryEngine " + name);
        return queryEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlProcedureEngine getCheckedProcedureEngine(String name) {
        SqlProcedureEngine procedureEngine = getProcedureEngine(name);
        if (procedureEngine == null)
            throw new SqlEngineException("Missing SqlQueryEngine " + name);
        return procedureEngine;
    }
}
