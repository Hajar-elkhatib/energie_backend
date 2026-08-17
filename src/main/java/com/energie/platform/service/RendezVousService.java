package com.energie.platform.service;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.exception.ApiException; import com.energie.platform.model.*; import com.energie.platform.repository.Repositories.RendezVousRepository; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalDate; import java.util.*;
@Service @RequiredArgsConstructor public class RendezVousService {private final RendezVousRepository repo;private final VisiteurService visiteurs;
 @Transactional public RendezVousResponse planifier(RendezVousCreationRequest r){return creer(r.visiteurId(),r.date(),r.heure());}
 @Transactional public RendezVousResponse creer(Long visiteurId,LocalDate date,String heure){RendezVous x=new RendezVous();x.setVisiteur(visiteurs.get(visiteurId));x.setDate(date);x.setHeure(heure);x.planifier();return Mapping.rdv(repo.save(x));}
 @Transactional(readOnly=true) public List<RendezVousResponse> liste(){return repo.findAll().stream().map(Mapping::rdv).toList();}
 @Transactional(readOnly=true) public List<RendezVousResponse> aVenir(){return repo.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now()).stream().map(Mapping::rdv).toList();}
 @Transactional public RendezVousResponse modifier(Long id,RendezVousStatutRequest r){RendezVous x=repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Rendez-vous introuvable"));x.setStatut(r.statut());return Mapping.rdv(repo.save(x));}
}
