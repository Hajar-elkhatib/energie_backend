package com.energie.platform.controller;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.service.ChatbotService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/chatbot") @Tag(name="Public - Chatbot") @RequiredArgsConstructor public class ChatbotController {private final ChatbotService service;
 @PostMapping("/messages") @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Envoyer un message au chatbot") public MessageResponse message(@Valid @RequestBody MessageCreationRequest r){return service.enregistrerMessageVisiteur(r);}
 @GetMapping("/conversations/{id}") @Operation(summary="Consulter l'historique d'une conversation") public ConversationResponse conversation(@PathVariable Long id){return service.consulterConversation(id);}}
