package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="simulations") @Getter @Setter @NoArgsConstructor
public class Simulation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  /** Référence publique non devinable : l'identifiant interne ne sort pas dans les URLs. */
  @Column(name="reference_publique", unique=true, length=36) private String referencePublique;
  @Column(nullable=false) private LocalDateTime dateCalcul;
  @Column(precision=12, scale=2) private BigDecimal coutEstime;
  /** Estimation déterministe issue d'un barème régional versionné, jamais une prime accordée. */
  @Column(precision=12, scale=2) private BigDecimal primeEstimee;
  @Column(length=500) private String primeStatut;
  @Column(length=1_000) private String primeSourceUrl;
  /** Fourchette financière calculée à partir des informations énergie du formulaire. */
  @Column(precision=12, scale=2) private BigDecimal economiesAnnuellesMin;
  @Column(precision=12, scale=2) private BigDecimal economiesAnnuellesMax;
  /** Facture d'énergie annuelle déclarée : avant et après la solution simulée. */
  @Column(precision=12, scale=2) private BigDecimal coutEnergieAnnuelAvant;
  @Column(precision=12, scale=2) private BigDecimal coutEnergieAnnuelApresMin;
  @Column(precision=12, scale=2) private BigDecimal coutEnergieAnnuelApresMax;
  @Column(precision=12, scale=2) private BigDecimal economiesEnergieMinKwh;
  @Column(precision=12, scale=2) private BigDecimal economiesEnergieMaxKwh;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private StatutSimulation statut;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="visiteur_id", nullable=false) private Visiteur visiteur;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="formulaire_id", nullable=false) private Formulaire formulaire;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="produit_id", nullable=false) private Produit produit;
  @OneToOne(mappedBy="simulation", cascade=CascadeType.ALL, orphanRemoval=true) private Score score;
  public void lancerSimulation(){ } public void calculerCout(){ }
}
