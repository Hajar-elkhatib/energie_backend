package com.energie.platform.controller;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.service.SimulationService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/simulations") @Tag(name="Public - Simulations") @RequiredArgsConstructor public class SimulationController {private final SimulationService service;
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Créer une demande de simulation") public SimulationResponse creer(@Valid @RequestBody SimulationCreationRequest r){return service.creerSimulation(r);}
 @GetMapping("/{id}") @Operation(summary="Consulter un résultat ou son statut d'attente") public SimulationResponse resultat(@PathVariable Long id){return service.consulterResultat(id);}}
