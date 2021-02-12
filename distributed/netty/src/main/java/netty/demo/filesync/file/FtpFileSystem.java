package netty.demo.filesync.file;

import netty.demo.filesync.task.FtpWorkPattern;
import netty.demo.filesync.task.SyncMode;
import netty.demo.filesync.task.Task;
import org.apache.commons.net.ftp.*;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PoolUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;
import java.util.TimerTask;

import static io.netty.util.internal.ThrowableUtil.stackTraceToString;

public class FtpFileSystem extends BaseFileSystem {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String ip;
    private int port;
    private String username;
    private String password;
    private FtpWorkPattern ftpWorkPattern;
    private String ftpCharset;
    private ObjectPool<FTPClient> pool;
    private TimerTask timerTask;
    private boolean isEnableMLST;
    private boolean isEnableMDTM;
    private boolean closedLog = false;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FtpWorkPattern getFtpWorkPattern() {
        return ftpWorkPattern;
    }

    public void setFtpWorkPattern(FtpWorkPattern ftpWorkPattern) {
        this.ftpWorkPattern = ftpWorkPattern;
    }

    public String getFtpCharset() {
        return ftpCharset;
    }

    public void setFtpCharset(String ftpCharset) {
        this.ftpCharset = ftpCharset;
    }

    private class NullFtpClientException extends Exception {
    }

    public class FtpClientPoolFactory extends BasePooledObjectFactory<FTPClient> {

        @Override
        public FTPClient create() throws Exception {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.setControlEncoding(ftpCharset);
                ftpClient.setConnectTimeout(10 * 1000);
                ftpClient.setDataTimeout(60 * 1000);
                ftpClient.connect(ip, port);
                ftpClient.setKeepAlive(true);
                ftpClient.setSoTimeout(10 * 60 * 1000);
                boolean success = ftpClient.login(username, password);
                if (!success) {
                    throw new Exception(String.format("ftp login failed, server: %s:%s, user: %s, password: %s",
                            ip, port, username, password));
                }

                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                isEnableMLST = ftpClient.hasFeature(FTPCmd.MLST.name());
                isEnableMLST = ftpClient.hasFeature(FTPCmd.MDTM.name());

                if (log.isDebugEnabled()) {
                    log.debug("ftp client has been created, {}->{}", ftpClient.getLocalAddress(), ftpClient.getRemoteAddress());
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("can't create ftp client, error: {}", stackTraceToString(e));
                }
                ftpClient.disconnect();
                ftpClient = null;
            }
            return ftpClient;
        }

        @Override
        public PooledObject<FTPClient> wrap(FTPClient obj) {
            return new DefaultPooledObject<>(obj);
        }

        @Override
        public void destroyObject(PooledObject<FTPClient> p) throws Exception {
            FTPClient ftpClient = p.getObject();

            try {
                if (log.isDebugEnabled()) {
                    log.debug("client is destroyed, {}->{}", ftpClient.getLocalAddress(), ftpClient.getRemoteAddress());
                }

                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.logout();
                }
            } catch (FTPConnectionClosedException e) {
                if (!closedLog) {
                    log.info("this client will logout, but ftp server closed this connection!");
                    closedLog = true;
                }
            } catch (IOException e) {
                log.info("this client will logout, but ftp server socket broke pipe!");
            } catch (Exception e) {
                log.error("error: {}", stackTraceToString(e));
            } finally {
                ftpClient.disconnect();
            }
        }

        @Override
        public void activateObject(PooledObject<FTPClient> p) throws Exception {
            FTPClient ftpClient = p.getObject();
            switch (ftpWorkPattern) {
                case FTP_PASV:
                    ftpClient.enterLocalPassiveMode();
                    break;
                case FTP_PORT:
                    ftpClient.enterLocalActiveMode();
                    break;
            }
        }

        @Override
        public boolean validateObject(PooledObject<FTPClient> p) {
            FTPClient ftpClient = p.getObject();
            return validateClient(ftpClient);
        }
    }

    public FtpFileSystem(String basePath) {
        super(basePath);
        GenericObjectPoolConfig<FTPClient> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(32);
        poolConfig.setMinIdle(8);
        poolConfig.setNumTestsPerEvictionRun(-3);
        poolConfig.setTimeBetweenEvictionRunsMillis(2 * 60 * 1000);
        poolConfig.setMaxWaitMillis(5 * 1000);
        poolConfig.setMinEvictableIdleTimeMillis(5 * 60 * 1000);
        poolConfig.setTestOnCreate(true);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        EvictionPolicy policy = (config, underTest, idleCount) -> {
            if (null == underTest.getObject()) {
                return true;
            }

            if (!validateClient((FTPClient) underTest.getObject())) {
                return true;
            }

            if ((config.getIdleSoftEvictTime() < underTest.getIdleTimeMillis() &&
                    config.getMinIdle() < idleCount && underTest.getIdleTimeMillis() > 5 * 60 * 1000) ||
                    config.getIdleEvictTime() < underTest.getIdleTimeMillis()) {
                return true;
            }
            return false;
        };
        poolConfig.setEvictionPolicy(policy);
        pool = new GenericObjectPool<>(new FtpFileSystem.FtpClientPoolFactory(), poolConfig);
        timerTask = PoolUtils.checkMinIdle(pool, 8, 1000);
    }

    private boolean validateClient(FTPClient ftpClient) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("validate ftp client, evict the client, {}->{}",
                        ftpClient.getLocalAddress(), ftpClient.getRemoteAddress());
            }

            ftpClient.noop();
            return true;
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.error("validate message send noop error, maining this client is invalid");
            }
        } catch (Exception e) {
            log.info("ftp client has been disconnected, evict the client, {}->{}, error: {}",
                    ftpClient.getLocalAddress(), ftpClient.getRemoteAddress(), e);
        }
        return false;
    }

    public FTPClient getClient() {
        FTPClient ftpClient = null;
        for (int i = 0; i < 200; i++) {
            try {
                ftpClient = pool.borrowObject();
                if (ftpClient != null) {
                    return ftpClient;
                }
                invalidateClient(ftpClient);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("get client error, but try again, last error: {}", stackTraceToString(e));
                }
            }
        }

        if (null == ftpClient) {
            log.error("The pool can not provide a valid client after try 200 times.");
        }
        return null;
    }

    public void returnClient(FTPClient ftpClient) {
        try {
            if (null == ftpClient) return;
            pool.returnObject(ftpClient);
        } catch (Exception e) {
            log.error("error: {}", stackTraceToString(e));
        }
    }

    private void invalidateClient(FTPClient client) {
        try {
            pool.invalidateObject(client);
        } catch (Exception e) {
            log.error("error: {}", stackTraceToString(e));
        }
    }

    private FTPFile mdtmFile(FTPClient ftpClient, String absPath) throws Exception {
        FTPFile ftpFile = null;
        int replyCode = ftpClient.mdtm(absPath);
        boolean isPositiveCompletion = FTPReply.isPositiveCompletion(replyCode);
        if (isPositiveCompletion) {
            String[] args = ftpClient.getReplyStrings();
            String reply = args[0].substring(4, 16);
            ftpFile.setName(absPath);
            ftpFile.setRawListing(reply);
            Calendar calendar = Utils.parseDateTime(reply);
            ftpFile.setTimestamp(calendar);
            return ftpFile;
        }
        return null;
    }

    private FTPFile addSizeFile(FTPClient ftpClient, String absPath) throws Exception {
        FTPFile ftpFile = mdtmFile(ftpClient, absPath);
        if (null == ftpFile) return null;
        int replyCode = ftpClient.sendCommand("size", absPath);
        boolean isPositiveCompletion = FTPReply.isPositiveCompletion(replyCode);
        if (isPositiveCompletion) {
            String[] args = ftpClient.getReplyStrings();
            String reply = args[0].substring(4); // skip the return code (e.g. 213) and the space
            ftpFile.setSize(Long.parseLong(reply));
            return ftpFile;
        }
        return null;
    }

    @Override
    public FileAttributes readAttributes(String path) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = getClient();
            if (null == ftpClient) {
                throw new FtpFileSystem.NullFtpClientException();
            }
            String absPath = buildAbsolutePath(path);
            FTPFile ftpFile = null;
            if (isEnableMLST) {
                ftpFile = ftpClient.mlistFile(absPath);
                if (null == ftpFile) {
                    return null;
                }
                Calendar calendar = ftpFile.getTimestamp();
                calendar.clear(Calendar.SECOND);
                ftpFile.setTimestamp(calendar);
            } else if (isEnableMDTM) {
                ftpFile = addSizeFile(ftpClient, absPath);
            } else {
                FTPFile[] ftpFiles = ftpClient.listFiles(absPath);
                if (null == ftpFiles || ftpFiles.length == 0) {
                    return null;
                }
                ftpFile = ftpFiles[0];
            }

            if (ftpFile == null) return null;
            FileAttributes attributes = new FileAttributes();
            attributes.setName(ftpFile.getName());
            attributes.setRegularFile(ftpFile.isFile());
            attributes.setDirectory(ftpFile.isDirectory());
            attributes.setSize(ftpFile.getSize());
            attributes.setModifyTime(ftpFile.getTimestamp().getTimeInMillis());
            return attributes;
        } finally {
            returnClient(ftpClient);
        }
    }

    @Override
    public IDirectory openDir(Task task, String path) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = getClient();
            if (null == ftpClient) {
                throw new FtpFileSystem.NullFtpClientException();
            }
            String absPath = buildAbsolutePath(path);
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (null != stackTrace && stackTrace.length > 0) {
                for (StackTraceElement s : stackTrace) {
                    if ("netty.demo.filesync.utils.FileUtils".equals(s.getClassName())
                            && "removeEmptyFolder".equals(s.getMethodName())) {
                        FTPFile[] files = ftpClient.listDirectories(absPath);
                        return new FtpDirectory(files);
                    }
                }
            }

            FTPListParseEngine engine = ftpClient.initiateListParsing(absPath);
            if (SyncMode.SYNC_MODE_MOVE.equals(task.getSyncMode())) {
                FTPFile[] ftpFiles = engine.getNext(10000);
                return new FtpDirectory(ftpFiles);
            } else {
                FTPFile[] ftpFiles = engine.getFiles();
                return new FtpDirectory(ftpFiles);
            }
        } finally {
            returnClient(ftpClient);
        }
    }

    @Override
    public boolean mkdir(String path) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = getClient();
            String absPath = buildAbsolutePath(path);
            return ftpClient.makeDirectory(absPath);
        } finally {
            returnClient(ftpClient);
        }
    }

    @Override
    public IFile openFile(String path) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = getClient();
            return new FtpFile(path, ftpClient, this);
        } finally {
            returnClient(ftpClient);
        }
    }

    @Override
    public void close() {
        try {
            timerTask.cancel();
            pool.clear();
            pool.close();
        } catch (Exception e) {
            log.error("error: {}", stackTraceToString(e));
        }
    }

    @Override
    public void deleteEmptyFolder(String path) throws IOException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getClient();
            if (path.indexOf("//") > -1) {
                path = path.substring(1);
            }
            String absPath = buildAbsolutePath(path);
            ftpClient.rmd(absPath);
        } finally {
            returnClient(ftpClient);
        }
    }

    @Override
    public Set<String> result(String filePath) throws Exception {
        return null;
    }

    @Override
    public void delete(String path) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = getClient();
            ftpClient.deleteFile(path);
        } finally {
            returnClient(ftpClient);
        }
    }
}
