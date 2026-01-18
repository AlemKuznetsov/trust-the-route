#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для генерации polyline из координат остановок
Использует Google Polyline Encoding Algorithm
"""

def encode_value(value):
    """Кодирует одно значение (широта или долгота)"""
    value = value << 1 if value >= 0 else (~value << 1)
    encoded = []
    while value >= 0x20:
        encoded.append(chr((0x20 | (value & 0x1F)) + 63))
        value >>= 5
    encoded.append(chr(value + 63))
    return ''.join(encoded)

def encode_polyline(points):
    """Кодирует список точек в polyline строку"""
    if not points:
        return ""
    
    encoded = []
    prev_lat = 0
    prev_lng = 0
    
    for lat, lng in points:
        lat_int = int(round(lat * 1e5))
        lng_int = int(round(lng * 1e5))
        
        delta_lat = lat_int - prev_lat
        delta_lng = lng_int - prev_lng
        
        encoded.append(encode_value(delta_lat))
        encoded.append(encode_value(delta_lng))
        
        prev_lat = lat_int
        prev_lng = lng_int
    
    return ''.join(encoded)

# Координаты остановок маршрута (40 остановок)
coordinates = [
    (55.732433, 37.604147),
    (55.736000, 37.593292),
    (55.737558, 37.588918),
    (55.739299, 37.585948),
    (55.739681, 37.585719),
    (55.742225, 37.584614),
    (55.748863, 37.582905),
    (55.748822, 37.582942),
    (55.756055, 37.584268),
    (55.758010, 37.584645),
    (55.759931, 37.585449),
    (55.762794, 37.587616),
    (55.765386, 37.590267),
    (55.766863, 37.592351),
    (55.771555, 37.602069),
    (55.772510, 37.605435),
    (55.773074, 37.609845),
    (55.773311, 37.613219),
    (55.773462, 37.619195),
    (55.773307, 37.627411),
    (55.772501, 37.633175),
    (55.771737, 37.638331),
    (55.769702, 37.644988),
    (55.768665, 37.648885),
    (55.766275, 37.652770),
    (55.763624, 37.656170),
    (55.760596, 37.657232),
    (55.758076, 37.657364),
    (55.754167, 37.656417),
    (55.751072, 37.655585),
    (55.748760, 37.655348),
    (55.746711, 37.655051),
    (55.741068, 37.652698),
    (55.738799, 37.649651),
    (55.732804, 37.640227),
    (55.731279, 37.637030),
    (55.730499, 37.630525),
    (55.729921, 37.624549),
    (55.730124, 37.619145),
    (55.730332, 37.613166),
    (55.732287, 37.604444)
]

# Генерируем polyline
polyline = encode_polyline(coordinates)
print(f"Polyline: {polyline}")
print(f"\nДлина polyline: {len(polyline)} символов")
print(f"Количество точек: {len(coordinates)}")
