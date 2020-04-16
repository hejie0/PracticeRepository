package rpc_framework.zookeeper.client.loadbanalce;

import java.util.List;
import java.util.Random;

public class RandomLoadBanalce extends AbstractLoadBanalce {

    @Override
    protected String doSelect(List<String> repos) {
        int len = repos.size();
        Random random = new Random();
        return repos.get(random.nextInt(len));
    }
}
