package co.yixiang.modules.yaoshitong.web.controller;

import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.service.ChatGroupMsgService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMsgQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMsgQueryVo;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.api.ApiResult;
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
 * 聊天组群聊天记录 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Slf4j
@RestController
@RequestMapping("/chatGroupMsg")
@Api("聊天组群聊天记录 API")
public class ChatGroupMsgController extends BaseController {

    @Autowired
    private ChatGroupMsgService chatGroupMsgService;

    /**
    * 添加聊天组群聊天记录
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加ChatGroupMsg对象",notes = "添加聊天组群聊天记录",response = ApiResult.class)
    public ApiResult<Boolean> addChatGroupMsg(@Valid @RequestBody ChatGroupMsg chatGroupMsg) throws Exception{
        boolean flag = chatGroupMsgService.save(chatGroupMsg);
        return ApiResult.result(flag);
    }

    /**
    * 修改聊天组群聊天记录
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改ChatGroupMsg对象",notes = "修改聊天组群聊天记录",response = ApiResult.class)
    public ApiResult<Boolean> updateChatGroupMsg(@Valid @RequestBody ChatGroupMsg chatGroupMsg) throws Exception{
        boolean flag = chatGroupMsgService.updateById(chatGroupMsg);
        return ApiResult.result(flag);
    }

    /**
    * 删除聊天组群聊天记录
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除ChatGroupMsg对象",notes = "删除聊天组群聊天记录",response = ApiResult.class)
    public ApiResult<Boolean> deleteChatGroupMsg(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = chatGroupMsgService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取聊天组群聊天记录
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取ChatGroupMsg对象详情",notes = "查看聊天组群聊天记录",response = ChatGroupMsgQueryVo.class)
    public ApiResult<ChatGroupMsgQueryVo> getChatGroupMsg(@Valid @RequestBody IdParam idParam) throws Exception{
        ChatGroupMsgQueryVo chatGroupMsgQueryVo = chatGroupMsgService.getChatGroupMsgById(idParam.getId());
        return ApiResult.ok(chatGroupMsgQueryVo);
    }

    /**
     * 聊天组群聊天记录分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取ChatGroupMsg分页列表",notes = "聊天组群聊天记录分页列表",response = ChatGroupMsgQueryVo.class)
    public ApiResult<Paging<ChatGroupMsgQueryVo>> getChatGroupMsgPageList(@Valid @RequestBody(required = false) ChatGroupMsgQueryParam chatGroupMsgQueryParam) throws Exception{
        Paging<ChatGroupMsgQueryVo> paging = chatGroupMsgService.getChatGroupMsgPageList(chatGroupMsgQueryParam);
        return ApiResult.ok(paging);
    }

}

