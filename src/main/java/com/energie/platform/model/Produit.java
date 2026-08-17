package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="produits") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Produit {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String nom;
  @Column(nullable=false) private String type;
  @Column(nullable=false, precision=12, scale=2) private BigDecimal prix;
  @Column(columnDefinition="TEXT") private String specifications;
  public void afficherDetails() { }
}
