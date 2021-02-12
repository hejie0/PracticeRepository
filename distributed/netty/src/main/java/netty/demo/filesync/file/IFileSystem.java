package netty.demo.filesync.file;

import netty.demo.filesync.task.Task;

import java.util.Set;

public interface IFileSystem {

    String getBasePath();

    FileAttributes readAttributes(String path) throws Exception;
    IDirectory openDir(Task task, String path) throws Exception;
    boolean mkdir(String path) throws Exception;
    IFile openFile(String path) throws Exception;
    String buildAbsolutePath(String path);
    void close();
    void deleteEmptyFolder(String path) throws Exception;

    Set<String> result(String filePath)throws Exception;
    void delete(String path) throws Exception;
}
