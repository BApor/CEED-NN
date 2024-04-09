import torch
import torch.nn as nn
import torch.nn.functional as F


class FirstCNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv1 = nn.Conv2d(3, 6, 5)
        self.conv2 = nn.Conv2d(6, 16, 5)
        self.maxpool = nn.MaxPool2d(2, 2)
        self.conv1_output_size = ((150 - 5 + 2 * 0) // 1 + 1) // 2
        self.conv2_output_size = ((self.conv1_output_size - 5 + 2 * 0) // 1 + 1) // 2
        self.fully_con1 = nn.Linear(16 * self.conv2_output_size * self.conv2_output_size, 120)
        self.fully_con2 = nn.Linear(120, 84)
        self.fully_con3 = nn.Linear(84, 2)

    def forward(self, x):
        x = self.conv1(x)
        x = F.relu(x)
        x = self.maxpool(x)
        x = self.conv2(x)
        x = F.relu(x)
        x = self.maxpool(x)
        x = x.view(-1, 16 * self.conv2_output_size * self.conv2_output_size)
        x = self.fully_con1(x)
        x = F.relu(x)
        x = self.fully_con2(x)
        x = F.relu(x)
        x = self.fully_con3(x)
        return x
