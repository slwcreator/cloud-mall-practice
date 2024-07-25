package com.slwer.cloud.mall.practice.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.ProductListReq;

public interface ProductService {
    void add(AddProductReq addProductReq);

    void update(Product product);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo<Product> listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo<Product> list(ProductListReq productListReq);

    void updateStock(Integer productId, Integer stock);
}
