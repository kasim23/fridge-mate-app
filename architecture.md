# FridgeMate — System Architecture

## Database Schema

```
┌─────────────────────────────────────────┐
│                  users                  │
├──────────────┬──────────────────────────┤
│ Column       │ Type / Constraints        │
├──────────────┼──────────────────────────┤
│ id           │ BIGSERIAL  PRIMARY KEY    │
│ name         │ VARCHAR    NOT NULL       │
│ email        │ VARCHAR    NOT NULL UNIQUE│
│ password     │ VARCHAR    NOT NULL       │  ← bcrypt hash
│ created_at   │ TIMESTAMP  NOT NULL       │
└──────────────┴──────────────────────────┘
        │
        │ 1
        │
        │ has many
        │
        │ N
┌─────────────────────────────────────────────────────────┐
│                      fridge_items                       │
├───────────────┬─────────────────────────────────────────┤
│ Column        │ Type / Constraints                       │
├───────────────┼─────────────────────────────────────────┤
│ id            │ BIGSERIAL   PRIMARY KEY                  │
│ user_id       │ BIGINT      NOT NULL  FK → users.id      │
│ name          │ VARCHAR     NOT NULL                     │
│ category      │ VARCHAR     NOT NULL                     │  ← enum: DAIRY | MEAT | PRODUCE ...
│ location      │ VARCHAR     NOT NULL                     │  ← enum: FRIDGE | PANTRY | FREEZER
│ quantity      │ DOUBLE      NOT NULL                     │
│ unit          │ VARCHAR     NOT NULL                     │  ← "kg" | "litres" | "pieces" ...
│ expiry_date   │ DATE        NOT NULL                     │
│ purchase_date │ DATE        NULLABLE                     │
│ notes         │ TEXT        NULLABLE                     │
│ created_at    │ TIMESTAMP   NOT NULL                     │
│ updated_at    │ TIMESTAMP   NOT NULL                     │
└───────────────┴─────────────────────────────────────────┘
```

---

## Application Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                        HTTP Clients                             │
│              (Angular UI  /  Postman  /  curl)                  │
└───────────────────────────────┬─────────────────────────────────┘
                                │  REST  (JSON over HTTP)
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Spring Security Layer                        │
│        JWT filter — validates token on every request            │
│        Public: POST /api/auth/register  POST /api/auth/login     │
│        Protected: everything else                               │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Controller Layer                           │
│                                                                 │
│   AuthController        /api/auth/**                            │
│   FridgeItemController  /api/items/**                           │
│   RecipeController      /api/recipes/**                         │
│                                                                 │
│   Responsibilities: parse HTTP request, validate input,         │
│   delegate to service, return HTTP response                     │
└──────────────┬──────────────────────────────────┬──────────────┘
               │                                  │
               ▼                                  ▼
┌──────────────────────────┐       ┌──────────────────────────────┐
│      Service Layer        │       │       Service Layer           │
│                           │       │                              │
│   UserService             │       │   RecipeService              │
│   FridgeItemService       │       │   EmailNotificationService   │
│                           │       │                              │
│   Responsibilities:       │       │   Calls external APIs:       │
│   business logic,         │       │   - Spoonacular (recipes)    │
│   validation rules,       │       │   - Gmail SMTP (email)       │
│   orchestration           │       │                              │
└──────────────┬────────────┘       └──────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Repository Layer                            │
│                                                                 │
│   UserRepository          extends JpaRepository                 │
│   FridgeItemRepository    extends JpaRepository                 │
│                                                                 │
│   Spring Data JPA generates SQL automatically from method       │
│   names: findByUser(), findByUserAndExpiryDateBetween()         │
└───────────────────────────────┬─────────────────────────────────┘
                                │  JDBC / Hibernate ORM
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        PostgreSQL                               │
│                tables: users, fridge_items                      │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                    Scheduler (background)                        │
│                                                                 │
│   ExpiryCheckScheduler — runs daily at 08:00                    │
│   1. Query fridge_items WHERE expiry_date <= today + 7 days     │
│   2. Group by user                                              │
│   3. Call RecipeService → Spoonacular                           │
│   4. Call EmailNotificationService → Gmail SMTP                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## REST API Endpoints

```
AUTH (public — no token required)
─────────────────────────────────────────────────────
POST   /api/auth/register     { name, email, password }
POST   /api/auth/login        { email, password }  →  { token }

FRIDGE ITEMS (protected — Bearer token required)
─────────────────────────────────────────────────────
GET    /api/items                    list all items for logged-in user
POST   /api/items                    add a new item
GET    /api/items/expiring?days=7    items expiring within N days
PUT    /api/items/{id}               update an item
DELETE /api/items/{id}               delete an item

RECIPES (protected — Bearer token required)
─────────────────────────────────────────────────────
GET    /api/recipes/suggestions      recipes using near-expiry ingredients
```

---

## Package Structure

```
src/main/java/com/fridgemate/
│
├── FridgeMateApplication.java       ← entry point
│
├── model/
│   ├── User.java                    ← @Entity → users table
│   ├── FridgeItem.java              ← @Entity → fridge_items table
│   └── enums/
│       ├── ItemCategory.java
│       └── ItemLocation.java
│
├── repository/
│   ├── UserRepository.java
│   └── FridgeItemRepository.java
│
├── service/
│   ├── UserService.java
│   ├── FridgeItemService.java
│   ├── RecipeService.java
│   └── EmailNotificationService.java
│
├── controller/
│   ├── AuthController.java
│   ├── FridgeItemController.java
│   └── RecipeController.java
│
├── dto/
│   ├── auth/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   └── AuthResponse.java
│   ├── FridgeItemRequest.java
│   ├── FridgeItemResponse.java
│   └── RecipeSuggestion.java
│
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── UserDetailsServiceImpl.java
│
├── config/
│   ├── SecurityConfig.java
│   └── AppConfig.java
│
└── scheduler/
    └── ExpiryCheckScheduler.java
```
