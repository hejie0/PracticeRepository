package netty.demo.filesync.audit;

import com.google.inject.Singleton;

import java.util.List;

/**
 * @Author: hejie
 * @Date: 2021/6/3 11:11
 * @Version: 1.0
 */
@Singleton
public class AuditServiceImpl implements IAuditService {
    @Override
    public void addAudit(Audit audit) {

    }

    @Override
    public List<Audit> getAudit() {
        return null;
    }
}
