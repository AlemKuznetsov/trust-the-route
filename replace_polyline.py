#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import json
import re

file_path = 'app/src/main/assets/routes/bus_b_cw.json'
new_polyline = 'ufdsI}podFiUzbAwHhZ{IpQkAl@}N|Emh@tIFGgl@iGeKkA_K_D{PqLgOqOeH_Lk\\w{@}DaToBqZo@aT]kd@\\ir@`Dac@vCe_@vKsh@lEkW|MgWrOgTzQsEvNYlWzDjRdDlMn@xKz@fb@tMdM`Rnd@zy@nH~RzCrg@rBjd@g@v`@i@jd@gKpu@'

# Читаем файл
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Заменяем polyline используя регулярное выражение
pattern = r'"polyline":\s*"[^"]+"'
replacement = f'"polyline": "{new_polyline}"'
content = re.sub(pattern, replacement, content)

# Записываем обратно
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Polyline успешно заменен!")
