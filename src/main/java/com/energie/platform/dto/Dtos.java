package com.energie.platform.dto;

import com.energie.platform.model.*;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public final class Dtos {
  private Dtos(){}
  public record VisiteurCreationRequest(@NotNull Profil profil, @NotBlank @Email String email, @NotNull Long regionId, String adresseDomicile, String typeLogement, String raisonSociale, String numeroTVA, String secteurActivite) {}
  public record VisiteurResponse(Long id, String email, Profil profil, String region, String adresseDomicile, String typeLogement, String raisonSociale, String numeroTVA, String secteurActivite, LocalDateTime dateCreation) {}
  public record ProduitRequest(@NotBlank String nom, @NotBlank String type, @NotNull @DecimalMin("0.0") BigDecimal prix, String specifications) {}
  public record ProduitResponse(Long id, String nom, String type, BigDecimal prix, String specifications) {}
  public record RegionUpdateRequest(@NotBlank String lois, @NotBlank String primes, String champsFormulaire) {}
  public record RegionResponse(Long id, String nom, String lois, String primes, String champsFormulaire) {}
  public record FormulaireCreationRequest(@NotNull Long visiteurId, @NotEmpty Set<Long> regionIds) {}
  /**
   * Accepts both the documented {"reponses": {...}} payload and a flattened
   * frontend payload such as {"etape": 2, "surface": 120, "consommation": 3500}.
   */
  public static final class EtapeFormulaireRequest {
    @Min(1) private int etape;
    @NotNull private Map<String, String> reponses = new LinkedHashMap<>();

    public int etape() { return etape; }
    public Map<String, String> reponses() { return reponses; }

    @JsonProperty("etape")
    public void setEtape(int etape) { this.etape = etape; }

    @JsonProperty("reponses")
    public void setReponses(Map<String, ?> valeurs) {
      this.reponses = new LinkedHashMap<>();
      if (valeurs != null) {
        valeurs.forEach((champ, valeur) -> this.reponses.put(champ, valeur == null ? "" : String.valueOf(valeur)));
      }
    }

    @JsonAnySetter
    public void ajouterChampDirect(String champ, Object valeur) {
      if (!"etape".equals(champ) && !"reponses".equals(champ)) {
        this.reponses.put(champ, valeur == null ? "" : String.valueOf(valeur));
      }
    }
  }
  public record FormulaireResponse(Long id, String profil, int etapeActuelle, LocalDateTime dateSoumission, Long visiteurId, List<RegionResponse> regions, Map<String,String> reponses) {}
  public record SimulationCreationRequest(@NotNull Long formulaireId, @NotNull Long produitId) {}
  public record RecommandationResponse(Long id, String description) {}
  public record ScoreResponse(Long id, float valeur, String criteres, List<RecommandationResponse> recommandations) {}
  public record SimulationResponse(Long id, String referencePublique, LocalDateTime dateCalcul, BigDecimal coutEstime, BigDecimal primeEstimee, String primeStatut, String primeSourceUrl, BigDecimal economiesAnnuellesMin, BigDecimal economiesAnnuellesMax, BigDecimal coutEnergieAnnuelAvant, BigDecimal coutEnergieAnnuelApresMin, BigDecimal coutEnergieAnnuelApresMax, BigDecimal economiesEnergieMinKwh, BigDecimal economiesEnergieMaxKwh, StatutSimulation statut, Long formulaireId, ProduitResponse produit, ScoreResponse score) {}
  public record SimulationResultatRequest(@NotNull @DecimalMin("0.0") BigDecimal coutEstime, @DecimalMin("0.0") BigDecimal primeEstimee, String primeStatut, String primeSourceUrl, @DecimalMin("0.0") BigDecimal economiesAnnuellesMin, @DecimalMin("0.0") BigDecimal economiesAnnuellesMax, @DecimalMin("0.0") BigDecimal coutEnergieAnnuelAvant, @DecimalMin("0.0") BigDecimal coutEnergieAnnuelApresMin, @DecimalMin("0.0") BigDecimal coutEnergieAnnuelApresMax, @DecimalMin("0.0") BigDecimal economiesEnergieMinKwh, @DecimalMin("0.0") BigDecimal economiesEnergieMaxKwh, @DecimalMin("0.0") @DecimalMax("100.0") float scoreValeur, String scoreCriteres, @NotNull List<@NotBlank String> recommandations) {}
  public record MessageCreationRequest(@NotNull Long visiteurId, Long conversationId, @NotBlank String message) {}
  public record ReponseIARequest(@NotBlank String reponse) {}
  public record MessageResponse(Long id, Long conversationId, String contenu, AuteurMessage auteur, StatutMessage statut, LocalDateTime dateCreation) {}
  public record ConversationResponse(Long id, Long visiteurId, List<MessageResponse> messages) {}
    public record RendezVousCreationRequest(@NotNull Long visiteurId, @NotNull @FutureOrPresent LocalDate date, @NotBlank @Pattern(regexp="^([01]\\d|2[0-3]):[0-5]\\d$",message="L'heure doit respecter le format HH:MM") String heure) {}
  public record RendezVousStatutRequest(@NotNull StatutRendezVous statut) {}
  public record RendezVousResponse(Long id, LocalDate date, String heure, StatutRendezVous statut, Long visiteurId) {}
  public record LoginRequest(@NotBlank String login, @NotBlank String motDePasse) {}
  public record TokenResponse(String token, String type) {}
  public record SimulationContexteResponse(Long simulationId, FormulaireResponse formulaire, ProduitResponse produit, VisiteurResponse visiteur, List<RegionResponse> regions) {}
  public record ConversationContexteResponse(ConversationResponse conversation, List<ProduitResponse> catalogue, List<FormulaireResponse> formulaires) {}
    public record AiRendezVousRequest(@NotNull @FutureOrPresent LocalDate date, @NotBlank @Pattern(regexp="^([01]\\d|2[0-3]):[0-5]\\d$",message="L'heure doit respecter le format HH:MM") String heure) {}
  public record AiSimulationRequest(@NotNull Long formulaireId, @NotNull Long produitId) {}
  public record StatistiquesResponse(long formulairesRecus, long totalVisiteurs, double tauxConversion, long particuliers, long societes, Map<String,Long> repartitionParRegion, List<RendezVousResponse> rendezVousAVenir) {}
}
