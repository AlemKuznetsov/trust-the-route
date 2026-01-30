-- Создание таблицы users для Trust The Route Backend
-- Выполните этот скрипт в PostgreSQL

-- Подключиться к базе данных
-- psql -U trust_user -d trust_the_route -h localhost

-- Создать таблицу users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создать индекс для быстрого поиска по email
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Проверить таблицу
\dt

-- Показать структуру таблицы
\d users
