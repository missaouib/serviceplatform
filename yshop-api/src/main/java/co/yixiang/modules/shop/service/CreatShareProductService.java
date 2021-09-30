package co.yixiang.modules.shop.service;

import co.yixiang.modules.shop.entity.YxStoreProduct;
import co.yixiang.modules.shop.web.dto.ProductDTO;

import java.awt.*;
import java.io.IOException;

public interface CreatShareProductService {

    String creatProductPic(YxStoreProduct productDTO, String qrcode, String spreadPicName, String spreadPicPath, String apiUrl) throws IOException, FontFormatException;
}
