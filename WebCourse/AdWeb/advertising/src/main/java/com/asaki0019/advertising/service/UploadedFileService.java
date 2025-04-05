package com.asaki0019.advertising.service;

import com.asaki0019.advertising.model.UploadedFile;

import java.io.FileNotFoundException;
import java.util.List;

public interface UploadedFileService {
    UploadedFile saveUploadedFile(UploadedFile uploadedFile); // 增加文件
    UploadedFile getUploadedFileById(String fileId); // 增加文件
    void deleteUploadedFileById(String id); // 删除文件
    void deleteFileFromFileSystem(String fileId) throws FileNotFoundException;
    List<UploadedFile> getAllUploadedFiles();
}
