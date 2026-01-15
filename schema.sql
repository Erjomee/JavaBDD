-- Script de création de la base de données PROG_BD
-- SGBD : PostgreSQL

-- Suppression des tables si elles existent déjà
DROP TABLE IF EXISTS programmeur CASCADE;
DROP TABLE IF EXISTS projet CASCADE;

-- Table Projet
CREATE TABLE projet (
                        id_projet SERIAL PRIMARY KEY,
                        nom_projet VARCHAR(200) NOT NULL,
                        date_debut DATE NOT NULL,
                        date_fin DATE,
                        statut VARCHAR(50) CHECK (statut IN ('En cours', 'Terminé', 'En attente', 'Annulé'))
);

-- Table Programmeur (avec clé étrangère vers projet)
CREATE TABLE programmeur (
                             id_programmeur SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             prenom VARCHAR(100) NOT NULL,
                             an_naissance INTEGER NOT NULL,
                             salaire DECIMAL(10, 2) NOT NULL,
                             prime DECIMAL(10, 2) DEFAULT 0.00,
                             id_projet INTEGER REFERENCES projet(id_projet) ON DELETE SET NULL
);

-- Insertion de données de test pour les projets
INSERT INTO projet (nom_projet, date_debut, date_fin, statut) VALUES
                                                                  ('Système de Gestion RH', '2024-01-15', '2024-12-31', 'En cours'),
                                                                  ('Application Mobile E-commerce', '2024-03-01', '2024-09-30', 'En cours'),
                                                                  ('Refonte Site Web Corporate', '2023-06-01', '2023-12-15', 'Terminé'),
                                                                  ('Plateforme IoT Smart Home', '2024-05-10', '2025-03-20', 'En cours'),
                                                                  ('Migration Cloud Infrastructure', '2024-02-01', NULL, 'En attente');

-- Insertion de données de test pour les programmeurs
INSERT INTO programmeur (nom, prenom, an_naissance, salaire, prime, id_projet) VALUES
                                                                                   ('Dupont', 'Jean', 1990, 45000.00, 5000.00, 1),
                                                                                   ('Martin', 'Sophie', 1988, 52000.00, 6000.00, 1),
                                                                                   ('Bernard', 'Luc', 1995, 38000.00, 3500.00, 2),
                                                                                   ('Dubois', 'Marie', 1992, 48000.00, 5500.00, 2),
                                                                                   ('Petit', 'Paul', 1985, 60000.00, 8000.00, 3),
                                                                                   ('Leroy', 'Alice', 1993, 47000.00, 4500.00, 4),
                                                                                   ('Moreau', 'Pierre', 1991, 50000.00, 5500.00, 4),
                                                                                   ('Simon', 'Emma', 1994, 42000.00, 4000.00, 5),
                                                                                   ('Laurent', 'Lucas', 1989, 55000.00, 7000.00, NULL),
                                                                                   ('Lefebvre', 'Chloé', 1996, 39000.00, 3000.00, NULL);

-- Affichage de confirmation
SELECT 'Base de données créée avec succès!' AS message;

-- Vérification des données
SELECT COUNT(*) AS nb_programmeurs FROM programmeur;
SELECT COUNT(*) AS nb_projets FROM projet;

-- Affichage des données
SELECT 'LISTE DES PROJETS' AS section;
SELECT * FROM projet ORDER BY id_projet;

SELECT 'LISTE DES PROGRAMMEURS' AS section;
SELECT p.id_programmeur, p.nom, p.prenom, p.an_naissance, p.salaire, p.prime,
       COALESCE(pr.nom_projet, 'Aucun projet') AS projet
FROM programmeur p
         LEFT JOIN projet pr ON p.id_projet = pr.id_projet
ORDER BY p.id_programmeur;