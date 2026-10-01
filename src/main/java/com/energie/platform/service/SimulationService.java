package com.energie.platform.service;

import com.energie.platform.dto.Dtos.*;
import com.energie.platform.exception.ApiException;
import com.energie.platform.exception.SimulationValidationException;
import com.energie.platform.model.*;
import com.energie.platform.repository.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SimulationService {
  public static final String CHAMP_SURFACE = "surface";
  public static final String CHAMP_CONSOMMATION = "consommation";
  public static final String CHAMP_CHAUFFAGE = "chauffage";
  public static final String CHAMP_ISOLATION = "niveauIsolation";
  public static final String CHAMP_ANNEE_BATIMENT = "anneeBatiment";
  public static final String CHAMP_TARIF_MODE = "tarifMode";
  public static final String CHAMP_TARIF_KWH = "tarifKwh";
  private final SimulationRepository repo;
  private final ScoreRepository scores;
  private final FormulaireService formulaires;
  private final ProduitService produits;

  @Transactional
  public SimulationResponse creerSimulation(SimulationCreationRequest r) {
    Formulaire f = formulaires.get(r.formulaireId());
    Produit p = produits.get(r.produitId());
    validerDonneesSimulation(f.getReponses());

    Simulation s = new Simulation();
    s.setReferencePublique(UUID.randomUUID().toString());
    s.setFormulaire(f);
    s.setVisiteur(f.getVisiteur());
    s.setProduit(p);
    s.setDateCalcul(LocalDateTime.now());
    s.setCoutEstime(null);
    s.setStatut(StatutSimulation.EN_ATTENTE);
    return Mapping.simulation(repo.save(s));
  }

  /**
   * Vérifie uniquement les entrées du formulaire. Le coût, le score et les
   * recommandations restent calculés par le service IA externe.
   */
  private void validerDonneesSimulation(Map<String, String> reponses) {
    List<String> champsManquants = new ArrayList<>();
    List<String> champsInvalides = new ArrayList<>();

    verifierValeur(reponses, CHAMP_SURFACE, champsManquants, champsInvalides);
    verifierValeur(reponses, CHAMP_CONSOMMATION, champsManquants, champsInvalides);
    verifierChoix(reponses, CHAMP_CHAUFFAGE, champsManquants);
    verifierChoix(reponses, CHAMP_ISOLATION, champsManquants);
    verifierChoix(reponses, CHAMP_ANNEE_BATIMENT, champsManquants);
    verifierChoix(reponses, CHAMP_TARIF_MODE, champsManquants);
    if ("facture".equals(reponses == null ? null : reponses.get(CHAMP_TARIF_MODE))) {
      verifierValeur(reponses, CHAMP_TARIF_KWH, champsManquants, champsInvalides);
    }

    if (!champsManquants.isEmpty()) {
      throw new SimulationValidationException(
        "FORMULAIRE_INCOMPLET",
        "Complétez les informations énergie requises avant la simulation.",
        champsManquants
      );
    }
    if (!champsInvalides.isEmpty()) {
      throw new SimulationValidationException(
        "FORMULAIRE_INVALIDE",
        "La surface, la consommation et le prix de l’énergie doivent être des nombres strictement positifs.",
        champsInvalides
      );
    }
  }

  private void verifierChoix(Map<String, String> reponses, String cle, List<String> champsManquants) {
    String valeur = reponses == null ? null : reponses.get(cle);
    if (valeur == null || valeur.isBlank()) champsManquants.add(cle);
  }

  private BigDecimal verifierValeur(Map<String, String> reponses, String cle, List<String> champsManquants, List<String> champsInvalides) {
    String brute = reponses == null ? null : reponses.get(cle);
    if (brute == null || brute.isBlank()) {
      champsManquants.add(cle);
      return null;
    }
    try {
      String normalisee = brute
        .trim()
        .replace("\u00A0", "")
        .replace("\u202F", "")
        .replaceAll("\\s+", "")
        .replace(',', '.');
      BigDecimal valeur = new BigDecimal(normalisee);
      if (valeur.signum() <= 0) {
        champsInvalides.add(cle);
        return null;
      }
      return valeur;
    } catch (NumberFormatException exception) {
      champsInvalides.add(cle);
      return null;
    }
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
    if (s.getStatut() == StatutSimulation.TERMINEE) {
      throw new ApiException(HttpStatus.CONFLICT, "Le résultat de cette simulation est déjà enregistré");
    }
    s.setCoutEstime(r.coutEstime());
    s.setPrimeEstimee(r.primeEstimee());
    s.setPrimeStatut(r.primeStatut());
    s.setPrimeSourceUrl(r.primeSourceUrl());
    s.setEconomiesAnnuellesMin(r.economiesAnnuellesMin());
    s.setEconomiesAnnuellesMax(r.economiesAnnuellesMax());
    s.setCoutEnergieAnnuelAvant(r.coutEnergieAnnuelAvant());
    s.setCoutEnergieAnnuelApresMin(r.coutEnergieAnnuelApresMin());
    s.setCoutEnergieAnnuelApresMax(r.coutEnergieAnnuelApresMax());
    s.setEconomiesEnergieMinKwh(r.economiesEnergieMinKwh());
    s.setEconomiesEnergieMaxKwh(r.economiesEnergieMaxKwh());
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
  public SimulationResponse consulterResultatParReference(String referencePublique) {
    Simulation simulation = repo.findByReferencePublique(referencePublique)
      .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Simulation introuvable"));
    return Mapping.simulation(simulation);
  }

  /** Attribue une référence publique aux simulations créées avant cette évolution. */
  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void initialiserReferencesPubliques() {
    repo.findAll().stream()
      .filter(simulation -> simulation.getReferencePublique() == null || simulation.getReferencePublique().isBlank())
      .forEach(simulation -> simulation.setReferencePublique(UUID.randomUUID().toString()));
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
