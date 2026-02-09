-- Таблицы для системы уведомлений

-- Таблица регистрации устройств
CREATE TABLE IF NOT EXISTS device_registrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(255) NOT NULL,
    platform VARCHAR(50) DEFAULT 'android',
    app_version VARCHAR(50) NOT NULL,
    os_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, device_id)
);

CREATE INDEX IF NOT EXISTS idx_device_registrations_user_id ON device_registrations(user_id);
CREATE INDEX IF NOT EXISTS idx_device_registrations_device_id ON device_registrations(device_id);

-- Таблица уведомлений
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    data TEXT, -- JSON строка с дополнительными данными
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id_read_created ON notifications(user_id, read, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(read);

-- Комментарии к таблицам
COMMENT ON TABLE device_registrations IS 'Регистрация устройств для получения push-уведомлений';
COMMENT ON TABLE notifications IS 'Уведомления для пользователей';

-- Проверка таблиц
\dt device_registrations
\dt notifications
