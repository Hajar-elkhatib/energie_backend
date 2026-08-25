package com.energie.platform.service;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.exception.ApiException; import com.energie.platform.model.Produit; import com.energie.platform.repository.Repositories.ProduitRepository; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service @RequiredArgsConstructor public class ProduitService { private final ProduitRepository repo;
  @Transactional(readOnly=true) public List<ProduitResponse> liste(String type){
    boolean isAll = type == null || type.isBlank() || "all".equalsIgnoreCase(type.trim());
    return (isAll ? repo.findAll() : repo.findByTypeIgnoreCase(type.trim())).stream().map(Mapping::produit).toList();
  }
 @Transactional(readOnly=true) public Produit get(Long id){return repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Produit introuvable"));}
 @Transactional(readOnly=true) public ProduitResponse detail(Long id){return Mapping.produit(get(id));}
 @Transactional public ProduitResponse creer(ProduitRequest r){return Mapping.produit(repo.save(entity(new Produit(),r)));}
 @Transactional public ProduitResponse modifier(Long id,ProduitRequest r){return Mapping.produit(repo.save(entity(get(id),r)));}
 @Transactional public void supprimer(Long id){repo.delete(get(id));}
 private Produit entity(Produit p,ProduitRequest r){p.setNom(r.nom());p.setType(r.type());p.setPrix(r.prix());p.setSpecifications(r.specifications());return p;}
}
