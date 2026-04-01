# Ingredient Module Design & Implementation Task

## Overview

Design and implement an **Ingredient** for a product system.  
Each product can be associated with multiple ingredients, and each association must record quantity and unit.

Example:
- Product: Mango Dessert
  - Mango Cubes: 160 g
  - Coconut Milk: 20 ml
  - Water: 300 ml

---

## 1. Database Design

### 1.1 Ingredient Table (`ingredient`)

Fields:

- id (Primary Key)
- name (Ingredient name)
- shelf_life (Expiration time after preparation, e.g., hours or days)
- storage_method (Storage method, e.g., refrigerated, frozen, room temperature)
- status (Soft delete flag: 1 = active, 0 = deleted)
- create_time
- update_time

---

### 1.2 Product-Ingredient Relation Table (`product_ingredient`)

This table represents a many-to-many relationship and stores usage details.

Fields:

- id (Primary Key)
- product_id (Product ID)
- ingredient_id (Ingredient ID)
- quantity (Usage amount, e.g., 160)
- unit (Unit, e.g., g, ml)
- create_time
- update_time

---

## 2. API Implementation

### 2.1 Ingredient Admin Controller

Create `IngredientAdminController` with basic CRUD operations:

- Create ingredient
- Get ingredient list (with pagination)
- Get ingredient detail by ID
- Update ingredient
- Delete ingredient (**soft delete using status field, no physical deletion**)

---

### 2.2 Product-Ingredient Binding APIs

Implement APIs to manage relationships between products and ingredients:

- Bind multiple ingredients to a product (batch operation supported)
- Unbind ingredient(s) from a product
- Get all ingredients for a product (including quantity and unit)

---

## 3. Technical Requirements

- Use standard RESTful API design
- Use unified response structure (e.g., Result or Response wrapper)
- Database fields should use snake_case naming convention
- Follow layered architecture: Controller / Service / Mapper
- Implement logical deletion using `status` field

---

## 4. Notes

- Ensure clean and maintainable code structure
- Include necessary DTOs / Entities / Mappers
- Assume a typical Java Spring Boot + MyBatis (or JPA) stack