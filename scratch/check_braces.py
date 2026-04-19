import os

def check_braces(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    stack = []
    lines = content.split('\n')
    for i, line in enumerate(lines):
        for char in line:
            if char == '{':
                stack.append(i + 1)
            elif char == '}':
                if not stack:
                    print(f"Extra closing brace at {filepath}:{i+1}")
                else:
                    stack.pop()
    
    if stack:
        for line_num in stack:
            print(f"Unclosed opening brace at {filepath}:{line_num}")

files = [
    r"app\src\main\java\com\turistgo\app\features\moderator\ModeratorDashboard.kt",
    r"app\src\main\java\com\turistgo\app\features\moderator\ModeratorStatsScreen.kt",
    r"app\src\main\java\com\turistgo\app\features\moderator\UserManagementScreen.kt",
    r"app\src\main\java\com\turistgo\app\features\moderator\ModeratorSettingsScreen.kt",
    r"app\src\main\java\com\turistgo\app\features\moderator\ModeratorProfileScreen.kt"
]

for file in files:
    full_path = os.path.join(r"C:\Users\Santiago\OneDrive\Escritorio\TuristGo", file)
    if os.path.exists(full_path):
        check_braces(full_path)
    else:
        print(f"File not found: {full_path}")
