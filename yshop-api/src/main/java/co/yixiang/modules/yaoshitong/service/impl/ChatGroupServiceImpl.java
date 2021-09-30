package co.yixiang.modules.yaoshitong.service.impl;

import cn.hutool.core.util.StrUtil;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.modules.shop.entity.MdPharmacistService;
import co.yixiang.modules.shop.service.MdPharmacistServiceService;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.yaoshitong.entity.ChatGroup;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMember;
import co.yixiang.modules.yaoshitong.entity.ChatGroupMsg;
import co.yixiang.modules.yaoshitong.entity.YaoshitongPatient;
import co.yixiang.modules.yaoshitong.mapper.ChatGroupMapper;
import co.yixiang.modules.yaoshitong.mapper.ChatGroupMsgMapper;
import co.yixiang.modules.yaoshitong.mapping.ChatGroupVoMap;
import co.yixiang.modules.yaoshitong.mapping.YaoshitongPrescriptionVoMap;
import co.yixiang.modules.yaoshitong.service.ChatGroupMemberService;
import co.yixiang.modules.yaoshitong.service.ChatGroupService;
import co.yixiang.modules.yaoshitong.service.YaoshitongPatientService;
import co.yixiang.modules.yaoshitong.web.param.ChatGroupQueryParam;
import co.yixiang.modules.yaoshitong.web.vo.ChatGroupQueryVo;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.utils.RedisUtils;
import co.yixiang.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * 聊天群组 服务实现类
 * </p>
 *
 * @author visa
 * @since 2020-09-02
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatGroupServiceImpl extends BaseServiceImpl<ChatGroupMapper, ChatGroup> implements ChatGroupService {

    @Autowired
    private ChatGroupMapper chatGroupMapper;

    @Autowired
    private ChatGroupMemberService chatGroupMemberService;

    @Autowired
    private MdPharmacistServiceService pharmacistService;

    @Autowired
    private YxUserService yxUserService;

    @Autowired
    private YaoshitongPatientService yaoshitongPatientService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    ChatGroupMsgMapper chatGroupMsgMapper;
    @Override
    public ChatGroup getChatGroupById(Serializable id) throws Exception{
        ChatGroup chatGroup = chatGroupMapper.selectById(id);
        List<Integer> memberIds = new ArrayList<>();
        //获取群聊组的成员
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("group_id",id);
        List<ChatGroupMember> memberList = chatGroupMemberService.list(queryWrapper);
        if(memberList!= null && memberList.size() >0) {
            for(ChatGroupMember member:memberList) {
                memberIds.add(member.getUid());

                // 查找手机号和姓名

                YxUser yxUser = yxUserService.getById(member.getUid());
                if(yxUser != null){
                    member.setName(yxUser.getRealName());
                    member.setPhone(yxUser.getPhone());
                    member.setAvatar(yxUser.getAvatar());
                    if(member.getIsManager() >0 ) {  // 管理员
                        MdPharmacistService pharmacist = pharmacistService.getOne(new QueryWrapper<MdPharmacistService>().eq("uid",member.getUid()),false);
                        if(pharmacist != null) {
                            member.setName(pharmacist.getName());
                            member.setPhone(pharmacist.getPhone());
                        }

                    } else {
                        YaoshitongPatient patient = yaoshitongPatientService.getOne(new QueryWrapper<YaoshitongPatient>().eq("phone",yxUser.getPhone()),false);
                        if(patient != null) {
                            member.setName(patient.getName());
                            member.setPhone(patient.getPhone());
                        }
                    }
                }

            }
        }
        chatGroup.setMemberIds(memberIds);
        chatGroup.setMemberList(memberList);
        Integer uid = SecurityUtils.getUserId().intValue();
        YxUser yxUser = yxUserService.getById(uid);
        chatGroup.setCurrentUser(yxUser);
        return chatGroup;
    }

    @Override
    public Paging<ChatGroup> getChatGroupPageList(ChatGroupQueryParam chatGroupQueryParam) throws Exception{
        Integer uid = SecurityUtils.getUserId().intValue();
        Page page = setPageParam(chatGroupQueryParam,OrderItem.desc("create_time"));
        QueryWrapper queryWrapper = QueryHelpPlus.getPredicate(ChatGroupQueryParam.class, chatGroupQueryParam);
        if(StrUtil.isNotBlank(chatGroupQueryParam.getKeyword())){
            queryWrapper.like("name",chatGroupQueryParam.getKeyword());
        }

        queryWrapper.apply(" EXISTS (SELECT 1 FROM chat_group_member cgm WHERE cgm.group_id = chat_group.id AND cgm.uid = {0})",uid);

        IPage<ChatGroup> iPage = chatGroupMapper.selectPage(page,queryWrapper);

        for(ChatGroup chatGroup:iPage.getRecords()) {

            String key = "msgUnread-group-"+chatGroup.getId() + "-" + uid;
            if(redisUtils.get(key) != null) {
                Integer unReadCount = Integer.valueOf(String.valueOf(redisUtils.get(key)));
                chatGroup.setUnRead(unReadCount);
            }
        }

        return new Paging(iPage);
    }

    @Override
    public boolean saveChatGroup(ChatGroup chatGroup) {
        if(chatGroup.getId() == null) {
            chatGroup.setMakerId(SecurityUtils.getUserId().intValue());
            chatGroup.setManagerId(SecurityUtils.getUserId().intValue());
        }
        this.saveOrUpdate(chatGroup);


        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("group_id",chatGroup.getId());
        chatGroupMemberService.remove(queryWrapper);

        List<Integer> memberIds = chatGroup.getMemberIds();

        if(memberIds != null && memberIds.size()>0) {
            Integer uid = SecurityUtils.getUserId().intValue();
            ChatGroupMember chatGroupMember = new ChatGroupMember();
            chatGroupMember.setGroupId(chatGroup.getId());
            chatGroupMember.setUid(uid);
            chatGroupMember.setIsManager(1);
            chatGroupMemberService.save(chatGroupMember);

            for(Integer patientUid:memberIds) {
                if(! patientUid.equals(uid)) {
                    ChatGroupMember chatGroupMember2 = new ChatGroupMember();
                    chatGroupMember2.setGroupId(chatGroup.getId());
                    chatGroupMember2.setUid(patientUid);
                    chatGroupMember2.setIsManager(0);
                    chatGroupMemberService.save(chatGroupMember2);
                }

            }
        }
        return true;
    }

}
