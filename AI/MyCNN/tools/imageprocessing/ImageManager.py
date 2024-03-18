import cv2 as cv
import os
import shutil


# Blue text messages are the messages of the Image Manager
blue_text = "\033[94m"

# Red text for errors
red_text = "\033[91m"

# White text for reset
reset_text = "\033[0m"


# A label datatype
class Label:
    def __init__(self, xtl: int, ytl: int, xbr: int, ybr: int):
        self.xtl = xtl
        self.xbr = xbr
        self.ytl = ytl
        self.ybr = ybr


# Static functions

def is_directory(path: str):

    # Checking if the given directories are ok

    try:
        if not os.path.exists(path) or not os.path.isdir(path):
            return ("The given work path does not exists or is not a directory!\n" +
                     f"{path}")
    except OSError as e:
        return e
    return None


def is_dataset(datasets_path: str):

    # Checking if a given directory is dataset directory

    if datasets_path.split('/')[len(datasets_path.split('/')) - 1] != "datasets":
        return ("The given path isn't a dataset! It should be named .../datasets" +
                  f"{datasets_path}")
    return None


def open_directories(path: str, fun_name: str):

    # Opening the directories inside the given path

    try:
        items = os.listdir(path)
        return [item for item in items if os.path.isdir(os.path.join(path, item))]
    except OSError as e:
        print(f"{red_text}Image Manager Error - function {fun_name}:"
              f"Error reading directories in path {path}: {e}{reset_text}")
        return None


def open_images(path: str, fun_name: str):

    # Opening the images inside the given path

    try:
        items = os.listdir(path)
        return [entry for entry in items if entry.lower().endswith('.jpg') and not entry.startswith('._')]
    except OSError as e:
        print(f"{red_text}Image Manager Error - function {fun_name}:"
              f" Error reading plant seed images in path {path}: {e}{reset_text}")
        return None


# The Image Manager class definition
class ImageManager:
    def __init__(self, images_path, datasets_path):
        if is_directory(images_path) is not None:
            print(f"{red_text}Image Manager tool instance creation ERROR:\n" +
                  is_directory(images_path) + reset_text)
            exit(1)
        if is_directory(datasets_path) is not None:
            print(f"{red_text}Image Manager tool instance creation ERROR:\n" +
                  is_directory(datasets_path) + reset_text)
            exit(1)
        if is_dataset(datasets_path) is not None:
            print(f"{red_text}Image Manager tool instance creation ERROR:\n" +
                  is_dataset(datasets_path) + reset_text)
            exit(1)
        self.images_path = images_path
        self.datasets_path = datasets_path

    def resize_to_size(self, seed_type: str, to_size: (int, int)):

        # The path to the seed images
        seed_type_dataset_path = f"{self.datasets_path}/{seed_type}"

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

        print(f"{blue_text}Image Manager - function resize_to_size: "
              f"Images from dataset {seed_type} successfully resized to size {to_size[0]}x{to_size[1]}!{reset_text}")

    def calculate_avg_dimension(self, seed_type: str, images: list[str]) -> (int, int):

        # Calculating the average dimension of the dataset images

        avg_width = 0
        avg_height = 0

        for image_name in images:
            image = cv.imread(f"{self.datasets_path}/{seed_type}/{image_name}")
            height, width, _ = image.shape
            avg_width += width
            avg_height += height

        return (int(avg_width / len(images))), int((avg_height / len(images)))

    def resize_to_type_avg(self, seed_type: str):

        # The path to the seed images
        seed_type_dataset_path = f"{self.datasets_path}/{seed_type}"

        # Opening images
        images = open_images(seed_type_dataset_path, "resize_to_type_avg")
        if images is None:
            return

        avg_dim = self.calculate_avg_dimension(seed_type, images)

        # Resizing to the average dimension
        self.resize_to_size(seed_type, avg_dim)

    def resize_dataset_each(self, to_size=(0, 0), to_type_avg=False):

        # Checking settings
        if (to_size[0] == 0 and to_size[1] == 0) and (not to_type_avg):
            print(f"{red_text}Image Manager Error - function resize_dataset_each: "
                  f"Please choose resize setting: to_size and to_type_avg attributes cannot be both blank!{reset_text}")
            return

        # Opening the directory
        directories = open_directories(self.datasets_path, "resize_dataset_each")
        if directories is None:
            return

        # Resizing each seed type dataset folder by the chosen setting

        if not to_size == (0, 0):
            datasets_avg = (0, 0)
            for seed_type in directories:
                seed_type_dataset_path = f"{self.datasets_path}/{seed_type}"
                images = open_images(seed_type_dataset_path, "resize_dataset_each")
                if images is None:
                    return
                datasets_avg = tuple(x + y for x, y in zip(datasets_avg, self.calculate_avg_dimension(seed_type, images)))
            datasets_avg = tuple(x / y for x, y in zip(datasets_avg, (len(directories), len(directories))))
            if datasets_avg == to_size:
                print(f"{blue_text}Image Manager - function resize_dataset_each: "
                      f"The datasets are already {to_size[0]}x{to_size[1]}!{reset_text}")
                return
            for seed_type in directories:
                self.resize_to_size(seed_type, to_size)
            print(f"{blue_text}Image Manager - function resize_dataset_each: "
                  f"Each dataset resized to size {to_size[0]}x{to_size[1]}!{reset_text}")
        elif to_type_avg:
            for seed_type in directories:
                self.resize_to_type_avg(seed_type)
            print(f"{blue_text}Image Manager - function resize_dataset_each: "
                  f"Each dataset resized to size of it's own type average!{reset_text}")

    def resize_to_dataset_avg(self):

        # Opening the directory
        directories = open_directories(self.datasets_path, "resize_to_dataset_avg")
        if directories is None:
            return

        # Calculating the dataset average

        dts_avg = 0

        for seed_type in directories:
            seed_type_dataset_path = f"{self.datasets_path}/{seed_type}"
            images = open_images(seed_type_dataset_path, "resize_to_dataset_avg")
            dts_avg += self.calculate_avg_dimension(seed_type, images)

        dts_avg = int(dts_avg / len(directories))

        print(f"{blue_text}Image Manager - function resize_to_dataset_avg: "
              f"Average dimension of the datasets is {dts_avg}x{dts_avg}{reset_text}")

        # Resizing each of the datasets
        self.resize_dataset_each(to_size=(dts_avg, dts_avg))

    def process_images_of_type(self, seed_type: str):

        # Function that processes images of a seed type

        # Checking if the seed type has been already processed
        if os.path.exists(f"{self.datasets_path}/{seed_type}"):
            print(f"{blue_text}Image Manager - function process_images_of_type: "
                  f"Seed type {seed_type} is already processed!{reset_text}")
            return

        print(f"{blue_text}Image Manager - function process_images_of_type: "
              f"Processing images of seed type {seed_type}...{reset_text}")

        # Checking if the directory hierarchy is correct

        # The path to the seed images
        seed_type_path = f"{self.images_path}/{seed_type}"

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

                # The absolute path to the seed type within the dataset
                dataset_type_path = f"{self.datasets_path}/{seed_type}"
                if not os.path.exists(dataset_type_path):
                    os.makedirs(dataset_type_path)

                # Creating individual names for the label images using image_from_label_id
                image_save_name = f"{image_name.split('.')[0]}_{image_from_label_id}.jpg"

                # Saving the label image
                cv.imwrite(f"{dataset_type_path}/{image_save_name}", image_from_label)
                image_from_label_id += 1

        print(f"{blue_text}Image Manager - function process_images_of_type: "
              f"Successfully processed plant seed type {seed_type}{reset_text}")

    def process_images_all(self):

        # Opening the seed type directories inside the given path

        directories = open_directories(self.images_path, "process_images_all")
        if directories is None:
            return

        for directory in directories:
            self.process_images_of_type(directory)

        print(f"{blue_text}Image Manager - function process_images_all: "
              f"Successfully processed all types!{reset_text}")

    def wipe_datasets(self):

        # Iterate over each item in the directory
        for item in os.listdir(self.datasets_path):

            # Construct the full path of the item
            item_path = os.path.join(self.datasets_path, item)

            # Check if the item is a directory
            if os.path.isdir(item_path):

                # Delete the directory
                try:
                    shutil.rmtree(item_path)
                except OSError as e:
                    print(f"Error deleting folder {item_path}: {e}")

        print(f"{blue_text}Image Manager - function wipe_datasets: "
              f"Datasets folder cleared!{reset_text}")
