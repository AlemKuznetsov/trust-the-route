#!/usr/bin/env python3
"""
Скрипт для загрузки медиафайлов в Yandex Object Storage
Требуется: pip install boto3
"""

import os
import boto3
from pathlib import Path
from botocore.config import Config

# Конфигурация
BUCKET_NAME = "trust-the-route-media"
ENDPOINT_URL = "https://storage.yandexcloud.net"
REGION = "ru-central1"

# Учетные данные (получите из Yandex Cloud Console)
AWS_ACCESS_KEY_ID = "YOUR_ACCESS_KEY"
AWS_SECRET_ACCESS_KEY = "YOUR_SECRET_KEY"

# Пути к файлам
ASSETS_DIR = Path("app/src/main/assets")
IMAGES_DIR = ASSETS_DIR / "images"
AUDIO_DIR = ASSETS_DIR / "audio"

# Пути в bucket
IMAGES_PREFIX = "images/routes/bus_b"
AUDIO_PREFIX = "audio/routes/bus_b"


def upload_files(s3_client, local_dir: Path, bucket_prefix: str, file_extensions: list):
    """Загружает файлы из локальной директории в bucket"""
    uploaded_count = 0
    
    for file_path in local_dir.rglob("*"):
        if file_path.is_file() and file_path.suffix.lower() in file_extensions:
            # Относительный путь от local_dir
            relative_path = file_path.relative_to(local_dir)
            s3_key = f"{bucket_prefix}/{relative_path}".replace("\\", "/")
            
            try:
                print(f"Загрузка: {file_path} -> s3://{BUCKET_NAME}/{s3_key}")
                s3_client.upload_file(
                    str(file_path),
                    BUCKET_NAME,
                    s3_key,
                    ExtraArgs={'ContentType': get_content_type(file_path.suffix)}
                )
                uploaded_count += 1
            except Exception as e:
                print(f"Ошибка при загрузке {file_path}: {e}")
    
    return uploaded_count


def get_content_type(extension: str) -> str:
    """Определяет Content-Type по расширению файла"""
    content_types = {
        '.jpg': 'image/jpeg',
        '.jpeg': 'image/jpeg',
        '.png': 'image/png',
        '.webp': 'image/webp',
        '.mp3': 'audio/mpeg',
        '.ogg': 'audio/ogg',
        '.wav': 'audio/wav',
    }
    return content_types.get(extension.lower(), 'application/octet-stream')


def main():
    # Проверка учетных данных
    if AWS_ACCESS_KEY_ID == "YOUR_ACCESS_KEY" or AWS_SECRET_ACCESS_KEY == "YOUR_SECRET_KEY":
        print("ОШИБКА: Укажите ваши учетные данные Yandex Cloud!")
        print("Получите их в разделе 'Сервисные аккаунты' -> 'Создать ключ'")
        return
    
    # Создание S3 клиента для Yandex Object Storage
    s3_client = boto3.client(
        's3',
        endpoint_url=ENDPOINT_URL,
        aws_access_key_id=AWS_ACCESS_KEY_ID,
        aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
        region_name=REGION,
        config=Config(signature_version='s3v4')
    )
    
    # Проверка существования bucket
    try:
        s3_client.head_bucket(Bucket=BUCKET_NAME)
        print(f"✓ Bucket '{BUCKET_NAME}' найден")
    except Exception as e:
        print(f"ОШИБКА: Bucket '{BUCKET_NAME}' не найден или недоступен: {e}")
        print("Создайте bucket в Yandex Cloud Console")
        return
    
    # Загрузка изображений
    if IMAGES_DIR.exists():
        print(f"\n📸 Загрузка изображений из {IMAGES_DIR}...")
        image_count = upload_files(
            s3_client,
            IMAGES_DIR,
            IMAGES_PREFIX,
            ['.jpg', '.jpeg', '.png', '.webp']
        )
        print(f"✓ Загружено изображений: {image_count}")
    else:
        print(f"⚠ Директория изображений не найдена: {IMAGES_DIR}")
    
    # Загрузка аудио
    if AUDIO_DIR.exists():
        print(f"\n🎵 Загрузка аудио из {AUDIO_DIR}...")
        audio_count = upload_files(
            s3_client,
            AUDIO_DIR,
            AUDIO_PREFIX,
            ['.mp3', '.ogg', '.wav']
        )
        print(f"✓ Загружено аудиофайлов: {audio_count}")
    else:
        print(f"⚠ Директория аудио не найдена: {AUDIO_DIR}")
    
    print("\n✅ Загрузка завершена!")
    print(f"\nURL для доступа к файлам:")
    print(f"https://storage.yandexcloud.net/{BUCKET_NAME}/{IMAGES_PREFIX}/")
    print(f"https://storage.yandexcloud.net/{BUCKET_NAME}/{AUDIO_PREFIX}/")


if __name__ == "__main__":
    main()
