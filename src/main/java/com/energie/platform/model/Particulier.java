package com.energie.platform.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor
public class Particulier extends Visiteur {
  private String adresseDomicile;
  private String typeLogement;
  public void demanderSimulation() { }
}
