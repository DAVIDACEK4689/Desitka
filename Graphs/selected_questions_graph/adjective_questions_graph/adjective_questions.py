import collections
import os
import re

import matplotlib.pyplot as plt


def extract_value_in_brackets(s):
    match = re.search(r'\[(.*?)\]', s)
    if match:
        return match.group(1)
    return None

directory_path = '../../selected_questions'
values = []

for filename in os.listdir(directory_path):
    if filename.endswith('.json'):
        with open(os.path.join(directory_path, filename), 'r') as f:
            lines = f.readlines()
            value = extract_value_in_brackets(lines[1])
            if value:
                values.append(value)

counts = collections.Counter(values)

# Calculate total count
total_count = sum(counts.values())

# Create a graph
plt.figure(figsize=(8, 5))
plt.bar(list(counts.keys()), list(counts.values()))
plt.xlabel('Okruh otázky')
plt.ylabel('Počet')
plt.grid(True)
plt.tight_layout()

# Adjust the space at the top of the figure
plt.subplots_adjust(top=0.95)

# Set the title of the graph
plt.title('Celkový počet: ' + str(total_count))

# Save the graph as PNG
plt.savefig('adjective_questions.png')

# Show the graph
plt.show()