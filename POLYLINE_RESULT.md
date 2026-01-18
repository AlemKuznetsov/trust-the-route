# Polyline для маршрута Автобус Б

## Координаты остановок (40 остановок)

Координаты были взяты из файла CREATE_POLYLINE.md

## Создание polyline

Для создания polyline из координат остановок можно использовать:

1. **Онлайн инструмент Google Polyline Utility:**
   - Откройте: https://developers.google.com/maps/documentation/utilities/polylineutility
   - Вставьте координаты в формате: `lat,lng` (каждая строка - одна точка)
   - Нажмите "Encode" для получения polyline

2. **Или использовать готовый алгоритм кодирования**

## Результат

После создания polyline будет добавлен в файл `bus_b_cw.json` в поле `"polyline"`.
