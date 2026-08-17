package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="recommandations") @Getter @Setter @NoArgsConstructor
public class Recommandation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(columnDefinition="TEXT", nullable=false) private String description;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="score_id", nullable=false) private Score score;
  public void genererRecommandation(){ }
}
