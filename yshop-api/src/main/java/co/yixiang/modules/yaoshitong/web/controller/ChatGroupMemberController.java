package co.yixiang.modules.yaoshitong.web.controller;

import co.yixiang.modules.yaoshitong.entity.ChatGroupMember;
import co.yixiang.modules.yaoshitong.service.ChatGroupMemberService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupMemberQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupMemberQueryVo;
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
 * 聊天群组成员 前端控制器
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Slf4j
@RestController
@RequestMapping("/chatGroupMember")
@Api("聊天群组成员 API")
public class ChatGroupMemberController extends BaseController {

    @Autowired
    private ChatGroupMemberService chatGroupMemberService;

    /**
    * 添加聊天群组成员
    */
    @PostMapping("/add")
    @ApiOperation(value = "添加ChatGroupMember对象",notes = "添加聊天群组成员",response = ApiResult.class)
    public ApiResult<Boolean> addChatGroupMember(@Valid @RequestBody ChatGroupMember chatGroupMember) throws Exception{
        boolean flag = chatGroupMemberService.save(chatGroupMember);
        return ApiResult.result(flag);
    }

    /**
    * 修改聊天群组成员
    */
    @PostMapping("/update")
    @ApiOperation(value = "修改ChatGroupMember对象",notes = "修改聊天群组成员",response = ApiResult.class)
    public ApiResult<Boolean> updateChatGroupMember(@Valid @RequestBody ChatGroupMember chatGroupMember) throws Exception{
        boolean flag = chatGroupMemberService.updateById(chatGroupMember);
        return ApiResult.result(flag);
    }

    /**
    * 删除聊天群组成员
    */
    @PostMapping("/delete")
    @ApiOperation(value = "删除ChatGroupMember对象",notes = "删除聊天群组成员",response = ApiResult.class)
    public ApiResult<Boolean> deleteChatGroupMember(@Valid @RequestBody IdParam idParam) throws Exception{
        boolean flag = chatGroupMemberService.removeById(idParam.getId());
        return ApiResult.result(flag);
    }

    /**
    * 获取聊天群组成员
    */
    @PostMapping("/info")
    @ApiOperation(value = "获取ChatGroupMember对象详情",notes = "查看聊天群组成员",response = ChatGroupMemberQueryVo.class)
    public ApiResult<ChatGroupMemberQueryVo> getChatGroupMember(@Valid @RequestBody IdParam idParam) throws Exception{
        ChatGroupMemberQueryVo chatGroupMemberQueryVo = chatGroupMemberService.getChatGroupMemberById(idParam.getId());
        return ApiResult.ok(chatGroupMemberQueryVo);
    }

    /**
     * 聊天群组成员分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取ChatGroupMember分页列表",notes = "聊天群组成员分页列表",response = ChatGroupMemberQueryVo.class)
    public ApiResult<Paging<ChatGroupMemberQueryVo>> getChatGroupMemberPageList(@Valid @RequestBody(required = false) ChatGroupMemberQueryParam chatGroupMemberQueryParam) throws Exception{
        Paging<ChatGroupMemberQueryVo> paging = chatGroupMemberService.getChatGroupMemberPageList(chatGroupMemberQueryParam);
        return ApiResult.ok(paging);
    }

}

