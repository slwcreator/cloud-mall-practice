package com.slwer.cloud.mall.practice.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.categoryproduct.common.ProductConstant;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.UpdateProductReq;
import com.slwer.cloud.mall.practice.categoryproduct.service.ProductService;
import com.slwer.cloud.mall.practice.common.common.ApiRestResponse;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

/**
 * 后台商品管理 Controller
 */
@RestController
public class ProductAdminController {

    @Resource
    ProductService productService;

    @Value("${file.upload.uri}")
    String uri;

    @Value("${file.upload.uri.scheme}")
    String scheme;

    @ApiOperation("上传文件")
    @PostMapping("/admin/upload/file")
    public ApiRestResponse<String> upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String suffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid + suffix;

        //文件目录
        File fileDirectory = new File(ProductConstant.FILE_UPLOAD_DIR);
        //文件全路径名
        File destFile = new File(ProductConstant.FILE_UPLOAD_DIR + newFileName);

        createFile(file, fileDirectory, destFile);
        String address = uri;
        return ApiRestResponse.success(scheme + address + "/category-product/images/" + newFileName);
    }

    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation("新增商品")
    @PostMapping("/admin/product/add")
    public ApiRestResponse<Product> addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse<Product> updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse<Product> deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse<Product> batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                          @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表")
    @GetMapping("/admin/product/list")
    public ApiRestResponse<PageInfo<Product>> list(@RequestParam Integer pageNum,
                                                   @RequestParam Integer pageSize) {
        PageInfo<Product> productPageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(productPageInfo);
    }

    @ApiOperation("上传图片")
    @PostMapping("/admin/upload/image")
    public ApiRestResponse<String> uploadImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String suffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid + suffix;

        //文件目录
        File fileDirectory = new File(ProductConstant.FILE_UPLOAD_DIR);
        //文件全路径名
        File destFile = new File(ProductConstant.FILE_UPLOAD_DIR + newFileName);

        createFile(file, fileDirectory, destFile);
        String address = uri;
        return ApiRestResponse.success(scheme + address + "/category-product/images/" + newFileName);
    }

    private static void createFile(MultipartFile file, File fileDirectory, File destFile) {
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }

        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
