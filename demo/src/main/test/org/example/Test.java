package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;

public class Test {
  private final String LOCAL_HOST="127.0.0.1";
  private final String ZK_PORT="2181";

  private ZooKeeper zooKeeper;
  @Before
  public void initZK(){
    try {
      zooKeeper=new ZooKeeper(LOCAL_HOST+":"+ZK_PORT,5000,null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @org.junit.Test
  public void test(){
    try {
      System.out.println(
          "zooKeeper.create(\"/ljc/hello\",\"Hello ZK\".getBytes(StandardCharsets.UTF_8),null,\n          CreateMode.PERSISTENT) = "
              + zooKeeper.create("/ljc/hello", "Hello ZK".getBytes(StandardCharsets.UTF_8), Ids.OPEN_ACL_UNSAFE,
              CreateMode.PERSISTENT));
    } catch (KeeperException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }finally {
      if(zooKeeper!=null){
        try {
          zooKeeper.close();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

}
