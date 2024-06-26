import pandas as pd
import matplotlib.pyplot as plt

# A MOBILENETEK PONTOSSAGI DIAGRAMJAINAK ELKESZITESEHEZ HASZNALTAM

# Load the CSV file
csv_path = '/Volumes/APORKA SSD/Allamvizsga/Eredmenyek/openvino/mobilenetssd/precission.csv'  # Replace with the actual path to your CSV file
data = pd.read_csv(csv_path, delimiter=';')
print(data)

# Extract data from the CSV file
epochs = data.iloc[:, 0]  # First column
precision = data.iloc[:, 1]  # Second column

# Plotting the data
plt.figure(figsize=(6, 6))
plt.plot(epochs, precision, marker='o', linestyle='-', color='b')
plt.title('MobileNetv2-SSD')
plt.xlabel('epochs')
plt.ylabel('mAP50')
plt.grid(True)

# Save the plot as an image file
plot_image_path = './ssd_mov2_plot.png'  # Replace with the desired path to save the plot
plt.savefig(plot_image_path)

# Show the plot (optional)
plt.show()
