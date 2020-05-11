package com.wutos.base.service.impl;

import com.wutos.base.domain.entity.SeaweedFsUploadResponseEntity;
import com.wutos.base.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ymk 2019/6/24
 */
@Service
public class FileServiceImpl implements IFileService {

    @Autowired
    private FileServer fileServer;

    @Override
    public List<Map<String, String>> saveFiles(MultipartFile[] files) throws IOException {
//        StringBuilder namebuilder = new StringBuilder();
//        StringBuilder fidbuilder = new StringBuilder();
        List<Map<String, String>> list = new ArrayList<>();

        String separater = ";";
        for (MultipartFile file : files) {
            Map<String, String> map = new HashMap<>();
            String filename = file.getOriginalFilename();
//            namebuilder.append(filename).append(separater);
            SeaweedFsUploadResponseEntity responseEntity = fileServer.uploadFile(filename, file.getInputStream());
//            fidbuilder.append(responseEntity.getFid()).append(separater);
            map.put(filename,responseEntity.getFid());
            list.add(map);
        }
        return list;
    }

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        SeaweedFsUploadResponseEntity responseEntity = fileServer.uploadFile(file.getOriginalFilename(), file.getInputStream());
        return responseEntity.getFid();
    }

    @Override
    public void deleteFile(String fid) {
        // TODO 删除文件待实现
    }

    @Override
    public void getFileByFid(String fid, HttpServletResponse response) throws IOException {
        fileServer.getFileToResponce(fid, response);
    }

    @Override
    public void getFileByFid1(String fid, HttpServletResponse response) throws IOException {
        fileServer.getFileToResponce1(fid, response);
        System.out.println(123);
    }
}
