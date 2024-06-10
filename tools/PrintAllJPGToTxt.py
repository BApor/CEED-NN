import os

folder_path = "/home/data/train/datasets/yolo_dataset/obj_train_data"
output_file = "/home/data/train/datasets/yolo_dataset/train.txt"


jpg_files = []

# Walk through the directory and its subdirectories
for root, dirs, files in os.walk(folder_path):
    for filename in files:
        if filename.lower().endswith('.jpg'):
            jpg_files.append(folder_path + '/' + filename)

# Write the list of .jpg files to the output text file
with open(output_file, 'w') as f:
    for jpg_file in jpg_files:
        f.write(jpg_file + '\n')

print(f"Found {len(jpg_files)} .jpg files. List saved to {output_file}.")