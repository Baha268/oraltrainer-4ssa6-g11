# 🎙️ OralTrainer — Plateforme d'entraînement et d'évaluation de l'expression orale (FR / EN)

> **Module MT-41 · Applications Web Distribuées** — Mini-projet 2026-2027
> Version 1.0 · novembre 2026

Application web distribuée, basée sur une architecture **microservices (Spring Boot / Spring Cloud)**, qui fait parler l'apprenant, analyse sa prise de parole et le prépare aux certifications officielles en **français** et en **anglais**, du niveau **B1 au C2** (CECRL).

---

## 📋 Informations du projet

| | |
|---|---|
| **Classe / groupe** | _4SSA6 Grp 1_ |
| **Dépôt Git** | https://github.com/Baha268/oraltrainer-4ssa6-g11 |
| **Enseignante** | _Mme Rim Douss_ |

### Équipe

| Membre | Nom et prénom | Microservice responsable |
|---|---|---|
| Étudiant 1 | _Med Hedi Guidara_ | `notification-service` |
| Étudiant 2 | _Beha Eddine Cherif_ | `content-service` |
| Étudiant 3 | _Ala Salem_ | `recording-service` |
| Étudiant 4 | _Yessmine Aissa Larousse_ | `simulation-service` |
| Étudiant 5 | _Feriel Dridi_ | `feedback-service` |
| Étudiant 6 | _Ahmed Chaabeni_ | `progress-service` |
| Tout le groupe | — | `analysis-service` (microservice commun, MongoDB) |

---

## 1. Présentation

### Problématique
L'oral est la compétence la plus déterminante aux examens de langue, mais la moins entraînée : peu de temps de parole en classe, presque aucun retour hors de la classe.

> Comment permettre à un apprenant de s'entraîner à l'oral aussi souvent qu'il le souhaite, avec un retour **immédiat, objectif et aligné sur le CECRL**, tout en laissant au formateur le dernier mot sur l'évaluation ?

### Idée générale
- **Pratique illimitée** : le temps de parole ne dépend plus de la taille du groupe ni du formateur.
- **Feedback immédiat et argumenté** : corrections, reformulations, exercices ciblés.
- **Simulations fidèles** aux épreuves officielles : préparation chronométrée, sujet tiré au sort, relances d'un examinateur virtuel, note estimée sur la grille du niveau visé.
- **Le formateur garde la main** : il réécoute, ajuste la note automatique et commente.

### Acteurs

| Acteur | Rôle |
|---|---|
| **Apprenant** (`LEARNER`) | S'enregistrer, recevoir un feedback, passer des épreuves blanches, suivre sa progression |
| **Formateur** (`TRAINER`) | Suivre ses groupes, valider/ajuster les évaluations, gérer les sujets |
| **Établissement** (`INSTITUTION`) | Piloter plusieurs classes, indicateurs agrégés, attestations internes |
| **Administrateur** (`ADMIN`) | Gérer le catalogue, relancer une analyse, supprimer des données (RGPD) |
| **Moteur vocal** (système) | Outil externe de transcription et d'analyse prosodique |

### Critères d'évaluation de chaque prise de parole

| Critère | Calculé par |
|---|---|
| Prononciation, Fluidité, Intonation | `analysis-service` (signal audio) |
| Lexique, Grammaire, Cohérence | `feedback-service` (texte transcrit) |

**Certifications visées :** DELF/DALF, TCF, Cambridge (B1 Preliminary → C2 Proficiency), IELTS, TOEFL. Les sujets officiels servent de modèles de format et ne sont **pas reproduits**.

---

## 2. Architecture

Le front-end n'appelle **que l'API Gateway**. Chaque microservice possède **sa propre base** et s'enregistre dans **Eureka**. Le `user-service` est un service commun au groupe, intégré avec Keycloak.
- Échanges **synchrones** : HTTP via **OpenFeign** (réponse immédiate nécessaire).
- Échanges **asynchrones** : **RabbitMQ** (traitement audio long, ou plusieurs services concernés par le même événement).

```
 Angular (keycloak-js)
        │
        ▼
 API Gateway :8080 ──── Keycloak :8180 (JWT)
        │
        ├──► user-service        :8081  (H2 — service commun, lié à Keycloak)
        ├──► content-service     :8082  (H2)
        ├──► recording-service   :8083  (H2)  ──► speech-engine (HTTP)
        ├──► simulation-service  :8084  (MySQL)
        ├──► feedback-service    :8085  (MySQL)
        ├──► progress-service    :8086  (MySQL)
        ├──► analysis-service    :8087  (MongoDB) ──► speech-engine (HTTP)
        └──► notification-service :8088  (MySQL)

 Eureka Server :8761      RabbitMQ :5672 (console :15672) — exchange topic « oral.events »
```

### Composants d'infrastructure

| Composant | Technologie | Port | Rôle |
|---|---|---|---|
| API Gateway | Spring Cloud Gateway | 8080 | Point d'entrée unique, routage `lb://`, validation JWT, CORS |
| Registre | Eureka Server | 8761 | Découverte des services |
| Sécurité | Keycloak | 8180 | Realm `oral-platform`, Authorization Code + PKCE, rôles `LEARNER`, `TRAINER`, `INSTITUTION`, `ADMIN` |
| Broker | RabbitMQ | 5672 / 15672 | Événements (exchange topic `oral.events`) |
| Moteur vocal | Conteneur `speech-engine` | — | `/transcribe` et `/analyze` en HTTP (mode simulé prévu en secours) |
| Conteneurisation | Docker / Docker Compose | — | Un Dockerfile par service + `docker-compose.yml` global |

### Sécurité
- Authentification via **Keycloak** (JWT) ; le champ `sub` est stocké dans `User.keycloakId`.
- Validation du jeton à la Gateway → **401** si absent/invalide.
- Autorisation par méthode (`@PreAuthorize`) dans chaque service → **403** si rôle insuffisant. Un apprenant ne lit que ses propres données.
- OpenFeign propage le jeton ; pour les traitements déclenchés par événement, un **compte de service** Keycloak (client credentials) est utilisé.

### Routage de la Gateway

| Préfixes | Service cible |
|---|---|
| `/api/users/**`, `/api/establishments/**`, `/api/groups/**` | `USER-SERVICE` (service commun) |
| `/api/notifications/**` | `NOTIFICATION-SERVICE` |
| `/api/certifications/**`, `/api/subjects/**`, `/api/grids/**`, `/api/paths/**`, `/api/exercises/**` | `CONTENT-SERVICE` |
| `/api/recordings/**` | `RECORDING-SERVICE` |
| `/api/simulations/**` | `SIMULATION-SERVICE` |
| `/api/feedbacks/**`, `/api/recommendations/**` | `FEEDBACK-SERVICE` |
| `/api/progress/**`, `/api/alerts/**`, `/api/attestations/**` | `PROGRESS-SERVICE` |
| `/api/analyses/**` | `ANALYSIS-SERVICE` |

---

## 3. Technologies

| Domaine | Technologie |
|---|---|
| Microservices | Spring Boot (Java 17+), Spring Web, Spring Data JPA |
| Documentation API | Swagger / OpenAPI (springdoc) — `/swagger-ui.html` sur chaque service |
| Gateway / Découverte | Spring Cloud Gateway, Eureka (Spring Cloud Netflix) |
| Communication | OpenFeign (sync), RabbitMQ / Spring AMQP (async — choix à confirmer en séance 7, Kafka en alternative) |
| Sécurité | Keycloak, OAuth2 / OpenID Connect, JWT |
| Bases de données | H2, MySQL, MongoDB |
| Conteneurisation | Docker, Docker Compose |
| Front-end | Angular, keycloak-js |
| Moteur vocal | Whisper ou Vosk (transcription) ; Praat/Parselmouth (prosodie) |
| Gestion de projet | Git / GitHub, GitHub Projects |

---

## 4. Les microservices

| MS | Service | Responsable | Port | Base | Responsabilités |
|---|---|---|---|---|---|
| 1 | `user-service` | Groupe (commun) | 8081 | H2 | Gestion commune des utilisateurs et intégration avec Keycloak |
| 2 | `content-service` | Étudiant 1 | 8082 | H2 | Certifications, banque de sujets, grilles CECRL, parcours, exercices |
| 3 | `recording-service` | Étudiant 2 | 8083 | H2 | Upload/stockage audio, transcription, événement `RecordingTranscribed` |
| 4 | `simulation-service` | Étudiant 3 | 8084 | MySQL | Épreuves blanches en 3 étapes, relances, note estimée |
| 5 | `feedback-service` | Étudiant 4 | 8085 | MySQL | Corrections grammaire/lexique/cohérence, recommandations, validation formateur |
| 6 | `progress-service` | Étudiant 5 | 8086 | MySQL | Progression, niveau estimé, tableau de bord, alertes, attestations |
| 7 | `notification-service` | Étudiant 6 | 8088 | MySQL | Création et gestion des notifications destinées aux apprenants et formateurs |
| 8 | `analysis-service` | Groupe | 8087 | MongoDB | Analyse acoustique (prononciation, fluidité, intonation, pauses, courbe de hauteur) |

### Principes de découpage
- Un service = une responsabilité métier = **une base** (aucune base partagée).
- **Pas de jointure entre services** : seuls les identifiants (`userId`, `recordingId`, …) sont stockés.
- **Aucune dépendance circulaire** : `user-service` (service commun) et `content-service` sont des services de référence qui n'appellent personne.
- Le calcul est séparé de la restitution (analysis → audio, feedback → texte, progress → agrégation).

### Choix des bases

| Base | Services | Justification |
|---|---|---|
| H2 | user, content, recording | Données de référence / métadonnées, schéma simple |
| MySQL | simulation, feedback, progress, notification | Données relationnelles et transactionnelles, historique et gestion des notifications |
| MongoDB | analysis | Résultat d'analyse = document à structure variable, lu en un bloc |

---

## 5. Communications entre services

### Synchrones (OpenFeign)

| Appelant | Appelé | Endpoint | Objet |
|---|---|---|---|
| simulation-service | user-service | `GET /api/users/{id}/goal` | Niveau et certification visés |
| simulation-service | content-service | `GET /api/subjects/random`, `GET /api/grids` | Sujet et grille |
| feedback-service | recording-service | `GET /api/recordings/{id}/transcript` | Texte à corriger |
| feedback-service | content-service | `GET /api/exercises`, `GET /api/grids` | Exercices et critères |
| analysis-service | recording-service | `GET /api/recordings/{id}/audio` | Audio à analyser |
| progress-service | user-service | `GET /api/users/{id}/goal`, `GET /api/groups/{id}/members` | Cible et membres |
| progress-service | content-service | `GET /api/certifications/{id}` | Niveau requis |

### Asynchrones (RabbitMQ)

| Événement | Clé de routage | Producteur | Consommateurs |
|---|---|---|---|
| `RecordingTranscribed` | `recording.transcribed` | recording | analysis, simulation |
| `AnalysisCompleted` | `analysis.completed` | analysis | feedback |
| `FeedbackGenerated` | `feedback.generated` | feedback | progress, simulation |
| `FeedbackValidated` | `feedback.validated` | feedback | progress, simulation |
| `SimulationFinished` | `simulation.finished` | simulation | progress, notification |

## 6. Conventions REST

| Sujet | Convention |
|---|---|
| Format | JSON UTF-8, dates ISO 8601, préfixe `/api`, ressources au pluriel |
| Pagination | `page` (à partir de 0) et `size` ; réponse : `content`, `page`, `size`, `totalElements` |
| Succès | `200` lecture/màj · `201` création (en-tête `Location`) · `202` traitement async accepté · `204` suppression |
| Erreurs | `400` · `401` · `403` · `404` · `409` (conflit d'état) · `413` · `415` · `503` |
| Format d'erreur | RFC 7807 (`ProblemDetail`) : `type`, `title`, `status`, `detail`, `instance` |
| Documentation | Swagger UI par service ; contrats OpenAPI exportés dans `Documentation/` |

---

## 7. Structure du dépôt

```
oraltrainer-4ssa6-g11/
├── Backend/
│   ├── api-gateway/
│   ├── eureka-server/
│   ├── user-service/           # service commun lié à Keycloak
│   ├── content-service/
│   ├── recording-service/
│   ├── simulation-service/
│   ├── feedback-service/
│   ├── progress-service/
│   ├── notification-service/
│   ├── analysis-service/
│   ├── events-contract/        # classes d'événements partagées
│   ├── speech-engine/          # conteneur du moteur vocal
│   └── docker-compose.yml
├── Frontend/
│   └── web/
├── Documentation/
│   ├── Presentation/
│   ├── Diagrams/
│   ├── Document_Conception_Plateforme_Oral.docx
│   └── Document_Conception_Plateforme_Oral_Groupe.docx
└── README.md
```
