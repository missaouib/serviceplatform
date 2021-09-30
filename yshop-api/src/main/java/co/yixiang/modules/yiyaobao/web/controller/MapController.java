package co.yixiang.modules.yiyaobao.web.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.web.param.IdParam;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.yiyaobao.entity.MdCountry;
import co.yixiang.modules.yiyaobao.service.MdCountryService;
import co.yixiang.modules.yiyaobao.web.param.MdCountryQueryParam;
import co.yixiang.modules.yiyaobao.web.vo.MapQueryVo;
import co.yixiang.modules.yiyaobao.web.vo.MdCountryQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 国家地区信息表 前端控制器
 * </p>
 *
 * @author visazhou
 * @since 2020-05-16
 */
@Slf4j
@RestController
@RequestMapping("/map")
@Api("腾讯地图API")
public class MapController extends BaseController {

   @Autowired
   private RestTemplate restTemplate;
   private final String district_search_url = "http://apis.map.qq.com/ws/district/v1/search";
   private final String mapKey = "M2BBZ-OHBWW-EPMRW-OAE57-H6ZB6-LQFMB";
   private final String suggestion_url = "https://apis.map.qq.com/ws/place/v1/suggestion/";
   private final String geocoder_url = "https://apis.map.qq.com/ws/geocoder/v1/";

    /**
    * 行政区域搜索
    */
    @PostMapping("/district/search")
    @ApiOperation(value = "行政区域搜索",notes = "行政区域搜索",response = ApiResult.class)
    @AnonymousAccess
    public ApiResult<String> districtSearch(@Valid @RequestBody MapQueryVo mapQueryVo) throws Exception{
        long begin = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> formEntity = new HttpEntity<String>(null, headers);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("keyword", mapQueryVo.getKeyword());
        maps.put("key", mapKey);


        ResponseEntity<String> exchange = restTemplate.exchange(district_search_url + "?keyword={keyword}&key={key}",
                HttpMethod.GET,
                formEntity, String.class, maps);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        log.info("{}", body);
        log.info("行政区域搜索 耗时：{} 毫秒",System.currentTimeMillis() -begin);
        return ApiResult.ok(jsonObject);
    }

    /**
     * 行政区域搜索
     */
    @PostMapping("/place/suggestion")
    @ApiOperation(value = "关键词输入提示",notes = "关键词输入提示",response = ApiResult.class)
    @AnonymousAccess
    public ApiResult<String> placeSuggestion(@Valid @RequestBody MapQueryVo mapQueryVo) throws Exception{
        long begin = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> formEntity = new HttpEntity<String>(null, headers);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("keyword", mapQueryVo.getKeyword());
        maps.put("key", mapKey);
        maps.put("region", mapQueryVo.getRegion());



        ResponseEntity<String> exchange = restTemplate.exchange(suggestion_url + "?keyword={keyword}&key={key}&region={region}",
                HttpMethod.GET,
                formEntity, String.class, maps);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        log.info("{}", body);
        log.info("关键词输入提示 耗时：{} 毫秒",System.currentTimeMillis() -begin);
        return ApiResult.ok(jsonObject);
    }


    /**
     * 地址解析（地址转坐标）
     */
    @PostMapping("/geocoder")
    @ApiOperation(value = "地址解析（地址转坐标）",notes = "地址解析（地址转坐标）",response = ApiResult.class)
    @AnonymousAccess
    public ApiResult<String> geocoder(@RequestBody MapQueryVo mapQueryVo) throws Exception{
        long begin = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> formEntity = new HttpEntity<String>(null, headers);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("address", mapQueryVo.getAddress());
        maps.put("key", mapKey);
        maps.put("region", mapQueryVo.getRegion());



        ResponseEntity<String> exchange = restTemplate.exchange(geocoder_url + "?address={address}&key={key}&region={region}",
                HttpMethod.GET,
                formEntity, String.class, maps);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        log.info("{}", body);
        log.info("地址解析（地址转坐标） 耗时：{} 毫秒",System.currentTimeMillis() -begin);
        return ApiResult.ok(jsonObject);
    }

    /**
     * 逆地址解析（坐标位置描述）
     */
    @PostMapping("/geocoderByLocation")
    @ApiOperation(value = "逆地址解析（坐标位置描述）",notes = "逆地址解析（坐标位置描述）",response = ApiResult.class)
    @AnonymousAccess
    public ApiResult<String> geocoderByLocation(@RequestBody MapQueryVo mapQueryVo) throws Exception{
        long begin = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> formEntity = new HttpEntity<String>(null, headers);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("location", mapQueryVo.getLatitude()+","+mapQueryVo.getLongitude());
        maps.put("key", mapKey);
        maps.put("poi_options", "address_format=short");



        ResponseEntity<String> exchange = restTemplate.exchange(geocoder_url + "?location={location}&key={key}&poi_options={poi_options}",
                HttpMethod.GET,
                formEntity, String.class, maps);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        log.info("{}", body);
        log.info("逆地址解析（坐标位置描述） 耗时：{} 毫秒",System.currentTimeMillis() -begin);
        return ApiResult.ok(jsonObject);
    }

}

