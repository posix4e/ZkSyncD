package org.apache.syncd;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;


/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class SyncBlock extends Thread implements Watcher {
  static ZooKeeper ZOOKEEPER;
  static Integer MUTEX;
  static final String DEST = Main.config.getString(ZkSyncConsts.DESTINATION_DIRECTORY);
  static boolean RUNNING = true;

  SyncBlock() {
    if (ZOOKEEPER == null) {
      try {
        ZOOKEEPER = new ZooKeeper(Main.config.getString(ZkSyncConsts.CONNECTION_STRING),
            ZkSyncConsts.SESSION_TIMEOUT,
            this);
        MUTEX = -1;
      } catch (IOException e) {
        System.err.println("Error had to reset zk");
        e.printStackTrace();
        ZOOKEEPER = null;
      }
    }

  }

  synchronized public void process(WatchedEvent event) {
    System.out.println("process:" + event.toString());
    synchronized (MUTEX) {
      MUTEX.notify();
    }
  }

  public void printAllChildren(String node) throws InterruptedException, KeeperException {
    System.out.println(node);
    if (ZOOKEEPER.exists(node, false).getNumChildren() > 0) {
      List<String> children = ZOOKEEPER.getChildren(node, this);
      for (String child : children) {
        if (node.endsWith("/") ){
          printAllChildren(node + child);
        } else {
          printAllChildren(node + "/" + child);
        }
      }
    }
  }

  public void run() {
    while (RUNNING) {
      try {
        ZOOKEEPER.getChildren(DEST, this);
        synchronized (MUTEX) {
          MUTEX.wait();
          printAllChildren(DEST);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch (KeeperException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

    }
    System.out.println("Exit");
  }
}