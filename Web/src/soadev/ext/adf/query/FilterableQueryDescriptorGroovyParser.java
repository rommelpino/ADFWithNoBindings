package soadev.ext.adf.query;

import groovy.util.Eval;

import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.AttributeDescriptor;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import org.codehaus.groovy.control.CompilationFailedException;

import soadev.ext.trinidad.model.SortableFilterableModel;

public class FilterableQueryDescriptorGroovyParser {
    public FilterableQueryDescriptorGroovyParser() {
        super();
    }
    
    public static void handleFilter(QueryEvent event) {
        RichTable table = (RichTable)event.getSource();
        SortableFilterableModel colModel =
            (SortableFilterableModel)table.getValue();

        FilterableQueryDescriptor descriptor =
            (FilterableQueryDescriptor)event.getDescriptor();
        String expression = parseGroovyExpression("o", descriptor);
        System.out.println(expression);
        List sortedFilteredList = colModel.getSortedFilteredIndexList();
        List wrappedData = (List)colModel.getWrappedData();
        sortedFilteredList.clear();

        try {
            int index = 0;
            for (Object obj : wrappedData) {
                if (expression == null || "".equals(expression)) {
                    sortedFilteredList.add(index);
                } else {
                    Boolean result = (Boolean)Eval.me("o", obj, expression);
                    result = result == null ? false : result;
                    if (result) {
                        sortedFilteredList.add(index);
                        colModel.setSortCriteria(colModel.getSortCriteria());
                    }
                }
                index++;
            }
        } catch (CompilationFailedException cfe) {
            System.out.println("CompilationFailedException on expression: " + expression);
        }
        
        
    }

    public static String parseGroovyExpression(String var,
                               FilterableQueryDescriptor descriptor) {
        var = var + ".";
        Map<String, Object> filterCriteria = descriptor.getFilterCriteria();
        StringWriter sw = new StringWriter();
        List<AttributeCriterion> criteria =
            ((FilterableQueryDescriptorImpl)descriptor).parseFilterCriteria();
        int index = 0;
        for (Criterion criterion : criteria) {
            if (criterion instanceof AttributeCriterionImpl) {
                AttributeCriterionImpl attrCriterion =
                    (AttributeCriterionImpl)criterion;
                if (index++ > 0) {
                    if (attrCriterion.getBeforeConjunction() ==
                        ConjunctionCriterion.Conjunction.OR) {
                        sw.append(" || ");
                    } else {
                        sw.append(" && ");
                    }
                }
                OperatorDef operator =
                    (OperatorDef)attrCriterion.getOperator().getValue();
                List values = attrCriterion.getValues();
                Object value = null;
                Object value1 = null;
                if (!values.isEmpty()) {
                    value = values.get(0);
                }
                if (values.size() > 1) {
                    value1 = values.get(1);
                }
                String attrName = attrCriterion.getAttribute().getName();
                String type =
                    attrCriterion.getAttribute().getType().getCanonicalName();
                sw.append(var);
                sw.append(attrName);
                AttributeDef attrDef = attrCriterion.getAttributeDef();
                if (operator == null) {
                    operator = OperatorDef.EQUALS;
                } else {
                    //non string operator applied to string
                    if (type.equals(Constants.STRING_TYPE) &&
                        !OperatorDef.isStringOperator(operator)) {
                        operator = attrDef.getDefaultOperator();
                    }
                    //string operator applied to non string
                    if (!type.equals(Constants.STRING_TYPE) &&
                        OperatorDef.isStringOperator(operator) &&
                        operator != OperatorDef.EQUALS) {
                        operator = attrDef.getDefaultOperator();
                    }
                }
                if ("java.util.Date".equals(type)) {
                    value = ((Date)value).getTime();
                    sw.append(".getTime()");
                }
                if ("java.util.Calendar".equals(type)) {
                    value = ((Calendar)value).getTimeInMillis();
                    sw.append(".getTimeInMillis()");
                }
                String regex = value.toString();
                regex = regex.replace("*", ".*");
                switch (operator) {
                case EQUALS:
                    sw.append(" == ");
                    sw.append(value.toString());
                    break;
                case GREATER_THAN:
                    sw.append(" > ");
                    sw.append(value.toString());
                    break;
                case GREATER_THAN_EQUALS:
                    sw.append(" >= ");
                    sw.append(value.toString());
                    break;
                case LESS_THAN:
                    sw.append(" < ");
                    sw.append(value.toString());
                    break;
                case LESS_THAN_EQUALS:
                    sw.append(" <= ");
                    sw.append(value.toString());
                    break;

                case STARTS_WITH:
                    sw.append(".matches('");
                    sw.append(regex);
                    sw.append(".*')");
                    break;
                case ENDS_WITH:
                    sw.append(".matches('.*");
                    sw.append(regex);
                    sw.append("')");
                    break;
                case CONTAINS:
                    sw.append(".matches('.*");
                    sw.append(regex);
                    sw.append(".*')");
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected operator");
                }

            }
        }
        return sw.toString();
    }
}
