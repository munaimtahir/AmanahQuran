#!/usr/bin/env python3
import os
import sys
import sqlite3
import unicodedata

def main():
    db_path = "apps/android/app/src/main/assets/database/amanah_quran_content_v1_candidate.sqlite"
    if not os.path.exists(db_path):
        db_path = os.path.join(os.path.dirname(__file__), "../", db_path)
    
    if not os.path.exists(db_path):
        print(f"Error: Database not found at {db_path}")
        sys.exit(1)

    print(f"Reading database: {db_path}")
    
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Query IndoPak texts joined with ayah details
    query = """
        SELECT qt.display_text, a.ayah_key, a.page_number
        FROM quran_texts qt
        JOIN ayahs a ON a.ayah_key = qt.ayah_key
        WHERE qt.script_type = 'INDOPAK'
    """
    
    try:
        cursor.execute(query)
        rows = cursor.fetchall()
    except Exception as e:
        print(f"Error querying database: {e}")
        conn.close()
        sys.exit(1)
        
    conn.close()

    # Analyze characters
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

    # Check font file if specified in command arguments
    font_path = sys.argv[1] if len(sys.argv) > 1 else None
    cmap_supported = set()
    cmap_check_run = False
    
    if font_path:
        if not os.path.exists(font_path):
            print(f"Warning: Font file not found at {font_path}")
        else:
            try:
                from fontTools.ttLib import TTFont
                print(f"Reading font file: {font_path}")
                font = TTFont(font_path)
                # Gather all code points supported in cmaps
                for table in font['cmap'].tables:
                    for cp in table.cmap.keys():
                        cmap_supported.add(cp)
                cmap_check_run = True
            except ImportError:
                print("Warning: fontTools library not found. Font cmap check could not run.")
            except Exception as e:
                print(f"Warning: Could not read font file: {e}")

    # Write report
    report_dir = "build/reports"
    os.makedirs(report_dir, exist_ok=True)
    report_path = os.path.join(report_dir, "indopak_glyph_coverage_report.txt")
    
    unsupported_count = 0
    with open(report_path, "w", encoding="utf-8") as f:
        f.write("=== INDOPAK GLYPH COVERAGE REPORT ===\n")
        if font_path:
            f.write(f"Audited Font: {font_path}\n")
            f.write(f"Cmap Check Run: {'YES' if cmap_check_run else 'NO'}\n")
        else:
            f.write("Audited Font: None provided\n")
            f.write("Cmap Check Run: NO\n")
        f.write(f"Total Unique Characters Found in IndoPak text: {len(char_data)}\n\n")
        
        f.write(f"{'CodePoint':<10} | {'Char':<4} | {'Count':<6} | {'Example Ayah':<12} | {'Page':<4} | {'Name':<35} | {'Supported' if cmap_check_run else ''}\n")
        f.write("-" * 100 + "\n")
        
        for cp in sorted(char_data.keys()):
            data = char_data[cp]
            char = data['char']
            count = data['count']
            ayah = data['example_ayah']
            page = data['example_page']
            
            try:
                name = unicodedata.name(char)
            except ValueError:
                name = "<UNKNOWN>"
                
            supported_str = ""
            if cmap_check_run:
                is_supported = cp in cmap_supported
                is_control = unicodedata.category(char) in ('Cf', 'Zs', 'Cc') or cp == 0x202E
                if is_supported or is_control:
                    supported_str = "YES"
                else:
                    supported_str = "NO"
                    unsupported_count += 1
            
            # Print control characters/spaces nicely
            char_display = char if cp > 32 and unicodedata.category(char)[0] != 'C' else f"[{cp}]"
            cp_str = f"U+{cp:04X}"
            f.write(f"{cp_str:<10} | {char_display:<4} | {count:<6} | {ayah:<12} | {page:<4} | {name:<35} | {supported_str}\n")
            
        if cmap_check_run:
            f.write(f"\nTotal Unsupported Characters: {unsupported_count}\n")
            
    print(f"Report written to: {report_path}")
    if cmap_check_run and unsupported_count > 0:
        print(f"BLOCKED: Font lacks {unsupported_count} glyphs used in display text.")
        sys.exit(1)
    sys.exit(0)

if __name__ == "__main__":
    main()
