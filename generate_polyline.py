#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для генерации polyline из координат достопримечательностей
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
        lat_int = int(lat * 1e5)
        lng_int = int(lng * 1e5)
        
        delta_lat = lat_int - prev_lat
        delta_lng = lng_int - prev_lng
        
        encoded.append(encode_value(delta_lat))
        encoded.append(encode_value(delta_lng))
        
        prev_lat = lat_int
        prev_lng = lng_int
    
    return ''.join(encoded)

# Координаты достопримечательностей в порядке следования (order 1-18)
coordinates = [
    (55.732557, 37.604571),  # attraction_1 - Новая Третьяковка
    (55.734102, 37.599051),  # attraction_2 - Пётр I
    (55.746119, 37.583316),  # attraction_3 - МИД РФ
    (55.755729, 37.582960),  # attraction_4 - Посольство США и Дом-музей Шаляпина
    (55.759238, 37.580256),  # attraction_5 - Высотка на Кудринской площади
    (55.759927, 37.585432),  # attraction_6 - Московский планетарий
    (55.766862, 37.592374),  # attraction_7 - Музей Булгакова
    (55.768639, 37.595068),  # attraction_8 - Театр сатиры
    (55.772545, 37.605475),  # attraction_9 - Оружейный, Бизнес-центр
    (55.773127, 37.610253),  # attraction_10 - Всероссийский музей декоративного искусства
    (55.773295, 37.613249),  # attraction_11 - Театр кукол имени Образцова
    (55.773515, 37.619266),  # attraction_12 - Памятник Сергею Образцову
    (55.772581, 37.632677),  # attraction_13 - Институт Склифосовского
    (55.769100, 37.647387),  # attraction_14 - Высотное здание у Красных ворот
    (55.753951, 37.656385),  # attraction_15 - Усадьба Усачёвых — Найдёновых
    (55.750482, 37.655740),  # attraction_16 - Высотное здание на Котельнической набережной
    (55.740974, 37.652649),  # attraction_17 - Бункер-42
    (55.731392, 37.637234)   # attraction_18 - Павелецкий вокзал
]

# Генерируем polyline
polyline = encode_polyline(coordinates)
print(f"Polyline: {polyline}")
print(f"\nДлина polyline: {len(polyline)} символов")
