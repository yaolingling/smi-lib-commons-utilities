/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

//import com.dell.esg.icee.common.exception.RuntimeCoreException;
//import com.dell.esg.icee.common.model.EnumErrorCode;

/**
 * Parser for filter query parameters. The parameter is expected to be a comma delimited string of column name and its value.
 *
 * EQUAL and CONTAIN operators are available.
 *
 * "eq,columnName,columnValue" to include resources where columnName is columnValue.
 *
 * "eq,columnName,columnValue,columnValue2[,columnValue3...]" to include resources where columnName is columnValue or columnValue2.
 *
 * "eq,columnName," to include resources where columnName is empty.
 *
 * "eq,columnName,,columnValue[,columnValue2...]" to include resources where columnName is empty or columnValue.
 *
 * "co,columnName,columnValue" to include resources where columnName contains columnValue.
 */
public class FilterParamParser {

    public static final char COLUMN_DELIMITER = ',';

    private List<String> filterParam;
    private List<FilterInfo> filterInfos = new LinkedList<>();
    private Set<String> validColumns;

    /**
     * filter operator enumeration.
     */
    public enum FilterOperator {
        // operator names should be lower-cased since that is how the FilterParamParser
        // compares them.
        EQUAL("eq", 1, -1, true), CONTAIN("co", 1, 1, false);

        private final String operator;
        private final int minimumNrValues;
        private final int maximumNrValues;
        private final boolean allowEmptyValue;


        private FilterOperator(String operator, int minimumNrValues, int maximumNrValues, boolean allowEmptyValue) {
            this.operator = operator;
            this.minimumNrValues = minimumNrValues;
            this.maximumNrValues = maximumNrValues;
            this.allowEmptyValue = allowEmptyValue;
        }


        public String getOperator() {
            return operator;
        }


        private int getMinimumNrValues() {
            return minimumNrValues;
        }


        private int getMaximumNrValues() {
            return maximumNrValues;
        }


        @Override
        public String toString() {
            return operator;
        }


        private boolean isAllowEmpty() {
            return allowEmptyValue;
        }
    }

    private static final Map<String, FilterOperator> filterOperators = new HashMap<>();

    static {
        for (FilterOperator filterOperator : FilterOperator.values()) {
            filterOperators.put(filterOperator.getOperator(), filterOperator);
        }
    }


    /**
     * Construct a parser for the given filter parameter value.
     *
     * @param filterParam the full filter parameters.
     * @param validColumns set of column names to use for validation against the filter parameter to be parsed. This parameter is optional and if null is used then no validation on
     * column names is done.
     */
    public FilterParamParser(List<String> filterParam, Set<String> validColumns) {
        this.filterParam = filterParam;
        this.validColumns = validColumns;
    }


    /**
     * Parses the filter parameter and returns the list of parsed information.
     *
     * @return list of column filtering information. It is not guaranteed that the order in the list is the order the columns were present in the filter query parameter.
     */
    public List<FilterInfo> parse() {

        filterInfos.clear();

        // Split the string and build a FilterInfo object for each
        // filter parameter.
        if (CollectionUtils.isEmpty(filterParam)) {
            return filterInfos;
        }

        List<String> columnInfo;
        for (String column : filterParam) {
            columnInfo = split(column);
            if (CollectionUtils.isEmpty(columnInfo)) {
                // TODO: return more specific enum -- RestCommonMessages.emptyFilterCriteria());
                // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                throw new RuntimeException("invalid data");
            }
            FilterInfo info = new FilterInfo(columnInfo);
            info.setSimpleFilter(false);
            filterInfos.add(info);
        }

        return filterInfos;
    }


    private List<String> split(String param) {
        LinkedList<String> stringList = new LinkedList<String>();
        int begin = 0;
        int end;

        do {
            end = param.indexOf(COLUMN_DELIMITER, begin);
            if (end != -1) {
                stringList.add(param.substring(begin, end).trim());
            } else {
                stringList.add(param.substring(begin).trim());
            }
            begin = end + 1;
        } while (end != -1);

        return stringList;
    }


    /**
     * Gets filter information. The parameter must be parsed first or this call will return an empty list. After parsing this method just returns the same list that was returned by
     * #parse and does not result in the parameter being parsed again.
     *
     * @return list of filter information.
     */
    public List<FilterInfo> getFilterInfos() {
        return filterInfos;
    }

    /**
     * Simple class for holding filtering information parsed for a filter query parameter value.
     */
    public class FilterInfo {

        private String columnName;
        private List<String> columnValue;
        private FilterOperator filterOperator = FilterOperator.EQUAL;
        private boolean simpleFilter = true;


        /**
         * Construct the Object. The input parameter will contain the name of the column as well as information about the filter operator.
         *
         * @param columnName the column to filter. columnValue the column value.
         * @param columnValue the column value
         */
        public FilterInfo(String columnName, String columnValue) {
            this.columnName = columnName;
            this.columnValue = new LinkedList<String>();
            this.columnValue.add(columnValue);
        }


        /**
         * Construct the Object. The input parameter will contain the name of the column as well as information about the filter operator.
         *
         * @param columnInfo information about the column to filter.
         */
        public FilterInfo(List<String> columnInfo) {
            // Operator
            setFilterOperator(parseFilterOperator(columnInfo.get(0)));

            // Check the number of values requirement.
            if (columnInfo.size() < 2 + getFilterOperator().getMinimumNrValues()) {
                // TODO: return more specific enum -- RestCommonMessages.tooFewFilterCriteria( getFilterOperator().toString() );
                // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                throw new RuntimeException("invalid data");
            }
            if (getFilterOperator().getMaximumNrValues() != -1 && columnInfo.size() > 2 + getFilterOperator().getMaximumNrValues()) {
                // TODO: return more specific enum -- RestCommonMessages.tooManyFilterCriteria(getFilterOperator().toString()));
                // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                throw new RuntimeException("invalid data");
            }

            // Column name
            setColumnName(columnInfo.get(1));
            if (getColumnName() == null || getColumnName().length() == 0) {
                // TODO:return more specific enum -- RestCommonMessages.emptyFilterColumnName(getFilterOperator().toString()));
                // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                throw new RuntimeException("invalid data");
            }
            if (null != validColumns) {
                if (!validColumns.contains(getColumnName())) {
                    // TODO:return more specific enum -- RestCommonMessages.invalidFilterColumn(getColumnName()));
                    // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                    throw new RuntimeException("invalid data");
                }
            }

            // Column value
            setColumnValue(columnInfo.subList(2, columnInfo.size()));
            if (!getFilterOperator().isAllowEmpty()) {
                for (String value : getColumnValue()) {
                    if (value.length() == 0) {
                        // TODO: return more specific enum --RestCommonMessages.emptyFilterColumnValue(getColumnName()));
                        // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                        throw new RuntimeException("invalid data");
                    }
                }
            }

        }


        /**
         * Helper method to parse out filter operator and return the operator.
         *
         * @param filterOperator string of filter operator
         * @return the filter operator object
         */
        private FilterOperator parseFilterOperator(String filterOperator) {

            FilterOperator operator = filterOperators.get(filterOperator);
            if (operator == null) {
                // TODO:return more specific enum -- RestCommonMessages.noMatchingFilterOperator(filterOperator));
                // throw new RuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA);
                throw new RuntimeException("invalid data");
            }

            return operator;
        }


        /**
         * Build a query value out of this filter info.
         *
         * @return query value
         */
        public String buildValueString() {
            StringBuilder valueString = new StringBuilder();

            if (this.simpleFilter) {
                valueString.append(columnValue.get(0));
            } else {
                valueString = valueString.append(filterOperator.toString());
                valueString.append(",").append(columnName).append(",");

                Iterator<String> itr = columnValue.iterator();
                String value;
                while (itr.hasNext()) {
                    value = itr.next();
                    valueString.append(value);
                    if (itr.hasNext()) {
                        valueString.append(",");
                    }
                }
            }

            return valueString.toString();
        }


        /**
         * Gets the column name.
         *
         * @return the column name.
         */
        public String getColumnName() {
            return columnName;
        }


        /**
         * Sets the column name.
         *
         * @param columnName the new columnName
         */
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }


        /**
         * Gets the filter operator.
         *
         * @return the filter operator.
         */
        public FilterOperator getFilterOperator() {
            return filterOperator;
        }


        /**
         * Sets the filter operator.
         *
         * @param filterOperator the new filter operator.
         */
        public void setFilterOperator(FilterOperator filterOperator) {
            this.filterOperator = filterOperator;
        }


        /**
         * Gets the column values.
         *
         * @return the column values.
         */
        public List<String> getColumnValue() {
            return columnValue;
        }


        /**
         * Sets the column values.
         *
         * @param columnValue the new column values.
         */
        public void setColumnValue(List<String> columnValue) {
            this.columnValue = columnValue;
        }


        /**
         * is specified as simple filter.
         *
         * @return true or false
         */
        public boolean isSimpleFilter() {
            return simpleFilter;
        }


        /**
         * Sets whether it is simple filter.
         *
         * @param simpleFilter the new state.
         */
        public void setSimpleFilter(boolean simpleFilter) {
            this.simpleFilter = simpleFilter;
        }

    }
}
