import torch
import torch.nn as nn
import torch.optim as optim
import torchvision
from PIL import Image
from torchvision import transforms


def image_to_tensor(image_path, target_size=(992, 736)):
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

model = torchvision.models.mobilenet
optimizer = optim.SGD(model.parameters(), lr=0.001, momentum=0.9)

checkpoint = torch.load('/Volumes/APORKA SSD/Allamvizsga/Eredmenyek/openvino/mobilenetv2/20240610_072458/last.ckpt')
model.load_state_dict(checkpoint['model_state_dict'])
optimizer.load_state_dict(checkpoint['optimizer_state_dict'])
epoch = checkpoint['epoch']
loss = checkpoint['loss']

model.eval()

input_tensor = image_to_tensor("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/tools/IMG_4386.JPG")
traced_script_module = torch.jit.trace(model, input_tensor)
traced_script_module.save("mobilenetv2.torchscript")
