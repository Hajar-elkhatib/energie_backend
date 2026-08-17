package com.energie.platform.service;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.exception.ApiException; import com.energie.platform.model.*; import com.energie.platform.repository.Repositories.*; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service @RequiredArgsConstructor public class FormulaireService {private final FormulaireRepository repo;private final VisiteurService visiteurs;private final RegionRepository regions;
 @Transactional public FormulaireResponse creer(FormulaireCreationRequest r){Formulaire f=new Formulaire(); Visiteur v=visiteurs.get(r.visiteurId()); f.setVisiteur(v);f.setProfil(v.getProfil().name());f.setEtapeActuelle(1);Set<Region> rs=new HashSet<>();for(Long id:r.regionIds())rs.add(regions.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Région introuvable")));f.setRegions(rs);return Mapping.formulaire(repo.save(f));}
 @Transactional(readOnly=true) public Formulaire get(Long id){return repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Formulaire introuvable"));}
 @Transactional public FormulaireResponse etape(Long id,EtapeFormulaireRequest r){Formulaire f=get(id);if(f.getDateSoumission()!=null)throw new ApiException(HttpStatus.CONFLICT,"Formulaire déjà soumis");f.getReponses().putAll(r.reponses());f.setEtapeActuelle(r.etape());return Mapping.formulaire(repo.save(f));}
 @Transactional public FormulaireResponse soumettre(Long id){Formulaire f=get(id);f.soumettre();return Mapping.formulaire(repo.save(f));}
 @Transactional(readOnly=true) public FormulaireResponse detail(Long id){return Mapping.formulaire(get(id));}
 @Transactional(readOnly=true) public List<FormulaireResponse> liste(){return repo.findAll().stream().map(Mapping::formulaire).toList();}
 @Transactional(readOnly=true) public List<FormulaireResponse> parVisiteur(Long id){return repo.findByVisiteurId(id).stream().map(Mapping::formulaire).toList();}
}
