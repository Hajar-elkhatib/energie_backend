package com.energie.platform;

import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.energie.platform.repository.Repositories.AdministrateurRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class EndToEndApiIT {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired PasswordEncoder encoder; @Autowired AdministrateurRepository admins;
 private long id(String body,String field)throws Exception{return json.readTree(body).path(field).asLong();}
 private String postJson(String uri,String body)throws Exception{return mvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();}
 @Test void parcoursPublicIaEtAdmin() throws Exception {
   var admin=admins.findByLogin("admin").orElseThrow(); admin.setMotDePasse(encoder.encode("password")); admins.save(admin);
   String v=postJson("/api/visiteurs","{\"profil\":\"PARTICULIER\",\"email\":\"lea@example.be\",\"regionId\":1,\"adresseDomicile\":\"Rue de Test 1\",\"typeLogement\":\"Maison\"}"); long visiteur=id(v,"id");
   String f=postJson("/api/formulaires","{\"visiteurId\":"+visiteur+",\"regionIds\":[1]}"); long formulaire=id(f,"id");
   mvc.perform(put("/api/formulaires/{id}/etape",formulaire).contentType(MediaType.APPLICATION_JSON).content("{\"etape\":2,\"reponses\":{\"surface\":\"100\"}}")).andExpect(status().isOk());
   mvc.perform(post("/api/formulaires/{id}/soumettre",formulaire)).andExpect(status().isOk());
   String s=postJson("/api/simulations","{\"formulaireId\":"+formulaire+",\"produitId\":1}"); long simulation=id(s,"id");
   mvc.perform(get("/api/ai/simulations/{id}/contexte",simulation).header("X-API-KEY","test-ai-key")).andExpect(status().isOk()).andExpect(jsonPath("$.visiteur.id").value(visiteur));
   mvc.perform(post("/api/ai/simulations/{id}/resultat",simulation).header("X-API-KEY","test-ai-key").contentType(MediaType.APPLICATION_JSON).content("{\"coutEstime\":6200,\"scoreValeur\":81.5,\"scoreCriteres\":\"orientation\",\"recommandations\":[\"Installer une batterie\"]}")) .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("TERMINEE"));
   mvc.perform(get("/api/simulations/{id}",simulation)).andExpect(status().isOk()).andExpect(jsonPath("$.score.recommandations[0].description").value("Installer une batterie"));
   String m=postJson("/api/chatbot/messages","{\"visiteurId\":"+visiteur+",\"message\":\"Quel produit choisir ?\"}");long message=id(m,"id"), conversation=id(m,"conversationId");
   mvc.perform(get("/api/ai/chatbot/messages/en-attente").header("X-API-KEY","test-ai-key")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(message));
   mvc.perform(post("/api/ai/chatbot/messages/{id}/reponse",message).header("X-API-KEY","test-ai-key").contentType(MediaType.APPLICATION_JSON).content("{\"reponse\":\"Voici une recommandation.\"}")) .andExpect(status().isOk());
   mvc.perform(get("/api/chatbot/conversations/{id}",conversation)).andExpect(status().isOk()).andExpect(jsonPath("$.messages.length()").value(2));
   String login= mvc.perform(post("/api/admin/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"admin\",\"motDePasse\":\"password\"}")) .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();String token=json.readTree(login).path("token").asText();
   mvc.perform(get("/api/admin/formulaires").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(formulaire));
   mvc.perform(get("/api/admin/statistiques").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.formulairesRecus").value(1));
 }
}
