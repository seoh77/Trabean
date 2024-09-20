package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final WebClient webClient;

    public ChatbotController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5000").build(); // Python 서버 URL
    }

    @PostMapping("/question")
    public Mono<String> askQuestion(@RequestBody QuestionRequest request) {
        String question = request.getQuestion();

        return webClient.post()
                .uri("/ask")  // Python 서버의 API 엔드포인트
                .bodyValue(question)
                .retrieve()
                .bodyToMono(String.class); // Python 서버의 응답을 받아 리턴
    }

    // RequestBody로 받기 위한 클래스
    public static class QuestionRequest {
        private String question;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
    }
}
