import torch
import torch.nn as nn
import matplotlib.pyplot as plt
import numpy as np
import os
from PIL import Image
import subprocess


# Purple text messages are the messages of the Results Manager
purple_text = "\033[95m"

# Red text for errors
red_text = "\033[91m"

# White text for reset
reset_text = "\033[0m"


# Static functions


# Function for checking if the given directories are ok
def is_directory(path: str):
    try:
        if not os.path.exists(path) or not os.path.isdir(path):
            return ("The given work path does not exists or is not a directory!\n" +
                     f"{path}")
    except OSError as e:
        return e
    return None


# Function for checking if a given directory is dataset directory
def is_results(datasets_path: str):
    if datasets_path.split('/')[len(datasets_path.split('/')) - 1] != "results":
        return ("The given path isn't a dataset! It should be named .../datasets" +
                  f"{datasets_path}")
    return None


# Function that inverse normalizes an image
def inverse_normalize(image, mean, std):
    return (image * std + mean).clip(0, 1)


# The Results Manager class definition
class ResultsManager:
    def __init__(self, results_path, train_loader, class_names):
        self.train_loader = train_loader
        self.class_names = class_names
        if is_directory(results_path) is not None:
            print(f"{red_text}Result Manager tool instance creation ERROR:\n" +
                  is_directory(results_path) + reset_text)
            exit(1)
        if is_results(results_path) is not None:
            print(f"{red_text}Result Manager tool instance creation ERROR:\n" +
                  is_results(results_path) + reset_text)
            exit(1)
        self.results_path = results_path
        self.current_result_dir = None
        self.current_train_name = None
        self.current_result_models = None

        # Function to create a new result directory
    def create_new_result_folder(self, time):

        # Initialize a counter for folders
        folder_number = 1

        # Count the number of already existing directories inside the results folder
        for item in os.listdir(self.results_path):
            item_path = os.path.join(self.results_path, item)
            if os.path.isdir(item_path):
                folder_number += 1

        # Saving some helping paths
        dir_name = f"{self.results_path}/train{folder_number}-{time}"
        self.current_result_dir = f"{dir_name}"
        self.current_train_name = f"train{folder_number}"
        self.current_result_models = f"{dir_name}/models"

        # Creating the new result directory
        os.makedirs(dir_name, exist_ok=True)
        os.makedirs(f"{dir_name}/models", exist_ok=True)

        print(f"{purple_text}Results Manager - function create_new_result_folder: "
              f"Successfully created a new result folder named {dir_name}!{reset_text}")

    # Function to show images from a batch along with their
    # labels and return the plot instance
    def save_plot_rand_train_data(self, mean, std):
        # To show the real colors of the image, we will have to inverse normalize it.
        # To do that we need the mean and the std deviation of the inverse normalization!
        mean = np.array(mean)
        std = np.array(std)

        # Opening the images and the labels
        for images, labels in self.train_loader:

            # Show random images from the batch along with their labels
            indices = np.random.choice(len(images), size=12, replace=True)

            # Create a figure for the plot
            fig = plt.figure(figsize=(8, 6))

            # Iterate over the selected images
            for i, idx in enumerate(indices):
                # Convert tensor to numpy array and rearrange dimensions
                image = images[idx].permute(1, 2, 0).numpy()

                # Inverse normalize the image
                image = inverse_normalize(image, mean, std)

                # Plot the image
                plt.subplot(4, 3, i + 1)
                plt.imshow(Image.fromarray((image * 255).astype(np.uint8)))
                plt.title(self.class_names[labels[idx]])
                plt.axis('off')

            # Save the plot as a JPG image
            fig.tight_layout()
            fig.savefig(f"{self.current_result_dir}/train_data_sample.jpg")
            plt.close(fig)  # Close the figure to release memory

        print(f"{purple_text}Results Manager - function save_plot_rand_train_data: "
              f"Successfully saved a plot about samples from the training data!{reset_text}")

    # Function to save a neural network int pth format
    def save_net_pth(self, net: nn.Module):
        torch.save(net.state_dict(), f'{self.current_result_models}/{self.current_train_name}.pth')
        print(f"{purple_text}Results Manager - function save_net_pth: "
              f"Successfully saved the trained network into pth format!{reset_text}")

    # Function to save a neural network int onnx formats
    def save_net_onnx(self, net: nn.Module, img_width: int, img_height: int, img_channel: int, img_batch: int):
        net.load_state_dict(torch.load(f"{self.current_result_models}/{self.current_train_name}.pth"))
        img_input = torch.randn(img_batch, img_channel, img_width, img_height)
        torch.onnx.export(net, img_input, f"{self.current_result_models}/{self.current_train_name}.onnx", verbose=True)
        subprocess.run(f"mo --input_model {self.current_result_models}/{self.current_train_name}.onnx --output_dir"
                            f" {self.current_result_models}", shell=True)
        print(f"{purple_text}Results Manager - function save_net_onnx: "
              f"Successfully saved the trained network into onnx formats!{reset_text}")

