import torch
import torch.nn as nn
import torch.optim as optim
import torchvision
from torchvision import datasets, transforms
from datetime import datetime
import warnings

from tools.resultsaving.ResultsManager import ResultsManager
from tools.imageprocessing.ImageManager import ImageManager

# Creating variables for the tools

training_time = datetime.now().strftime("%Y-%m-%d_%H:%M:%S")
dataset_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/datasets"
images_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/plantseeds"
results_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/results"

# Creating instances of the tools
imman = ImageManager(images_path, dataset_path)

# Create datasets

imman.process_images_of_type("3. Austrian Winter Pea")
imman.resize_to_dataset_avg()
print()

# Loading the datasets with normalization
mean, std_deviation = (0.5, 0.5, 0.5), (0.5, 0.5, 0.5)
train_set = datasets.ImageFolder(dataset_path,
                transform=transforms.Compose(
                            [transforms.ToTensor(),
                             transforms.Normalize(mean, std_deviation)]))

# Creating the train loader
train_loader = torch.utils.data.DataLoader(train_set, batch_size=32,
                                           shuffle=True, num_workers=0)

# Creating an instance of the Results Manager,
# with it, a new folder and saving a sample data plot
reman = ResultsManager(results_path, train_loader, train_set.classes)
reman.create_new_result_folder(training_time)
reman.save_plot_rand_train_data(mean, std_deviation)
print()


# TRAINING PROGRAM NOT IMPLEMENTED!!!
# .
# .
# .


# Clearing datasets
imman.wipe_datasets()





