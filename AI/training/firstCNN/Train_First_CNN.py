import torch
import torch.nn as nn
import torch.optim as optim
import torchvision
from torchvision import datasets, transforms

from tools.resultsaving import Results_Manager as reman


transform = transforms.Compose(
    [transforms.ToTensor(),
     transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))])

trainset = datasets.ImageFolder("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/datasets",
                               transform=transform)

train_loader = torch.utils.data.DataLoader(trainset, batch_size=32, shuffle=True, num_workers=0)

class_names = trainset.classes

reman.get_plot_rand_train_data(train_loader, class_names).savefig("results/sample_training.jpg")



