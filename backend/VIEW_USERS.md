# Просмотр зарегистрированных пользователей

## Способ 1: Через PostgreSQL (рекомендуется)

### Подключитесь к VM через Cloud Shell или SSH:

```bash
# Подключение к VM
ssh ubuntu@158.160.217.181

# Или через Cloud Shell в консоли Yandex Cloud
```

### Подключитесь к PostgreSQL:

```bash
# Подключение к базе данных
sudo -u postgres psql -d trust_the_route

# Или если нужно указать пользователя
psql -U trust_user -d trust_the_route -h localhost
```

### Просмотр пользователей:

```sql
-- Показать всех пользователей (без паролей)
SELECT id, email, name, created_at, updated_at 
FROM users 
ORDER BY created_at DESC;

-- Показать только email и имя
SELECT email, name, created_at 
FROM users 
ORDER BY created_at DESC;

-- Подсчет пользователей
SELECT COUNT(*) as total_users FROM users;
```

### Выход из PostgreSQL:

```sql
\q
```

## Способ 2: Через простой SQL запрос (без подключения к psql)

```bash
# На VM выполните:
sudo -u postgres psql -d trust_the_route -c "SELECT id, email, name, created_at FROM users ORDER BY created_at DESC;"
```

## Способ 3: Создать endpoint в backend (для удобства)

Если хотите, могу добавить endpoint `/api/v1/auth/users` для просмотра списка пользователей (только для администратора или с аутентификацией).

## Пример вывода:

```
                id                 |        email         |    name     |      created_at      
-----------------------------------+-----------------------+-------------+---------------------
 f60ab7a0-e537-468d-846b-4eedb9... | test@example.com      | Test User   | 2026-01-30 00:35:47
```
