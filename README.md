# MarketHub E-Commerce Backend

Spring Boot 3.2 + PostgreSQL backend for the MarketHub multi-vendor marketplace.

## Quick Start

### With Docker Compose
```bash
docker-compose up -d
```
Backend runs at `http://localhost:8080/api/v1`

### Manual Setup
1. Install Java 17+ and PostgreSQL 16+
2. Create database: `createdb markethub`
3. Set environment variables (see application.yml)
4. Run: `./gradlew bootRun`

## API Endpoints (90+)

### Auth (9 endpoints)
- POST /auth/login, /auth/register, /auth/refresh, /auth/logout
- POST /auth/logout-all, /auth/forgot-password, /auth/reset-password, /auth/verify-email
- GET /auth/me

### Products (10 endpoints)
- GET /products, /products/{id}, /products/slug/{slug}
- GET /products/categories, /products/brands, /products/search
- GET /products/featured, /products/trending, /products/deals
- GET /products/{id}/related, /products/{productId}/reviews

### Cart (7 endpoints)
- GET /cart
- POST /cart/items, /cart/validate-coupon, /cart/shipping
- PUT /cart/items/{id}
- DELETE /cart/items/{id}, /cart/clear

### Orders (6 endpoints)
- GET /orders, /orders/{id}, /orders/{id}/track
- POST /orders, /orders/{id}/return
- PATCH /orders/{id}/cancel, /orders/{id}

### Reviews (3 endpoints)
- GET /products/{productId}/reviews
- POST /reviews
- PATCH /reviews/{id}/helpful

### Wishlist (2 endpoints)
- GET /wishlist
- POST /wishlist/toggle

### User (5 endpoints)
- GET /users/profile, /users/addresses
- PUT /users/profile, /users/addresses/{id}
- POST /users/addresses
- DELETE /users/addresses/{id}
- PATCH /users/addresses/{id}/default

### Vendor (20 endpoints)
- GET/PUT /vendor/profile
- CRUD /vendor/products
- GET/PATCH /vendor/orders
- GET /vendor/analytics, /vendor/financials
- CRUD /vendor/coupons
- GET/PATCH /vendor/inventory
- GET /vendor/inventory/low-stock
- GET/PATCH /vendor/returns
- GET/PUT /vendor/shipping-settings
- POST /vendor/onboarding
- GET/PUT /vendor/store-customization
- GET /vendor/payouts
- POST /vendor/products/bulk-upload
- GET /vendors/{id}, /vendors/{id}/products

### Admin (35+ endpoints)
- Users, Vendors, Products, Orders management
- Categories CRUD, Coupons CRUD
- CMS Banners/Sections/Pages
- Commission config & overrides
- Fraud monitoring & actions
- Email template management
- Platform settings
- Audit log
- Vendor application approval

### Notifications (4 endpoints)
- GET /notifications, /notifications/unread-count
- PATCH /notifications/{id}/read, /notifications/read-all

## Security
- JWT access tokens (15min) + httpOnly cookie refresh tokens (7d)
- SHA-256 hashed refresh tokens in DB
- Token rotation on every refresh
- Account lockout after 5 failed attempts (30min)
- BCrypt-12 password hashing
- Role-based access (CUSTOMER, VENDOR, ADMIN)

## Default Admin Login
- Email: admin@markethub.com
- Password: Admin@123
