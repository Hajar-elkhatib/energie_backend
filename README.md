# Energie Platform Backend

Backend Spring Boot 3 / Java 17 de génération de demandes pour la rénovation énergétique belge. Il expose un catalogue, des formulaires régionaux, des simulations asynchrones, une messagerie chatbot, des rendez-vous et une administration sécurisée.

## Démarrer en local

1. Démarrer PostgreSQL : `docker compose up -d`
2. Lancer l'application : `mvn spring-boot:run`
3. Ouvrir Swagger : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

La base locale est `energie_platform_db`, avec l'utilisateur `energie_dev` et le mot de passe `energie_dev_password`.

Identifiants de développement : `admin` / `password`. Au premier démarrage du profil `dev`, le marqueur BCrypt du jeu SQL est remplacé une seule fois par un hash BCrypt frais; un mot de passe modifié ensuite n’est pas écrasé.

La clé de développement du service IA est `dev-ai-api-key-change-me`, à envoyer dans l’en-tête `X-API-KEY`. En production, définir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` et `AI_API_KEY` avec des secrets sûrs.

## Sécurité

- Toutes les routes visiteurs sont publiques.
- `POST /api/admin/auth/login` retourne un JWT. Les autres routes `/api/admin/**` exigent `Authorization: Bearer <token>`.
- Toutes les routes `/api/ai/**` exigent `X-API-KEY`; elles sont conçues pour un service technique externe, non pour un utilisateur.

## Contrat IA

Le backend **n’implémente volontairement aucun calcul IA**, aucune formule de coût ou score et aucune génération conversationnelle. Il crée une `Simulation` en `EN_ATTENTE`, puis le service externe lit son contexte avec `GET /api/ai/simulations/{id}/contexte` et écrit le résultat avec `POST /api/ai/simulations/{id}/resultat`.

De même, un message visiteur est créé `EN_ATTENTE`; le service externe le lit via `/api/ai/chatbot/messages/en-attente` et fournit sa réponse par `/api/ai/chatbot/messages/{id}/reponse`. La réponse est ensuite visible dans la conversation publique.

## Choix de modélisation

- L’héritage `Visiteur → Particulier/Societe` utilise JPA `JOINED`. La table commune contient l’identité, le profil et la région, tandis que les colonnes métier propres à chaque profil restent dans des tables séparées. C’est plus propre et extensible qu’une table très creuse.
- `Formulaire` conserve les réponses dynamiques dans une table `@ElementCollection`, car les champs dépendent de la région. Chaque formulaire porte une ou plusieurs régions de configuration afin de respecter le diagramme et d’autoriser un MVP flexible.
- `MessageConversation` est une entité liée à `ChatbotIA`, plutôt qu’un unique texte `historiqueConversation`, afin de préserver dates, auteur et statut de traitement. Une conversation est créée automatiquement au premier message d’un visiteur.
- L’API de résultat accepte plusieurs recommandations. Le diagramme indique une recommandation par score, mais une relation un-à-plusieurs est nécessaire pour respecter `recommandations[]` imposé par le contrat IA; un score peut donc en avoir une ou plusieurs.
- Les statuts simulation sont `EN_ATTENTE` et `TERMINEE`; les rendez-vous sont `PLANIFIE`, `CONFIRME`, `ANNULE`.

## Production

Le MVP utilise `spring.jpa.hibernate.ddl-auto=update` pour accélérer le développement. Avant la production, passer à Flyway ou Liquibase : créer une migration initiale depuis le schéma validé, régler `ddl-auto=validate`, versionner toute évolution de structure et retirer l’initialisation automatique de données de démonstration.

## Vérification

`mvn clean install` lance les tests avec une base H2 isolée. PostgreSQL reste la base d’exécution locale et de production.
