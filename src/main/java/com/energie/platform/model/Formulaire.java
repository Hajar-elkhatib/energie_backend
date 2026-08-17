package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="formulaires") @Getter @Setter @NoArgsConstructor
public class Formulaire {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String profil;
  @Column(nullable=false) private int etapeActuelle;
  private LocalDateTime dateSoumission;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="visiteur_id", nullable=false) private Visiteur visiteur;
  @ManyToMany @JoinTable(name="formulaire_regions", joinColumns=@JoinColumn(name="formulaire_id"), inverseJoinColumns=@JoinColumn(name="region_id")) private Set<Region> regions = new HashSet<>();
  @ElementCollection @CollectionTable(name="formulaire_reponses", joinColumns=@JoinColumn(name="formulaire_id")) @MapKeyColumn(name="champ") @Column(name="valeur", columnDefinition="TEXT") private Map<String,String> reponses = new HashMap<>();
  public void remplirEtape(){ } public void valider(){ } public void soumettre(){ this.dateSoumission=LocalDateTime.now(); }
}
