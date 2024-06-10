import json
import os

def update_file_names(json_file_path, image_directory):
    # Load the JSON data from the file
    with open(json_file_path, 'r') as f:
        data = json.load(f)
    
    # Identify the key that contains the list of items
    key_with_list = 'images'  # Adjust this to match the key in your JSON structure
    
    # Check if the data has the expected structure
    if key_with_list in data and isinstance(data[key_with_list], list):
        for item in data[key_with_list]:
            if isinstance(item, dict):
                file_name = item.get('file_name')
                if file_name:
                    # Update the file_name to include the full path
                    new_file_name = os.path.join(image_directory, file_name)
                    item['file_name'] = new_file_name
            else:
                print(f"Expected a dictionary but got {type(item)}")
    else:
        print(f"Expected a dictionary with a list under key '{key_with_list}', but got {type(data)}")
    
    # Write the updated data back to the same JSON file
    with open(json_file_path, 'w') as f:
        json.dump(data, f, indent=4)

if __name__ == "__main__":
    # Define the input JSON file path
    json_file_path = '/Volumes/APORKA SSD/Allamvizsga/Docker/train/datasets/coco_from_ultralytics/annotations/stuff_val.json'  # Replace with your input JSON file path
    image_dir = '/home/data/train/datasets/yolo_ultralytics_dataset/images/val'  # Replace with the path to the image directory
    
    # Call the function to update file names
    update_file_names(json_file_path, image_dir)