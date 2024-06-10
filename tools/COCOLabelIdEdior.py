import json

# Parameters

jsonPath = '/Volumes/APORKA SSD/Allamvizsga/Docker/train/datasets/yolo_dataset/annotations/instances_01.json'
# Open the JSON file
with open(jsonPath, 'r') as file:
    # Read the content of the file
    json_rows = file.readlines()

# Loop through each row in the JSON data
for index in range(len(json_rows)):
    # Replace "category_id":1 with "category_id":3
    if '"category_id":3' in json_rows[index]:
        # Replace "category_id":1 with "category_id":3
        json_rows[index] = json_rows[index].replace('"category_id":3', '"category_id":1')

# Write the updated data back to the JSON file
with open(jsonPath, 'w') as file:
    file.writelines(json_rows)