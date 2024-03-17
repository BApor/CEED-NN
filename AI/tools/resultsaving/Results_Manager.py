import torch.optim as optim
import torchvision
from torchvision import datasets, transforms
import matplotlib.pyplot as plt
import numpy as np


def get_plot_rand_train_data(train_loader, class_names):
    # Function to show images from a batch along with their labels and return the plot instance

    for images, labels in train_loader:

        # Show random images from the batch along with their labels
        indices = np.random.choice(len(images), size=16, replace=False)

        fig = plt.figure(figsize=(10, 5))
        for i in range(len(images[indices])):
            # Convert tensor to numpy array and rearrange dimensions
            image = images[i].permute(1, 2, 0).numpy()

            plt.subplot(4, 8, i + 1)
            plt.imshow(image)
            plt.title(class_names[labels[indices][i]])
            plt.axis('off')

        # Save the plot as a JPG image
        return fig
