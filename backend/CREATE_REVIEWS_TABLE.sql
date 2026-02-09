-- Создание таблицы для отзывов маршрутов
CREATE TABLE IF NOT EXISTS route_reviews (
    id SERIAL PRIMARY KEY,
    route_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    visited_attractions TEXT, -- Список ID достопримечательностей через запятую
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Индексы для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_route_reviews_route_id ON route_reviews(route_id);
CREATE INDEX IF NOT EXISTS idx_route_reviews_user_id ON route_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_route_reviews_created_at ON route_reviews(created_at DESC);

-- Комментарии к таблице
COMMENT ON TABLE route_reviews IS 'Отзывы и оценки маршрутов пользователями';
COMMENT ON COLUMN route_reviews.rating IS 'Оценка от 1 до 5';
COMMENT ON COLUMN route_reviews.visited_attractions IS 'Список ID посещенных достопримечательностей через запятую';
