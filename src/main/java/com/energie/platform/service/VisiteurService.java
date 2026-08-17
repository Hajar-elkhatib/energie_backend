package com.energie.platform.service;

import com.energie.platform.dto.Dtos.*;
import com.energie.platform.exception.ApiException;
import com.energie.platform.model.*;
import com.energie.platform.repository.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class VisiteurService {
 private final VisiteurRepository visiteurs; private final RegionRepository regions;
 @Transactional public VisiteurResponse creer(VisiteurCreationRequest r){
   Region region=regions.findById(r.regionId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Région introuvable")); Visiteur v;
   if(r.profil()==Profil.PARTICULIER){Particulier p=new Particulier();p.setAdresseDomicile(r.adresseDomicile());p.setTypeLogement(r.typeLogement());v=p;}
   else {Societe s=new Societe();s.setRaisonSociale(r.raisonSociale());s.setNumeroTVA(r.numeroTVA());s.setSecteurActivite(r.secteurActivite());v=s;}
   v.setEmail(r.email());v.setProfil(r.profil());v.setRegion(region);return Mapping.visiteur(visiteurs.save(v));
 }
 @Transactional(readOnly=true) public Visiteur get(Long id){return visiteurs.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Visiteur introuvable"));}
 @Transactional(readOnly=true) public VisiteurResponse consulter(Long id){return Mapping.visiteur(get(id));}
}
