# Руководство по развертыванию бэкенда на сервере

Пошаговая инструкция по развертыванию Ktor бэкенда на Yandex Cloud VM.

---

## 📋 Предварительные требования

- ✅ PostgreSQL установлен и настроен
- ✅ Java JDK 17 установлен
- ✅ База данных `trust_the_route` создана
- ✅ Пользователь `trust_user` создан

---

## 🗄️ Шаг 1: Создать таблицу в базе данных

Подключитесь к PostgreSQL и выполните:

```bash
psql -U trust_user -d trust_the_route -h localhost
```

Выполните SQL команду для создания таблицы:

```sql
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Проверить таблицу
\dt

-- Выйти
\q
```

---

## 📦 Шаг 2: Загрузить проект на сервер

### Вариант 1: Через Git (рекомендуется)

```bash
# На сервере
cd ~
git clone <ваш_репозиторий> trust-the-route-backend
cd trust-the-route-backend/backend
```

### Вариант 2: Через SCP (если проект локально)

```bash
# На вашем компьютере (PowerShell)
scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 -r backend ubuntu@158.160.180.232:~/trust-the-route-backend/
```

### Вариант 3: Создать проект на сервере

```bash
# На сервере
mkdir -p ~/trust-the-route-backend/backend
cd ~/trust-the-route-backend/backend
# Скопировать файлы вручную
```

---

## ⚙️ Шаг 3: Настроить переменные окружения

Создайте файл `.env` или установите переменные окружения:

```bash
# На сервере
cd ~/trust-the-route-backend/backend

# Создать файл с переменными окружения
nano .env
```

**Содержимое `.env`:**
```
DB_PASSWORD=ваш_пароль_от_базы_данных
JWT_SECRET=ваш_секретный_ключ_минимум_32_символа_длинный
```

**Или установить переменные окружения:**
```bash
export DB_PASSWORD="ваш_пароль_от_базы_данных"
export JWT_SECRET="ваш_секретный_ключ_минимум_32_символа_длинный"
```

---

## 🔧 Шаг 4: Установить Gradle (если нужно)

```bash
# Проверить, установлен ли Gradle
gradle --version

# Если не установлен, установить через SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.5
```

---

## 🏗️ Шаг 5: Собрать проект

```bash
cd ~/trust-the-route-backend/backend

# Собрать проект
./gradlew build

# Или если Gradle не установлен глобально
chmod +x gradlew
./gradlew build
```

---

## 🚀 Шаг 6: Запустить приложение

### Вариант 1: Запуск через Gradle (для разработки)

```bash
./gradlew run
```

### Вариант 2: Запуск JAR файла (для production)

```bash
# Собрать JAR
./gradlew build

# Запустить
java -jar build/libs/trust-the-route-backend-1.0.0.jar
```

### Вариант 3: Запуск в фоновом режиме (screen/tmux)

```bash
# Установить screen
sudo apt install screen -y

# Запустить screen
screen -S backend

# Запустить приложение
./gradlew run

# Отключиться от screen (Ctrl+A, затем D)

# Вернуться к screen
screen -r backend
```

---

## 🧪 Шаг 7: Проверить работу API

```bash
# Проверить, что сервер запущен
curl http://localhost:8080/api/v1/auth/register

# Или проверить через браузер
# Откройте: http://158.160.180.232:8080/api/v1/auth/register
```

---

## 🔒 Шаг 8: Настроить Firewall

Убедитесь, что порт 8080 открыт:

```bash
sudo ufw allow 8080
sudo ufw status
```

---

## 🌐 Шаг 9: Настроить Nginx (опционально, для production)

### Установить Nginx

```bash
sudo apt install nginx -y
```

### Создать конфигурацию

```bash
sudo nano /etc/nginx/sites-available/trust-the-route-backend
```

**Содержимое:**
```nginx
server {
    listen 80;
    server_name api.trusttheroute.com;  # Замените на ваш домен

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Активировать конфигурацию

```bash
sudo ln -s /etc/nginx/sites-available/trust-the-route-backend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 🔐 Шаг 10: Настроить SSL (Let's Encrypt)

```bash
# Установить Certbot
sudo apt install certbot python3-certbot-nginx -y

# Получить сертификат
sudo certbot --nginx -d api.trusttheroute.com
```

---

## 📝 Шаг 11: Настроить автозапуск (systemd)

Создать сервис для автозапуска:

```bash
sudo nano /etc/systemd/system/trust-the-route-backend.service
```

**Содержимое:**
```ini
[Unit]
Description=Trust The Route Backend API
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/trust-the-route-backend/backend
Environment="DB_PASSWORD=ваш_пароль"
Environment="JWT_SECRET=ваш_секретный_ключ"
ExecStart=/usr/bin/java -jar /home/ubuntu/trust-the-route-backend/backend/build/libs/trust-the-route-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Активировать сервис:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable trust-the-route-backend
sudo systemctl start trust-the-route-backend
sudo systemctl status trust-the-route-backend
```

---

## ✅ Чеклист развертывания

- [ ] Таблица `users` создана в базе данных
- [ ] Проект загружен на сервер
- [ ] Переменные окружения настроены
- [ ] Проект собран (`./gradlew build`)
- [ ] Приложение запущено и работает
- [ ] Firewall настроен (порт 8080 открыт)
- [ ] Nginx настроен (опционально)
- [ ] SSL сертификат установлен (опционально)
- [ ] Автозапуск настроен (опционально)

---

## 🐛 Решение проблем

### Проблема: Приложение не запускается

```bash
# Проверить логи
./gradlew run --info

# Проверить, что порт не занят
sudo netstat -tulpn | grep 8080
```

### Проблема: Ошибка подключения к базе данных

```bash
# Проверить, что PostgreSQL запущен
sudo systemctl status postgresql

# Проверить подключение
psql -U trust_user -d trust_the_route -h localhost
```

### Проблема: Ошибка компиляции

```bash
# Очистить и пересобрать
./gradlew clean build
```

---

**После выполнения всех шагов ваш бэкенд будет готов к работе!**
