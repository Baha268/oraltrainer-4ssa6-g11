# OralTrainer (`oraltrainer-4ssa6-g11`)

Plateforme d'entraînement et de simulation d'épreuves orales basée sur une architecture **Microservices** (Spring Boot / Java 17), offrant enregistrement audio, transcription, analyse linguistique/acoustique, retours pédagogiques personnalisés et suivi de progression.

---

## 1. Présentation générale

### Problématique
La préparation aux examens oraux, soutenances et entretiens professionnels manque souvent de rétroaction objective, régulière et personnalisée sur le débit de parole, la fluidité, la gestion du temps et la qualité linguistique.

### Objectifs
- Proposer des parcours et exercices pédagogiques structurés par niveau et thématique.
- Simuler des épreuves orales chronométrées avec questions de relance.
- Capturer les enregistrements vocaux, générer leurs transcriptions et en extraire des métriques linguistiques et acoustiques.
- Produire des retours pédagogiques exploitables, des tableaux de bord de progression et des attestations.
- Garantir une architecture modulaire, scalable et documentée via **OpenAPI 3 / Swagger UI**.

---

## 2. Stack technique

| Couche | Technologies (Cible architecturale) | État actuel du dépôt |
| :--- | :--- | :--- |
| **Langage & Build** | Java 17 (`JDK 17`), Apache Maven (`3.9.x`) | Configuré (Java `17.0.14`, Maven `3.9.16`) |
| **Back-end Microservices** | Spring Boot `3.3.5`, Spring Web, Springdoc OpenAPI (`2.6.0`) | Initialisé pour les 8 services métier (MVC sans BDD) |
| **Infrastructure Microservices** | Spring Cloud Gateway, Netflix Eureka Server | Dossiers réservés (`api-gateway`, `eureka-server`) |
| **Sécurité & IAM** | Keycloak (OAuth2 / OpenID Connect) | Prévu dans l'architecture cible |
| **Bases de données** | Architecture *Database-per-Service* (PostgreSQL / MongoDB) | Non connectées à cette étape (couche MVC sans BDD) |
| **Conteneurisation** | Docker & Docker Compose | Prévu pour l'orchestration des services |
| **Front-end** | Application Web cliente (`Frontend/web/`) | Dossier réservé |

---

## 3. Cartographie des microservices et ports

> [!NOTE]
> À l'étape actuelle d'initialisation, les 8 microservices métier exposent chacun un endpoint de santé `GET /api/<service>/hello` et leur documentation Swagger/OpenAPI sans base de données. Les colonnes *Base de données prévue* et *Infrastructure* reflètent la conception cible.

| Service | Rôle & Responsabilités | Base de données prévue | Port | Statut | Membre responsable (Branche Git) |
| :--- | :--- | :--- | :---: | :--- | :--- |
| `notification-service` | Notifications et alertes utilisateurs | PostgreSQL (`notification_db`) | `8088` | Initialisé (Hello + Swagger) | Med Hedi Guidara (`Med-Hedi-guidara`) |
| `content-service` | Référentiel pédagogique, cours, modules et exercices | PostgreSQL (`content_db`) | `8082` | Initialisé (Hello + Swagger) | Beha Eddine Cherif (`Sherif-Bahaeddine`) |
| `recording-service` | Enregistrements audio et transcriptions | MongoDB / Stockage objet | `8083` | Initialisé (Hello + Swagger) | Ala Salem (`Salem_Ala`) |
| `simulation-service` | Épreuves blanches chronométrées et relances | PostgreSQL (`simulation_db`) | `8084` | Initialisé (Hello + Swagger) | Yessmine Aissa Larousse (`Yessmine_Aissa_Larousse`) |
| `feedback-service` | Génération des retours pédagogiques et recommandations | MongoDB (`feedback_db`) | `8085` | Initialisé (Hello + Swagger) | Feriel Dridi (`Dridi-Feriel`) |
| `progress-service` | Suivi de progression, tableaux de bord et attestations | PostgreSQL (`progress_db`) | `8086` | Initialisé (Hello + Swagger) | Ahmed Chaabeni (`Ahmed-Chaabeni`) |
| `user-service` | Gestion des utilisateurs, profils, rôles et groupes | PostgreSQL (`user_db`) | `8081` | Initialisé (Hello + Swagger) | À attribuer |
| `analysis-service` | Analyse linguistique et acoustique des enregistrements | MongoDB (`analysis_db`) | `8087` | Initialisé (Hello + Swagger) | À attribuer |
| `api-gateway` | Point d'entrée unique, routage et filtrage sécurité | N/A | `8080` *(cible)* | Dossier initialisé | Infrastructure |
| `eureka-server` | Registre de découverte de services | N/A | `8761` *(cible)* | Dossier initialisé | Infrastructure |
| `speech-engine` | Moteur de traitement vocal / ASR | N/A | — | Dossier initialisé | Core / IA |
| `events-contract` | Contrats d'événements partagés | N/A | — | Dossier initialisé | Transverse |

---

## 4. Arborescence du dépôt

```text
oraltrainer-4ssa6-g11/
├── Backend/
│   ├── api-gateway/
│   ├── eureka-server/
│   ├── events-contract/
│   ├── speech-engine/
│   ├── user-service/            # Port 8081
│   ├── content-service/         # Port 8082
│   ├── recording-service/       # Port 8083
│   ├── simulation-service/      # Port 8084
│   ├── feedback-service/        # Port 8085
│   ├── progress-service/        # Port 8086
│   ├── analysis-service/        # Port 8087
│   └── notification-service/    # Port 8088
├── Frontend/
│   └── web/                     # Application cliente web
├── Documentation/
│   ├── Diagrams/                # Architecture, cas d'utilisation, classes, séquences
│   └── Presentation/            # Supports de présentation
├── .gitignore
└── README.md
```

---

## 5. Organisation des branches Git

Pour préserver la propreté du dépôt entre la phase de conception et les développements :

- **`main`** : Branche protégée dédiée uniquement à la structure du projet, à la conception et à la documentation (aucun code source de développement non validé).
- **`develop`** : Branche d'intégration regroupant les microservices fonctionnels et les tests validés.
- **`feat/<nom-service-ou-fonctionnalite>`** : Branches de travail individuelles créées depuis `develop` (ex. `feat/user-service-hello`, `feat/content-crud`) et fusionnées via Pull Request après revue et passage des tests Maven.

---

## 6. Vérification locale & Swagger UI

Chaque microservice dans `Backend/` est un projet Maven autonome :

```bash
# Compiler et exécuter les tests d'un microservice (ex. user-service)
mvn -f Backend/user-service/pom.xml clean test

# Démarrer le microservice
mvn -f Backend/user-service/pom.xml spring-boot:run
```

Une fois démarré (ex. sur le port `8081`) :
- **Endpoint Hello** : `http://localhost:8081/api/user/hello`
- **Spécification OpenAPI** : `http://localhost:8081/v3/api-docs`
- **Swagger UI** : `http://localhost:8081/swagger-ui/index.html`
