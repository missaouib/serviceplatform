package co.yixiang.modules.yaoshitong.mapper;


import co.yixiang.modules.yaoshitong.web.vo.ChatMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMsgMapper extends BaseMapper<ChatMsg> {
    //插入聊天记录
    void InsertChatMsg(ChatMsg chatMsg);
    //查询聊天记录
    List<ChatMsg>  LookTwoUserMsg(@Param("chatMsg") ChatMsg chatMsg);


}