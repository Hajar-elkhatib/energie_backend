package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name="rendez_vous") @Getter @Setter @NoArgsConstructor
public class RendezVous {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private LocalDate date;
  @Column(nullable=false) private String heure;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private StatutRendezVous statut;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="visiteur_id", nullable=false) private Visiteur visiteur;
  public void planifier(){statut=StatutRendezVous.PLANIFIE;} public void confirmer(){statut=StatutRendezVous.CONFIRME;} public void annuler(){statut=StatutRendezVous.ANNULE;}
}
