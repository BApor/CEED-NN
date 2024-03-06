import cv2 as cv
import os

def process_images(path: str, dataset_path: str, to_size=(32, 32)):

    # Checking if the directories are correctly given

    try:
        if not os.path.exists(path) and os.path.isdir(path):
            print("Image Manager Error: The given IMAGE AND LABELS does not exists or is not a directory!")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    try:
        if not os.path.exists(f"{path}/images") and os.path.isdir(f"{path}/images") \
        or not os.path.exists(f"{path}/labels") and os.path.isdir(f"{path}/labels")        :
            print("Image Manager Error: The IMAGE AND LABELS isn't following the next file architecture:")
            print("\t\t:")
            print("\t\t:")
            print("\t\t| image path")
            print("\t\t       | images")
            print("\t\t       | labels\n")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    try:
        if not os.path.exists(dataset_path) and os.path.isdir(dataset_path):
            print("Error: The given DATASET DESTINATION PATH does not exists or is not a directory!")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    # Opening the images directory
    image_dir = os.listdir(f"{path}/images")

    # Opening the labels directory

    labels = None

    try:
        with open(f"{path}/labels/annotations.xml", "r") as xml_file:
            labels = xml_file.read()
    except FileNotFoundError:
        print("Image Manager Error: The labels file annotations.xml in the given labels directory does not exist!")
        return
    except IOError as e:
        print(f"Image Manager Error: Unable to read the labels file annotations.xml! {e}")
        return

    if labels is None:
        print("Image Manager Error: The labels file annotations.xml is empty!")
        return

    # Storing image names in a list
    image_names = [file
                   for file in image_dir
                   if file.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp')) and
                   file.lower().startswith('IMG')]

    i = 0
    for image_name in image_names:
        image = cv.imread(f"{path}/images/{image_name}")
        cv.imwrite(f"{str(i)}image.jpg", image)
        i += 1

process_images("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/plantseeds/3. Austrian Winter Pea", "/")






