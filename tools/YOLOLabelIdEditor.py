import os

# Parameters

lbls_path = "C:/Apor/MTDK/ai/datasets/ultralytics_dataset/labels/train/Sweet_Corn_9"
new_id = '8'
lbls = None

try:
    items = os.listdir(lbls_path)
    lbls = [entry for entry in items if entry.lower().endswith('.txt') and not entry.startswith('._')]
except OSError as e:
 print("Error reading plant seed labels in path!")

for lbl in lbls:
    with open(f"{lbls_path}/{lbl}", 'r') as file:
        lines = file.readlines()

    # Modify the first number in each line and store the modified lines
    modified_lines = []
    for line in lines:
        # Split the line into individual elements
        elements = line.strip().split()
        # Modify the first element (assuming it's always an integer)
        elements[0] = new_id
        # Join the elements back into a line
        modified_line = ' '.join(elements) + '\n'
        # Add the modified line to the list
        modified_lines.append(modified_line)

    # Write the modified lines back to the same file
    with open(f"{lbls_path}/{lbl}", 'w') as file:
        file.writelines(modified_lines)

print("Modification complete. Check your text files.")