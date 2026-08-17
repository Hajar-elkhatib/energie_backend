package com.energie.platform.controller;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.service.VisiteurService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/visiteurs") @Tag(name="Public - Visiteurs") @RequiredArgsConstructor public class VisiteurController {private final VisiteurService service;
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Créer un particulier ou une société") public VisiteurResponse creer(@Valid @RequestBody VisiteurCreationRequest r){return service.creer(r);}}
