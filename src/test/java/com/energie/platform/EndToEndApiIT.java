package com.energie.platform;

import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.energie.platform.repository.Repositories.AdministrateurRepository;
import java.util.UUID;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest @AutoConfigureMockMvc
class EndToEndApiIT {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired PasswordEncoder encoder; @Autowired AdministrateurRepository admins; @MockBean JavaMailSender mailSender;
 private long id(String body,String field)throws Exception{return json.readTree(body).path(field).asLong();}
 private String postJson(String uri,String body)throws Exception{return mvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();}
 private long formulaireSoumis(String surface, String consommation) throws Exception {
   String email="simulation-"+UUID.randomUUID()+"@example.be";
   String visiteurJson=postJson("/api/visiteurs","{\"profil\":\"PARTICULIER\",\"email\":\""+email+"\",\"regionId\":1,\"adresseDomicile\":\"Rue de Test 1\",\"typeLogement\":\"Maison\"}");
   long visiteur=id(visiteurJson,"id");
   long formulaire=id(postJson("/api/formulaires","{\"visiteurId\":"+visiteur+",\"regionIds\":[1]}"),"id");
   String reponses="{\"etape\":2,\"reponses\":{\"surface\":"+json.writeValueAsString(surface)+",\"consommation\":"+json.writeValueAsString(consommation)+",\"chauffage\":\"gaz\",\"niveauIsolation\":\"faible\",\"anneeBatiment\":\"1970_1990\",\"tarifMode\":\"inconnu\"}}";
   mvc.perform(put("/api/formulaires/{id}/etape",formulaire).contentType(MediaType.APPLICATION_JSON).content(reponses)).andExpect(status().isOk());
   mvc.perform(post("/api/formulaires/{id}/soumettre",formulaire)).andExpect(status().isOk());
   return formulaire;
 }
 @Test void parcoursPublicIaEtAdmin() throws Exception {
   var admin=admins.findByLogin("admin").orElseThrow(); admin.setMotDePasse(encoder.encode("password")); admins.save(admin);
   String v=postJson("/api/visiteurs","{\"profil\":\"PARTICULIER\",\"email\":\"lea@example.be\",\"regionId\":1,\"adresseDomicile\":\"Rue de Test 1\",\"typeLogement\":\"Maison\"}"); long visiteur=id(v,"id");
   String f=postJson("/api/formulaires","{\"visiteurId\":"+visiteur+",\"regionIds\":[1]}"); long formulaire=id(f,"id");
   mvc.perform(put("/api/formulaires/{id}/etape",formulaire).contentType(MediaType.APPLICATION_JSON).content("{\"etape\":2,\"surface\":100,\"consommation\":3500,\"chauffage\":\"gaz\",\"niveauIsolation\":\"faible\",\"anneeBatiment\":\"1970_1990\",\"tarifMode\":\"inconnu\"}"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.reponses.surface").value("100")).andExpect(jsonPath("$.reponses.consommation").value("3500"));
   mvc.perform(post("/api/formulaires/{id}/soumettre",formulaire)).andExpect(status().isOk());
   String s=postJson("/api/simulations","{\"formulaireId\":"+formulaire+",\"produitId\":1}"); long simulation=id(s,"id"); String referencePublique=json.readTree(s).path("referencePublique").asText();
   mvc.perform(get("/api/simulations/reference/{referencePublique}",referencePublique)).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
   mvc.perform(get("/api/ai/simulations/en-attente").header("X-API-KEY","test-ai-key")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(simulation));
   mvc.perform(get("/api/ai/simulations/{id}/contexte",simulation).header("X-API-KEY","test-ai-key")).andExpect(status().isOk()).andExpect(jsonPath("$.visiteur.id").value(visiteur)).andExpect(jsonPath("$.formulaire.reponses.surface").value("100"));
   mvc.perform(post("/api/ai/simulations/{id}/resultat",simulation).header("X-API-KEY","test-ai-key").contentType(MediaType.APPLICATION_JSON).content("{\"coutEstime\":6200,\"scoreValeur\":81.5,\"scoreCriteres\":\"orientation\",\"recommandations\":[\"Installer une batterie\"]}")) .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("TERMINEE"));
   mvc.perform(get("/api/simulations/reference/{referencePublique}",referencePublique)).andExpect(status().isOk()).andExpect(jsonPath("$.score.recommandations[0].description").value("Installer une batterie"));
   String m=postJson("/api/chatbot/messages","{\"visiteurId\":"+visiteur+",\"message\":\"Quel produit choisir ?\"}");long message=id(m,"id"), conversation=id(m,"conversationId");
   mvc.perform(get("/api/ai/chatbot/messages/en-attente").header("X-API-KEY","test-ai-key")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(message));
   mvc.perform(post("/api/ai/chatbot/messages/{id}/reponse",message).header("X-API-KEY","test-ai-key").contentType(MediaType.APPLICATION_JSON).content("{\"reponse\":\"Voici une recommandation.\"}")) .andExpect(status().isOk());
   mvc.perform(get("/api/chatbot/conversations/{id}",conversation)).andExpect(status().isOk()).andExpect(jsonPath("$.messages.length()").value(2));
   String login= mvc.perform(post("/api/admin/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"admin\",\"motDePasse\":\"password\"}")) .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();String token=json.readTree(login).path("token").asText();
   mvc.perform(get("/api/admin/formulaires").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(formulaire));
   mvc.perform(get("/api/admin/statistiques").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.formulairesRecus").value(1));
 }

 @Test void catalogueAccepteAliasPompeInsensibleAuxAccentsEtALaCasse() throws Exception {
   mvc.perform(get("/api/produits").param("type", "POMPE"))
     .andExpect(status().isOk())
     .andExpect(jsonPath("$[0].type").value("pompe à chaleur"));
   mvc.perform(get("/api/produits").param("type", "pompe a chaleur"))
     .andExpect(status().isOk())
     .andExpect(jsonPath("$[0].type").value("pompe à chaleur"));
 }

 @Test void rendezVousRefuseUnCreneauDejaReserveEtApparaitDansLePlanningAdmin() throws Exception {
   var admin=admins.findByLogin("admin").orElseThrow(); admin.setMotDePasse(encoder.encode("password")); admins.save(admin);
   String first=postJson("/api/visiteurs","{\"profil\":\"PARTICULIER\",\"email\":\"rdv-"+UUID.randomUUID()+"@example.be\",\"regionId\":1,\"adresseDomicile\":\"Rue Test 1\",\"typeLogement\":\"Maison\"}");
   String second=postJson("/api/visiteurs","{\"profil\":\"PARTICULIER\",\"email\":\"rdv-"+UUID.randomUUID()+"@example.be\",\"regionId\":1,\"adresseDomicile\":\"Rue Test 2\",\"typeLogement\":\"Maison\"}");
   String date=LocalDate.now().plusDays(30).toString();
   String rendezVous=postJson("/api/rendez-vous","{\"visiteurId\":"+id(first,"id")+",\"date\":\""+date+"\",\"heure\":\"10:00\"}");
   mvc.perform(post("/api/rendez-vous").contentType(MediaType.APPLICATION_JSON).content("{\"visiteurId\":"+id(second,"id")+",\"date\":\""+date+"\",\"heure\":\"10:00\"}"))
     .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Ce créneau est déjà réservé. Choisissez une autre heure."));
   String login= mvc.perform(post("/api/admin/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"admin\",\"motDePasse\":\"password\"}")) .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
   String token=json.readTree(login).path("token").asText();
   mvc.perform(get("/api/admin/rendez-vous").header("Authorization","Bearer "+token))
     .andExpect(status().isOk()).andExpect(jsonPath("$[?(@.id == "+id(rendezVous,"id")+")]").isNotEmpty());
   mvc.perform(put("/api/admin/rendez-vous/{id}",id(rendezVous,"id")).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"statut\":\"CONFIRME\"}"))
     .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("CONFIRME"));
   verify(mailSender).send(any(SimpleMailMessage.class));
   mvc.perform(put("/api/admin/rendez-vous/{id}",id(rendezVous,"id")).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"statut\":\"ANNULE\"}"))
     .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("ANNULE"));
   mvc.perform(get("/api/admin/rendez-vous").header("Authorization","Bearer "+token))
     .andExpect(status().isOk()).andExpect(jsonPath("$[?(@.id == "+id(rendezVous,"id")+")]").isEmpty());
   verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
 }

 @Test void validationSimulationRefuseLesValeursInvalidesEtAccepteLesFormatsBelges() throws Exception {
   long sansSurface=formulaireSoumis("", "18500");
   mvc.perform(post("/api/simulations").contentType(MediaType.APPLICATION_JSON).content("{\"formulaireId\":"+sansSurface+",\"produitId\":1}"))
     .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("FORMULAIRE_INCOMPLET")).andExpect(jsonPath("$.champs[0]").value("surface"));

   long consommationInvalide=formulaireSoumis("120", "inconnue");
   mvc.perform(post("/api/simulations").contentType(MediaType.APPLICATION_JSON).content("{\"formulaireId\":"+consommationInvalide+",\"produitId\":1}"))
     .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("FORMULAIRE_INVALIDE")).andExpect(jsonPath("$.champs[0]").value("consommation"));

   long nonPositives=formulaireSoumis("0", "-1");
   mvc.perform(post("/api/simulations").contentType(MediaType.APPLICATION_JSON).content("{\"formulaireId\":"+nonPositives+",\"produitId\":1}"))
     .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("FORMULAIRE_INVALIDE"));

   long formatBelge=formulaireSoumis("120,5", "18 500");
   String simulation=postJson("/api/simulations","{\"formulaireId\":"+formatBelge+",\"produitId\":1}");
   long simulationId=id(simulation,"id"); String referencePublique=json.readTree(simulation).path("referencePublique").asText();
   mvc.perform(get("/api/simulations/reference/{referencePublique}",referencePublique)).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
   mvc.perform(get("/api/ai/simulations/en-attente").header("X-API-KEY","test-ai-key")).andExpect(status().isOk()).andExpect(jsonPath("$[?(@.id == "+simulationId+")]").isNotEmpty());
 }
}
