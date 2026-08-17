package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Inheritance(strategy=InheritanceType.JOINED) @Getter @Setter @NoArgsConstructor
public abstract class Visiteur {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) protected Long id;
  @Column(nullable=false) protected String email;
  @Column(nullable=false) protected LocalDateTime dateCreation;
  @Enumerated(EnumType.STRING) @Column(nullable=false) protected Profil profil;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="region_id") protected Region region;
  @PrePersist void prePersist(){ if(dateCreation==null) dateCreation=LocalDateTime.now(); }
  public void choisirProfil() { } public void consulterPageAccueil() { } public void consulterCatalogue() { } public void remplirFormulaire() { }
}
