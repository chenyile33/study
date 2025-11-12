// src/main/java/com/example/ai/ChatController.java
package com.example.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenyile
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    // 自动注入 ChatClient.Builder，由 Spring AI 提供
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are a helpful assistant.")
                .build();
    }

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

    public record ChatRequest(String message) {}
    public record ChatResponse(String content) {}
}