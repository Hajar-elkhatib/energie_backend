package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity @Table(name="chatbots") @Getter @Setter @NoArgsConstructor
public class ChatbotIA {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(columnDefinition="TEXT") private String historiqueConversation;
  @OneToOne @JoinColumn(name="visiteur_id", nullable=false, unique=true) private Visiteur visiteur;
  @OneToMany(mappedBy="conversation", cascade=CascadeType.ALL, orphanRemoval=true) @OrderBy("dateCreation ASC") private List<MessageConversation> messages = new ArrayList<>();
  public void repondreQuestions(){} public void aiderChoixProduit(){} public void calculerScore(){} public void genererRecommandations(){} public void gererPriseRDV(){}
}
