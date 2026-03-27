-- Seed admin user (password: Admin@123)
INSERT INTO users (id, name, email, phone, password, role, email_verified, active) VALUES
('admin-001', 'Admin User', 'admin@markethub.com', '9999999999', '$2a$12$LJ3mRGnGKiQ0t6VCTxouZOmBHx6X5L6Fp.7lnOaK5sYyH3SFiqlUW', 'ADMIN', true, true);

-- Seed email templates
INSERT INTO email_templates (id, name, subject, body) VALUES
('tpl-welcome', 'Welcome Email', 'Welcome to MarketHub!', '<h1>Welcome {{name}}</h1><p>Thanks for joining MarketHub.</p>'),
('tpl-order-confirm', 'Order Confirmation', 'Order #{{orderId}} Confirmed', '<h1>Order Confirmed</h1><p>Your order has been placed.</p>'),
('tpl-password-reset', 'Password Reset', 'Reset your password', '<h1>Password Reset</h1><p>Click <a href="{{link}}">here</a> to reset.</p>'),
('tpl-vendor-approved', 'Vendor Approved', 'Your store is live!', '<h1>Congratulations!</h1><p>Your vendor application has been approved.</p>');

-- Seed categories
INSERT INTO categories (id, name, slug, active) VALUES
('cat-electronics', 'Electronics', 'electronics', true),
('cat-fashion', 'Fashion', 'fashion', true),
('cat-home', 'Home & Living', 'home-living', true),
('cat-books', 'Books', 'books', true),
('cat-sports', 'Sports', 'sports', true);

-- Default commission config
INSERT INTO commission_config (id, category, rate) VALUES
('comm-default', 'default', 10.0),
('comm-electronics', 'Electronics', 8.0),
('comm-fashion', 'Fashion', 15.0);

-- Default platform settings
INSERT INTO platform_settings (id, settings) VALUES
('settings-1', '{"siteName": "MarketHub", "supportEmail": "support@markethub.com", "currency": "INR", "taxRate": 18, "freeShippingAbove": 499}');
