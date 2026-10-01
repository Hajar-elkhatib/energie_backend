package com.energie.platform.service;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.exception.ApiException; import com.energie.platform.model.Produit; import com.energie.platform.repository.Repositories.ProduitRepository; import com.energie.platform.repository.Repositories.SimulationRepository; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.text.Normalizer; import java.util.*;
@Service @RequiredArgsConstructor public class ProduitService { private final ProduitRepository repo; private final SimulationRepository simulations;
  @Transactional(readOnly=true) public List<ProduitResponse> liste(String type){
    boolean isAll = type == null || type.isBlank() || "all".equalsIgnoreCase(type.trim());
    if (isAll) return catalogueSansDoublons().stream().map(Mapping::produit).toList();
    String typeRecherche = normaliserType(type);
    if ("pompe".equals(typeRecherche)) typeRecherche = "pompe a chaleur";
    final String filtre = typeRecherche;
    return catalogueSansDoublons().stream()
      .filter(produit -> normaliserType(produit.getType()).equals(filtre))
      .map(Mapping::produit)
      .toList();
  }
 @Transactional(readOnly=true) public Produit get(Long id){return repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Produit introuvable"));}
 @Transactional(readOnly=true) public ProduitResponse detail(Long id){return Mapping.produit(get(id));}
 @Transactional public ProduitResponse creer(ProduitRequest r){
   if (repo.existsByNomAndTypeAndPrixAndSpecifications(r.nom(), r.type(), r.prix(), r.specifications())) {
     throw new ApiException(HttpStatus.CONFLICT,"Un produit identique existe déjà dans le catalogue");
   }
   return Mapping.produit(repo.save(entity(new Produit(),r)));
 }
 @Transactional public ProduitResponse modifier(Long id,ProduitRequest r){return Mapping.produit(repo.save(entity(get(id),r)));}
 @Transactional public void supprimer(Long id){
   Produit produit=get(id);
   if(simulations.countByProduitId(id)>0) throw new ApiException(HttpStatus.CONFLICT,"Ce produit est déjà utilisé dans une simulation. Modifiez-le pour conserver l'historique des résultats.");
   repo.delete(produit);
 }
 private Produit entity(Produit p,ProduitRequest r){p.setNom(r.nom());p.setType(r.type());p.setPrix(r.prix());p.setSpecifications(r.specifications());return p;}
 private String normaliserType(String valeur){return Normalizer.normalize(valeur.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);}
 private List<Produit> catalogueSansDoublons(){
   Set<String> identites = new HashSet<>();
   return repo.findAll().stream().filter(produit -> identites.add(
     produit.getNom()+"\u0000"+produit.getType()+"\u0000"+produit.getPrix()+"\u0000"+produit.getSpecifications()
   )).toList();
 }
}
