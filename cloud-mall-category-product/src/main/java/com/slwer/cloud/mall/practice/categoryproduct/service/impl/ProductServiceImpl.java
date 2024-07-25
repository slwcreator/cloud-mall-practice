package com.slwer.cloud.mall.practice.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.categoryproduct.model.dao.ProductMapper;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.slwer.cloud.mall.practice.categoryproduct.model.query.ProductListQuery;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.ProductListReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.slwer.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.slwer.cloud.mall.practice.categoryproduct.service.ProductService;
import com.slwer.cloud.mall.practice.common.common.Constant;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    CategoryService categoryService;

    @Resource
    ProductMapper productMapper;

    @Override
    public void add(AddProductReq addProductReq) {
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }

        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Product product) {
        Product productOld = productMapper.selectByName(product.getName());
        //重名且 id 不同则不允许更新
        if (productOld != null && !productOld.getId().equals(product.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        //找不到记录，无法删除
        if (productOld == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo<Product> listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectListForAdmin();
        return new PageInfo<>(productList);
    }

    @Override
    public Product detail(Integer id) {
        return productMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据条件，以分页的方式，查询商品数据
     */
    @Override
    public PageInfo<Product> list(ProductListReq productListReq) {
        ProductListQuery productListQuery = new ProductListQuery();

        //搜索处理
        if (StringUtils.isNotEmpty(productListReq.getKeyword())) {
            String keyword = "%" + productListReq.getKeyword() + "%";
            productListQuery.setKeyword(keyword);
        }

        //目录处理：如果查某个目录下的商品，不仅要查该目录下的，还需要查该目录子目录下的所有商品，所以需要拿到一个目录 id 的 list
        if (productListReq.getCategoryId() != null) {
            //调用以前编写的一个【递归查询所有子目录】的方法；获取当前目录和当前目录的所有子目录的递归查询结果
            List<CategoryVO> categoryVOList = categoryService.listForCustomer(productListReq.getCategoryId());

            //上面的 categoryVOList 是一个嵌套的结果，要想获取当前目录和其子目录的 CategoryId 的集合，还需要做以下处理：
            //首先，创建一个 List 用来存放所有的 CategoryId
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        //排序条件的处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            //如果前端没有传 orderBy 参数，或参数的值不符合在【Constant.ProductListOrderBy.PRICE_ASC_DESC】中定义的格式
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(productListQuery);
        return new PageInfo<>(productList);
    }

    /**
     * 工具方法，遍历【List<CategoryVO> categoryVOList】这种递归嵌套的数据结构，获取其中所有的 categoryId;
     */
    private void getCategoryIds(List<CategoryVO> categoryVOList, List<Integer> categoryIds) {
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());

                //递归调用
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

    @Override
    public void updateStock(Integer productId, Integer stock) {
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateByPrimaryKeySelective(product);
    }
}
