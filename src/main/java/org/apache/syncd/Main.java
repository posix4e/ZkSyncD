package org.apache.syncd; /**                      3
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;


public class Main {

    static Configuration config = new SystemConfiguration();
    public static void main(String [] args) throws IOException, KeeperException, InterruptedException {

        if (args.length != 2) {
            System.out.println("usage: zksyncd <zookeeper_connection_string> <destination_directory>");
            System.out.println("For documentation on the zookeeper connection string check the zookeeper api.");
        }
        config.setProperty(ZkSyncConsts.CONNECTION_STRING, args[0]);
        config.setProperty(ZkSyncConsts.DESTINATION_DIRECTORY, args[1]);
        SyncBlock syncBlock = new SyncBlock();
        syncBlock.run();
        syncBlock.join();
    }
}
