package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity @Table(name="scores") @Getter @Setter @NoArgsConstructor
public class Score {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private float valeur;
  @Column(columnDefinition="TEXT") private String criteres;
  @OneToOne @JoinColumn(name="simulation_id", nullable=false, unique=true) private Simulation simulation;
  @OneToMany(mappedBy="score", cascade=CascadeType.ALL, orphanRemoval=true) private List<Recommandation> recommandations = new ArrayList<>();
  public void calculerScore(){ }
}
