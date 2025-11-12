package com.example.springai.controller;

import jakarta.annotation.Resource;
import org.reactivestreams.Publisher;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenyile
 */
@RestController
@RequestMapping("/api/stream")
public class StreamController {

    @Resource
    ChatClient chatClient;

    // GET /api/stream?q=写一段关于春天的短文
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> stream(@RequestParam("q") String q) {
        // 将每个增量片段映射成纯文本 SSE 事件
        return chatClient.prompt()
                .user(q)
                .stream()
                .content()
                .map(chunk -> chunk == null ? "" : chunk);
    }
}