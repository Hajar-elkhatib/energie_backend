package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="simulations") @Getter @Setter @NoArgsConstructor
public class Simulation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private LocalDateTime dateCalcul;
  @Column(precision=12, scale=2) private BigDecimal coutEstime;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private StatutSimulation statut;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="visiteur_id", nullable=false) private Visiteur visiteur;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="formulaire_id", nullable=false) private Formulaire formulaire;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="produit_id", nullable=false) private Produit produit;
  @OneToOne(mappedBy="simulation", cascade=CascadeType.ALL, orphanRemoval=true) private Score score;
  public void lancerSimulation(){ } public void calculerCout(){ }
}
