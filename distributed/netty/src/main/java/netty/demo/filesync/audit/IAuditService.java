package netty.demo.filesync.audit;

import com.google.inject.ImplementedBy;

import java.util.List;

/**
 * @Author: hejie
 * @Date: 2021/6/3 11:06
 * @Version: 1.0
 */
@ImplementedBy(AuditServiceImpl.class)
public interface IAuditService {

    void addAudit(Audit audit);
    List<Audit> getAudit();

}
