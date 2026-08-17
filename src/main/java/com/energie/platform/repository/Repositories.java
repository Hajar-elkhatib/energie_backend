package com.energie.platform.repository;

import com.energie.platform.model.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public final class Repositories {
  private Repositories(){}
  public interface VisiteurRepository extends JpaRepository<Visiteur,Long>{ long countByProfil(Profil profil); }
  public interface ParticulierRepository extends JpaRepository<Particulier,Long>{}
  public interface SocieteRepository extends JpaRepository<Societe,Long>{}
  public interface ProduitRepository extends JpaRepository<Produit,Long>{ List<Produit> findByTypeIgnoreCase(String type); }
  public interface RegionRepository extends JpaRepository<Region,Long>{}
  public interface FormulaireRepository extends JpaRepository<Formulaire,Long>{ List<Formulaire> findByVisiteurId(Long visiteurId); long countByDateSoumissionIsNotNull(); }
  public interface SimulationRepository extends JpaRepository<Simulation,Long>{ List<Simulation> findByStatut(StatutSimulation statut); }
  public interface ScoreRepository extends JpaRepository<Score,Long>{}
  public interface RecommandationRepository extends JpaRepository<Recommandation,Long>{}
  public interface ChatbotIARepository extends JpaRepository<ChatbotIA,Long>{ Optional<ChatbotIA> findByVisiteurId(Long visiteurId); }
  public interface MessageConversationRepository extends JpaRepository<MessageConversation,Long>{ List<MessageConversation> findByStatutAndAuteur(StatutMessage statut, AuteurMessage auteur); }
  public interface RendezVousRepository extends JpaRepository<RendezVous,Long>{ List<RendezVous> findByDateGreaterThanEqualOrderByDateAsc(java.time.LocalDate date); }
  public interface AdministrateurRepository extends JpaRepository<Administrateur,Long>{ Optional<Administrateur> findByLogin(String login); }
}
