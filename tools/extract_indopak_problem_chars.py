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

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
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

    char_data = {}
    for text, ayah_key, page_number in rows:
        if not text:
            continue
        for i, char in enumerate(text):
            code_point = ord(char)
            if code_point not in char_data:
                # Find context
                start = max(0, i - 15)
                end = min(len(text), i + 16)
                context = text[start:end].replace('\n', ' ')
                
                char_data[code_point] = {
                    'char': char,
                    'count': 0,
                    'example_ayah': ayah_key,
                    'example_page': page_number,
                    'context': context
                }
            char_data[code_point]['count'] += 1

    build_dir = "build/reports"
    os.makedirs(build_dir, exist_ok=True)
    
    unique_path = os.path.join(build_dir, "indopak_unique_codepoints.txt")
    problem_path = os.path.join(build_dir, "indopak_problem_candidates.txt")

    # 1. Unique code points report
    with open(unique_path, "w", encoding="utf-8") as f:
        f.write("=== INDOPAK UNIQUE CODE POINTS ===\n")
        f.write(f"Total Unique Characters: {len(char_data)}\n\n")
        f.write(f"{'CodePoint':<10} | {'Char':<4} | {'Count':<6} | {'Example':<10} | {'Page':<4} | {'Name':<35}\n")
        f.write("-" * 85 + "\n")
        for cp in sorted(char_data.keys()):
            data = char_data[cp]
            char = data['char']
            try:
                name = unicodedata.name(char)
            except ValueError:
                name = "<UNKNOWN>"
            char_display = char if cp > 32 and unicodedata.category(char)[0] != 'C' else f"[{cp}]"
            cp_str = f"U+{cp:04X}"
            f.write(f"{cp_str:<10} | {char_display:<4} | {data['count']:<6} | {data['example_ayah']:<10} | {data['example_page']:<4} | {name:<35}\n")

    # 2. Problem candidates (e.g. private use area, annotations, formatting, rare high characters)
    problem_count = 0
    with open(problem_path, "w", encoding="utf-8") as f:
        f.write("=== INDOPAK POTENTIAL PROBLEM GLYPH CANDIDATES ===\n\n")
        f.write(f"{'CodePoint':<10} | {'Char':<4} | {'Count':<6} | {'Example':<10} | {'Name':<35}\n")
        f.write("-" * 85 + "\n")
        
        for cp in sorted(char_data.keys()):
            data = char_data[cp]
            char = data['char']
            category = unicodedata.category(char)
            
            # Check if this character is a likely problem candidate
            # (Private use, combining annotations, formatting, or Unicode block U+08D0-U+08FF Arabic extended)
            is_problem = (
                category in ('Cf', 'Co', 'Cc') or 
                (cp >= 0x08D0 and cp <= 0x08FF) or
                (cp >= 0x0600 and cp <= 0x061F) or  # signs and stop signs
                (cp >= 0x06D6 and cp <= 0x06ED)     # annotated signs
            )
            
            if is_problem:
                problem_count += 1
                try:
                    name = unicodedata.name(char)
                except ValueError:
                    name = "<UNKNOWN>"
                char_display = char if cp > 32 and category[0] != 'C' else f"[{cp}]"
                cp_str = f"U+{cp:04X}"
                f.write(f"{cp_str:<10} | {char_display:<4} | {data['count']:<6} | {data['example_ayah']:<10} | {name:<35}\n")
                f.write(f"  [Context] ... {data['context']} ...\n\n")
                
    print(f"Extraction completed.")
    print(f"Unique characters list written to: {unique_path}")
    print(f"Problem candidates list written to: {problem_path} (Found: {problem_count} candidates)")

if __name__ == "__main__":
    main()
