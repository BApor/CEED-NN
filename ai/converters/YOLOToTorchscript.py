from ultralytics import YOLO

# Load a model  # load an official model
model = YOLO("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/models/yolov3/best3.pt")  # load a custom trained model

# Export the model
model.export(format="torchscript", imgsz=640, task="detect")
