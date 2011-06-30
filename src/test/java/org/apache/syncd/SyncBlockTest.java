package org.apache.syncd;
/**
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

import junit.framework.TestCase;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.apache.zookeeper.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

public class SyncBlockTest extends TestCase implements Watcher {
  private static final String DESTINATION_DIRECTORY = "/";
  private static final Configuration conf = HBaseConfiguration.create();
  private static HBaseTestingUtility TEST_UTIL;
  private static SyncBlock SYNC_BLOCK;
  private String CONNECTION_STRING = "";

  @BeforeClass
  public void setUp() throws Exception {
    TEST_UTIL = new HBaseTestingUtility(conf);
    MiniZooKeeperCluster cluster = TEST_UTIL.startMiniZKCluster();
    Main.config = new SystemConfiguration();
    CONNECTION_STRING += "127.0.0.1:";
    CONNECTION_STRING += String.valueOf(cluster.getClientPort());
    Main.config.setProperty(ZkSyncConsts.CONNECTION_STRING, CONNECTION_STRING);
    Main.config.setProperty(ZkSyncConsts.DESTINATION_DIRECTORY, DESTINATION_DIRECTORY);
    SYNC_BLOCK = new SyncBlock();
    SYNC_BLOCK.start();

  }
  @AfterClass
  public void tearDown() throws IOException, InterruptedException {
    SyncBlock.RUNNING = false;
    synchronized (SyncBlock.MUTEX){
      SyncBlock.MUTEX.notify();
    }
    TEST_UTIL.shutdownMiniZKCluster();
  }

  public void testProcess() throws IOException, InterruptedException, KeeperException {
    ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STRING, 300, this);
    Thread.sleep(10000);

    zooKeeper.create("/test", "baz".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                CreateMode.PERSISTENT);
    Thread.sleep(10000);

  }

  public void testPrintAllChildren() throws Exception {

  }

  public void process(WatchedEvent event) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
