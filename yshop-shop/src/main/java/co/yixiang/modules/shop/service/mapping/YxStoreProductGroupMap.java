package co.yixiang.modules.shop.service.mapping;

import co.yixiang.modules.shop.domain.YxStoreProductGroup;
import co.yixiang.modules.shop.service.dto.YxStoreProductGroupQueryVo;

import java.util.ArrayList;
import java.util.List;

public class YxStoreProductGroupMap {

    public static YxStoreProductGroupQueryVo toDto(YxStoreProductGroup arg0) {
        if ( arg0 == null ) {
            return null;
        }

        YxStoreProductGroupQueryVo yxStoreProductGroupQueryVo = new YxStoreProductGroupQueryVo();

        yxStoreProductGroupQueryVo.setId( arg0.getId() );
        yxStoreProductGroupQueryVo.setParentProductYiyaobaoSku( arg0.getParentProductYiyaobaoSku() );
        yxStoreProductGroupQueryVo.setParentProductId( arg0.getParentProductId() );
        yxStoreProductGroupQueryVo.setProductYiyaobaoSku( arg0.getProductYiyaobaoSku() );
        yxStoreProductGroupQueryVo.setProductId( arg0.getProductId() );
        yxStoreProductGroupQueryVo.setNum( arg0.getNum() );
        yxStoreProductGroupQueryVo.setUnitPrice( arg0.getUnitPrice() );
        yxStoreProductGroupQueryVo.setCreateTime( arg0.getCreateTime() );
        yxStoreProductGroupQueryVo.setUpdateTime( arg0.getUpdateTime() );
        yxStoreProductGroupQueryVo.setIsDel( arg0.getIsDel()==1?true:false );
        yxStoreProductGroupQueryVo.setProductUnique( arg0.getProductUnique() );

        return yxStoreProductGroupQueryVo;
    }

    public static List<YxStoreProductGroupQueryVo> toDto(List<YxStoreProductGroup> arg0) {
        if ( arg0 == null ) {
            return null;
        }

        List<YxStoreProductGroupQueryVo> list = new ArrayList<YxStoreProductGroupQueryVo>( arg0.size() );
        for ( YxStoreProductGroup yxStoreProductGroup : arg0 ) {
            list.add( toDto( yxStoreProductGroup ) );
        }

        return list;
    }
}
