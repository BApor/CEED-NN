from ultralytics import YOLO
import torch
from PIL import Image
from torchvision import transforms

def image_to_tensor(image_path, target_size=(640, 640)):
    image = Image.open(image_path).convert("RGB")
    
    transform = transforms.Compose([
        transforms.Resize(target_size),  # Resize image to 640x640
        transforms.ToTensor(),  # Convert image to tensor
        ])
    
    # Apply the transformation
    tensor = transform(image)
    
    # Add a batch dimension
    tensor = tensor.unsqueeze(0)  # Shape: (1, 3, 640, 640)
    
    return tensor


model = YOLO("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/backup/best8.pt", task="detect")
input_tensor = image_to_tensor("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/backup/IMG_4386.JPG")
result = model(input_tensor)
print(result)