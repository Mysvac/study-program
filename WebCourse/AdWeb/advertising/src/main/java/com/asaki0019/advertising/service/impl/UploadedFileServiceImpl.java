package com.asaki0019.advertising.service.impl;

import com.asaki0019.advertising.mapper.UploadedFileMapper;
import com.asaki0019.advertising.model.UploadedFile;
import com.asaki0019.advertising.service.UploadedFileService;
import com.asaki0019.advertising.utils.Utils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileMapper uploadedFileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    public UploadedFileServiceImpl(UploadedFileMapper uploadedFileMapper) {
        this.uploadedFileMapper = uploadedFileMapper;
    }

    @Override
    public UploadedFile saveUploadedFile(UploadedFile uploadedFile) {
        try {
            // 插入数据，MyBatis-Plus 会自动返回自增主键
            uploadedFileMapper.insert(uploadedFile);
            return uploadedFile; // 返回包含自增主键的 UploadedFile 对象
        } catch (RuntimeException e) {
            Utils.logError("保存上传文件记录失败", e, "fileName: " + uploadedFile.getFileName());
            throw e; // 重新抛出异常
        }
    }

    @Override
    public UploadedFile getUploadedFileById(String fileId) {
        try {
            return uploadedFileMapper.selectById(fileId);
        } catch (RuntimeException e) {
            Utils.logError("获取上传文件记录失败", e, "fileId: " + fileId);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public void deleteUploadedFileById(String id) {
        try {
            uploadedFileMapper.deleteById(id);
        } catch (RuntimeException e) {
            Utils.logError("删除上传文件记录失败", e, "fileId: " + id);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public void deleteFileFromFileSystem(String fileId) {
        try {
            if (StringUtils.isEmpty(fileId)) {
                throw new IllegalArgumentException("文件 ID 不能为空");
            }
            UploadedFile uploadedFile = uploadedFileMapper.selectById(fileId);
            if (uploadedFile == null) {
                throw new RuntimeException("文件不存在");
            }
            String fileUrl = uploadedFile.getFileUrl();
            if (StringUtils.isEmpty(fileUrl)) {
                throw new RuntimeException("文件路径为空");
            }

            // 我在数据库中保存的时对应的URL："http://10.100.164.22:8080/uploads/" + fileName;
            // 我需要替换未实际的路径：uploadDir + "\\" + (去掉网络前缀)fileName
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // 组合成实际的文件路径
            String filePath = uploadDir + File.separator + fileName;

            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    Utils.log(Utils.LogLevel.INFO, "文件删除成功: " + filePath, null, "fileId: " + fileId);
                } else {
                    throw new RuntimeException("文件删除失败: " + filePath);
                }
            } else {
                throw new RuntimeException("文件不存在: " + filePath);
            }
        } catch (RuntimeException e) {
            Utils.logError("从文件系统删除文件失败", e, "fileId: " + fileId);
            throw new RuntimeException("从文件系统删除文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UploadedFile> getAllUploadedFiles() {
        try {
            return uploadedFileMapper.selectList(null);
        } catch (RuntimeException e) {
            Utils.logError("获取所有上传文件记录失败", e, null);
            throw e; // 重新抛出异常
        }
    }
}