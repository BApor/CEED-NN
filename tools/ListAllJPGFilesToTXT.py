import os

folder_path_win = "C:\\Apor\\MTDK\\ai\\datasets\\darknet_dataset\\data"

def list_jpg_files(folder_path, folder_path_win):
    with open("jpg_files.txt", "w") as txt_file:
        for root, dirs, files in os.walk(folder_path):
            for file in files:
                if file.lower().endswith(".jpg") and not file.startswith("._"):
                    txt_file.write(folder_path_win + file + "\n")

# Módosítsd a folder_path értékét a célkönyvtár elérési útvonalára
folder_path = "C:/Apor/MTDK/ai/datasets/darknet_dataset/data"
list_jpg_files(folder_path, folder_path_win)