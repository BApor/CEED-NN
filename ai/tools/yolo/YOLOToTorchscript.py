from ultralytics import YOLO

# A .PT FORMATUMU YOLO KONVERTALASA .TORCHSCRIPT-BE AZ ULTRALYTICS 
# KERETRENDSZER FELHASZNALASAVAL

model = YOLO("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/AI/models/yolov6/best.pt")  
model.export(format="torchscript", imgsz=640, task="detect")
