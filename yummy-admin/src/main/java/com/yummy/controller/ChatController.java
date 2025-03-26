package com.yummy.controller;

import com.yummy.common.core.controller.BaseController;
import com.yummy.common.core.domain.AjaxResult;
import com.yummy.patentSys.domain.ChatSession;
import com.yummy.patentSys.domain.ChatMessage;
import com.yummy.patentSys.service.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI聊天助手控制器
 */
@RestController
@RequestMapping("/patentSys/chat")
public class ChatController extends BaseController {

    @Autowired
    private IChatService chatService;

    /**
     * 获取聊天历史列表
     */
    @GetMapping("/history")
    public AjaxResult getChatHistory() {
        List<ChatSession> chatSessions = chatService.getChatSessionsByUserId(getUserId());
        return AjaxResult.success(chatSessions);
    }

    /**
     * 获取聊天详情
     */
    @GetMapping("/detail/{chatId}")
    public AjaxResult getChatDetail(@PathVariable Long chatId) {
        List<ChatMessage> messages = chatService.getChatMessages(chatId, getUserId());
        return AjaxResult.success(messages);
    }

    /**
     * 创建新的聊天会话
     */
    @PostMapping("/create")
    public AjaxResult createChat() {
        ChatSession session = chatService.createChatSession(getUserId());
        return AjaxResult.success(session);
    }

    /**
     * 更新聊天会话信息
     */
    @PutMapping("/update/{chatId}")
    public AjaxResult updateChat(@PathVariable Long chatId, @RequestBody ChatSession chatSession) {
        chatSession.setId(chatId);
        chatSession.setUserId(getUserId());
        chatService.updateChatSession(chatSession);
        return AjaxResult.success();
    }

    /**
     * 删除聊天会话
     */
    @DeleteMapping("/delete/{chatId}")
    public AjaxResult deleteChat(@PathVariable Long chatId) {
        chatService.deleteChatSession(chatId, getUserId());
        return AjaxResult.success();
    }

    /**
     * 发送聊天消息
     */
    @PostMapping("/send")
    public AjaxResult sendMessage(@RequestBody ChatMessage message) {
        message.setUserId(getUserId());
        ChatMessage reply = chatService.processAndReply(message);
        return AjaxResult.success(reply);
    }
}
