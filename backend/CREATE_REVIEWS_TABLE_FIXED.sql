-- Исправленная версия создания таблицы route_reviews
-- user_id должен быть UUID, чтобы соответствовать users.id

CREATE TABLE IF NOT EXISTS route_reviews (
    id SERIAL PRIMARY KEY,
    route_id VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    visited_attractions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_route_reviews_route_id ON route_reviews(route_id);
CREATE INDEX IF NOT EXISTS idx_route_reviews_user_id ON route_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_route_reviews_created_at ON route_reviews(created_at DESC);
