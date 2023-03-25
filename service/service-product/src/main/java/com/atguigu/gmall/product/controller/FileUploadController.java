package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.minio.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Api(tags = "文件上传控制器")
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    //读取配置文件中的配置
    @Value("${minio.endpointUrl}")
    private String endpointUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secreKey}")
    private String secreKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @ApiOperation("MinIO文件上传")
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url="";

        try {
            // 创建minioClient客户端对象
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(endpointUrl)
                            .credentials(accessKey, secreKey)
                            .build();

            // 判断指定的桶是否存在
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // 如果不存在指定的桶，创建
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket " + bucketName + " already exists.");
            }
            //下面注释这个不行，无法明确知道文件的位置
//        // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
//        // 'asiatrip'.
//        minioClient.uploadObject(
//                UploadObjectArgs.builder()
//                        .bucket("asiatrip")
//                        .object("asiaphotos-2015.zip")
//                        .filename("/home/user/Photos/asiaphotos.zip")
//                        .build());
//        System.out.println(
//                "'/home/user/Photos/asiaphotos.zip' is successfully uploaded as "
//                        + "object 'asiaphotos-2015.zip' to bucket 'asiatrip'.");

            //文件名称
            //文件名称
            String fileName = System.currentTimeMillis() + UUID.randomUUID().toString();

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                            file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            //拼接一个文件路径
             url=endpointUrl+"/"+bucketName+"/"+fileName;
            System.out.println("图片的路径:"+url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.ok(url);
    }

}
