# LearnHub

Plateforme de gestion de cours en ligne - Projet DevOps de groupe.

Équipe : Ouahiba, Malak, Salma, Moncef

## Stack

Java 21, Spring Boot 4.1.2, Spring Data JPA, PostgreSQL, JUnit 5, Docker / Docker Compose, Kubernetes (Minikube), GitHub, Jira.

## Ce qui marche déjà

- CRUD Category complet : créer, lister, consulter, modifier, supprimer
- DTO : CategoryRequest / CategoryResponse, jamais d'entité exposée
- Gestion des erreurs : 400, 404, 409, sans stack trace
- Tests JUnit de CategoryService
- Documentation Swagger : http://localhost:8080/swagger-ui.html
- Dockerfile multi-stage + compose.yaml : app + PostgreSQL avec volume postgres_data

## Lancer le projet

1. Créer un fichier `.env` à partir de `.env.example` :
```bash
cp .env.example .env