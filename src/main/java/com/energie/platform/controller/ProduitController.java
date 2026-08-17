package com.energie.platform.controller;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.service.ProduitService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.responses.ApiResponse; import io.swagger.v3.oas.annotations.tags.Tag; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/produits") @Tag(name="Public - Catalogue") @RequiredArgsConstructor public class ProduitController {private final ProduitService service;
 @GetMapping @Operation(summary="Lister le catalogue",responses=@ApiResponse(responseCode="200",description="Catalogue retourné")) public List<ProduitResponse> liste(@RequestParam(required=false) String type){return service.liste(type);}
 @GetMapping("/{id}") @Operation(summary="Détail d'un produit") public ProduitResponse detail(@PathVariable Long id){return service.detail(id);}}
