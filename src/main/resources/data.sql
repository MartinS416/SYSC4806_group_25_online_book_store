-- ========================
--   BOOKS
-- ========================
INSERT INTO book (title, author, price, category, stock) VALUES
('The Hobbit', 'J.R.R. Tolkien', 14.99, 'Fantasy', 25),
('1984', 'George Orwell', 12.49, 'Dystopian', 40),
('Clean Code', 'Robert C. Martin', 32.99, 'Programming', 12),
('To Kill a Mockingbird', 'Harper Lee', 10.99, 'Classic', 18),
('Dune', 'Frank Herbert', 19.99, 'Science Fiction', 30),
('The Pragmatic Programmer', 'Andrew Hunt', 27.50, 'Programming', 15),
('Atomic Habits', 'James Clear', 16.00, 'Self-help', 20),
('The Catcher in the Rye', 'J.D. Salinger', 9.99, 'Classic', 25);

-- ========================
--   ADMIN USER
-- ========================
INSERT INTO customer (email, password, role, username)
VALUES (
  'admin@me.com',
  '$2a$12$SQ3/DdK1SotgHu54wqUTkOWFWGRQWvYxRa0YqqdwQxzqPrMV8fITe',
  'ADMIN',
  'AdminUser'
);

-- ========================
--   ADDRESS FOR ADMIN
-- ========================
INSERT INTO address (
  customer_id,
  first_name,
  last_name,
  street,
  unit,
  city,
  region,
  postcode,
  country
)
VALUES (
  1,
  'Admin',
  'User',
  '123 Main St',
  '',
  'Ottawa',
  'ON',
  'K1A0B1',
  'Canada'
);

-- ========================
--   ORDERS
--   IMPORTANT:
--   Your Order entity has NO "total" column. Only "total_amount".
-- ========================
INSERT INTO orders (
  name,
  email,
  phone,
  customer_id,
  address_id,
  status,
  total,
  created_at
)
VALUES
('Admin User', 'admin@me.com', '555-1234', 1, 1, 'NEW', 29.98, '2025-11-23T10:15:00Z'),
('Admin User', 'admin@me.com', '555-1234', 1, 1, 'NEW', 32.99, '2025-11-24T13:40:00Z'),
('Admin User', 'admin@me.com', '555-1234', 1, 1, 'NEW', 44.48, '2025-11-25T16:20:00Z'),
('Admin User', 'admin@me.com', '555-1234', 1, 1, 'NEW', 19.99, '2025-11-26T09:05:00Z');

-- ========================
--   ORDER LINES
-- ========================
INSERT INTO order_line (order_id, book_id, quantity, price) VALUES
(1, 1, 1, 14.99),
(1, 2, 1, 14.99),

(2, 3, 1, 32.99),

(3, 4, 1, 10.99),
(3, 5, 1, 33.49),

(4, 6, 1, 19.99);
