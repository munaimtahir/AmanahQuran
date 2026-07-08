#!/usr/bin/env python3
import os
import sys
import sqlite3
import unicodedata

def load_supported_codepoints(font_path):
    from fontTools.ttLib import TTFont
    font = TTFont(font_path)
    cmap_supported = set()
    for table in font['cmap'].tables:
        for cp in table.cmap.keys():
            cmap_supported.add(cp)
    return cmap_supported


def check_font_coverage(db_path, script_type, font_paths, report_path):
    if not os.path.exists(db_path):
        print(f"Error: Database not found at {db_path}")
        return False

    if isinstance(font_paths, str):
        font_paths = [font_paths]

    for font_path in font_paths:
        if not os.path.exists(font_path):
            print(f"Error: Font file not found at {font_path}")
            return False

    print(f"Auditing glyph coverage: scriptType={script_type}, fonts={', '.join(font_paths)}")

    # Load font cmap
    try:
        font_support = []
        for font_path in font_paths:
            font_support.append((font_path, load_supported_codepoints(font_path)))
        cmap_supported = set()
        for _, supported in font_support:
            cmap_supported.update(supported)
    except ImportError:
        print("fontTools unavailable. Cannot verify font cmap coverage.")
        sys.exit(1)
    except Exception as e:
        print(f"Error reading font file(s) {font_paths}: {e}")
        return False

    # Connect database
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    query = """
        SELECT qt.display_text, a.ayah_key, a.page_number
        FROM quran_texts qt
        JOIN ayahs a ON a.ayah_key = qt.ayah_key
        WHERE qt.script_type = ?
    """
    
    try:
        cursor.execute(query, (script_type,))
        rows = cursor.fetchall()
    except Exception as e:
        print(f"Error querying database: {e}")
        conn.close()
        return False
        
    conn.close()

    # Collect characters
    char_data = {}
    for text, ayah_key, page_number in rows:
        if not text:
            continue
        for char in text:
            code_point = ord(char)
            if code_point not in char_data:
                char_data[code_point] = {
                    'char': char,
                    'count': 0,
                    'example_ayah': ayah_key,
                    'example_page': page_number
                }
            char_data[code_point]['count'] += 1

    unsupported_count = 0
    os.makedirs(os.path.dirname(report_path), exist_ok=True)
    
    with open(report_path, "w", encoding="utf-8") as f:
        f.write(f"=== {script_type} GLYPH COVERAGE REPORT ===\n")
        f.write("Fonts:\n")
        for font_path in font_paths:
            f.write(f"- {font_path}\n")
        f.write(f"Total Unique Quran Code Points: {len(char_data)}\n")
        
        f.write(f"\n{'CodePoint':<10} | {'Char':<4} | {'Count':<6} | {'Example':<10} | {'Page':<4} | {'Name':<35} | {'Supported'} | {'Font(s)'}\n")
        f.write("-" * 110 + "\n")
        
        for cp in sorted(char_data.keys()):
            data = char_data[cp]
            char = data['char']
            category = unicodedata.category(char)
            
            supporting_fonts = [os.path.basename(path) for path, supported in font_support if cp in supported]
            is_supported = len(supporting_fonts) > 0
            is_control = category in ('Cf', 'Zs', 'Cc') or cp == 0x202E

            if is_supported or is_control:
                supported_str = "YES"
            else:
                supported_str = "NO"
                unsupported_count += 1
                
            try:
                name = unicodedata.name(char)
            except ValueError:
                name = "<UNKNOWN>"
                
            char_display = char if cp > 32 and category[0] != 'C' else f"[{cp}]"
            cp_str = f"U+{cp:04X}"
            support_note = ",".join(supporting_fonts) if supporting_fonts else "-"
            f.write(f"{cp_str:<10} | {char_display:<4} | {data['count']:<6} | {data['example_ayah']:<10} | {data['example_page']:<4} | {name:<35} | {supported_str} | {support_note}\n")
            
        f.write(f"\nTotal Unsupported Characters: {unsupported_count}\n")
        
    print(f"Report written to: {report_path}")
    if unsupported_count > 0:
        print(f"BLOCKED: {script_type} font lacks {unsupported_count} glyphs used in display text.")
        return False
        
    print(f"{script_type} glyph coverage check passed successfully.")
    return True

def main():
    db_path = "apps/android/app/src/main/assets/database/quran.db"
    if not os.path.exists(db_path):
        db_path = os.path.join(os.path.dirname(__file__), "../", db_path)
    if not os.path.exists(db_path):
        legacy_db_path = "apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite"
        if os.path.exists(legacy_db_path):
            db_path = legacy_db_path
        else:
            db_path = os.path.join(os.path.dirname(__file__), "../", legacy_db_path)

    # 1. Audit IndoPak
    indopak_font = "apps/android/app/src/main/res/font/digital_khatt_indopak.otf"
    if not os.path.exists(indopak_font):
        indopak_font = os.path.join(os.path.dirname(__file__), "../", indopak_font)
    
    indopak_report = "build/reports/indopak_glyph_coverage_report.txt"
    if not os.path.isabs(indopak_report):
        indopak_report = os.path.join(os.path.dirname(__file__), "../", indopak_report)

    indopak_ok = check_font_coverage(db_path, "INDOPAK", [indopak_font], indopak_report)

    # 2. Audit Uthmani
    uthmani_font_primary = "apps/android/app/src/main/res/font/digital_khatt_v2.otf"
    uthmani_font_fallback = "apps/android/app/src/main/res/font/indopak_nastaleeq.ttf"
    if not os.path.exists(uthmani_font_primary):
        uthmani_font_primary = os.path.join(os.path.dirname(__file__), "../", uthmani_font_primary)
    if not os.path.exists(uthmani_font_fallback):
        uthmani_font_fallback = os.path.join(os.path.dirname(__file__), "../", uthmani_font_fallback)

    uthmani_report = "build/reports/uthmani_glyph_coverage_report.txt"
    if not os.path.isabs(uthmani_report):
        uthmani_report = os.path.join(os.path.dirname(__file__), "../", uthmani_report)

    uthmani_ok = check_font_coverage(db_path, "UTHMANI", [uthmani_font_primary, uthmani_font_fallback], uthmani_report)

    if not indopak_ok or not uthmani_ok:
        sys.exit(1)
        
    sys.exit(0)

if __name__ == "__main__":
    main()
