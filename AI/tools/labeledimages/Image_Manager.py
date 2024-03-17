import cv2 as cv
import os


# Red text variables for errors
red_text = "\033[91m"
reset_text = "\033[0m"


class Label:
    def __init__(self, xtl: int, ytl: int, xbr: int, ybr: int):
        self.xtl = xtl
        self.xbr = xbr
        self.ytl = ytl
        self.ybr = ybr


def is_directory(path: str, fun_name: str) -> bool:

    # Checking if the given directories are ok

    try:
        if not os.path.exists(path) or not os.path.isdir(path):
            print(f"{red_text}Image Manager Error - function {fun_name}: "
                  f"The given work path does not exists or is not a directory!\n")
            print(f"{path}{reset_text}")
            return True
    except OSError as e:
        print(f"{red_text}Image Manager Error - function {fun_name}: {e}{reset_text}")
        return True

    return False


def is_dataset(dataset_path: str, fun_name: str):

    # Checking if a given directory is dataset directory

    if dataset_path.split('/')[len(dataset_path.split('/')) - 1] != "datasets":
        print(f"{red_text}Image Manager Error - function {fun_name}: "
              f"The given path isn't a dataset! It should be named .../datasets")
        print(f"{dataset_path}{reset_text}")
        return


def open_directories(path: str, fun_name: str):

    # Checking if the directory is correctly given

    if is_directory(path, "open_directories"):
        return

    # Opening the directories inside the given path

    try:
        items = os.listdir(path)
        return [item for item in items if os.path.isdir(os.path.join(path, item))]
    except OSError as e:
        print(f"{red_text}Image Manager Error - function {fun_name}:"
              f"Error reading directories in path {path}: {e}{reset_text}")
        return None


def open_images(path: str, fun_name: str):

    # Checking if the directory is correctly given

    if is_directory(path, "open_images"):
        return

    # Opening the images inside the given path

    try:
        items = os.listdir(path)
        return [entry for entry in items if entry.lower().endswith('.jpg') and not entry.startswith('._IMG')]
    except OSError as e:
        print(f"{red_text}Image Manager Error - function {fun_name}:"
              f" Error reading plant seed images in path {path}: {e}{reset_text}")
        return None


def resize_to_size(seed_type: str, dataset_path: str, to_size: (int, int)):

    # Checking if the directories are correctly given
    if is_directory(dataset_path, "resize_to_size"):
        return

    # Checking if the path is a dataset
    if is_dataset(dataset_path, "resize_to_size"):
        return

    # The path to the seed images
    seed_type_dataset_path = f"{dataset_path}/{seed_type}"

    # Opening images
    images = open_images(seed_type_dataset_path, "resize_to_size")
    if images is None:
        return

    for image_name in images:

        # Opening the image
        image = cv.imread(f"{seed_type_dataset_path}/{image_name}")

        # Resizing the image
        image = cv.resize(image, to_size)

        # Removing the non-resized image
        os.remove(f"{seed_type_dataset_path}/{image_name}")

        # Saving the resized image
        cv.imwrite(f"{seed_type_dataset_path}/{image_name}", image)

    print(f"Image Manager - function resize_to_size: "
          f"Images from dataset {seed_type} successfully resized to size {to_size[0]}x{to_size[1]}!")


def calculate_avg_dimension(seed_type_dataset_path: str, images: list[str]) -> int:

    # Calculating the average dimension of the dataset images

    avg_width = 0
    avg_height = 0

    for image_name in images:
        image = cv.imread(f"{seed_type_dataset_path}/{image_name}")
        height, width, _ = image.shape
        avg_width += width
        avg_height += height

    return int(((avg_width / len(images)) + (avg_height / len(images))) / 2)


def resize_to_type_avg(seed_type: str, dataset_path: str):

    # Checking if the directories are correctly given
    if is_directory(dataset_path, "resize_to_type_avg"):
        return

    # Checking if the path is a dataset
    if is_dataset(dataset_path, "resize_to_type_avg"):
        return

    # The path to the seed images
    seed_type_dataset_path = f"{dataset_path}/{seed_type}"

    # Opening images
    images = open_images(seed_type_dataset_path, "resize_to_type_avg")
    if images is None:
        return

    avg_dim = calculate_avg_dimension(seed_type_dataset_path, images)

    # Resizing to the average dimension
    resize_to_size(seed_type, dataset_path, (avg_dim, avg_dim))


def resize_dataset_each(dataset_path: str, to_size=(0, 0), to_type_avg=False):

    # Checking settings
    if to_size == (0, 0) and not to_type_avg:
        print(f"{red_text}Image Manager Error - function resize_dataset_each: "
              f"Please choose resize setting: to_size and to_type_avg attributes cannot be both blank!{reset_text}")
        return

    # Checking if the directories are correctly given
    if is_directory(dataset_path, "resize_dataset_each"):
        return

    # Checking if the path is a dataset
    if is_dataset(dataset_path, "resize_dataset_each"):
        return

    # Opening the directory
    directories = open_directories(dataset_path, "resize_dataset_each")
    if directories is None:
        return

    # Resizing each seed type dataset folder by the chosen setting

    if not to_size == (0, 0):
        for seed_type in directories:
            resize_to_size(seed_type, dataset_path, to_size)
        print("Image Manager - function resize_dataset_each: "
              f"Each dataset resized to size {to_size[0]}x{to_size[1]}!")
    elif to_type_avg:
        for seed_type in directories:
            resize_to_type_avg(seed_type, dataset_path)
        print("Image Manager - function resize_dataset_each: "
              "Each dataset resized to size of it's own type average!")


def resize_to_dataset_avg(dataset_path: str):
    # Checking if the directories are correctly given
    if is_directory(dataset_path, "resize_to_dataset_avg"):
        return

    # Checking if the path is a dataset
    if is_dataset(dataset_path, "resize_to_dataset_avg"):
        return

    # Opening the directory
    directories = open_directories(dataset_path, "resize_to_dataset_avg")
    if directories is None:
        return

    # Calculating the dataset average

    dts_avg = 0

    for seed_type in directories:
        seed_type_dataset_path = f"{dataset_path}/{seed_type}"
        images = open_images(seed_type_dataset_path, "resize_to_dataset_avg")
        dts_avg += calculate_avg_dimension(seed_type_dataset_path, images)

    dts_avg = int(dts_avg / len(directories))

    print("Image Manager - function resize_to_dataset_avg: "
          f"Average dimension of the datasets is {dts_avg}x{dts_avg}")

    # Resizing each of the datasets
    resize_dataset_each(dataset_path, to_size=(dts_avg, dts_avg))


def process_images_of_type(seed_type: str, path: str, dataset_path: str, to_size=(0, 0)):

    # Checking if the directories are correctly given
    if is_directory(path, "process_images_of_type"):
        return

    if is_directory(dataset_path, "process_images_of_type"):
        return

    # Checking if the directory hierarchy is correct

    # The path to the seed images
    seed_type_path = f"{path}/{seed_type}"

    try:
        if not os.path.exists(f"{seed_type_path}/images") or not os.path.isdir(f"{seed_type_path}/images") \
        or not os.path.exists(f"{seed_type_path}/labels") or not os.path.isdir(f"{seed_type_path}/labels"):
            print(f"{red_text}Image Manager Error - function process_images_of_type: "
                  f"The given work path isn't following the next file architecture:")
            print("\t\t:")
            print("\t\t:")
            print("\t\t| image_and_labels_path")
            print("\t\t       | seed_type")
            print("\t\t             | images")
            print(f"\t\t             | labels\n{reset_text}")
            return
    except OSError as e:
        print(f"Image Manager Error - function process_images_of_type: {e}")
        return

    # Opening the labels directory

    try:
        with open(f"{seed_type_path}/labels/annotations.xml", "r") as file:
            xml_file = file.read()
    except FileNotFoundError:
        print(f"{red_text}Image Manager Error - function process_images_of_type: "
              f"The labels file annotations.xml in the given labels directory does not exist!{reset_text}")
        return
    except IOError as e:
        print(f"{red_text}Image Manager Error - function process_images_of_type: "
              f"Unable to read the labels file annotations.xml! {e}{reset_text}")
        return

    if xml_file is None:
        print(f"{red_text}Image Manager Error - function process_images_of_type: "
              f"The labels file annotations.xml is empty!{reset_text}")
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

    print("Image Manager - function process_images_of_type: "
          f"Successfully processed plant seed type {seed_type}")


def process_images_all(path: str, dataset_path: str, to_size=(0, 0)):

    # Checking if the directories are correctly given
    if is_directory(path, "process_images_all"):
        return

    if is_directory(dataset_path, "process_images_all"):
        return

    # Opening the seed type directories inside the given path

    directories = open_directories(path, "process_images_all")
    if directories is None:
        return

    for directory in directories:
        process_images_of_type(directory, path, dataset_path, to_size)

    print("Image Manager - function process_images_all: "
          "Successfully processed all types!")