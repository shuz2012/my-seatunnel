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

package org.apache.seatunnel.e2e.transform;

import org.apache.seatunnel.e2e.common.container.TestContainer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.testcontainers.containers.Container;

import java.io.IOException;

public class TestFieldMapperIT extends TestSuiteBase {

    @TestTemplate
    public void testFieldMapper(TestContainer container) throws IOException, InterruptedException {
        Container.ExecResult execResult = container.executeJob("/field_mapper_transform.conf");
        Assertions.assertEquals(0, execResult.getExitCode());

        Container.ExecResult execResult1 =
                container.executeJob("/field_mapper_transform_without_result_table.conf");
        Assertions.assertEquals(0, execResult1.getExitCode());
    }

    @TestTemplate
    public void testFieldMapperMultiTable(TestContainer container)
            throws IOException, InterruptedException {
        Container.ExecResult execResult =
                container.executeJob("/field_mapper_transform_multi_table.conf");
        Assertions.assertEquals(0, execResult.getExitCode());
    }
}
