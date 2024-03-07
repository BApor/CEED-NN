import cv2 as cv
import os


class Label:
    def __init__(self, xtl: int, ytl: int, xbr: int, ybr: int):
        self.xtl = xtl
        self.xbr = xbr
        self.ytl = ytl
        self.ybr = ybr

def process_images_of_type(seed_type: str, path: str, dataset_path: str, to_size=(0, 0)):

    # Checking if the directories are correctly given

    red_text = "\033[91m"
    reset_text = "\033[0m"

    try:
        if not os.path.exists(path) or not os.path.isdir(path):
            print(f"{red_text}Image Manager Error: The given work path does not exists or is not a directory!\n")
            print(f"{path}{reset_text}")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    # The path to the seed images
    seed_type_path = f"{path}/{seed_type}"

    try:
        if not os.path.exists(f"{seed_type_path}/images") or not os.path.isdir(f"{seed_type_path}/images") \
        or not os.path.exists(f"{seed_type_path}/labels") or not os.path.isdir(f"{seed_type_path}/labels"):
            print(f"{red_text}Image Manager Error: The given work path isn't following the next file architecture:")
            print("\t\t:")
            print("\t\t:")
            print("\t\t| image_and_labels_path")
            print("\t\t       | seed_type")
            print("\t\t             | images")
            print(f"\t\t             | labels\n{reset_text}")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    try:
        if not os.path.exists(dataset_path) or not os.path.isdir(dataset_path):
            print(f"{red_text}Image Manager Error: The given dataset destination path "
                  "does not exists or is not a directory!")
            print(f"{dataset_path}{reset_text}")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    # Opening the labels directory

    xml_file = None

    try:
        with open(f"{seed_type_path}/labels/annotations.xml", "r") as file:
            xml_file = file.read()
    except FileNotFoundError:
        print(f"{red_text}Image Manager Error: The labels file annotations.xml in "
              f"the given labels directory does not exist!{reset_text}")
        return
    except IOError as e:
        print(f"Image Manager Error: Unable to read the labels file annotations.xml! {e}")
        return

    if xml_file is None:
        print(f"{red_text}Image Manager Error: The labels file annotations.xml is empty!{reset_text}")
        return

    # Processing images

    image = None
    image_name = None
    image_from_label_id = 1

    # Main cycle processes the .xml file
    for row in xml_file.split('\n'):

        # If the row is an image declaration the open image,
        # else if it is a box then process image and export the labeled
        if row.strip().lower().startswith('<image'):
            image_name = row.split()[2].split('"')[1]
            image = cv.imread(f"{seed_type_path}/images/{image_name}")
            continue
        elif row.strip().lower().startswith('<box'):
            row_array = row.split('"')

            # Label coordinates
            label = Label(int(float(row_array[7])), int(float(row_array[9])),
                          int(float(row_array[11])), int(float(row_array[13])))

            # Cutting out the labeled part
            image_from_label = image[label.ytl:label.ybr, label.xtl:label.xbr]

            # If to_size is given then resize the image
            if to_size != (0, 0):
                image_from_label = cv.resize(image_from_label, to_size)

            # The absolute path to the seed type within the dataset
            dataset_type_path = f"{dataset_path}/{seed_type}"
            if not os.path.exists(dataset_type_path):
                os.makedirs(dataset_type_path)

            # Creating individual names for the label images using image_from_label_id
            image_save_name = f"{image_name.split('.')[0]}_{image_from_label_id}.jpg"

            # Saving the label image
            cv.imwrite(f"{dataset_type_path}/{image_save_name}", image_from_label)
            image_from_label_id += 1


# process_images_of_type("3. Austrian Winter Pea",
#                        "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/image-manager/datasets",
#                        "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/image-manager/datasets",
#                        (0, 0))

def process_images_all(path: str, dataset_path: str):
    # Checking if the directories are correctly given

    red_text = "\033[91m"
    reset_text = "\033[0m"

    try:
        if not os.path.exists(path) or not os.path.isdir(path):
            print(f"{red_text}Image Manager Error: The given work path does not exists or is not a directory!")
            print(f"{path}{reset_text}")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

    try:
        if not os.path.exists(dataset_path) or not os.path.isdir(dataset_path):
            print(f"{red_text}Image Manager Error: The given dataset destination path "
                  "does not exists or is not a directory!")
            print(f"{dataset_path}{reset_text}")
            return
    except OSError as e:
        print(f"Image Manager Error: {e}")
        return

process_images_all("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/image-manager/datasets", "/adas/ad")