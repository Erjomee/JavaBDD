# 🚀 Gestion de Programmeurs - Application Java avec PostgreSQL

Application Java complète de gestion de programmeurs et de projets avec API REST et interface console interactive.

Réalisé par **Tran Jérome** - **Mychalski Ronan** - **Vong A Lau Benoit**

Groupe **ING2 - LSI2**

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Démarrage rapide](#-démarrage-rapide)
- [API REST](#-api-rest)
- [Structure de la base de données](#-structure-de-la-base-de-données)

## ✨ Fonctionnalités

### Interface Console Interactive
- Afficher tous les programmeurs
- Afficher un programmeur par ID
- Ajouter un nouveau programmeur
- Supprimer un programmeur
- Modifier le salaire d'un programmeur
- Afficher la liste des projets
- Afficher les programmeurs d'un même projet
- Gestion complète avec validation des entrées

### API REST
- **Programmeurs** : CRUD complet (GET, POST, PUT, DELETE)
- **Projets** : Gestion des projets (GET, POST, DELETE)
- Modification de la prime d'un programmeur
- Attribution/retrait d'un programmeur à un projet
- Support CORS pour intégration front-end

### Interface Web
- Interface graphique moderne pour gérer les programmeurs et projets
- Visualisation en temps réel des données
- Formulaires interactifs

## 🏗 Architecture

```
JavaBDD/
├── src/
│   ├── backend/
│   │   ├── Controller/
│   │   │   └── RestController.java      # API REST
│   │   ├── Entity/
│   │   │   ├── Programmeur.java         # Entité Programmeur
│   │   │   └── Projet.java              # Entité Projet
│   │   ├── Repository/
│   │   │   ├── ActionsBDD.java          # Interface DAO
│   │   │   ├── ActionsBDDImpl.java      # Implémentation DAO
│   │   │   └── ConnexionBDD.java        # Connexion PostgreSQL
│   │   ├── Menu.java                    # Interface console
│   │   └── Start.java                   # Point d'entrée console
│   └── front/                            # Interface Web
├── docker-compose.yml                    # Configuration Docker
├── Dockerfile                            # Image Docker
├── schema.sql                            # Schéma de base de données
├── console.bat                           # Script Windows pour le menu
└── README.md
```

## 📦 Prérequis

- **Docker**, **Docker Desktop** et **Docker Compose** installés
- Port **8080** disponible pour l'API REST
- Port **5432** disponible pour PostgreSQL

## 🚀 Démarrage rapide

### 1. Cloner et se placer dans le projet

```bash
git clone <url-du-repo>
cd JavaBDD
```

### 2. Démarrer les services Docker

```bash
# Démarrer la base de données et l'API REST
docker-compose up -d db app-rest

# (Optionnel) Démarrer aussi le conteneur console
docker-compose --profile console up -d app-console
```

### 3. Vérifier que tout fonctionne

```bash 
# Vérifier les conteneurs
docker ps
```

Vous devriez voir `my_postgres` et `java_app_rest` en cours d'exécution.

## 🖥 Utilisation

### Mode 1 : Interface Web (Front-end)

L'API REST est accessible sur **http://localhost:8080**

Pour lancer l'interface web :

```bash
# Se placer dans le dossier front
cd src/front
```

```bash
# Lancer l'appli React
npm start
```


L'interface web sera accessible sur le port défini par votre application front-end.

### Mode 2 : Menu Console Interactive

#### Option A : Utiliser le script Windows (Recommandé)

```bash
.\console.bat
```

#### Option B : Ligne de commande

```bash
# S'assurer que le conteneur console existe
docker-compose --profile console up -d app-console

# Lancer le menu interactif
docker exec -it java_app_console java -cp "out:/usr/share/java/postgresql.jar" backend.Start
```

#### Navigation dans le menu

```
********* MENU *************
1. Afficher tous les programmeurs
2. Afficher un programmeur
3. Supprimer un programmeur
4. Ajouter un programmeur
5. Modifier le salaire
6. Afficher la liste des projets
7. Programmeurs d'un même projet
8. Quitter le programme
Votre choix : 
```

**Pour quitter : tapez 8 puis Entrée**

### Arrêter les services
```bash
# 1. Arrêter tous les conteneurs
docker-compose --profile console down

# 2. Supprimer l'ancien conteneur orphelin
docker rm -f java_app

# 3. Nettoyer les réseaux
docker network prune -f
```

### Reconstruire après modification du code
```bash
docker-compose build
docker-compose up -d
```

### Réinitialiser la base de données
```bash
docker-compose down -v
docker-compose up -d
```

## 🌐 API REST

### Endpoints Programmeurs

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/programmeurs` | Récupérer tous les programmeurs |
| GET | `/api/programmeurs/{id}` | Récupérer un programmeur par ID |
| POST | `/api/programmeurs` | Ajouter un nouveau programmeur |
| PUT | `/api/programmeurs/{id}` | Modifier le salaire d'un programmeur |
| PUT | `/api/programmeurs/{id}/prime` | Modifier la prime d'un programmeur |
| PUT | `/api/programmeurs/{id}/projet` | Assigner à un projet (ou retirer avec `idProjet: 0`) |
| DELETE | `/api/programmeurs/{id}` | Supprimer un programmeur |

### Endpoints Projets

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/projets` | Récupérer tous les projets |
| POST | `/api/projets` | Ajouter un nouveau projet |
| DELETE | `/api/projets/{id}` | Supprimer un projet |

### Exemples de requêtes

#### Ajouter un programmeur
```http
POST /api/programmeurs
Content-Type: application/json

{
  "nom": "Martin",
  "prenom": "Sophie",
  "anNaissance": 1995,
  "salaire": 42000,
  "prime": 3000,
  "idProjet": 2
}
```

#### Modifier la prime
```http
PUT /api/programmeurs/1/prime
Content-Type: application/json

{
  "prime": 6000
}
```

#### Ajouter un projet
```http
POST /api/projets
Content-Type: application/json

{
  "nom_projet": "Application Mobile",
  "dateDebut": "2024-03-01",
  "dateFin": "2024-12-31",
  "statut": "En cours"
}
```

## 🗄 Structure de la base de données

### Table `programmeur`

| Colonne        | Type    | Description                    |
|----------------|---------|--------------------------------|
| id_programmeur | SERIAL  | Identifiant unique (PK)        |
| nom            | VARCHAR | Nom du programmeur             |
| prenom         | VARCHAR | Prénom du programmeur          |
| an_naissance   | INTEGER | Année de naissance             |
| salaire        | DECIMAL | Salaire annuel                 |
| prime          | DECIMAL | Prime annuelle                 |
| id_projet      | INTEGER | ID du projet assigné (FK, nullable) |

### Table `projet`

| Colonne      | Type    | Description                |
|--------------|---------|----------------------------|
| id_projet    | SERIAL  | Identifiant unique (PK)    |
| nom_projet   | VARCHAR | Nom du projet              |
| date_debut   | DATE    | Date de début              |
| date_fin     | DATE    | Date de fin prévue         |
| statut       | VARCHAR | Statut (En cours, Terminé) |

## 🛠 Commandes utiles

### Voir les logs
```bash
docker-compose logs -f app-rest
```

### Accéder à la base de données
```bash
docker exec -it my_postgres psql -U myuser -d mydb
```

## 📝 Notes importantes

- **CORS** : L'API REST autorise toutes les origines pour le développement
- **Données** : Les données sont persistées dans un volume Docker et survivent aux redémarrages
- **Port 8080** : Assurez-vous que ce port est disponible pour l'API REST

## 👤 Auteur

Jérome TRAN - Ronan MYCHALSKI - Benoit VONG A LAU

Projet réalisé dans le cadre du cours ALSI54 - Programmation en Java - EFREI Paris

---
