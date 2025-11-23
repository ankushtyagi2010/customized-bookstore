# API Documentation

## Overview

CustomBooks uses a traditional MVC architecture with server-side rendered pages. This document describes the available endpoints and their functionality.

## Base URL

```
http://localhost:8080
```

## Authentication

The application uses session-based authentication with Spring Security. Users must be logged in to access protected endpoints.

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Home page |
| GET | `/home` | Home page (alias) |
| GET | `/books` | Book catalog |
| GET | `/books/{id}` | Book details |
| GET | `/login` | Login page |
| GET | `/register` | Registration page |
| POST | `/login` | Process login |
| POST | `/register` | Process registration |
| GET | `/about` | About page |
| GET | `/contact` | Contact page |

### Protected Endpoints (Requires Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customize/{templateId}` | Customization wizard |
| POST | `/customize/{templateId}` | Save customization |
| GET | `/customize/edit/{customizedBookId}` | Edit customization |
| POST | `/customize/edit/{customizedBookId}` | Update customization |
| GET | `/preview/{customizedBookId}` | Preview customized book |
| POST | `/preview/{id}/add-to-cart` | Add to cart from preview |
| GET | `/cart` | View shopping cart |
| POST | `/cart/add/{customizedBookId}` | Add item to cart |
| POST | `/cart/update/{cartItemId}` | Update item quantity |
| POST | `/cart/remove/{cartItemId}` | Remove item from cart |
| POST | `/cart/clear` | Clear entire cart |
| GET | `/orders` | Order history (supports filters) |
| GET | `/orders/{orderId}` | Order details |
| GET | `/orders/checkout` | Checkout page |
| POST | `/orders/checkout` | Process checkout |
| GET | `/orders/confirmation/{orderId}` | Order confirmation page |
| POST | `/orders/{orderId}/cancel` | Cancel order |
| GET | `/profile` | User profile page |
| POST | `/profile/update` | Update personal info |
| POST | `/profile/password` | Change password |
| POST | `/profile/address/add` | Add shipping address |
| POST | `/profile/address/delete/{id}` | Delete address |
| POST | `/profile/address/default/{id}` | Set default address |

### Admin Endpoints (Requires ADMIN Role)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin` | Admin dashboard |
| GET | `/admin/books` | List book templates |
| GET | `/admin/books/new` | New book form |
| GET | `/admin/books/edit/{id}` | Edit book form |
| POST | `/admin/books/save` | Save book template |
| POST | `/admin/books/delete/{id}` | Delete book template |
| POST | `/admin/books/toggle-active/{id}` | Toggle book active status |
| POST | `/admin/books/toggle-featured/{id}` | Toggle featured status |
| GET | `/admin/characters` | List character images |
| GET | `/admin/characters/new` | New character form |
| GET | `/admin/characters/edit/{id}` | Edit character form |
| POST | `/admin/characters/save` | Save character image |
| POST | `/admin/characters/delete/{id}` | Delete character |
| GET | `/admin/orders` | List all orders |
| GET | `/admin/orders/{id}` | View order details |
| POST | `/admin/orders/{id}/status` | Update order status |
| GET | `/admin/users` | List all users |

---

## Endpoint Details

### Authentication

#### POST /register

Register a new user account.

**Request Body (form-data):**
```
firstName: string (required)
lastName: string (required)
email: string (required, valid email)
password: string (required, min 6 chars)
confirmPassword: string (required, must match password)
phone: string (optional)
```

**Response:** Redirects to `/login` on success

---

#### POST /login

Authenticate user.

**Request Body (form-data):**
```
username: string (email)
password: string
remember-me: boolean (optional)
```

**Response:** Redirects to `/` on success, `/login?error=true` on failure

---

### Book Catalog

#### GET /books

List all active books with optional filtering.

**Query Parameters:**
```
category: string (optional) - Filter by category
ageGroup: string (optional) - Filter by age group
```

**Categories:** adventure, fantasy, educational, bedtime, birthday
**Age Groups:** 0-2, 3-5, 6-8, 9-12

---

#### GET /books/{id}

Get detailed information about a specific book template.

**Path Parameters:**
```
id: string - Book template ID
```

---

### Customization

#### GET /customize/{templateId}

Display customization wizard for a book template.

**Path Parameters:**
```
templateId: string - Book template ID
```

---

#### POST /customize/{templateId}

Save book customization and create a CustomizedBook.

**Request Body (form-data):**
```
customTitle: string (optional)
dedication: string (optional)
characterCustomizations[0].slotId: string
characterCustomizations[0].customName: string
characterCustomizations[0].selectedImageId: string
customCoverImageUrl: string (optional)
coverImage: file (optional)
```

**Response:** Redirects to `/preview/{customizedBookId}`

---

### Shopping Cart

#### POST /cart/add/{customizedBookId}

Add a customized book to the shopping cart.

**Path Parameters:**
```
customizedBookId: string - Customized book ID
```

**Query Parameters:**
```
quantity: integer (default: 1)
```

---

#### POST /cart/update/{cartItemId}

Update the quantity of a cart item.

**Path Parameters:**
```
cartItemId: string - Cart item ID
```

**Request Body (form-data):**
```
quantity: integer
```

---

### Orders

#### POST /orders/checkout

Process checkout and create an order.

**Request Body (form-data):**
```
fullName: string (required)
street: string (required)
city: string (required)
state: string (required)
zipCode: string (required)
country: string (required)
phone: string (optional)
```

**Response:** Redirects to `/orders/confirmation/{orderId}` on success

---

### Profile Management

#### GET /profile

Display user profile page with tabs for personal info, addresses, and security.

**Response:** Renders profile page with user data and statistics

---

#### POST /profile/update

Update user's personal information.

**Request Body (form-data):**
```
firstName: string (required)
lastName: string (required)
phone: string (optional)
```

**Response:** Redirects to `/profile` with success/error message

---

#### POST /profile/password

Change user's password.

**Request Body (form-data):**
```
currentPassword: string (required)
newPassword: string (required, min 6 chars)
confirmPassword: string (required, must match newPassword)
```

**Response:** Redirects to `/profile` with success/error message

---

#### POST /profile/address/add

Add a new shipping address to user's profile.

**Request Body (form-data):**
```
label: string (optional, e.g., "Home", "Work")
street: string (required)
city: string (required)
state: string (required)
zipCode: string (required)
country: string (required)
isDefault: boolean (optional)
```

**Response:** Redirects to `/profile` with success/error message

---

#### POST /profile/address/delete/{addressId}

Delete a shipping address.

**Path Parameters:**
```
addressId: string - Address ID
```

**Response:** Redirects to `/profile`

---

#### POST /profile/address/default/{addressId}

Set an address as the default shipping address.

**Path Parameters:**
```
addressId: string - Address ID
```

**Response:** Redirects to `/profile`

---

### Order Management

#### GET /orders

List user's orders with optional filtering and search.

**Query Parameters:**
```
status: string (optional) - Filter by status (PENDING, CONFIRMED, PROCESSING, READY, DELIVERED, CANCELLED)
search: string (optional) - Search by order number
```

**Response:** Renders order list with filter controls

---

#### POST /orders/{orderId}/cancel

Cancel an order. Only allowed for PENDING or CONFIRMED orders.

**Path Parameters:**
```
orderId: string - Order ID
```

**Response:** Redirects to `/orders/{orderId}` with success/error message

---

#### GET /orders/confirmation/{orderId}

Display order confirmation page after successful checkout.

**Path Parameters:**
```
orderId: string - Order ID
```

**Response:** Renders confirmation page with order summary and next steps

---

### Admin - Book Templates

#### POST /admin/books/save

Create or update a book template.

**Request Body (form-data/multipart):**
```
id: string (optional, for updates)
title: string (required)
description: string
category: string
ageGroup: string
basePrice: decimal (required)
coverImage: file (optional)
customizationOptions.allowTitleCustomization: boolean
customizationOptions.allowCoverCustomization: boolean
customizationOptions.allowDedication: boolean
characterSlots[0].slotName: string
characterSlots[0].placeholder: string
characterSlots[0].defaultName: string
pages[0].pageNumber: integer
pages[0].content: string
pages[0].imagePosition: string (TOP|BOTTOM|LEFT|RIGHT|FULL)
```

---

### Admin - Orders

#### POST /admin/orders/{id}/status

Update order status.

**Path Parameters:**
```
id: string - Order ID
```

**Request Body (form-data):**
```
status: string (PENDING|CONFIRMED|PROCESSING|READY|SHIPPED|DELIVERED|CANCELLED)
```

---

## Error Responses

### 401 Unauthorized
User is not authenticated. Redirects to login page.

### 403 Forbidden
User does not have permission. Redirects to access-denied page.

### 404 Not Found
Resource not found. Shows error page.

### 500 Internal Server Error
Server error. Shows error page with message.

---

## Data Models

### User Registration DTO
```java
{
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  confirmPassword: String,
  phone: String
}
```

### Book Customization DTO
```java
{
  bookTemplateId: String,
  customTitle: String,
  dedication: String,
  characterCustomizations: [
    {
      slotId: String,
      customName: String,
      selectedImageId: String,
      customImageUrl: String
    }
  ],
  customCoverImageUrl: String
}
```

### Shipping Address
```java
{
  fullName: String,
  street: String,
  city: String,
  state: String,
  zipCode: String,
  country: String,
  phone: String
}
```

### Profile Update DTO
```java
{
  firstName: String (required),
  lastName: String (required),
  phone: String (optional)
}
```

### Change Password DTO
```java
{
  currentPassword: String (required),
  newPassword: String (required, min 6 chars),
  confirmPassword: String (required, must match newPassword)
}
```

### Address DTO
```java
{
  label: String (optional, e.g., "Home", "Work"),
  street: String (required),
  city: String (required),
  state: String (required),
  zipCode: String (required),
  country: String (required),
  isDefault: Boolean (optional)
}
```

---

## Rate Limiting

Currently, no rate limiting is implemented. Consider adding rate limiting for production deployment.

## CORS

CORS is not configured as the application uses server-side rendering. For API-only endpoints, configure CORS appropriately.

---

*Last updated: November 23, 2025 - Phase 7 (Profile & Order Enhancements)*
