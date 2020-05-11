package com.wutos.base.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ymk 2019/6/24
 * 用于执行文件上传下载删除的操作，只关联文件本身，不涉及数据库
 */
public interface IFileService {
    List<Map<String, String>> saveFiles(MultipartFile[] files) throws IOException;

    void deleteFile(String fid);

    void getFileByFid(String fid, HttpServletResponse response) throws IOException;

    void getFileByFid1(String fid, HttpServletResponse response) throws IOException;

    String saveFile(MultipartFile file) throws IOException;
}
