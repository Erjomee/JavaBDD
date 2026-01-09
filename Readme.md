# Application Java + PostgreSQL

## Démarrer l'application

```bash
docker-compose up --build
```

## Arrêter l'application

Appuyez sur `Ctrl+C`

## Repartir de zéro (supprimer les données)

```bash
docker-compose down -v
docker-compose up --build
```

## Accéder à la base de données

```bash
docker exec -it my_postgres psql -U myuser -d mydb
```

Puis tapez :
```sql
SELECT * FROM personne;
\q
```

## Connexion depuis un client SQL

- **Host** : localhost
- **Port** : 5432
- **Database** : mydb
- **User** : myuser
- **Password** : mypassword