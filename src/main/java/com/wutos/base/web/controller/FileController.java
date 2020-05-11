package com.wutos.base.web.controller;


import com.wutos.base.common.util.BaseResponse;
import com.wutos.base.service.IFileService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * 新的文件上传下载接口，原来的老的接口尽量弃用
 */
@RestController
@RequestMapping(value = "wutos/file")
public class FileController {

    Logger logger = LoggerFactory.getLogger("FileController");

    @Autowired
    private IFileService fileService;

    /**
     * 上传多个文件
     *
     */
    @ApiOperation("上传文件，返回文件名与fid的对应关系")
    @PostMapping("/list")
    public BaseResponse<?> saveFiles(MultipartFile[] file) throws IOException {
        List<Map<String, String>> list = fileService.saveFiles(file);
        return BaseResponse.getInstance(list);
    }

    /**
     *为了兼容ehs之前的多文件上传
     * @param files
     * @return
     * @throws IOException
     */
    @ApiOperation("上传文件，返回文件名与fid的对应关系")
    @PostMapping("/listForOld")
    public BaseResponse<?> saveFilesForOld(MultipartFile[] files) throws IOException {
        List<Map<String, String>> list = fileService.saveFiles(files);
        return BaseResponse.getInstance(list);
    }

    /**
     * 上传单个文件
     *
     */
    @ApiOperation("上传文件，返回文件名与fid的对应关系")
    @PostMapping("/")
    public BaseResponse<?> saveFile(MultipartFile file) throws IOException {
        String s = fileService.saveFile(file);
        return BaseResponse.getInstance(s);
    }

    /**
     * 上传单个文件   为了兼容ehs之前的多文件上传
     *
     */
    @ApiOperation("上传文件，返回文件名与fid的对应关系")
    @PostMapping("/singFileForOld")
    public BaseResponse<?> saveFileForOld(MultipartFile files) throws IOException {
        String s = fileService.saveFile(files);
        return BaseResponse.getInstance(s);
    }

    /**
     * 删除文件
     *
     */
    @ApiOperation("根据path,fileId和filename(File字段)删除文件,多个以$分隔")
    @DeleteMapping("/{fid}")
    public BaseResponse<?> deleteByFiles(@PathVariable("fid") String fid) {
        fileService.deleteFile(fid);
        return BaseResponse.getInstance();
    }

    /**
     * 下载文件
     */
    @ApiOperation("根据fid下载文件")
    @GetMapping("/{fid}")
    public void downloadByFid(@PathVariable("fid") String fid , HttpServletResponse response) throws IOException {
        fileService.getFileByFid(fid , response);
    }

    /**
     * 下载文件
     */
    @ApiOperation("根据fid下载文件")
    @GetMapping("/download/{fid}")
    public void downloadByFid1(@PathVariable("fid") String fid , HttpServletResponse response) throws IOException {
        fileService.getFileByFid1(fid ,response);
    }
}
