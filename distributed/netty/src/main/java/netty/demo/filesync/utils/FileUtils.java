package netty.demo.filesync.utils;

import netty.demo.filesync.file.FileAttributes;
import netty.demo.filesync.file.IDirectory;
import netty.demo.filesync.file.IFileSystem;
import netty.demo.filesync.task.Task;

import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static void removeEmptyFolder (Task task, String filePath) throws Exception {
        IFileSystem fileSystem = task.getFileSystem();
        List<String> checkDelete = new ArrayList<>();
        IDirectory directory = fileSystem.openDir(task, filePath);
        while (true) {
            FileAttributes attributes = directory.readNextFile();
            if (attributes == null) {
                break;
            } else {
                if (".".equals(attributes.getName()) || "..".equals(attributes.getName())) {
                    continue;
                } else {
                    if (attributes.isDirectory()) {
                        String dirName = attributes.getName();
                        String parent = "" ;
                        if (dirName.indexOf("/") >= 0 && "/".equals(filePath)) {
                            parent = filePath + attributes.getName();
                        } else {
                            parent = filePath + "/" + attributes.getName();
                        }
                        checkDelete.add(parent);
                        removeEmptyFolder(task, parent);
                    } else {
                        continue;
                    }
                }
            }
        }

        if (!checkDelete.isEmpty()) {
            for (String strPath: checkDelete) {
                fileSystem.deleteEmptyFolder(strPath);
            }
        }
        if (directory != null) {
            directory.close();
        }

    }

}
