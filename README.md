# 🎙️ OralTrain — Plateforme d'entraînement et d'évaluation de l'expression orale (FR / EN)

> **Module AWD · Applications Web Distribuées** — Mini-projet 2026-2027
> **Version 2.0 · novembre 2026** (Nouvelle répartition validée)

Application web distribuée, basée sur une architecture **microservices (Spring Boot / Spring Cloud + Node.js / NestJS)**, qui fait parler l'apprenant, analyse sa prise de parole et le prépare aux certifications officielles en **français** et en **anglais**, des niveaux **CECRL A1 à C2**.

---

## 📋 Informations du projet

| | |
|---|---|
| **Projet** | OralTrain |
| **Classe / Équipe** | _4SSA6-G1_ |
| **Dépôt Git** | https://github.com/Baha268/oraltrainer-4ssa6-g11 |
| **Encadrante** | _Mme Rim Douss_ |

### Nouvelle répartition de l'équipe (v2)

| Membre | Nom et prénom | Microservice responsable | Port | Base | Statut |
|---|---|---|---:|---|---|
| Étudiant 1 | _Beha Eddine Cherif_ | `content-service` | `8082` | H2 | Inchangé |
| Étudiant 2 | _Ala Salem_ | `recording-service` | `8083` | H2 + MongoDB (GridFS) | Élargi (+ analyse vocale) |
| Étudiant 3 | _Yessmine Aissa Larousse_ | `simulation-service` | `8084` | MySQL | Inchangé |
| Étudiant 4 | _Feriel Dridi_ | `feedback-service` | `8085` | MySQL | Inchangé |
| Étudiant 5 | _Ahmed Chaabeni_ | `progress-service` | `8086` | MySQL | Renforcé (+ objectifs, reco) |
| Étudiant 6 | _Med Hedi Guidara_ | `classroom-service` | `8089` | MySQL | Nouveau |
| **Groupe** | _Équipe complète_ | `user-service` | `8081` | H2 | Commun (Keycloak) |
| **Groupe** | _Équipe complète_ | `notification-service` | `8088` | MongoDB | Commun (**Node.js / NestJS**) |

> **Évolutions majeures de la v2 :**
> - **`analysis-service` supprimé** (port `8087` libéré) : l'analyse vocale (prononciation, fluidité, intonation, pauses, hauteur) est intégrée dans `recording-service` ; l'analyse linguistique reste dans `feedback-service`.
> - **`classroom-service` (`:8089`) ajouté** (Med Hedi Guidara) : établissements, classes, inscriptions, parcours assignés, commentaires du formateur et tableaux de bord de classe/établissement.
> - **Deux services communs** : `user-service` (`:8081`, Spring Boot) et `notification-service` (`:8088`, **Node.js / NestJS** + WebSocket + RabbitMQ).

---

## 1. Acteurs et rôles Keycloak

| Acteur | Rôle Keycloak | Fonctionnalités principales |
|---|---|---|
| **Visiteur** | — *(public)* | Consulter la présentation de la plateforme et des certifications, créer un compte |
| **Apprenant** | `APPRENANT` | S'entraîner sur un exercice, passer un examen blanc, consulter feedbacks, progression, objectifs et attestations, recevoir les notifications |
| **Formateur** | `FORMATEUR` | Gérer ses classes, assigner des parcours, valider ou ajuster les feedbacks, commenter, recevoir les alertes, consulter le tableau de bord de classe |
| **Admin établissement** | `ADMIN_ETABLISSEMENT` | Gérer l'établissement, ses classes et ses formateurs ; consulter le tableau de bord d'établissement |
| **Admin plateforme** | `ADMIN` | Gérer le catalogue (certifications, sujets, grilles, exercices), les utilisateurs et les rôles ; suivre les traitements audio en échec |

---

## 2. Architecture technique cible

```text
                       Front-end web (SPA Angular)
                    apprenant · formateur · administrateur
                       │                        │
            login OIDC │                        │ HTTPS / REST + JWT (+ WebSocket)
                       ▼                        ▼
                 Keycloak :8180 ◄────── API Gateway :8080 (local :8090)
                       │                        │
        Config Server :8888             Eureka Server :8761
                       │                        │
        ┌──────────────┴────────────────────────┴──────────────┐
        ├──► user-service         :8081  (Spring Boot · H2)
        ├──► content-service      :8082  (Spring Boot · H2)
        ├──► recording-service    :8083  (Spring Boot · H2 + MongoDB GridFS) ──► speech-engine (HTTP)
        ├──► simulation-service   :8084  (Spring Boot · MySQL)
        ├──► feedback-service     :8085  (Spring Boot · MySQL)
        ├──► progress-service     :8086  (Spring Boot · MySQL)
        ├──► classroom-service    :8089  (Spring Boot · MySQL)
        └──► notification-service :8088  (Node.js / NestJS · MongoDB)
                       │
                       ▼
          RabbitMQ :5672 / :15672 — exchange topic « oral.events »
```

### Composants d'infrastructure

| Composant | Technologie | Port | Rôle |
|---|---|---:|---|
| **API Gateway** | Spring Cloud Gateway | `8080` *(local `8090`)* | Point d'entrée unique, routage `lb://` via Eureka, validation JWT (JWKS Keycloak), CORS, relai WebSocket |
| **Eureka Server** | Spring Cloud Netflix Eureka | `8761` | Annuaire d'enregistrement et de découverte des services |
| **Config Server** | Spring Cloud Config | `8888` | Configuration centralisée chargée par chaque service au démarrage |
| **Keycloak** | Keycloak | `8180` | Authentification OIDC, rôles, émission des JWT |
| **RabbitMQ** | RabbitMQ | `5672` / `15672` | Bus d'événements asynchrone (exchange topic `oral.events` + DLQ) |
| **speech-engine** | Whisper / Vosk + Praat / Parselmouth | HTTP | Transcription et analyse prosodique (appelé uniquement par `recording-service`) |

---

## 3. Responsabilités et entités des microservices

| Service | Port | Base | Entités principales | Responsabilités clés |
|---|---:|---|---|---|
| `user-service` | `8081` | H2 | `Utilisateur`, `Role`, `CodeRole` | Profils métier, rôles Keycloak, unicité `keycloakId`/`email`, publie `user.registered` |
| `content-service` | `8082` | H2 | `Certification`, `SujetOral`, `Exercice`, `GrilleEvaluation`, `CritereEvaluation` | Catalogue pédagogique CECRL (A1–C2), sujets, grilles pondérées (100 %), exercices par tags |
| `recording-service` | `8083` | H2 + MongoDB | `Enregistrement`, `FichierAudio` (GridFS), `Transcription`, `AnalyseVocale`, `ScorePrononciation` | Stockage audio, transcription + analyse prosodique via `speech-engine`, relance (max 3) |
| `simulation-service` | `8084` | MySQL | `Simulation`, `EtapeSimulation`, `RelanceExaminateur`, `NoteEstimee` | Examen blanc en 3 étapes (`PREPARATION`, `MONOLOGUE`, `INTERACTION`), relances, note estimée |
| `feedback-service` | `8085` | MySQL | `Feedback`, `ScoreCritere`, `Correction`, `Recommandation` | Retour linguistique, scores CECRL par critère, recommandations, validation/ajustement formateur |
| `progress-service` | `8086` | MySQL | `Progression`, `HistoriqueScore`, `Objectif`, `AlerteProgression`, `Attestation` | Carnet de suivi temporel, tendances, objectifs, alertes, attestations, reco d'exercices |
| `notification-service` | `8088` | MongoDB | `EvenementTraite`, `Notification`, `ModeleMessage`, `PreferenceNotification` | **NestJS** : écoute tous les événements RabbitMQ (idempotent), notifications WebSocket et e-mail |
| `classroom-service` | `8089` | MySQL | `Etablissement`, `Classe`, `Inscription`, `ParcoursAssigne`, `CommentaireFormateur` | Établissements, classes, inscriptions, parcours assignés, commentaires, dashboard agrégé |

---

## 4. Communications entre microservices

### 4.1 Communications synchrones — REST / OpenFeign (+ Resilience4j)

| Appelant | Appelé | Endpoint (exemple) | But |
|---|---|---|---|
| `simulation` | `content` | `GET /api/subjects/{id}` | Récupérer le sujet et la certification de l'épreuve |
| `recording` | `simulation` | `GET /api/simulations/{id}/steps/{stepId}/status` | Vérifier que la simulation existe et que l'étape est active |
| `recording` | `speech-engine` | `POST /transcribe`, `POST /analyse` | Transcription (Whisper/Vosk) et prosodie (Praat/Parselmouth) |
| `feedback` | `recording` | `GET /api/recordings/{id}/transcription`, `/analysis` | Lire la transcription et les scores vocaux |
| `feedback` | `content` | `GET /api/exercises/{id}`, `GET /api/rubrics/{id}` | Lire l'exercice et la grille CECRL |
| `progress` | `content` | `GET /api/certifications/{id}`, `GET /api/exercises?tags=` | Niveau minimal d'attestation ; exercices à recommander |
| `classroom` | `progress` | `GET /api/progress/summary?userIds=` | Indicateurs agrégés pour le tableau de bord de classe |
| `classroom` | `content` | `GET /api/subjects/{id}` | Vérifier le sujet d'un parcours assigné |
| `progress` | `classroom` | `GET /api/classes/by-learner/{userId}` | Retrouver le formateur de l'apprenant pour lui adresser une alerte |
| `classroom` | `user` | `GET /api/users/{id}` | Formateur et membres d'une classe |
| `notification` | `user` | `GET /api/users/{id}` | E-mail et rôle du destinataire |

### 4.2 Communications asynchrones — RabbitMQ (exchange topic `oral.events`)

| Événement | Publié par | Consommé par | Effet |
|---|---|---|---|
| `recording.analysed` | `recording` | `feedback`, `notification` | Déclenche la génération du feedback ; prévient l'apprenant |
| `recording.failed` | `recording` | `notification` | Alerte l'apprenant et l'administrateur ; relance proposée |
| `feedback.generated` | `feedback` | `simulation`, `progress`, `notification` | Note estimée, point d'historique, notification apprenant |
| `feedback.validated` | `feedback` | `progress`, `notification` | Mise à jour du point d'historique après validation/ajustement formateur |
| `simulation.completed` | `simulation` | `progress`, `notification` | Note globale d'examen blanc et notification de fin d'épreuve |
| `progress.alert.raised` | `progress` | `notification` | Alerte au formateur (apprenant en difficulté) |
| `attestation.issued` | `progress` | `notification` | Attestation disponible pour l'apprenant |
| `class.path.assigned` | `classroom` | `notification` | Notification du nouveau parcours aux apprenants de la classe |
| `trainer.comment.added` | `classroom` | `notification` | Notification d'un commentaire formateur à l'apprenant |
| `user.registered` | `user` | `notification` | Message de bienvenue |

---

## 5. Structure du dépôt

```text
oraltrainer-4ssa6-g11/
├── Backend/
│   ├── api-gateway/            # Spring Cloud Gateway (:8080 / local :8090)
│   ├── eureka-server/          # Spring Cloud Netflix Eureka (:8761)
│   ├── config-server/          # Spring Cloud Config (:8888)
│   ├── user-service/           # Service commun Spring Boot (:8081)
│   ├── content-service/        # Beha Eddine Cherif (:8082)
│   ├── recording-service/      # Ala Salem (:8083 — audio + transcription + analyse vocale)
│   ├── simulation-service/     # Yessmine Aissa Larousse (:8084)
│   ├── feedback-service/       # Feriel Dridi (:8085)
│   ├── progress-service/       # Ahmed Chaabeni (:8086)
│   ├── notification-service/   # Service commun Node.js / NestJS (:8088)
│   ├── classroom-service/      # Med Hedi Guidara (:8089)
│   ├── events-contract/        # Enveloppe et contrats d'événements partagés
│   └── speech-engine/          # Conteneur Whisper/Vosk + Praat/Parselmouth
├── Frontend/
│   └── web/                    # SPA Web
├── Documentation/
│   ├── Diagrams/
│   ├── Presentation/
│   ├── OralTrain_Dossier_Conception_v2.pdf
│   ├── OralTrain.odt
│   ├── Document_Conception_Plateforme_Oral.docx
│   └── Document_Conception_Plateforme_Oral_Groupe.docx
└── README.md
```
