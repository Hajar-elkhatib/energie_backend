package com.energie.platform.service;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.exception.ApiException; import com.energie.platform.model.*; import com.energie.platform.repository.Repositories.RendezVousRepository; import java.time.*; import java.util.*; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Isolation; import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor public class RendezVousService {private final RendezVousRepository repo;private final VisiteurService visiteurs;private final Optional<EmailNotificationService> emailNotificationService;
 @Transactional public RendezVousResponse planifier(RendezVousCreationRequest r){return creer(r.visiteurId(),r.date(),r.heure());}
 @Transactional(isolation=Isolation.SERIALIZABLE) public RendezVousResponse creer(Long visiteurId,LocalDate date,String heure){
   if(repo.existsByDateAndHeureAndStatutIn(date,heure,List.of(StatutRendezVous.PLANIFIE,StatutRendezVous.CONFIRME)))
     throw new ApiException(HttpStatus.CONFLICT,"Ce créneau est déjà réservé. Choisissez une autre heure.");
   RendezVous x=new RendezVous();x.setVisiteur(visiteurs.get(visiteurId));x.setDate(date);x.setHeure(heure);x.planifier();return Mapping.rdv(repo.save(x));
 }
 @Transactional(isolation=Isolation.SERIALIZABLE) public RendezVousResponse modifierCreneau(Long id,Long visiteurId,LocalDate date,String heure){
   RendezVous x=repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Rendez-vous introuvable"));
   if(!x.getVisiteur().getId().equals(visiteurId))throw new ApiException(HttpStatus.FORBIDDEN,"Ce rendez-vous ne correspond pas à votre profil");
   if(x.getStatut()==StatutRendezVous.ANNULE)throw new ApiException(HttpStatus.BAD_REQUEST,"Un rendez-vous annulé ne peut plus être modifié");
   boolean occupe=repo.findByDateAndStatutIn(date,List.of(StatutRendezVous.PLANIFIE,StatutRendezVous.CONFIRME)).stream().anyMatch(r->!r.getId().equals(id)&&r.getHeure().equals(heure));
   if(occupe)throw new ApiException(HttpStatus.CONFLICT,"Ce créneau est déjà réservé. Choisissez une autre heure.");
   x.setDate(date);x.setHeure(heure);x.planifier();return Mapping.rdv(repo.save(x));
 }
 @Transactional public RendezVousResponse annulerPourVisiteur(Long id,Long visiteurId){
   RendezVous x=repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Rendez-vous introuvable"));
   if(!x.getVisiteur().getId().equals(visiteurId))throw new ApiException(HttpStatus.FORBIDDEN,"Ce rendez-vous ne correspond pas à votre profil");
   return annulerAvecNotification(x);
 }
 @Transactional(readOnly=true) public List<RendezVousResponse> liste(){return repo.findAll().stream().map(Mapping::rdv).toList();}
 @Transactional(readOnly=true) public List<String> creneauxDisponibles(LocalDate date){
   if(date.isBefore(LocalDate.now()) || date.getDayOfWeek()==DayOfWeek.SATURDAY || date.getDayOfWeek()==DayOfWeek.SUNDAY) return List.of();
   Set<String> occupes=repo.findByDateAndStatutIn(date,List.of(StatutRendezVous.PLANIFIE,StatutRendezVous.CONFIRME)).stream().map(RendezVous::getHeure).collect(java.util.stream.Collectors.toSet());
   return java.util.stream.IntStream.range(9,17).mapToObj(heure->String.format("%02d:00",heure)).filter(creneau->!occupes.contains(creneau)).toList();
 }
 @Transactional(readOnly=true) public List<RendezVousResponse> aVenir(){return repo.findByDateGreaterThanEqualAndStatutInOrderByDateAsc(LocalDate.now(),List.of(StatutRendezVous.PLANIFIE,StatutRendezVous.CONFIRME)).stream().map(Mapping::rdv).toList();}
 @Transactional public RendezVousResponse modifier(Long id,RendezVousStatutRequest r){
   RendezVous x=repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Rendez-vous introuvable"));
   boolean confirmationInitiale=r.statut()==StatutRendezVous.CONFIRME&&x.getStatut()!=StatutRendezVous.CONFIRME;
   if(r.statut()==StatutRendezVous.CONFIRME&&x.getStatut()==StatutRendezVous.ANNULE)throw new ApiException(HttpStatus.BAD_REQUEST,"Un rendez-vous annulé ne peut pas être confirmé");
   if(r.statut()==StatutRendezVous.ANNULE)return annulerAvecNotification(x);
   if(r.statut()==StatutRendezVous.CONFIRME)x.confirmer(); else x.planifier();
   RendezVousResponse resultat=Mapping.rdv(repo.save(x));
   if(confirmationInitiale)emailNotificationService.ifPresent(service->service.envoyerConfirmationRendezVous(x));
   return resultat;
 }
 private RendezVousResponse annulerAvecNotification(RendezVous rendezVous){
   rendezVous.annuler();
   RendezVousResponse resultat=Mapping.rdv(repo.save(rendezVous));
   emailNotificationService.ifPresent(service->service.envoyerAnnulationRendezVous(rendezVous));
   return resultat;
 }
}
