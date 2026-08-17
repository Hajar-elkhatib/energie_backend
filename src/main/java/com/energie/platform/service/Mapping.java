package com.energie.platform.service;

import com.energie.platform.dto.Dtos.*;
import com.energie.platform.model.*;
import java.util.*;

final class Mapping {
  private Mapping(){}
  static ProduitResponse produit(Produit p){return new ProduitResponse(p.getId(),p.getNom(),p.getType(),p.getPrix(),p.getSpecifications());}
  static RegionResponse region(Region r){return new RegionResponse(r.getId(),r.getNom(),r.getLois(),r.getPrimes(),r.getChampsFormulaire());}
  static VisiteurResponse visiteur(Visiteur v){ return new VisiteurResponse(v.getId(),v.getEmail(),v.getProfil(),v.getRegion()==null?null:v.getRegion().getNom(),v instanceof Particulier p?p.getAdresseDomicile():null,v instanceof Particulier p?p.getTypeLogement():null,v instanceof Societe s?s.getRaisonSociale():null,v instanceof Societe s?s.getNumeroTVA():null,v instanceof Societe s?s.getSecteurActivite():null,v.getDateCreation()); }
  static FormulaireResponse formulaire(Formulaire f){return new FormulaireResponse(f.getId(),f.getProfil(),f.getEtapeActuelle(),f.getDateSoumission(),f.getVisiteur().getId(),f.getRegions().stream().map(Mapping::region).toList(),new HashMap<>(f.getReponses()));}
  static ScoreResponse score(Score s){return s==null?null:new ScoreResponse(s.getId(),s.getValeur(),s.getCriteres(),s.getRecommandations().stream().map(r->new RecommandationResponse(r.getId(),r.getDescription())).toList());}
  static SimulationResponse simulation(Simulation s){return new SimulationResponse(s.getId(),s.getDateCalcul(),s.getCoutEstime(),s.getStatut(),s.getFormulaire().getId(),produit(s.getProduit()),score(s.getScore()));}
  static MessageResponse message(MessageConversation m){return new MessageResponse(m.getId(),m.getConversation().getId(),m.getContenu(),m.getAuteur(),m.getStatut(),m.getDateCreation());}
  static ConversationResponse conversation(ChatbotIA c){return new ConversationResponse(c.getId(),c.getVisiteur().getId(),c.getMessages().stream().map(Mapping::message).toList());}
  static RendezVousResponse rdv(RendezVous r){return new RendezVousResponse(r.getId(),r.getDate(),r.getHeure(),r.getStatut(),r.getVisiteur().getId());}
}
