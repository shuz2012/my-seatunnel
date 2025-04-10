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

package org.apache.seatunnel.connectors.seatunnel.elasticsearch.dto;

import org.apache.seatunnel.api.configuration.ReadonlyConfig;
import org.apache.seatunnel.connectors.seatunnel.elasticsearch.config.ElasticsearchSinkOptions;

import lombok.Data;

/** index config by seatunnel */
@Data
public class IndexInfo {

    private String index;
    private String type;
    private String[] primaryKeys;
    private String keyDelimiter;

    public IndexInfo(String index, ReadonlyConfig config) {
        this.index = index;
        type = config.get(ElasticsearchSinkOptions.INDEX_TYPE);
        if (config.getOptional(ElasticsearchSinkOptions.PRIMARY_KEYS).isPresent()) {
            primaryKeys = config.get(ElasticsearchSinkOptions.PRIMARY_KEYS).toArray(new String[0]);
        }
        keyDelimiter = config.get(ElasticsearchSinkOptions.KEY_DELIMITER);
    }
}
