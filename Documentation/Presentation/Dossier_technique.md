# MODULE MT-41 · APPLICATIONS WEB DISTRIBUÉES — Mini-projet 2026-2027

**Plateforme d'entraînement et d'évaluation de l'expression orale en français et en anglais**

---

## Partie 1 — Dossier technique · Validation de la conception (séance 4)

| Membre | Nom et prénom | Microservice responsable |
| :--- | :--- | :--- |
| Étudiant 1 | Med Hedi Guidara | `notification-service` / `user-service` |
| Étudiant 2 | Beha Eddine Cherif | `content-service` |
| Étudiant 3 | Ala Salem | `recording-service` |
| Étudiant 4 | Yessmine Aissa Larousse | `simulation-service` |
| Étudiant 5 | Feriel Dridi | `feedback-service` |
| Étudiant 6 | Ahmed Chaabeni | `progress-service` |
| Tout le groupe | Équipe G11 | `analysis-service` (microservice commun, MongoDB) |

- **Classe / groupe :** 4SSA6 — G11
- **Dépôt Git :** https://github.com/Baha268/oraltrainer-4ssa6-g11
- **Version :** 1.0 · novembre 2026

### Sommaire
1. Présentation du sujet
2. Acteurs et fonctionnalités
3. Architecture globale
4. Technologies choisies
5. Découpage en microservices
6. Fiches détaillées des microservices (diagrammes de classes, contrats d'API, événements)
7. Interactions entre les services
8. Données et conformité
9. Organisation du dépôt Git et gestion de projet
10. Répartition des tâches et plan de présentation
11. Planning prévisionnel
12. Risques et parades
13. Points à valider avec l'enseignante
- Annexe A · Conventions REST
- Annexe B · Exemples d'événements

---

### 1. Présentation du sujet

#### 1.1 Contexte et problématique
L'oral est la compétence la plus déterminante aux examens de langue, et pourtant la moins entraînée. En classe, chaque apprenant ne dispose que de quelques minutes de parole par séance ; hors de la classe, il n'a presque jamais de retour sur sa prononciation, sa fluidité ou la justesse de ses phrases. Le jour de l'épreuve, il découvre souvent pour la première fois le format, la contrainte de temps et la pression d'un examinateur.

**Problématique :** comment permettre à un apprenant de s'entraîner à l'oral aussi souvent qu'il le souhaite, avec un retour immédiat, objectif et aligné sur le CECRL, tout en laissant au formateur le dernier mot sur l'évaluation ?

#### 1.2 Idée générale
La plateforme est une application web distribuée qui fait parler l'apprenant, analyse sa prise de parole (transcription, prononciation, fluidité, intonation, grammaire, lexique, cohérence) et le prépare, niveau par niveau (B1 à C2), aux certifications officielles en français et en anglais.

- **Pratique illimitée :** le temps de parole n'est plus limité par la taille du groupe ni par la disponibilité du formateur.
- **Feedback immédiat et argumenté** sur chaque enregistrement, avec corrections, reformulations et exercices ciblés.
- **Simulations fidèles aux épreuves officielles :** préparation chronométrée, sujet tiré au sort, relances d'un examinateur virtuel, note estimée sur la grille du niveau visé.
- **Le formateur garde la main :** il réécoute, ajuste la note proposée par l'automatisme et commente. L'évaluation automatique est une aide, pas une décision définitive.

#### 1.3 Périmètre retenu
Pour tenir les délais de la séance 10, le périmètre est volontairement borné :

| Dans le périmètre (MVP) | Extensions possibles si le temps le permet |
| :--- | :--- |
| Enregistrement et transcription | Examinateur virtuel basé sur un LLM |
| Scores de prononciation, fluidité, intonation, lexique, grammaire, cohérence | Application mobile |
| Épreuve blanche en 3 étapes avec relances issues d'une banque de relances | Notifications par e-mail |
| Progression, alertes, tableau de bord formateur | Export PDF des attestations |
| Attestation de niveau | |
| Front-end web | |

---

### 2. Acteurs et fonctionnalités

#### 2.1 Acteurs

| Acteur | Profil | Besoins principaux |
| :--- | :--- | :--- |
| **Apprenant (`LEARNER`)** | Étudiant, salarié ou candidat à la mobilité préparant une certification B1 à C2 | S'enregistrer, recevoir un feedback détaillé, passer des épreuves blanches, suivre sa progression, obtenir une attestation |
| **Formateur (`TRAINER`)** | Enseignant ou coach de langue | Suivre ses groupes, repérer les apprenants en difficulté, valider ou ajuster l'évaluation automatique, commenter, gérer les sujets et parcours |
| **Établissement (`INSTITUTION`)** | École ou centre de langues | Piloter plusieurs classes, consulter des indicateurs agrégés, délivrer des attestations internes |
| **Administrateur (`ADMIN`)** | Équipe technique de la plateforme | Gérer le catalogue (certifications, grilles), relancer une transcription ou une analyse, supprimer des données (RGPD) |
| **Moteur vocal (système)** | Outil externe de transcription et d'analyse prosodique | Reçoit un audio, renvoie texte, mots horodatés, phonèmes, pauses et courbe de hauteur |

#### 2.2 Diagramme de cas d'utilisation général
*(Figure 1 · Cas d'utilisation général)*

#### 2.3 Fonctionnalités principales

| Réf. | Fonctionnalité | Description | Service principal |
| :--- | :--- | :--- | :--- |
| F1 | Gestion des comptes, classes et objectifs | Profil, rôle, établissement, groupes, certification et niveau visés | `user-service` |
| F2 | Référentiel pédagogique | Catalogue des certifications, banque de sujets par langue/niveau/type de tâche, grilles et descripteurs CECRL, parcours et exercices | `content-service` |
| F3 | Enregistrement et transcription | Upload audio depuis le navigateur, stockage, transcription Speech-to-Text | `recording-service` |
| F4 | Analyse vocale | Scores de prononciation, fluidité, intonation ; résultat mot par mot, pauses, courbe de hauteur | `analysis-service` |
| F5 | Correction et feedback | Grammaire, lexique, cohérence, reformulations, exercices recommandés, validation du formateur | `feedback-service` |
| F6 | Simulation d'examen | Préparation chronométrée, sujet tiré au sort, relances, note estimée sur la grille | `simulation-service` |
| F7 | Progression et alertes | Évolution par critère, niveau estimé, écart avec la cible, vue formateur, alertes | `progress-service` |
| F8 | Attestations de niveau | Délivrance d'une attestation interne si le niveau est atteint | `progress-service` |

#### 2.4 Critères d'évaluation de chaque prise de parole
Les six critères reprennent les aspects qualitatifs de l'oral décrits par le CECRL. Ils sont répartis entre deux services selon la nature du traitement : le signal audio d'un côté, le texte transcrit de l'autre.

| Critère | Ce qui est mesuré | Calculé par |
| :--- | :--- | :--- |
| **Prononciation** | Phonèmes mal réalisés, accentuation, liaisons, mots incompréhensibles | `analysis-service` (audio) |
| **Fluidité** | Débit en mots par minute, pauses, hésitations, répétitions | `analysis-service` (audio) |
| **Intonation** | Courbe mélodique, questions, emphase, variété prosodique | `analysis-service` (audio) |
| **Lexique** | Étendue et précision du vocabulaire, répétitions, mots plus riches suggérés | `feedback-service` (texte) |
| **Grammaire** | Erreurs d'accord, de conjugaison, de construction, avec correction | `feedback-service` (texte) |
| **Cohérence** | Structure du discours, connecteurs logiques, respect de la consigne | `feedback-service` (texte) |

**Niveaux et certifications visés :** B1 à C2 ; DELF/DALF, TCF, Cambridge (B1 Preliminary à C2 Proficiency), IELTS, TOEFL. Les sujets officiels sont protégés : ils servent de modèles de format et ne sont pas reproduits.

---

### 3. Architecture globale

#### 3.1 Diagramme d'architecture
*(Figure 2 · Architecture globale conforme à la stack imposée)*

Le front-end n'appelle que l'API Gateway. Chaque microservice possède sa propre base de données et s'enregistre dans Eureka. Les échanges synchrones passent par HTTP (OpenFeign) ; les échanges asynchrones passent par RabbitMQ. Le traitement audio, coûteux et lent, est isolé derrière des événements pour ne jamais bloquer l'interface.

#### 3.2 Composants d'infrastructure (partie commune du groupe)

| Composant | Technologie | Rôle |
| :--- | :--- | :--- |
| **API Gateway** | Spring Cloud Gateway (`:8080`) | Point d'entrée unique, routage par préfixe vers les services (`lb://NOM-DU-SERVICE`), validation du JWT, CORS |
| **Registre** | Eureka Server (`:8761`) | Enregistrement des services au démarrage ; résolution des adresses par la Gateway et par OpenFeign |
| **Sécurité** | Keycloak (`:8180`) | Realm `oral-platform`, client front (Authorization Code + PKCE), rôles `LEARNER`, `TRAINER`, `INSTITUTION`, `ADMIN` |
| **Broker** | RabbitMQ (`:5672`, console `:15672`) | Exchange topic `oral.events` ; une file par service consommateur |
| **Moteur vocal** | Conteneur `speech-engine` | Outil externe exposant `/transcribe` et `/analyze` en HTTP ; appelé par `recording-service` et `analysis-service` comme simple client HTTP. Mode simulé prévu en secours |
| **Conteneurisation** | Docker, Docker Compose | Un `Dockerfile` par service, un `docker-compose.yml` global, réseau interne, healthchecks |
| **Front-end** | Application web (Angular) | Consomme les API via la Gateway, authentification via Keycloak |

#### 3.3 Sécurité
- **Authentification :** le front redirige vers Keycloak (Authorization Code + PKCE) et reçoit un JWT. Le champ `sub` identifie l'utilisateur ; il est stocké dans `User.keycloakId` par `user-service`.
- **Contrôle à la Gateway :** le jeton est validé avant routage (Spring Security, resource server). Une requête sans jeton valide reçoit `401`.
- **Autorisation dans chaque service :** les rôles du jeton sont vérifiés par méthode (`@PreAuthorize`). Un rôle insuffisant reçoit `403`. Un apprenant ne peut lire que ses propres données.
- **Appels entre services :** OpenFeign propage le jeton de l'utilisateur. Pour les traitements déclenchés par un événement (aucun utilisateur connecté), le service utilise un compte de service Keycloak (`client_credentials`).
- **Données personnelles :** une voix est une donnée personnelle, voire biométrique. Le droit à l'effacement est prévu (`DELETE` sur les enregistrements et analyses).

#### 3.4 Routage de la Gateway

| Préfixes | Service cible |
| :--- | :--- |
| `/api/users/`, `/api/establishments/`, `/api/groups/` | `lb://USER-SERVICE` |
| `/api/certifications/`, `/api/subjects/`, `/api/grids/`, `/api/paths/`, `/api/exercises/` | `lb://CONTENT-SERVICE` |
| `/api/recordings/` | `lb://RECORDING-SERVICE` |
| `/api/simulations/` | `lb://SIMULATION-SERVICE` |
| `/api/feedbacks/`, `/api/recommendations/` | `lb://FEEDBACK-SERVICE` |
| `/api/progress/`, `/api/alerts/`, `/api/attestations/` | `lb://PROGRESS-SERVICE` |
| `/api/analyses/` | `lb://ANALYSIS-SERVICE` |

#### 3.5 Quand utiliser le synchrone, quand utiliser l'asynchrone

| Mode | Critère de choix dans ce projet |
| :--- | :--- |
| **Synchrone (OpenFeign)** | Le service appelant a besoin de la réponse tout de suite pour répondre à l'utilisateur : lire l'objectif d'un apprenant, tirer un sujet, charger une grille, lire une transcription. |
| **Asynchrone (RabbitMQ)** | Le traitement est long (audio) ou concerne plusieurs services à la fois (la progression et la simulation réagissent au même feedback). L'émetteur ne doit pas attendre ni connaître ses consommateurs. |

---

### 4. Technologies choisies

La stack est imposée par le cours et identique pour tous les groupes. Tous les microservices sont développés en Spring Boot.

| Domaine | Technologie | Usage dans le projet |
| :--- | :--- | :--- |
| Microservices | Spring Boot (Java 17+), Spring Web, Spring Data JPA | Un microservice par étudiant, plus le service commun |
| Documentation API | Swagger / OpenAPI (`springdoc`) | Chaque service expose `/swagger-ui.html` |
| API Gateway | Spring Cloud Gateway | Routage et validation du JWT |
| Découverte | Eureka (Spring Cloud Netflix) | Registre des services |
| Communication synchrone | Spring Cloud OpenFeign | Appels REST entre services |
| Communication asynchrone | RabbitMQ (Spring AMQP) | Événements ; choix à confirmer pour la séance 7 (Kafka est l'alternative) |
| Sécurité | Keycloak, OAuth2 / OpenID Connect, JWT | Authentification et rôles |
| Bases de données | H2, MySQL, MongoDB | Une base par service (voir 5.4) |
| Conteneurisation | Docker, Docker Compose | Lancement global de l'application |
| Front-end | Angular, `keycloak-js` | Application web ; peut être générée avec l'aide de l'IA puis relue et adaptée |
| Moteur vocal (outil) | Whisper ou Vosk pour la transcription ; bibliothèque de prosodie type Praat/Parselmouth | Conteneur externe appelé en HTTP. Ce n'est pas un microservice métier |
| Gestion de projet et code | Git / GitHub, GitHub Projects | Dépôt, branches, pull requests, suivi des tâches |

---

### 5. Découpage en microservices

L'équipe compte 6 étudiants : le découpage prévoit 6 microservices individuels (un par étudiant, avec sa base) et 1 microservice commun en technologie avancée (MongoDB), soit 7 microservices. L'infrastructure (Gateway, Eureka, Keycloak, RabbitMQ, Docker) et le front-end sont réalisés par le groupe.

#### 5.1 Vue d'ensemble

| MS | Service | Responsable | Base | Responsabilités |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `user-service` | Étudiant 1 | H2 | Profils, rôles métier, établissements, classes, inscriptions, objectif (langue, certification, niveau visé) |
| 2 | `content-service` | Étudiant 2 | H2 | Catalogue des certifications, banque de sujets, grilles et descripteurs CECRL, parcours, leçons, exercices |
| 3 | `recording-service` | Étudiant 3 | H2 | Réception et stockage de l'audio, métadonnées, transcription via le moteur vocal, publication de `RecordingTranscribed` |
| 4 | `simulation-service` | Étudiant 4 | MySQL | Épreuves blanches : tirage du sujet, étapes chronométrées, relances de l'examinateur, note estimée |
| 5 | `feedback-service` | Étudiant 5 | MySQL | Correction grammaire, lexique, cohérence, reformulations, exercices recommandés, validation par le formateur |
| 6 | `progress-service` | Étudiant 6 | MySQL | Progression par critère, niveau estimé, écart avec la cible, tableau de bord, alertes, attestations |
| 7 | `analysis-service` | Groupe | MongoDB | Analyse acoustique : prononciation mot par mot, fluidité, intonation, pauses, courbe de hauteur |

#### 5.2 Principes de découpage
- Un service, une responsabilité métier et une seule base. Aucune base n'est partagée.
- Pas de jointure entre services : un service ne stocke que les identifiants (`userId`, `recordingId`, `certificationId`) des autres et les interroge en cas de besoin.
- Pas de dépendance circulaire. `user-service` et `content-service` sont des services de référence qui n'appellent personne ; les services de traitement les appellent.
- Le calcul est séparé de la restitution : `analysis-service` calcule sur l'audio, `feedback-service` sur le texte, `progress-service` agrège. Ainsi, le service commun de technologie avancée ne dépend pas du reste de la logique métier.

#### 5.3 Équilibre de la charge de travail
Les services « de référence » (`user-service`, `content-service`) sont plus simples en logique mais plus larges en CRUD ; les services « de traitement » (`simulation`, `feedback`, `progress`) portent davantage de logique et d'événements. Pour équilibrer, les étudiants 1 à 3 portent aussi l'essentiel de l'infrastructure (Keycloak, Gateway et Eureka, Docker), et les étudiants 4 à 6 le front-end des écrans les plus riches (voir section 10).

#### 5.4 Choix des bases de données

| Base | Services | Justification |
| :--- | :--- | :--- |
| **H2** | `user`, `content`, `recording` | Données de référence ou métadonnées, schéma simple, mise en route rapide. Mode fichier persistant dans Docker |
| **MySQL** | `simulation`, `feedback`, `progress` | Données transactionnelles avec relations (une simulation et ses étapes, un feedback et ses corrections), historique et agrégations |
| **MongoDB** | `analysis` (commun) | Le résultat d'analyse est un document à structure variable (mots, phonèmes, pauses, courbe de hauteur) qui s'adapte mal à un schéma relationnel fixe et se lit toujours en un bloc |

---

### 6. Fiches détaillées des microservices

**Légende des rôles :** `L` apprenant, `T` formateur, `I` établissement, `A` administrateur, `S` compte de service, `*` tout utilisateur authentifié. Chaque endpoint est préfixé par `/api` et accessible uniquement via la Gateway. Les codes `401` (jeton absent ou invalide) et `500` s'appliquent à tous les endpoints et ne sont pas répétés.

#### 6.1 `user-service`
- **Responsable :** Étudiant 1 · **Base de données :** H2 · **Port :** `8081`

**Responsabilités :**
- Stocker le profil métier lié au compte Keycloak (l'authentification elle-même est déléguée à Keycloak).
- Gérer établissements, classes et inscriptions des apprenants.
- Gérer l'objectif d'apprentissage : langue, certification visée, niveau cible, date.

*(Figure 3 · Diagramme de classes de `user-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/users` | Créer le profil après inscription Keycloak | `*` | `201, 400, 409` |
| `GET` | `/api/users/{id}` | Détail d'un utilisateur | `L (soi), T, I, A` | `200, 403, 404` |
| `PUT` | `/api/users/{id}` | Modifier un profil | `L (soi), A` | `200, 400, 403, 404` |
| `PUT` | `/api/users/{id}/goal` | Définir l'objectif (langue, certification, niveau) | `L (soi)` | `200, 201, 400, 404` |
| `GET` | `/api/users/{id}/goal` | Lire l'objectif | `L, T, S` | `200, 403, 404` |
| `POST` | `/api/establishments` | Créer un établissement | `A` | `201, 400, 403` |
| `GET` | `/api/establishments` | Lister les établissements (paginé) | `I, A` | `200, 403` |
| `POST` | `/api/groups` | Créer une classe | `T, I` | `201, 400, 403, 404` |
| `GET` | `/api/groups/{id}` | Détail d'une classe | `T, I` | `200, 403, 404` |
| `POST` | `/api/groups/{id}/members` | Inscrire un apprenant dans une classe | `T, I` | `201, 403, 404, 409` |
| `GET` | `/api/groups/{id}/members` | Lister les membres d'une classe | `T, I, S` | `200, 403, 404` |
| `DELETE` | `/api/groups/{id}/members/{userId}` | Retirer un apprenant | `T, I` | `204, 403, 404` |

- **Événements :** Aucun événement publié ni consommé : c'est un service de référence (feuille du graphe de dépendances).
- **Synchrone :** `simulation-service` et `progress-service` appellent `GET /api/users/{id}/goal` ; `progress-service` appelle `GET /api/groups/{id}/members`.
- **Asynchrone :** Aucune.

#### 6.2 `content-service`
- **Responsable :** Étudiant 2 · **Base de données :** H2 · **Port :** `8082`

**Responsabilités :**
- Fournir le référentiel pédagogique : certifications, sujets, grilles CECRL, parcours.
- Permettre au formateur d'ajouter et de modifier des sujets.
- Fournir un tirage aléatoire de sujet adapté à la langue, au niveau et à la certification.

*(Figure 4 · Diagramme de classes de `content-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/certifications` | Catalogue (filtres langue, niveau) | `*` | `200` |
| `GET` | `/api/certifications/{id}` | Format, durées, niveaux, seuils | `*` | `200, 404` |
| `GET` | `/api/subjects?language&level&taskType` | Sujets filtrés (paginé) | `*` | `200, 400` |
| `GET` | `/api/subjects/random` | Tirage au sort d'un sujet | `L, S` | `200, 400, 404` |
| `POST` | `/api/subjects` | Ajouter un sujet | `T, A` | `201, 400, 403` |
| `PUT` | `/api/subjects/{id}` | Modifier un sujet | `T, A` | `200, 400, 403, 404` |
| `DELETE` | `/api/subjects/{id}` | Supprimer un sujet | `A` | `204, 403, 404` |
| `GET` | `/api/grids?language&level` | Grille avec critères et descripteurs | `*` | `200, 400, 404` |
| `GET` | `/api/paths?language&level` | Parcours disponibles | `L, T` | `200` |
| `GET` | `/api/paths/{id}/lessons` | Leçons d'un parcours | `L, T` | `200, 404` |
| `GET` | `/api/exercises?tag&level` | Exercices par thématique d'erreur | `L, T, S` | `200, 400` |

- **Événements :** Aucun événement publié ni consommé.
- **Synchrone :** `simulation-service` appelle `GET /api/subjects/random` et `GET /api/grids` ; `feedback-service` appelle `GET /api/exercises?tag=…`.
- **Asynchrone :** Aucune.

#### 6.3 `recording-service`
- **Responsable :** Étudiant 3 · **Base de données :** H2 · **Port :** `8083`

**Responsabilités :**
- Recevoir l'audio du navigateur (`webm/opus` ou `wav`), contrôler format et taille, le stocker sur un volume du service.
- Appeler le moteur vocal pour obtenir la transcription avec mots horodatés.
- Publier `RecordingTranscribed` lorsque la transcription est prête.
- Gérer la suppression des enregistrements (RGPD).

*(Figure 5 · Diagramme de classes de `recording-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/recordings` | Envoyer un audio (multipart : fichier, `language`, `context`, `simulationId`, `stepId`) | `L` | `201, 400, 403, 413, 415` |
| `GET` | `/api/recordings/{id}` | Métadonnées et statut | `L (soi), T, S` | `200, 403, 404` |
| `GET` | `/api/recordings?userId` | Historique des enregistrements (paginé) | `L (soi), T` | `200, 403` |
| `GET` | `/api/recordings/{id}/audio` | Télécharger l'audio (Range supporté) | `L (soi), T, S` | `200, 206, 403, 404` |
| `GET` | `/api/recordings/{id}/transcript` | Transcription avec mots horodatés | `L (soi), T, S` | `200, 403, 404, 409` |
| `POST` | `/api/recordings/{id}/retry-transcription` | Relancer une transcription en échec | `A` | `202, 403, 404, 409` |
| `DELETE` | `/api/recordings/{id}` | Supprimer l'audio et la transcription | `L (soi), A` | `204, 403, 404` |

- **Événements :** Publie `RecordingTranscribed` (clé `recording.transcribed`). Ne consomme aucun événement.

#### 6.4 `simulation-service`
- **Responsable :** Étudiant 4 · **Base de données :** MySQL · **Port :** `8084`

**Responsabilités :**
- Orchestrer une épreuve blanche en trois étapes : préparation chronométrée, monologue, interaction avec relances.
- Choisir les relances de l'examinateur à partir de la transcription et de la banque de relances du sujet.
- Calculer la note estimée par critère sur la grille du niveau visé, quand tous les feedbacks sont reçus.
- Publier `SimulationFinished`.

*(Figure 6 · Diagramme de classes de `simulation-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/simulations` | Démarrer une épreuve (certification, niveau, langue) | `L` | `201, 400, 404, 503` |
| `GET` | `/api/simulations/{id}` | État de l'épreuve et de ses étapes | `L (soi), T` | `200, 403, 404` |
| `GET` | `/api/simulations?userId` | Historique des épreuves (paginé) | `L (soi), T` | `200, 403` |
| `POST` | `/api/simulations/{id}/steps` | Démarrer l'étape suivante (chrono) | `L (soi)` | `201, 403, 404, 409` |
| `PUT` | `/api/simulations/{id}/steps/{stepId}/recording` | Associer l'enregistrement à l'étape | `L (soi)` | `200, 400, 404, 409` |
| `GET` | `/api/simulations/{id}/steps/{stepId}/prompt` | Relance de l'examinateur | `L (soi)` | `200, 404, 409` |
| `POST` | `/api/simulations/{id}/finish` | Clore l'épreuve et lancer le calcul | `L (soi)` | `202, 403, 404, 409` |
| `GET` | `/api/simulations/{id}/result` | Note estimée et bilan | `L (soi), T` | `200, 403, 404, 409` |

- **Événements :** Consomme `RecordingTranscribed` (`context = SIMULATION`), `FeedbackGenerated`, `FeedbackValidated`. Publie `SimulationFinished` (`simulation.finished`).

#### 6.5 `feedback-service`
- **Responsable :** Étudiant 5 · **Base de données :** MySQL · **Port :** `8085`

**Responsabilités :**
- Générer le feedback textuel : corrections de grammaire, de lexique et de cohérence, reformulations, commentaire global.
- Calculer les scores de lexique, grammaire et cohérence, et les regrouper avec les trois scores acoustiques.
- Recommander des exercices ciblant les erreurs récurrentes.
- Permettre au formateur de valider ou d'ajuster l'évaluation et d'ajouter un commentaire.

*(Figure 7 · Diagramme de classes de `feedback-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/feedbacks/recording/{recordingId}` | Feedback d'un enregistrement | `L (soi), T` | `200, 403, 404` |
| `GET` | `/api/feedbacks/{id}` | Détail d'un feedback | `L (soi), T` | `200, 403, 404` |
| `GET` | `/api/feedbacks?userId` | Historique des feedbacks (paginé) | `L (soi), T` | `200, 403` |
| `GET` | `/api/feedbacks/{id}/corrections` | Corrections détaillées | `L (soi), T` | `200, 403, 404` |
| `GET` | `/api/recommendations/{userId}` | Exercices recommandés | `L (soi), T` | `200, 403, 404` |
| `PUT` | `/api/feedbacks/{id}/review` | Valider ou ajuster les scores | `T` | `200, 400, 403, 404, 409` |
| `POST` | `/api/feedbacks/{id}/comments` | Ajouter un commentaire de formateur | `T` | `201, 400, 403, 404` |

- **Événements :** Consomme `AnalysisCompleted` (`analysis.completed`). Publie `FeedbackGenerated` (`feedback.generated`) et `FeedbackValidated` (`feedback.validated`).

#### 6.6 `progress-service`
- **Responsable :** Étudiant 6 · **Base de données :** MySQL · **Port :** `8086`

**Responsabilités :**
- Enregistrer chaque évaluation sous forme d'instantané de progression (six critères et niveau estimé).
- Calculer l'écart avec le niveau cible et le niveau estimé.
- Produire le tableau de bord d'une classe et les alertes (stagnation, score bas, inactivité).
- Délivrer des attestations de niveau si le niveau requis est atteint.

*(Figure 8 · Diagramme de classes de `progress-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/progress/users/{userId}` | Progression actuelle et niveau estimé | `L (soi), T` | `200, 403, 404` |
| `GET` | `/api/progress/users/{userId}/history?from&to&criterion` | Historique pour les courbes | `L (soi), T` | `200, 400, 403` |
| `GET` | `/api/progress/users/{userId}/gap` | Écart avec la cible | `L (soi), T` | `200, 403, 404, 503` |
| `GET` | `/api/progress/groups/{groupId}/dashboard` | Tableau de bord d'une classe | `T, I` | `200, 403, 404, 503` |
| `GET` | `/api/alerts?groupId&status` | Alertes (paginé) | `T, I` | `200, 403` |
| `PUT` | `/api/alerts/{id}/ack` | Acquitter une alerte | `T, I` | `200, 403, 404` |
| `POST` | `/api/attestations` | Délivrer une attestation (si niveau atteint) | `T, I` | `201, 400, 403, 404, 409` |
| `GET` | `/api/attestations?userId` | Attestations d'un apprenant | `L (soi), T, I` | `200, 403` |
| `GET` | `/api/attestations/{id}` | Détail d'une attestation | `L (soi), T, I` | `200, 403, 404` |

- **Événements :** Consomme `FeedbackGenerated`, `FeedbackValidated`, `SimulationFinished`. Ne publie aucun événement.

#### 6.7 `analysis-service` (microservice commun, MongoDB)
- **Développé par :** Tout le groupe (réparti en section 10.2) ; présenté par un seul membre · **Base de données :** MongoDB · **Port :** `8087`

**Responsabilités :**
- Consommer `RecordingTranscribed`, récupérer l'audio auprès de `recording-service` et appeler le moteur vocal pour l'analyse prosodique.
- Stocker le résultat détaillé dans un document MongoDB : scores, mots, phonèmes, pauses, courbe de hauteur.
- Publier `AnalysisCompleted` avec les trois scores acoustiques et le débit.

*(Figure 9 · Modèle de document de `analysis-service`)*

**Contrat d'API :**

| Méthode | Endpoint | Description | Rôle | Codes de réponse |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/analyses/recording/{recordingId}` | Résultat détaillé d'un enregistrement | `L (soi), T, S` | `200, 403, 404` |
| `GET` | `/api/analyses?userId&language` | Analyses d'un apprenant (paginé) | `L (soi), T` | `200, 403` |
| `GET` | `/api/analyses/{id}/words` | Détail mot par mot et phonèmes | `L (soi), T` | `200, 403, 404` |
| `POST` | `/api/analyses/recording/{recordingId}/reanalyze` | Relancer une analyse | `A` | `202, 403, 404` |
| `DELETE` | `/api/analyses/{id}` | Supprimer une analyse | `L (soi), A` | `204, 403, 404` |

- **Événements :** Consomme `RecordingTranscribed`. Publie `AnalysisCompleted` (`analysis.completed`).

---

### 7. Interactions entre les services

#### 7.1 Communications synchrones (OpenFeign)

| Appelant | Appelé | Endpoint | Objet |
| :--- | :--- | :--- | :--- |
| `simulation-service` | `user-service` | `GET /api/users/{id}/goal` | Niveau et certification visés |
| `simulation-service` | `content-service` | `GET /api/subjects/random` ; `GET /api/grids` | Sujet et grille de l'épreuve |
| `feedback-service` | `recording-service` | `GET /api/recordings/{id}/transcript` | Texte à corriger |
| `feedback-service` | `content-service` | `GET /api/exercises` ; `GET /api/grids` | Exercices et critères |
| `analysis-service` | `recording-service` | `GET /api/recordings/{id}/audio` | Audio à analyser |
| `progress-service` | `user-service` | `GET /api/users/{id}/goal` ; `GET /api/groups/{id}/members` | Cible et membres de la classe |
| `progress-service` | `content-service` | `GET /api/certifications/{id}` | Niveau requis pour une attestation |

**Tolérance aux pannes :** chaque appel Feign a un timeout court ; en cas d'échec, le service répond `503 Service Unavailable` avec un message explicite (aucun appel en cascade infini). Le graphe d'appels est acyclique.

#### 7.2 Communications asynchrones (RabbitMQ)
Un exchange de type topic `oral.events` reçoit tous les événements. Chaque consommateur déclare sa propre file (par exemple `feedback.analysis-completed`) liée à la clé de routage voulue. Les messages sont en JSON et portent un `eventId` pour que les consommateurs soient idempotents. Les messages en échec répétés partent dans une dead letter queue. Le choix final du broker sera confirmé en séance 7.

| Événement | Clé de routage | Producteur | Consommateurs | Champs principaux |
| :--- | :--- | :--- | :--- | :--- |
| `RecordingTranscribed` | `recording.transcribed` | `recording` | `analysis`, `simulation` | `eventId, occurredAt, recordingId, userId, language, audioUrl, transcript, durationSec, context, simulationId?, stepId?` |
| `AnalysisCompleted` | `analysis.completed` | `analysis` | `feedback` | `eventId, recordingId, userId, language, analysisId, scores{pronunciation, fluency, intonation}, speechRateWpm` |
| `FeedbackGenerated` | `feedback.generated` | `feedback` | `progress`, `simulation` | `eventId, feedbackId, recordingId, userId, simulationId?, scores{6 critères}` |
| `FeedbackValidated` | `feedback.validated` | `feedback` | `progress`, `simulation` | `eventId, feedbackId, recordingId, userId, trainerId, adjustedScores{6 critères}` |
| `SimulationFinished` | `simulation.finished` | `simulation` | `progress` | `eventId, simulationId, userId, certificationId, level, estimatedScores[], estimatedLevel, targetReached` |

#### 7.3 Matrice des dépendances

| Appelant ↓ / Appelé → | `user` | `content` | `recording` | `simulation` | `feedback` | `progress` | `analysis` |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| `user` | | | | | | | |
| `content` | | | | | | | |
| `recording` | | | | | | | |
| `simulation` | sync | sync | événement | | événement | | |
| `feedback` | | sync | sync | | | | événement |
| `progress` | sync | sync | | événement | événement | | |
| `analysis` | | | sync + événement | | | | |

#### 7.4 Scénario 1 : enregistrement libre
*(Figure 10 · Chaîne de traitement d'un enregistrement)*

La réponse `201` de l'upload est immédiate : l'interface n'attend ni la transcription ni l'analyse. L'apprenant consulte le feedback quand il est prêt (interrogation périodique de `GET /api/feedbacks/recording/{id}`, qui répond `404` tant qu'il n'existe pas).

#### 7.5 Scénario 2 : épreuve blanche
*(Figure 11 · Déroulement d'une épreuve blanche)*

---

### 8. Données et conformité

**Règle :** pas d'apprenants réels. Une voix est une donnée personnelle, voire biométrique. Le projet utilise des corpus publics, des voix synthétiques ou des enregistrements de l'équipe avec accord écrit.

#### 8.1 Corpus publics envisagés

| Source | Langue | Usage dans la plateforme | Licence (à revérifier) |
| :--- | :--- | :--- | :--- |
| Mozilla Common Voice | FR, EN | Tester la transcription sur des voix et accents variés | CC0 |
| speechocean762 | EN | Calibrer le score de prononciation | CC BY 4.0 (citer la source) |
| L2-ARCTIC | EN | Tester la détection de phonèmes mal prononcés | CC BY-NC 4.0 : usage non commercial uniquement |
| Multilingual LibriSpeech | FR, EN | Références de fluidité et d'intonation (sous-ensemble) | CC BY 4.0 (citer la source) |
| CECRL, Conseil de l'Europe (2020) | FR, EN | Descripteurs pour construire les grilles et feedbacks | Citer la source |

#### 8.2 Données générées
- **Sujets d'oral par niveau :** rédigés par l'équipe à partir des descripteurs CECRL, puis relus. Les sujets officiels (DELF, DALF, Cambridge) servent de modèles de format sans être reproduits.
- **Audio de test :** synthèse vocale locale (par exemple Piper) en français et en anglais, ou enregistrements de l'équipe avec accord écrit.
- **Apprenants et historiques :** générés avec Faker et un script de seed à graine fixe, pour des démonstrations reproductibles. Chaque service possède son propre script de seed.

#### 8.3 Données gérées par chaque service

| Service | Données (entités principales) |
| :--- | :--- |
| `user-service` | `User`, `Establishment`, `ClassGroup`, `Enrollment`, `LearningGoal` |
| `content-service` | `Certification`, `Subject`, `Grid`, `Criterion`, `Descriptor`, `LearningPath`, `Lesson`, `Exercise` |
| `recording-service` | `Recording` (+ fichier audio sur volume), `Transcript`, `TranscriptWord` |
| `simulation-service` | `Simulation`, `SimulationStep`, `ExaminerPrompt`, `EstimatedScore` |
| `feedback-service` | `Feedback`, `Correction`, `RecommendedExercise`, `TrainerReview` |
| `progress-service` | `ProgressSnapshot`, `Alert`, `Attestation` |
| `analysis-service` | Document `AnalysisResult` (scores, mots, phonèmes, pauses, courbe de hauteur) |

---

### 9. Organisation du dépôt Git et gestion de projet

#### 9.1 Structure du dépôt

```text
oraltrainer-<classe>-<groupe>/
├── Backend/
│   ├── api-gateway/
│   ├── eureka-server/
│   ├── user-service/
│   ├── content-service/
│   ├── recording-service/
│   ├── simulation-service/
│   ├── feedback-service/
│   ├── progress-service/
│   ├── analysis-service/
│   ├── events-contract/        # classes d'événements partagées
│   ├── speech-engine/          # conteneur du moteur vocal
│   └── docker-compose.yml
├── Frontend/
│   └── web/
├── Documentation/
│   ├── Presentation/           # présentation de la séance 4
│   ├── Diagrams/               # architecture, cas d'utilisation, classes, séquences
│   └── Dossier_technique.docx
└── README.md
```

#### 9.2 Branches et conventions
- `main` : version stable, protégée (pas de push direct). `develop` : intégration.
- Une branche par fonctionnalité / membre : `feature/user-service`, `feature/recording-service`, `feature/fe-web`, etc.
- Intégration par pull request vers `develop`, relue par au moins un autre membre.
- Messages de commit au format `type: résumé` (`feat`, `fix`, `docs`, `refactor`, `test`).
- Un `README` par service (lancement, variables, endpoints) en plus du `README` racine.

#### 9.3 Outil de gestion de projet
- GitHub Projects : relié au dépôt, aux issues et aux pull requests.
- Tableau Kanban : `Backlog`, `À faire`, `En cours`, `En revue`, `Terminé`.
- Une issue par tâche, étiquetée par service et rattachée à un jalon (`S5` à `S10`).

---

### 10. Répartition des tâches et plan de présentation

#### 10.1 Responsabilités individuelles

| Membre | Microservice | Base | Tâches transverses associées |
| :--- | :--- | :--- | :--- |
| Étudiant 1 | `user-service` | H2 | Keycloak : realm, client front, rôles, compte de service (séance 8) ; authentification dans le front |
| Étudiant 2 | `content-service` | H2 | API Gateway et Eureka Server (séance 5) ; données de seed du référentiel (sujets, grilles) |
| Étudiant 3 | `recording-service` | H2 | Docker : Dockerfiles et `docker-compose` (séance 9) ; conteneur `speech-engine` ; enregistrement audio dans le front |
| Étudiant 4 | `simulation-service` | MySQL | Bibliothèque `events-contract` et configuration RabbitMQ (séance 7) ; écrans de simulation du front |
| Étudiant 5 | `feedback-service` | MySQL | Écrans feedback et espace formateur du front ; revue des contrats d'API (OpenAPI) |
| Étudiant 6 | `progress-service` | MySQL | Tableau de bord du front ; `README` racine ; script de seed Faker des apprenants et historiques |

Chaque étudiant assure aussi, pour son service : documentation Swagger, enregistrement Eureka, tests, Dockerfile et README.

#### 10.2 Tâches du groupe

| Tâche commune | Répartition | Séances |
| :--- | :--- | :--- |
| `analysis-service` : modèle MongoDB et API REST | Étudiant 5 (modèle), Étudiant 6 (API) | S5, S6 |
| `analysis-service` : consommation et publication d'événements | Étudiant 4 | S7 |
| `analysis-service` : intégration du moteur vocal | Étudiant 3 | S6, S7 |
| `analysis-service` : tests et jeu de données | Étudiants 1 et 2 | S7, S9 |
| Front-end web : structure, authentification, navigation | Étudiant 1 | S6, S8 |
| Front-end web : pages par domaine | Chacun, pour son domaine (voir 10.1) | S6 à S9 |
| Intégration et recette bout en bout, démo | Tous | S9, S10 |

#### 10.3 Plan de la présentation (13 min 30)

| N° | Contenu | Orateur | Durée | Type |
| :--- | :--- | :--- | :---: | :--- |
| 1 | Idée générale, problématique, acteurs | Étudiant 1 | 1:00 | Groupe |
| 2 | Fonctionnalités et cas d'utilisation | Étudiant 2 | 1:00 | Groupe |
| 3 | Architecture globale : diagramme, Gateway, sécurité | Étudiant 3 | 1:30 | Groupe |
| 4 | Découpage, choix des bases, technologies | Étudiant 4 | 1:00 | Groupe |
| 5 | Communications synchrones et asynchrones | Étudiant 5 | 1:00 | Groupe |
| 6 | Git, outil de gestion, répartition, planning, risques | Étudiant 6 | 1:00 | Groupe |
| 7 à 12 | Un microservice chacun : classes, API, interactions | Étudiants 1 à 6 | 6 × 1:00 | Individuel |
| 13 | Microservice commun `analysis-service` | Étudiant 3 | 1:00 | Commun |

---

### 11. Planning prévisionnel

| Séance | Objectifs d'équipe | Livrables individuels |
| :--- | :--- | :--- |
| S1 à S3 | Équipe, dépôt Git, sujet, découpage, contrats d'API, diagrammes | Fiche du microservice : classes et API |
| S4 | Jalon 1 : validation de la conception (aucun développement avant) | Présentation, dossier technique, dépôt Git conforme |
| S5 | API Gateway et Eureka ; squelettes des 7 services | Service démarrant, entités, CRUD principal, Swagger, enregistrement Eureka, jeu de seed |
| S6 | Communication synchrone (OpenFeign) ; début du front | Appels sortants du service, gestion du `503` ; pages du front |
| S7 | Communication asynchrone (RabbitMQ) ; `analysis-service` | Publication et consommation des événements, idempotence |
| S8 | Sécurité Keycloak ; rôles dans les services | `@PreAuthorize` sur les endpoints, `401` et `403` testés |
| S9 | Dockerisation ; recette de bout en bout | Dockerfile, intégration au `docker-compose`, README du service |
| S10 | Jalon 2 : démonstration et soutenance | Démo front et back, présentation de son microservice, questions individuelles |

---

### 12. Risques et parades

| Risque | Proba. | Impact | Parade |
| :--- | :--- | :--- | :--- |
| Moteur vocal lent, imprécis ou difficile à installer | Forte | Fort | Modèle léger, audios courts, traitement asynchrone, mode simulé renvoyant des résultats de test pour que la démo ne dépende pas du moteur |
| Messages perdus ou doublons avec le broker | Moy. | Moy. | `eventId` et consommateurs idempotents, file de rebut, contrats d'événements dans une bibliothèque partagée |
| Un service en attente d'un autre bloque le développement | Forte | Moy. | Contrats OpenAPI figés à S4, réponses simulées (WireMock), jeux de seed |
| Retard de l'intégration Keycloak (S8) | Moy. | Moy. | Profil `dev` sans sécurité pour développer, export du realm versionné dans le dépôt |
| Trop de conteneurs pour les machines (RAM) | Moy. | Moy. | Images légères, profils Compose, démarrage échelonné, healthchecks |
| Déséquilibre de charge ou absence d'un membre | Moy. | Fort | Tableau partagé, binômes de relecture, tâches transverses réparties, point d'équipe à chaque séance |
| Conflits Git | Moy. | Faible | Une branche par service, pull requests courtes, dossier par service |
| Données vocales et droit à l'effacement | Faible | Fort | Aucun apprenant réel, corpus publics ou accords écrits, endpoints `DELETE` |
| Licences des corpus (usage non commercial, citation) | Moy. | Moy. | Vérifier avant usage, citer dans le README, privilégier CC0 et CC BY |
| Périmètre trop ambitieux (examinateur LLM, mobile) | Moy. | Moy. | MVP défini en 1.3 ; extensions seulement après la recette de S9 |

---

### 13. Points à valider avec l'enseignante
1. Le découpage retenu : 6 microservices individuels (un par étudiant) et 1 microservice commun en MongoDB.
2. Le broker asynchrone : RabbitMQ proposé, Kafka possible, à confirmer pour la séance 7.
3. Le moteur vocal (transcription et prosodie) : outil externe en conteneur appelé en HTTP par des services Spring Boot, sans être compté comme microservice.
4. Le front-end : application web seule dans le périmètre, application mobile en extension.
5. Le service commun : confirmer qu'une analyse acoustique pilotée par événements répond à l'attendu de « technologie avancée ».

---

### Annexe A · Conventions REST

| Sujet | Convention |
| :--- | :--- |
| Format | JSON, UTF-8 ; dates ISO 8601 ; préfixe `/api` ; ressources au pluriel |
| Pagination | `page` (à partir de 0) et `size` ; réponse avec `content, page, size, totalElements` |
| Codes de succès | `200` lecture ou mise à jour ; `201` création (en-tête `Location`) ; `202` traitement asynchrone accepté ; `204` suppression |
| Codes d'erreur | `400` requête invalide ; `401` jeton absent ou invalide ; `403` rôle insuffisant ; `404` ressource absente ; `409` conflit d'état ; `413` fichier trop gros ; `415` format non supporté ; `503` service appelé indisponible |
| Format d'erreur | RFC 7807 (`ProblemDetail`) : `type, title, status, detail, instance` |
| Documentation | Swagger UI sur chaque service ; contrat OpenAPI exporté dans `Documentation/` |

### Annexe B · Exemples d'événements

`AnalysisCompleted` (clé de routage `analysis.completed`) :
```json
{
  "eventId": "8f1c2c1e-5a5e-4a61-9c1f-3a2b4d7e9a10",
  "occurredAt": "2026-12-02T10:15:30Z",
  "recordingId": 42,
  "userId": 7,
  "language": "EN",
  "analysisId": "6650f1a2c3d4e5f607182930",
  "scores": { "pronunciation": 72.5, "fluency": 64.0, "intonation": 58.3 },
  "speechRateWpm": 118
}
```

`FeedbackGenerated` (clé de routage `feedback.generated`) :
```json
{
  "eventId": "b3a7d2e0-1c44-4f0e-8c55-0e9a6f1d2b33",
  "occurredAt": "2026-12-02T10:15:41Z",
  "feedbackId": 311,
  "recordingId": 42,
  "userId": 7,
  "simulationId": null,
  "scores": {
    "pronunciation": 72.5,
    "fluency": 64.0,
    "intonation": 58.3,
    "lexicon": 61.0,
    "grammar": 55.5,
    "coherence": 70.0
  }
}
```

Exemple de réponse d'erreur (`409`, transcription non prête) :
```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "La transcription de l'enregistrement 42 n'est pas encore disponible.",
  "instance": "/api/recordings/42/transcript"
}
```

---

## Partie 2 — Document de conception · Jalon 1 (séance 4)

- **Équipe :** 6 étudiants
- **Architecture :** 6 microservices individuels + 1 microservice commun (technologie avancée), infrastructure Spring Cloud commune, front-end web et mobile
- **Version :** 1.0 · novembre 2026

### 1. Présentation du sujet

#### 1.1 Vision
L’oral est la compétence la plus déterminante aux examens de langue, et pourtant la moins entraînée. En classe, chaque apprenant ne dispose que de quelques minutes de parole par séance, et il n’a presque jamais de retour sur sa prononciation ou sa fluidité en dehors.
La plateforme permet à l’apprenant de s’enregistrer aussi souvent qu’il le souhaite, de recevoir un retour immédiat sur chaque prise de parole, de s’entraîner sur des simulations fidèles aux épreuves officielles et de mesurer sa progression vers le niveau visé (B1 à C2 du CECRL), en français et en anglais. Le formateur retrouve en un coup d’œil les difficultés de son groupe.

#### 1.2 Acteurs et besoins

| Acteur | Profil | Besoins principaux |
| :--- | :--- | :--- |
| Apprenant | Étudiant, salarié ou candidat à la mobilité qui prépare une certification de niveau B1 à C2 | S’enregistrer, recevoir un feedback détaillé, passer des épreuves blanches, suivre sa progression, obtenir une attestation de niveau |
| Formateur | Enseignant ou coach de langue | Suivre ses groupes, repérer les apprenants en difficulté, valider ou ajuster l’évaluation automatique, ajouter ses commentaires |
| Établissement | École ou centre de langues | Piloter plusieurs classes, consulter des indicateurs agrégés, délivrer des attestations de niveau internes |

#### 1.3 Fonctionnalités clés
- **Enregistrement et analyse :** l’apprenant parle depuis son navigateur, l’audio est transcrit (Speech-to-Text) puis analysé (prononciation, débit, pauses, hésitations).
- **Correction et feedback personnalisé :** grammaire, lexique, intonation, exemples de reformulation et exercices ciblant les erreurs récurrentes.
- **Simulation d’examens oraux :** temps de préparation chronométré, sujet tiré au sort, relances d’un examinateur virtuel, note estimée sur la grille du niveau visé.
- **Tableaux de bord :** évolution de chaque critère, écart avec le niveau cible, vue formateur et alertes.
- **Certifications :** catalogue des épreuves (DELF, DALF, TCF, Cambridge, IELTS, TOEFL), niveau requis, préparation et attestations de niveau.
- **Critères d’évaluation de chaque prise de parole :** prononciation, fluidité, intonation, lexique, grammaire et cohérence, alignés sur les descripteurs du CECRL.

### 2. Stack technique imposée

| Composant | Technologie | Usage |
| :--- | :--- | :--- |
| Microservices | Spring Boot | Un microservice par étudiant, avec sa propre base |
| API Gateway | Spring Cloud Gateway | Point d’entrée unique et routage |
| Discovery | Eureka | Enregistrement et découverte des services |
| Sécurité | Keycloak | Authentification, rôles, tokens JWT |
| Conteneurisation | Docker · Docker Compose | Images et lancement global |
| Bases de données | H2 · MySQL | MongoDB ou PostgreSQL pour le microservice avancé, une base par service |
| Front-end | Application web | Consomme les API via la gateway, authentification via Keycloak |

### 3. Découpage en microservices

| MS | Service | Responsable | Base | Responsabilités |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `user-progress-service` | Étudiant 1 | H2 | Utilisateurs, rôles, établissements, classes et groupes, niveau et certification visés, progression |
| 2 | `exam-content-service` | Étudiant 2 | H2 | Sujets d’oral par niveau et langue, grilles et descripteurs CECRL |
| 3 | `recording-service` | Étudiant 3 | H2 | Upload audio, métadonnées, transcription Speech-to-Text |
| 4 | `simulation-service` | Étudiant 4 | MySQL | Épreuve blanche : tirage du sujet, chronomètre, relances de l’examinateur virtuel, note estimée |
| 5 | `feedback-service` | Étudiant 5 | MySQL | Correction de grammaire et de lexique, reformulations, exercices recommandés, validation et commentaires du formateur |
| 6 | `certification-service` | Étudiant 6 | MySQL | Catalogue des certifications, format et niveau requis, écart avec le niveau visé, attestations de niveau |
| Avancé | `analysis-service` | Groupe | MongoDB | Scores de prononciation, fluidité et intonation, résultats détaillés mot par mot |

#### 3.1 Entités principales par service

| Service | Entités principales |
| :--- | :--- |
| `user-progress-service` | `User, Role, Establishment, Group, TargetLevel, ProgressSnapshot, Alert` |
| `exam-content-service` | `Subject, Grid, Descriptor, Criterion` |
| `recording-service` | `Recording, Transcript` |
| `simulation-service` | `Simulation, SimulationStep, ExaminerPrompt, EstimatedScore` |
| `feedback-service` | `Feedback, Correction, Exercise, TrainerReview` |
| `certification-service` | `Certification, ExamFormat, Attestation` |
| `analysis-service` (MongoDB) | Document `Analysis` : scores par critère, liste de mots avec phonèmes, pauses, courbe d’intonation |

### 4. Contrats d'API (Synthèse Jalon 1)
- `user-progress-service` : `/api/users`, `/api/users/{id}`, `/api/users/{id}/target`, `/api/establishments`, `/api/groups`, `/api/progress/{userId}`, `/api/groups/{id}/dashboard`, `/api/alerts`
- `exam-content-service` : `/api/subjects?level=B2&lang=EN`, `/api/subjects/random`, `POST /api/subjects`, `/api/grids/{level}`
- `recording-service` : `POST /api/recordings`, `GET /api/recordings/{id}`, `GET /api/recordings/{id}/transcript`
- `simulation-service` : `POST /api/simulations`, `POST /api/simulations/{id}/answers`, `GET /api/simulations/{id}/result`
- `feedback-service` : `/api/feedbacks/{recordingId}`, `/api/feedbacks/user/{userId}`, `/api/exercises/recommended/{userId}`, `PUT /api/feedbacks/{id}/validate`, `POST /api/feedbacks/{id}/comments`
- `certification-service` : `/api/certifications`, `/api/certifications/{id}`, `/api/certifications/{id}/readiness/{userId}`, `POST /api/attestations`, `/api/attestations/user/{userId}`
- `analysis-service` : `/api/analyses/{recordingId}`, `/api/analyses/user/{userId}`

### 5. Communication entre microservices
- **Synchrone (OpenFeign) :** `simulation-service` → `exam-content-service`, `certification-service`, `user-progress-service` ; `certification-service` → `user-progress-service`.
- **Asynchrone (RabbitMQ / Kafka) :** `RecordingTranscribed` (`recording-service` → `analysis-service`), `AnalysisCompleted` (`analysis-service` → `feedback-service`, `user-progress-service`), `SimulationFinished` (`simulation-service` → `user-progress-service`, `certification-service`), `AttestationIssued` (`certification-service` → `user-progress-service`).

### 6. Architecture cible & Routage Gateway
*(Figure 1 · Architecture cible conforme à la stack imposée)*

- API Gateway (`:8080`), Eureka, Keycloak, Docker Compose, Front-end Web (obligatoire) + Mobile (bonus).

### 7. Organisation du dépôt Git & 8. Répartition des rôles & 9. Planning & 10. Livrables et évaluation & 11. Données et conformité & 12. Points à valider
- **Critères d'évaluation (40 % du module) :** choix architecturaux, outil de gestion de projet, choix fonctionnels et données, résultats d’exécution, technologies respectées, structuration du code, communication entre les microservices, richesse des API.
- **Conformité RGPD :** aucun apprenant réel, corpus publics (`Mozilla Common Voice`, `speechocean762`, `L2-ARCTIC`, `Multilingual LibriSpeech`), audio synthétique, données simulées Faker.
