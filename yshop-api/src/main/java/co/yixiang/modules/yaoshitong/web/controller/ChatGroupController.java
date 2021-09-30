package co.yixiang.modules.yaoshitong.web.controller;

import co.yixiang.modules.yaoshitong.entity.ChatGroup;
import co.yixiang.modules.yaoshitong.service.ChatGroupService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
import co.yixiang.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import co.yixiang.common.web.vo.Paging;
import co.yixiang.common.web.param.IdParam;

/**
 * <p>
 * 聊天群组 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Slf4j
@RestController
@RequestMapping("/chatGroup")
@Api("聊天群组 API")
public class ChatGroupController extends BaseController {

    @Autowired
    private ChatGroupService chatGroupService;

    /**
    * 添加聊天群组
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加ChatGroup对象",notes = "添加聊天群组",response = ApiResult.class)
    public ApiResult<Boolean> addChatGroup(@Valid @RequestBody ChatGroup chatGroup) throws Exception{
        boolean flag = chatGroupService.saveChatGroup(chatGroup);
        return ApiResult.result(flag);
    }

    /**
    * 修改聊天群组
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改ChatGroup对象",notes = "修改聊天群组",response = ApiResult.class)
    public ApiResult<Boolean> updateChatGroup(@Valid @RequestBody ChatGroup chatGroup) throws Exception{
        boolean flag = chatGroupService.saveChatGroup(chatGroup);
        return ApiResult.result(flag);
    }

    /**
    * 删除聊天群组
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除ChatGroup对象",notes = "删除聊天群组",response = ApiResult.class)
    public ApiResult<Boolean> deleteChatGroup(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = chatGroupService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取聊天群组
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取ChatGroup对象详情",notes = "查看聊天群组",response = ChatGroupQueryVo.class)
    public ApiResult<ChatGroup> getChatGroup(@Valid @RequestBody IdParam idParam) throws Exception{
        ChatGroup chatGroup = chatGroupService.getChatGroupById( Integer.valueOf(idParam.getId()));
        return ApiResult.ok(chatGroup);
    }

    /**
     * 聊天群组分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取ChatGroup分页列表",notes = "聊天群组分页列表",response = ChatGroupQueryVo.class)
    public ApiResult<Paging<ChatGroup>> getChatGroupPageList(@Valid @RequestBody(required = false) ChatGroupQueryParam chatGroupQueryParam) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
       // chatGroupQueryParam.setManagerId(uid);
        Paging<ChatGroup> paging = chatGroupService.getChatGroupPageList(chatGroupQueryParam);
        return ApiResult.ok(paging);
    }

}

