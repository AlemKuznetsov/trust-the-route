#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Скрипт для извлечения текста из PDF файла"""

try:
    import PyPDF2
    HAS_PYPDF2 = True
except ImportError:
    HAS_PYPDF2 = False

try:
    import pdfplumber
    HAS_PDFPLUMBER = True
except ImportError:
    HAS_PDFPLUMBER = False

def extract_text_pypdf2(pdf_path):
    """Извлечение текста с помощью PyPDF2"""
    text = ""
    with open(pdf_path, 'rb') as file:
        pdf_reader = PyPDF2.PdfReader(file)
        for page_num, page in enumerate(pdf_reader.pages):
            text += f"\n--- Страница {page_num + 1} ---\n"
            text += page.extract_text()
    return text

def extract_text_pdfplumber(pdf_path):
    """Извлечение текста с помощью pdfplumber"""
    text = ""
    with pdfplumber.open(pdf_path) as pdf:
        for page_num, page in enumerate(pdf.pages):
            text += f"\n--- Страница {page_num + 1} ---\n"
            text += page.extract_text() or ""
    return text

if __name__ == "__main__":
    import sys
    pdf_path = "Пдп.pdf"
    
    if not HAS_PYPDF2 and not HAS_PDFPLUMBER:
        print("Ошибка: Необходимо установить PyPDF2 или pdfplumber")
        print("Установка: pip install PyPDF2")
        sys.exit(1)
    
    try:
        if HAS_PDFPLUMBER:
            text = extract_text_pdfplumber(pdf_path)
        else:
            text = extract_text_pypdf2(pdf_path)
        
        # Сохраняем в файл
        with open("Пдп_текст.txt", "w", encoding="utf-8") as f:
            f.write(text)
        
        print(f"Текст извлечен из {pdf_path}")
        print(f"Сохранен в Пдп_текст.txt")
        print("\n--- Начало текста ---")
        print(text[:2000])  # Первые 2000 символов
        
    except Exception as e:
        print(f"Ошибка при извлечении текста: {e}")
        sys.exit(1)
