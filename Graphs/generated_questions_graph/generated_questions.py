import matplotlib.pyplot as plt
import numpy as np

# Initialize lists to store x and y values
x = []
y1 = []
y2 = []

# Open the file and read it line by line
with open('output.log', 'r') as file:
    for line in file:
        # Check if the line contains the necessary information
        if "Questions created for views limit:" in line:
            # Extract the number and add it to the x values
            x.append(int(line.split(":")[1].strip()))
        elif "Total questions count:" in line:
            # Extract the number and add it to the y1 values
            y1.append(int(line.split(":")[1].strip()))
        elif "Unique questions count:" in line:
            # Extract the number and add it to the y2 values
            y2.append(int(line.split(":")[1].strip()))

# Create an array with the positions of each bar along the x-axis
x_pos = np.arange(len(x))

# For better clarity, let's make the bars half the width of default size
barWidth = 0.4

# Create first graph for 'Total Questions Count'
plt.figure(figsize=(8, 5))
plt.bar(x_pos, y1, width = barWidth, color = 'b', label='Celkový počet otázek')
plt.xlabel('Views limit')
plt.ylabel('Počet otázek')
plt.xticks(x_pos, x)
plt.legend()
plt.grid(True)
plt.savefig('total_questions.png')
plt.show()

# Create second graph for 'Unique Questions Count'
plt.figure(figsize=(8, 5))
plt.bar(x_pos, y2, width = barWidth, color = 'g')
plt.xlabel('Views limit')
plt.ylabel('Počet otázek')
plt.xticks(x_pos, x)
plt.legend()
plt.grid(True)
plt.savefig('unique_questions.png')
plt.title('')
plt.show()