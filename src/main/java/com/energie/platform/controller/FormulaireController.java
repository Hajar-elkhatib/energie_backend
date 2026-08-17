package com.energie.platform.controller;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.service.FormulaireService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/formulaires") @Tag(name="Public - Formulaires") @RequiredArgsConstructor public class FormulaireController {private final FormulaireService service;
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Démarrer un formulaire") public FormulaireResponse creer(@Valid @RequestBody FormulaireCreationRequest r){return service.creer(r);}
 @PutMapping("/{id}/etape") @Operation(summary="Enregistrer une étape") public FormulaireResponse etape(@PathVariable Long id,@Valid @RequestBody EtapeFormulaireRequest r){return service.etape(id,r);}
 @PostMapping("/{id}/soumettre") @Operation(summary="Finaliser un formulaire") public FormulaireResponse soumettre(@PathVariable Long id){return service.soumettre(id);}}
