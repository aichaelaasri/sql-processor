/*
 * generated by Xtext
 */
package org.sqlproc.dsl.ui.contentassist;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.sqlproc.dsl.processorDsl.Artifacts;
import org.sqlproc.dsl.processorDsl.ColumnUsage;
import org.sqlproc.dsl.processorDsl.ConstantUsage;
import org.sqlproc.dsl.processorDsl.IdentifierUsage;
import org.sqlproc.dsl.processorDsl.MappingRule;
import org.sqlproc.dsl.processorDsl.MappingUsage;
import org.sqlproc.dsl.processorDsl.MetaStatement;
import org.sqlproc.dsl.processorDsl.PojoDefinition;
import org.sqlproc.dsl.processorDsl.PojoUsage;
import org.sqlproc.dsl.processorDsl.ProcessorDslPackage;
import org.sqlproc.dsl.processorDsl.TableDefinition;
import org.sqlproc.dsl.processorDsl.TableUsage;
import org.sqlproc.dsl.resolver.DbResolver;
import org.sqlproc.dsl.resolver.PojoResolver;

import com.google.inject.Inject;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class ProcessorDslProposalProvider extends AbstractProcessorDslProposalProvider {

    @Inject
    PojoResolver pojoResolver;

    @Inject
    DbResolver dbResolver;

    private static final List<String> ON_OFF = Arrays.asList(new String[] { "ON", "OFF" });
    private static final List<String> STATEMEN_TYPE = Arrays.asList(new String[] { "QRY", "CRUD", "CALL" });
    private static final List<String> MAPPING_TYPE = Arrays.asList(new String[] { "OUT" });
    private static final List<String> OPTION_TYPE = Arrays
            .asList(new String[] { "OPT", "LOPT", "IOPT", "SOPT", "BOPT" });
    private static final List<String> TYPES = Arrays.asList(new String[] { "int", "integer", "long", "byte", "short",
            "float", "double", "character", "char", "string", "str", "time", "date", "datetime", "timestamp", "stamp",
            "bool", "boolean", "bigint", "biginteger", "bigdec", "bigdecimal", "bytearr", "bytearray", "bytes", "text",
            "blob", "clob", "einteger", "eint", "enumstring", "estring", "fromdate", "todate", "h_big_decimal",
            "h_big_integer", "h_blob", "h_boolean", "h_binary", "h_byte", "h_clob", "h_timestamp", "h_date",
            "h_double", "h_float", "h_date", "h_character", "h_integer", "h_long", "h_short", "h_string", "h_text",
            "h_timestamp", "h_time" });

    @Override
    public void completeProperty_DoResolvePojo(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(ON_OFF, "ON_OFF", context, acceptor);
    }

    @Override
    public void completeProperty_DoResolveDb(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(ON_OFF, "ON_OFF", context, acceptor);
    }

    @Override
    public void completeMetaStatement_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(STATEMEN_TYPE, "STATEMEN_TYPE", context, acceptor);
    }

    @Override
    public void completeMappingRule_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(MAPPING_TYPE, "MAPPING_TYPE", context, acceptor);
    }

    @Override
    public void completeOptionalFeature_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(OPTION_TYPE, "OPTION_TYPE", context, acceptor);
    }

    @Override
    public void completeColumn_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(TYPES, "IDENT", context, acceptor);
    }

    @Override
    public void completeConstant_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(TYPES, "IDENT", context, acceptor);
    }

    @Override
    public void completeIdentifier_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(TYPES, "IDENT", context, acceptor);
    }

    @Override
    public void completeMappingItem_Type(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        addProposalList(TYPES, "IDENT", context, acceptor);
    }

    protected void addProposalList(List<String> values, String lexerRule, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (values == null)
            return;
        for (String value : values) {
            String proposal = getValueConverter().toString(value, lexerRule);
            ICompletionProposal completionProposal = createCompletionProposal(proposal, context);
            acceptor.accept(completionProposal);
        }
    }

    @Override
    public void completeColumn_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!completeUsage(model, assignment, context, acceptor, ProcessorDslPackage.Literals.COLUMN_USAGE.getName()))
            super.completeColumn_Name(model, assignment, context, acceptor);
    }

    @Override
    public void completeConstant_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!completeUsage(model, assignment, context, acceptor, ProcessorDslPackage.Literals.CONSTANT_USAGE.getName()))
            super.completeConstant_Name(model, assignment, context, acceptor);
    }

    @Override
    public void completeIdentifier_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!completeUsage(model, assignment, context, acceptor,
                ProcessorDslPackage.Literals.IDENTIFIER_USAGE.getName()))
            super.completeIdentifier_Name(model, assignment, context, acceptor);
    }

    public boolean completeUsage(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor, String name) {
        if (!isResolvePojo())
            return false;
        MetaStatement metaStatement = EcoreUtil2.getContainerOfType(model, MetaStatement.class);
        Artifacts artifacts = EcoreUtil2.getContainerOfType(metaStatement, Artifacts.class);
        IScope scope = getScopeProvider().getScope(artifacts, ProcessorDslPackage.Literals.ARTIFACTS__USAGES);
        PojoDefinition pojoDefinition = findPojo(artifacts.eResource().getResourceSet(), scope, name,
                metaStatement.getName());
        if (pojoDefinition == null) {
            String proposal = getValueConverter().toString("Error: I can't load pojo for " + model, "IDENT");
            ICompletionProposal completionProposal = createCompletionProposal(proposal, context);
            acceptor.accept(completionProposal);
            return true;
        }
        String prefix = context.getPrefix();
        int pos = prefix.lastIndexOf('.');
        if (pos > 0) {
            prefix = prefix.substring(0, pos + 1);
        } else {
            prefix = "";
        }
        String clazz = getClassName(pojoDefinition.getClass_(), prefix);
        if (clazz == null)
            return false;
        PropertyDescriptor[] descriptors = pojoResolver.getPropertyDescriptors(clazz);
        if (descriptors == null) {
            return false;
        } else {
            for (PropertyDescriptor descriptor : descriptors) {
                if ("class".equals(descriptor.getName()))
                    continue;
                String proposal = getValueConverter().toString(descriptor.getName(), "IDENT");
                ICompletionProposal completionProposal = createCompletionProposal(prefix + proposal, context);
                acceptor.accept(completionProposal);
            }
            return true;
        }
    }

    @Override
    public void completeMappingColumn_Name(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!isResolvePojo()) {
            super.completeMappingColumn_Name(model, assignment, context, acceptor);
            return;
        }
        MappingRule mappingRule = EcoreUtil2.getContainerOfType(model, MappingRule.class);
        Artifacts artifacts = EcoreUtil2.getContainerOfType(mappingRule, Artifacts.class);
        IScope scope = getScopeProvider().getScope(artifacts, ProcessorDslPackage.Literals.ARTIFACTS__USAGES);
        PojoDefinition pojoDefinition = findPojo(artifacts.eResource().getResourceSet(), scope,
                ProcessorDslPackage.Literals.MAPPING_USAGE.getName(), mappingRule.getName());
        if (pojoDefinition == null) {
            String proposal = getValueConverter().toString("Error: I can't load pojo for " + model, "IDENT");
            ICompletionProposal completionProposal = createCompletionProposal(proposal, context);
            acceptor.accept(completionProposal);
        }
        String prefix = context.getPrefix();
        int pos = prefix.lastIndexOf('.');
        if (pos > 0) {
            prefix = prefix.substring(0, pos + 1);
        } else {
            prefix = "";
        }
        String clazz = getClassName(pojoDefinition.getClass_(), prefix);
        if (clazz == null)
            return;
        PropertyDescriptor[] descriptors = pojoResolver.getPropertyDescriptors(clazz);
        if (descriptors == null) {
            super.completeMappingColumn_Name(model, assignment, context, acceptor);
        } else {
            for (PropertyDescriptor descriptor : descriptors) {
                if ("class".equals(descriptor.getName()))
                    continue;
                String proposal = getValueConverter().toString(descriptor.getName(), "IDENT");
                ICompletionProposal completionProposal = createCompletionProposal(prefix + proposal, context);
                acceptor.accept(completionProposal);
            }
        }
    }

    protected PojoDefinition findPojo(ResourceSet resourceSet, IScope scope, String typeName, String name) {
        Iterable<IEObjectDescription> iterable = scope.getAllElements();
        for (Iterator<IEObjectDescription> iter = iterable.iterator(); iter.hasNext();) {
            IEObjectDescription description = iter.next();
            if (typeName.equals(description.getEClass().getName())) {
                PojoUsage pojoUsage = (PojoUsage) resourceSet.getEObject(description.getEObjectURI(), true);
                if (name.equals(getUsageName(pojoUsage))) {
                    // return ((PojoUsage) description.getEObjectOrProxy()).getPojo(); // neni inicializovan!
                    return pojoUsage.getPojo();
                }
            }
        }
        return null;
    }

    protected String getUsageName(EObject pojoUsage) {
        if (pojoUsage instanceof ColumnUsage)
            return ((ColumnUsage) pojoUsage).getStatement().getName();
        if (pojoUsage instanceof IdentifierUsage)
            return ((IdentifierUsage) pojoUsage).getStatement().getName();
        if (pojoUsage instanceof ConstantUsage)
            return ((ConstantUsage) pojoUsage).getStatement().getName();
        if (pojoUsage instanceof MappingUsage)
            return ((MappingUsage) pojoUsage).getStatement().getName();
        return "";
    }

    protected boolean isPrimitive(Class<?> clazz) {
        if (clazz == null)
            return true;
        if (clazz.isPrimitive())
            return true;
        if (clazz == String.class)
            return true;
        if (clazz == java.util.Date.class)
            return true;
        if (clazz == java.sql.Date.class)
            return true;
        if (clazz == java.sql.Time.class)
            return true;
        if (clazz == java.sql.Timestamp.class)
            return true;
        if (clazz == java.sql.Blob.class)
            return true;
        if (clazz == java.sql.Clob.class)
            return true;
        if (clazz == java.math.BigDecimal.class)
            return true;
        if (clazz == java.math.BigInteger.class)
            return true;
        return false;
    }

    protected String getClassName(String baseClass, String property) {
        if (baseClass == null || property == null)
            return baseClass;
        int pos1 = property.indexOf('.');
        if (pos1 == -1)
            return baseClass;
        String checkProperty = property;
        pos1 = checkProperty.indexOf('=');
        if (pos1 > 0) {
            int pos2 = checkProperty.indexOf('.', pos1);
            if (pos2 > pos1)
                checkProperty = checkProperty.substring(0, pos1) + checkProperty.substring(pos2);
        }
        String innerProperty = null;
        pos1 = checkProperty.indexOf('.');
        if (pos1 > 0) {
            innerProperty = checkProperty.substring(pos1 + 1);
            checkProperty = checkProperty.substring(0, pos1);
        }
        PropertyDescriptor[] descriptors = pojoResolver.getPropertyDescriptors(baseClass);
        if (descriptors == null)
            return null;
        PropertyDescriptor innerDesriptor = null;
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals(checkProperty)) {
                innerDesriptor = descriptor;
                break;
            }
        }
        if (innerDesriptor == null)
            return null;
        Class<?> innerClass = innerDesriptor.getPropertyType();
        if (innerClass.isArray()) {
            ParameterizedType type = (ParameterizedType) innerDesriptor.getReadMethod().getGenericReturnType();
            if (type.getActualTypeArguments() == null || type.getActualTypeArguments().length == 0)
                return null;
            innerClass = (Class<?>) type.getActualTypeArguments()[0];
            if (isPrimitive(innerClass))
                return null;
            return getClassName(innerClass.getName(), innerProperty);
        } else if (Collection.class.isAssignableFrom(innerClass)) {
            ParameterizedType type = (ParameterizedType) innerDesriptor.getReadMethod().getGenericReturnType();
            if (type.getActualTypeArguments() == null || type.getActualTypeArguments().length == 0)
                return null;
            innerClass = (Class<?>) type.getActualTypeArguments()[0];
            if (isPrimitive(innerClass))
                return null;
            return getClassName(innerClass.getName(), innerProperty);
        } else {
            if (isPrimitive(innerClass))
                return null;
            return getClassName(innerClass.getName(), innerProperty);
        }
    }

    protected boolean isResolvePojo() {
        return pojoResolver.isResolvePojo();

    }

    protected boolean isResolveDb() {
        return dbResolver.isResolveDb();
    }

    @Override
    public void completeTableDefinition_Table(EObject model, Assignment assignment, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!isResolveDb()) {
            super.completeTableDefinition_Table(model, assignment, context, acceptor);
            return;
        }
        for (String table : dbResolver.getTables()) {
            if (table.indexOf('$') >= 0)
                continue;
            String proposal = getValueConverter().toString(table, "IDENT");
            ICompletionProposal completionProposal = createCompletionProposal(proposal, context);
            acceptor.accept(completionProposal);
        }
    }

    @Override
    public void complete_DatabaseColumn(EObject model, RuleCall ruleCall, ContentAssistContext context,
            ICompletionProposalAcceptor acceptor) {
        if (!isResolveDb()) {
            super.complete_DatabaseColumn(model, ruleCall, context, acceptor);
            return;
        }
        String prefix = context.getPrefix();
        int pos = prefix.indexOf('.');
        if (pos > 0) {
            prefix = prefix.substring(0, pos);
        } else {
            prefix = null;
        }
        MetaStatement metaStatement = EcoreUtil2.getContainerOfType(model, MetaStatement.class);
        Artifacts artifacts = EcoreUtil2.getContainerOfType(metaStatement, Artifacts.class);
        TableDefinition tableDefinition = getTableDefinition(artifacts, metaStatement, prefix);
        if (tableDefinition != null && tableDefinition.getTable() != null) {
            for (String column : dbResolver.getColumns(tableDefinition.getTable())) {
                String proposal = getValueConverter().toString(column, "IDENT");
                String completion = prefix != null ? prefix + '.' + proposal : proposal;
                ICompletionProposal completionProposal = createCompletionProposal(completion, context);
                acceptor.accept(completionProposal);
            }
        }
    }

    protected TableDefinition getTableDefinition(Artifacts artifacts, MetaStatement statement, String prefix) {
        TableUsage usage = null;
        IScope scope = getScopeProvider().getScope(artifacts, ProcessorDslPackage.Literals.ARTIFACTS__TABLE_USAGES);
        Iterable<IEObjectDescription> iterable = scope.getAllElements();
        for (Iterator<IEObjectDescription> iter = iterable.iterator(); iter.hasNext();) {
            IEObjectDescription description = iter.next();
            if (ProcessorDslPackage.Literals.TABLE_USAGE.getName().equals(description.getEClass().getName())) {
                TableUsage tableUsage = (TableUsage) artifacts.eResource().getResourceSet()
                        .getEObject(description.getEObjectURI(), true);
                if (tableUsage.getStatement().getName().equals(statement.getName())) {
                    if (prefix == null && tableUsage.getPrefix() == null) {
                        usage = tableUsage;
                        break;
                    }
                    if (prefix != null && prefix.equals(tableUsage.getPrefix())) {
                        usage = tableUsage;
                        break;
                    }
                }
            }
        }
        if (usage != null && usage.getTable() != null && usage.getTable().getName() != null) {
            scope = getScopeProvider().getScope(artifacts, ProcessorDslPackage.Literals.ARTIFACTS__TABLES);
            iterable = scope.getAllElements();
            for (Iterator<IEObjectDescription> iter = iterable.iterator(); iter.hasNext();) {
                IEObjectDescription description = iter.next();
                if (ProcessorDslPackage.Literals.TABLE_DEFINITION.getName().equals(description.getEClass().getName())) {
                    TableDefinition tableDefinition = (TableDefinition) artifacts.eResource().getResourceSet()
                            .getEObject(description.getEObjectURI(), true);
                    if (usage.getTable().getName().equals(tableDefinition.getName())) {
                        return tableDefinition;
                    }
                }
            }
        }
        return null;
    }
}
