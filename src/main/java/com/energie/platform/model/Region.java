package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="regions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Region {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String nom;
  @Column(columnDefinition="TEXT") private String lois;
  @Column(columnDefinition="TEXT") private String primes;
  @Column(columnDefinition="TEXT") private String champsFormulaire;
  public void configurerChamps() { }
}
