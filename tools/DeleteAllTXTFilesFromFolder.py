import os

def delete_txt_files(folder_path):
    for file in os.listdir(folder_path):
        if file.endswith(".txt"):
            os.remove(os.path.join(folder_path, file))

# Modify the folder_path variable to the path of the target folder
folder_path = "C:/Apor/MTDK/ai/datasets/darknet_dataset/data"
# delete_txt_files(folder_path)
