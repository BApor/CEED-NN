from ultralytics import YOLO

model = YOLO("yolov3u.pt")

model.train(data="coco128.yaml")