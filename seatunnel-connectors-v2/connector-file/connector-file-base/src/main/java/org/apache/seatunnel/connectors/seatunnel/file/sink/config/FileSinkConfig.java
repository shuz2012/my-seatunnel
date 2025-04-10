/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.connectors.seatunnel.file.sink.config;

import org.apache.seatunnel.shade.com.typesafe.config.Config;

import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.common.exception.CommonErrorCodeDeprecated;
import org.apache.seatunnel.connectors.seatunnel.file.config.BaseFileSinkConfig;
import org.apache.seatunnel.connectors.seatunnel.file.config.BaseSinkConfig;
import org.apache.seatunnel.connectors.seatunnel.file.config.FileFormat;
import org.apache.seatunnel.connectors.seatunnel.file.config.PartitionConfig;
import org.apache.seatunnel.connectors.seatunnel.file.exception.FileConnectorException;
import org.apache.seatunnel.format.csv.constant.CsvStringQuoteMode;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.seatunnel.shade.com.google.common.base.Preconditions.checkArgument;

@Data
public class FileSinkConfig extends BaseFileSinkConfig implements PartitionConfig {

    private List<String> sinkColumnList;

    private List<String> partitionFieldList;

    private String partitionDirExpression;

    private boolean isPartitionFieldWriteInFile =
            BaseSinkConfig.IS_PARTITION_FIELD_WRITE_IN_FILE.defaultValue();

    private String tmpPath = BaseSinkConfig.TMP_PATH.defaultValue();

    private String fileNameTimeFormat = BaseSinkConfig.FILENAME_TIME_FORMAT.defaultValue();

    private boolean isEnableTransaction = BaseSinkConfig.IS_ENABLE_TRANSACTION.defaultValue();

    private String encoding = BaseSinkConfig.ENCODING.defaultValue();

    // ---------------------generator by config params-------------------

    private List<Integer> sinkColumnsIndexInRow;

    private List<Integer> partitionFieldsIndexInRow;

    private int maxRowsInMemory;

    private String sheetName;

    private String xmlRootTag = BaseSinkConfig.XML_ROOT_TAG.defaultValue();

    private String xmlRowTag = BaseSinkConfig.XML_ROW_TAG.defaultValue();

    private Boolean xmlUseAttrFormat;

    private Boolean parquetWriteTimestampAsInt96 =
            BaseSinkConfig.PARQUET_AVRO_WRITE_TIMESTAMP_AS_INT96.defaultValue();

    private List<String> parquetAvroWriteFixedAsInt96 =
            BaseSinkConfig.PARQUET_AVRO_WRITE_FIXED_AS_INT96.defaultValue();

    private CsvStringQuoteMode csvStringQuoteMode =
            BaseSinkConfig.CSV_STRING_QUOTE_MODE.defaultValue();

    public FileSinkConfig(@NonNull Config config, @NonNull SeaTunnelRowType seaTunnelRowTypeInfo) {
        super(config);
        checkArgument(
                !CollectionUtils.isEmpty(Arrays.asList(seaTunnelRowTypeInfo.getFieldNames())));

        if (config.hasPath(BaseSinkConfig.SINK_COLUMNS.key())
                && !CollectionUtils.isEmpty(
                        config.getStringList(BaseSinkConfig.SINK_COLUMNS.key()))) {
            this.sinkColumnList = config.getStringList(BaseSinkConfig.SINK_COLUMNS.key());
        }

        // if the config sink_columns is empty, all fields in SeaTunnelRowTypeInfo will being write
        if (CollectionUtils.isEmpty(this.sinkColumnList)) {
            // construct a new ArrayList, because `list` generated by `Arrays.asList` do not support
            // remove and add operations.
            this.sinkColumnList =
                    new ArrayList<>(Arrays.asList(seaTunnelRowTypeInfo.getFieldNames()));
        }

        if (config.hasPath(BaseSinkConfig.PARTITION_BY.key())) {
            this.partitionFieldList = config.getStringList(BaseSinkConfig.PARTITION_BY.key());
        } else {
            this.partitionFieldList = Collections.emptyList();
        }

        if (config.hasPath(BaseSinkConfig.PARTITION_DIR_EXPRESSION.key())
                && !StringUtils.isBlank(
                        config.getString(BaseSinkConfig.PARTITION_DIR_EXPRESSION.key()))) {
            this.partitionDirExpression =
                    config.getString(BaseSinkConfig.PARTITION_DIR_EXPRESSION.key());
        }

        if (config.hasPath(BaseSinkConfig.IS_PARTITION_FIELD_WRITE_IN_FILE.key())) {
            this.isPartitionFieldWriteInFile =
                    config.getBoolean(BaseSinkConfig.IS_PARTITION_FIELD_WRITE_IN_FILE.key());
        }

        if (config.hasPath(BaseSinkConfig.TMP_PATH.key())
                && !StringUtils.isBlank(config.getString(BaseSinkConfig.TMP_PATH.key()))) {
            this.tmpPath = config.getString(BaseSinkConfig.TMP_PATH.key());
        }

        if (config.hasPath(BaseSinkConfig.FILENAME_TIME_FORMAT.key())
                && !StringUtils.isBlank(
                        config.getString(BaseSinkConfig.FILENAME_TIME_FORMAT.key()))) {
            this.fileNameTimeFormat = config.getString(BaseSinkConfig.FILENAME_TIME_FORMAT.key());
        }

        if (config.hasPath(BaseSinkConfig.IS_ENABLE_TRANSACTION.key())) {
            this.isEnableTransaction =
                    config.getBoolean(BaseSinkConfig.IS_ENABLE_TRANSACTION.key());
        }

        if (config.hasPath(BaseSinkConfig.ENCODING.key())) {
            this.encoding = config.getString(BaseSinkConfig.ENCODING.key());
        }

        if (this.isEnableTransaction
                && !this.fileNameExpression.contains(BaseSinkConfig.TRANSACTION_EXPRESSION)) {
            throw new FileConnectorException(
                    CommonErrorCodeDeprecated.ILLEGAL_ARGUMENT,
                    "file_name_expression must contains "
                            + BaseSinkConfig.TRANSACTION_EXPRESSION
                            + " when is_enable_transaction is true");
        }

        // check partition field must in seaTunnelRowTypeInfo
        if (!CollectionUtils.isEmpty(this.partitionFieldList)
                && (CollectionUtils.isEmpty(this.sinkColumnList)
                        || !new HashSet<>(this.sinkColumnList)
                                .containsAll(this.partitionFieldList))) {
            throw new FileConnectorException(
                    CommonErrorCodeDeprecated.ILLEGAL_ARGUMENT,
                    "partition fields must in sink columns");
        }

        if (!CollectionUtils.isEmpty(this.partitionFieldList) && !isPartitionFieldWriteInFile) {
            if (!this.sinkColumnList.removeAll(this.partitionFieldList)) {
                throw new FileConnectorException(
                        CommonErrorCodeDeprecated.ILLEGAL_ARGUMENT,
                        "remove partition field from sink columns error");
            }
        }

        if (CollectionUtils.isEmpty(this.sinkColumnList)) {
            throw new FileConnectorException(
                    CommonErrorCodeDeprecated.ILLEGAL_ARGUMENT, "sink columns can not be empty");
        }

        Map<String, Integer> columnsMap =
                new HashMap<>(seaTunnelRowTypeInfo.getFieldNames().length);
        String[] fieldNames = seaTunnelRowTypeInfo.getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            columnsMap.put(fieldNames[i].toLowerCase(), i);
        }

        // init sink column index and partition field index, we will use the column index to found
        // the data in SeaTunnelRow
        this.sinkColumnsIndexInRow =
                this.sinkColumnList.stream()
                        .map(column -> columnsMap.get(column.toLowerCase()))
                        .filter(e -> e != null)
                        .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(this.partitionFieldList)) {
            this.partitionFieldsIndexInRow =
                    this.partitionFieldList.stream()
                            .map(columnsMap::get)
                            .collect(Collectors.toList());
        }

        if (config.hasPath(BaseSinkConfig.MAX_ROWS_IN_MEMORY.key())) {
            this.maxRowsInMemory = config.getInt(BaseSinkConfig.MAX_ROWS_IN_MEMORY.key());
        }

        if (config.hasPath(BaseSinkConfig.SHEET_NAME.key())) {
            this.sheetName = config.getString(BaseSinkConfig.SHEET_NAME.key());
        }

        if (FileFormat.XML.equals(this.fileFormat)) {
            if (!config.hasPath(BaseSinkConfig.XML_USE_ATTR_FORMAT.key())) {
                throw new FileConnectorException(
                        CommonErrorCodeDeprecated.ILLEGAL_ARGUMENT,
                        "User must define xml_use_attr_format when file_format_type is xml");
            }

            this.xmlUseAttrFormat = config.getBoolean(BaseSinkConfig.XML_USE_ATTR_FORMAT.key());

            if (config.hasPath(BaseSinkConfig.XML_ROOT_TAG.key())) {
                this.xmlRootTag = config.getString(BaseSinkConfig.XML_ROOT_TAG.key());
            }

            if (config.hasPath(BaseSinkConfig.XML_ROW_TAG.key())) {
                this.xmlRowTag = config.getString(BaseSinkConfig.XML_ROW_TAG.key());
            }
        }

        if (FileFormat.PARQUET.equals(this.fileFormat)) {
            if (config.hasPath(BaseSinkConfig.PARQUET_AVRO_WRITE_TIMESTAMP_AS_INT96.key())) {
                this.parquetWriteTimestampAsInt96 =
                        config.getBoolean(
                                BaseSinkConfig.PARQUET_AVRO_WRITE_TIMESTAMP_AS_INT96.key());
            }
            if (config.hasPath(BaseSinkConfig.PARQUET_AVRO_WRITE_FIXED_AS_INT96.key())) {
                this.parquetAvroWriteFixedAsInt96 =
                        config.getStringList(
                                BaseSinkConfig.PARQUET_AVRO_WRITE_FIXED_AS_INT96.key());
            }
        }

        if (FileFormat.CSV.equals(this.fileFormat)) {
            if (config.hasPath(BaseSinkConfig.CSV_STRING_QUOTE_MODE.key())) {
                this.csvStringQuoteMode =
                        CsvStringQuoteMode.valueOf(
                                config.getString(BaseSinkConfig.CSV_STRING_QUOTE_MODE.key()));
            }
        }
    }
}
