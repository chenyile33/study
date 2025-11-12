// src/main/java/com/example/ai/ChatController.java
package com.example.springai.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author chenyile
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    ChatClient chatClient;

    // 简单对话：POST /api/chat  { "message": "你好，帮我写一首五言绝句" }
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {
        String content = chatClient
                .prompt()
                .user(req.message())
                .call()
                .content();
        return new ChatResponse(content);
    }

    @GetMapping(value = "stream", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        return chatClient
                .prompt()
                .user(prompt)
                //会话记忆
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    public record ChatRequest(String message) {
    }

    public record ChatResponse(String content) {
    }
}