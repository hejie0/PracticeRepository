package rpc_framework.zookeeper.client.loadbanalce;

import java.util.List;

public interface LoadBanalce {

    String selectHost(List<String> repos);

}
