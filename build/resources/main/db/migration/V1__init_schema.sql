-- MarketHub Database Schema
-- All tables for the e-commerce platform

CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(512),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    active BOOLEAN DEFAULT true,
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    email_verification_token VARCHAR(255),
    email_verified BOOLEAN DEFAULT false,
    reset_password_token VARCHAR(255),
    reset_password_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(512) NOT NULL,
    device_info VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    slug VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    original_price DOUBLE PRECISION DEFAULT 0,
    discount INT DEFAULT 0,
    category VARCHAR(100),
    subcategory VARCHAR(100),
    brand VARCHAR(100),
    images JSONB DEFAULT '[]',
    rating DOUBLE PRECISION DEFAULT 0,
    review_count INT DEFAULT 0,
    vendor_id VARCHAR(255),
    vendor_name VARCHAR(255),
    stock INT DEFAULT 0,
    stock_status VARCHAR(20) DEFAULT 'IN_STOCK',
    tags JSONB DEFAULT '[]',
    featured BOOLEAN DEFAULT false,
    trending BOOLEAN DEFAULT false,
    specifications JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_products_vendor ON products(vendor_id);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_slug ON products(slug);

CREATE TABLE cart_items (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255),
    product_image VARCHAR(512),
    vendor_id VARCHAR(255),
    vendor_name VARCHAR(255),
    price DOUBLE PRECISION DEFAULT 0,
    original_price DOUBLE PRECISION DEFAULT 0,
    quantity INT DEFAULT 1,
    stock INT DEFAULT 0,
    variant VARCHAR(255)
);
CREATE INDEX idx_cart_user ON cart_items(user_id);

CREATE TABLE orders (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id),
    status VARCHAR(30) DEFAULT 'PLACED',
    items JSONB DEFAULT '[]',
    total DOUBLE PRECISION DEFAULT 0,
    tax DOUBLE PRECISION DEFAULT 0,
    shipping DOUBLE PRECISION DEFAULT 0,
    discount DOUBLE PRECISION DEFAULT 0,
    grand_total DOUBLE PRECISION DEFAULT 0,
    payment_method VARCHAR(50),
    shipping_address_id VARCHAR(255),
    coupon_code VARCHAR(50),
    timeline JSONB DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_orders_user ON orders(user_id);

CREATE TABLE reviews (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    user_name VARCHAR(255),
    rating INT NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    helpful INT DEFAULT 0,
    verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_reviews_product ON reviews(product_id);

CREATE TABLE wishlist_items (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    UNIQUE(user_id, product_id)
);

CREATE TABLE addresses (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255),
    phone VARCHAR(20),
    line1 VARCHAR(500),
    line2 VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    label VARCHAR(20) DEFAULT 'HOME',
    is_default BOOLEAN DEFAULT false
);
CREATE INDEX idx_addresses_user ON addresses(user_id);

CREATE TABLE notifications (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(255),
    message TEXT,
    read BOOLEAN DEFAULT false,
    action_url VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_notifications_user ON notifications(user_id);

CREATE TABLE vendors (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL REFERENCES users(id),
    store_name VARCHAR(255),
    description TEXT,
    logo VARCHAR(512),
    banner VARCHAR(512),
    category VARCHAR(100),
    rating DOUBLE PRECISION DEFAULT 0,
    total_products INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE coupons (
    id VARCHAR(255) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DOUBLE PRECISION NOT NULL,
    min_order DOUBLE PRECISION DEFAULT 0,
    max_discount DOUBLE PRECISION,
    label VARCHAR(255),
    vendor_id VARCHAR(255),
    active BOOLEAN DEFAULT true,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    image VARCHAR(512),
    parent_id VARCHAR(255),
    sort_order INT DEFAULT 0,
    active BOOLEAN DEFAULT true
);

CREATE TABLE banners (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    subtitle VARCHAR(255),
    image VARCHAR(512),
    link VARCHAR(512),
    sort_order INT DEFAULT 0,
    active BOOLEAN DEFAULT true
);

CREATE TABLE cms_sections (
    id VARCHAR(255) PRIMARY KEY,
    key VARCHAR(100),
    title VARCHAR(255),
    type VARCHAR(50),
    config JSONB DEFAULT '{}',
    sort_order INT DEFAULT 0,
    active BOOLEAN DEFAULT true
);

CREATE TABLE cms_pages (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    slug VARCHAR(255) UNIQUE,
    content TEXT,
    published BOOLEAN DEFAULT true
);

CREATE TABLE commission_config (
    id VARCHAR(255) PRIMARY KEY,
    category VARCHAR(100),
    vendor_id VARCHAR(255),
    rate DOUBLE PRECISION NOT NULL,
    is_override BOOLEAN DEFAULT false
);

CREATE TABLE audit_log (
    id VARCHAR(255) PRIMARY KEY,
    action VARCHAR(100),
    user_id VARCHAR(255),
    user_name VARCHAR(255),
    details TEXT,
    severity VARCHAR(20) DEFAULT 'INFO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendor_applications (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    store_name VARCHAR(255),
    category VARCHAR(100),
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE platform_settings (
    id VARCHAR(255) PRIMARY KEY,
    settings JSONB DEFAULT '{}'
);

CREATE TABLE email_templates (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    subject VARCHAR(255),
    body TEXT,
    active BOOLEAN DEFAULT true
);

CREATE TABLE fraud_reports (
    id VARCHAR(255) PRIMARY KEY,
    type VARCHAR(50),
    reference_id VARCHAR(255),
    reason TEXT,
    status VARCHAR(20) DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendor_shipping_settings (
    id VARCHAR(255) PRIMARY KEY,
    vendor_id VARCHAR(255) UNIQUE NOT NULL,
    settings JSONB DEFAULT '{}'
);

CREATE TABLE vendor_store_customization (
    id VARCHAR(255) PRIMARY KEY,
    vendor_id VARCHAR(255) UNIQUE NOT NULL,
    customization JSONB DEFAULT '{}'
);

CREATE TABLE vendor_payouts (
    id VARCHAR(255) PRIMARY KEY,
    vendor_id VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
