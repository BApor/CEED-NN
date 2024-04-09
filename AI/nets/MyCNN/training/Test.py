import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms
import os
from PIL import Image

from MyCNN import FirstCNN

dataset_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/data"

# Define the transformations
mean, std_deviation = (0.5, 0.5, 0.5), (0.5, 0.5, 0.5)
transform = transforms.Compose(
    [transforms.ToTensor(),
     transforms.Normalize(mean, std_deviation)])

# Load the model architecture
model = FirstCNN()

# Load the trained model
model.load_state_dict(torch.load("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/results/train1-2024-03-18_22:33:21/models/train1.pth"))  # Change the model path accordingly
model.eval()

# Classes
classes = datasets.ImageFolder(dataset_path).classes

# Images to predict
image_folder_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/predict/topredict"

# Predictions
predictions = {}

# Predict
for filename in os.listdir(image_folder_path):
    if filename.endswith(".jpg") or filename.endswith(".png"):
        image_path = os.path.join(image_folder_path, filename)
        image = Image.open(image_path)
        image = transform(image).unsqueeze(0)  # Add batch dimension
        with torch.no_grad():
            output = model(image)

        probabilities = torch.nn.functional.softmax(output[0], dim=0)
        _, predicted_class = torch.max(probabilities, 0)

        predicted_class = classes[predicted_class.item()]
        predictions[filename] = predicted_class

print("\nImage Classifications:\n")
for filename, predicted_class in predictions.items():
    print(f"{filename}: Class {predicted_class}")

