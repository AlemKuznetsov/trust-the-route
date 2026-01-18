# Как создать Polyline из координат остановок

## Инструкция

1. Откройте онлайн инструмент Google Polyline Utility:
   https://developers.google.com/maps/documentation/utilities/polylineutility

2. Скопируйте координаты остановок из файла `polyline_coordinates_formatted.txt` или `CREATE_POLYLINE.md`

3. Вставьте координаты в поле "Decoded Polyline" в формате:
   ```
   55.732433,37.604147
   55.736000,37.593292
   ...
   ```

4. Нажмите кнопку "Encode" для получения закодированного polyline

5. Скопируйте полученную строку и вставьте в поле `"polyline"` в файле `bus_b_cw.json`

## Альтернативный способ

Используйте готовый алгоритм кодирования из файла `PolylineEncoder.kt` в проекте.
