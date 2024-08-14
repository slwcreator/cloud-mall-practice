package com.slwer.cloud.mall.practice.cartorder.service.impl;

import com.slwer.cloud.mall.practice.cartorder.feign.ProductFeignClient;
import com.slwer.cloud.mall.practice.cartorder.model.pojo.Product;
import com.slwer.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.slwer.cloud.mall.practice.common.common.Constant;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    ProductFeignClient productFeignClient;

    @Test
    public void validSaleStatusAndStock() {
        CartVO cartVO = new CartVO();
        cartVO.setProductId(27);
        cartVO.setQuantity(1);

        Product fakeProduct = new Product();
        fakeProduct.setStatus(1);
        fakeProduct.setStock(4);

        Mockito.when(productFeignClient.detailForFeign(27)).thenReturn(fakeProduct);
        Product product = productFeignClient.detailForFeign(cartVO.getProductId());
        //判断商品是否存在和是否上架
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (cartVO.getQuantity() > product.getStock()) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
        }

        Assert.assertNotNull(product);
        Assert.assertEquals(1, (int) product.getStatus());
        Assert.assertTrue(product.getStock() > cartVO.getQuantity());
    }
}