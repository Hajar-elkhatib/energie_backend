package com.energie.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="messages_conversation") @Getter @Setter @NoArgsConstructor
public class MessageConversation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(columnDefinition="TEXT", nullable=false) private String contenu;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private AuteurMessage auteur;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private StatutMessage statut;
  @Column(nullable=false) private LocalDateTime dateCreation;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="conversation_id", nullable=false) private ChatbotIA conversation;
  @PrePersist void prePersist(){ if(dateCreation==null) dateCreation=LocalDateTime.now(); }
}
