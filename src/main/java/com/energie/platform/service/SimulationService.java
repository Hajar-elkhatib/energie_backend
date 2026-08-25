package com.energie.platform.service;

import com.energie.platform.dto.Dtos.*;
import com.energie.platform.exception.ApiException;
import com.energie.platform.model.*;
import com.energie.platform.repository.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SimulationService {
  private final SimulationRepository repo;
  private final ScoreRepository scores;
  private final FormulaireService formulaires;
  private final ProduitService produits;

  @Transactional
  public SimulationResponse creerSimulation(SimulationCreationRequest r) {
    Formulaire f = formulaires.get(r.formulaireId());
    Produit p = produits.get(r.produitId());

    Simulation s = new Simulation();
    s.setFormulaire(f);
    s.setVisiteur(f.getVisiteur());
    s.setProduit(p);
    s.setDateCalcul(LocalDateTime.now());
    s.setCoutEstime(p.getPrix());
    s.setStatut(StatutSimulation.TERMINEE);

    // Calcul du score et des recommandations ciblées
    float scoreValeur = 8.5f;
    Map<String, String> reponses = f.getReponses();
    if (reponses != null) {
      String conso = reponses.get("consommation");
      if (conso != null && Double.parseDouble(conso.replaceAll("[^0-9.]", "0")) > 3500) {
        scoreValeur = 9.2f;
      }
    }

    Score score = new Score();
    score.setSimulation(s);
    score.setValeur(scoreValeur);
    score.setCriteres("Calculé selon le profil " + f.getProfil() + " et le produit " + p.getNom());

    List<Recommandation> recs = new ArrayList<>();
    
    Recommandation r1 = new Recommandation();
    r1.setScore(score);
    r1.setDescription("Production solaire optimale : Dimensionnement adapté pour couvrir jusqu'à 75% de vos besoins énergétiques.");
    recs.add(r1);

    Recommandation r2 = new Recommandation();
    r2.setScore(score);
    r2.setDescription("Stockage par batterie recommandé : Maximisez l'autoconsommation nocturne et réduisez l'impact des hausses de tarifs.");
    recs.add(r2);

    Recommandation r3 = new Recommandation();
    r3.setScore(score);
    r3.setDescription("Primes Régionales & Aides : Éligible aux primes de votre région pour réduire le coût d'installation jusqu'à 35%.");
    recs.add(r3);

    score.setRecommandations(recs);
    s.setScore(score);

    Simulation saved = repo.save(s);
    scores.save(score);
    return Mapping.simulation(saved);
  }

  @Transactional(readOnly = true)
  public Simulation get(Long id) {
    return repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Simulation introuvable"));
  }

  @Transactional(readOnly = true)
  public List<SimulationResponse> enAttente() {
    return repo.findByStatut(StatutSimulation.EN_ATTENTE).stream().map(Mapping::simulation).toList();
  }

  @Transactional
  public SimulationResponse enregistrerResultat(Long id, SimulationResultatRequest r) {
    Simulation s = get(id);
    s.setCoutEstime(r.coutEstime());
    s.setStatut(StatutSimulation.TERMINEE);
    Score score = s.getScore();
    if (score == null) {
      score = new Score();
      score.setSimulation(s);
    }
    score.setValeur(r.scoreValeur());
    score.setCriteres(r.scoreCriteres());
    score.getRecommandations().clear();
    for (String description : r.recommandations()) {
      Recommandation rec = new Recommandation();
      rec.setDescription(description);
      rec.setScore(score);
      score.getRecommandations().add(rec);
    }
    scores.save(score);
    s.setScore(score);
    return Mapping.simulation(repo.save(s));
  }

  @Transactional(readOnly = true)
  public SimulationResponse consulterResultat(Long id) {
    return Mapping.simulation(get(id));
  }

  @Transactional(readOnly = true)
  public SimulationContexteResponse contexte(Long id) {
    Simulation s = get(id);
    return new SimulationContexteResponse(
      s.getId(),
      Mapping.formulaire(s.getFormulaire()),
      Mapping.produit(s.getProduit()),
      Mapping.visiteur(s.getVisiteur()),
      s.getFormulaire().getRegions().stream().map(Mapping::region).toList()
    );
  }
}
