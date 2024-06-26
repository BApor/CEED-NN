import os

# MAC GEPEN A PROJEKTBOL KITORLI A MAC ALTAL KESZITETT BIZTONSAGI MASOLATOKAT
# A BIZTONSAGI MASOLATOK MIATT ANDROID STUDIOBA NEHA HIBAK KELETKEZTEK FUTASKOR
# TOVABBA A TANITOHALMAZBAN IS FELESLEGES A MASOLAT

folder_path = '/Volumes/APORKA SSD/Allamvizsga/Program'
deleted_files_count = 0

# Walk through the directory and its subdirectories
for root, dirs, files in os.walk(folder_path):
    for filename in files:
        if filename.startswith('._'):
            file_path = os.path.join(root, filename)
            if os.path.isfile(file_path):
                os.remove(file_path)
                deleted_files_count += 1

print(f"Total number of deleted files: {deleted_files_count}")