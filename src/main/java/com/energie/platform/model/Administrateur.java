package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="administrateurs") @Getter @Setter @NoArgsConstructor
public class Administrateur {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String login;
  @Column(nullable=false) private String motDePasse;
  public void gererDonneesCRM(){} public void consulterStatistiques(){} public void gererCatalogue(){} public void configurerChampsFormulaire(){} public void gererPlanningRDV(){}
}
