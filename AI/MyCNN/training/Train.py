import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms
from datetime import datetime

from tools.resultsaving.ResultsManager import ResultsManager
from tools.imageprocessing.ImageManager import ImageManager
from tools.trainingassistent import TrainigAssistant as ta
from MyCNN import FirstCNN

# Yellow text messages are the messages of the Results Manager
yellow_text = "\033[93m"

# White text for reset
reset_text = "\033[0m"

# Creating variables for the tools

training_time = datetime.now().strftime("%Y-%m-%d_%H:%M:%S")
dataset_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/datasets"
images_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/plantseeds"
results_path = "/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/training/firstCNN/results"

# Declaring instances of the Image Manager tool
imman = ImageManager(images_path, dataset_path)

# Create datasets
imman.process_images_of_type("3. Austrian Winter Pea")
imman.process_images_of_type("0. Others")
# imman.resize_to_dataset_avg()
imman.resize_dataset_each(to_size=(150, 150))
print()

# Loading the datasets with normalization
mean, std_deviation = (0.5, 0.5, 0.5), (0.5, 0.5, 0.5)
train_set = datasets.ImageFolder(dataset_path,
                transform=transforms.Compose(
                            [transforms.ToTensor(),
                             transforms.Normalize(mean, std_deviation)]))

# Declaring the train loader
train_loader = torch.utils.data.DataLoader(train_set, batch_size=150,
                                           shuffle=True, num_workers=0)

# Declaring an instance of the Results Manager tool,
# then with this a new folder and saving a sample data plot
reman = ResultsManager(results_path, train_loader, train_set.classes)
reman.create_new_result_folder(training_time)
reman.save_plot_rand_train_data(mean, std_deviation)
print()

# Declaring the network
net = FirstCNN()
criterion = nn.CrossEntropyLoss()
optimizer = optim.SGD(net.parameters(), lr=0.001, momentum=0.9)

# Training
for epoch in range(20):
    print(f'{yellow_text}{epoch + 1}. epoch of training!{reset_text}')
    running_loss = 0.0
    total_accuracy = 0.0
    total_batches = 0
    for i, data in enumerate(train_loader, 0):
        inputs, labels = data
        optimizer.zero_grad()

        outputs = net(inputs)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()

        running_loss += loss.item()

        current_accuracy = ta.calc_accuracy(outputs, labels)
        total_accuracy += current_accuracy
        total_batches += 1
        if i % 2000 == 1999:
            print(f'{yellow_text}[%d, %5d] loss: %.3f, current accuracy: %.3f'
                  % (epoch + 1, i + 1, running_loss / 2000, current_accuracy) +
                  reset_text)
            running_loss = 0.0
    epoch_accuracy = total_accuracy / total_batches
    print(f'{yellow_text}Epoch {epoch + 1} finished. Epoch accuracy: {epoch_accuracy:.3f}{reset_text}')
print("Train finished!\n")

# Saving the models in different formats
reman.save_net_pth(net)
reman.save_net_onnx(net, 150, 150, 3, 1)
print()

print(f'{yellow_text}Final accuracy after {epoch + 1} epochs: {epoch_accuracy:.3f}{reset_text}')

# Clearing datasets
# imman.wipe_datasets()





