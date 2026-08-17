package com.energie.platform.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor
public class Societe extends Visiteur {
  private String raisonSociale;
  private String numeroTVA;
  private String secteurActivite;
  public void demanderSimulation() { }
}
