from ultralytics import YOLO
import cv2

model = YOLO("C:\\Apor\\Annotator\\runs\\detect\\train\\weights\\best.pt")
model.val(data="C:\\Apor\\MTDK\\ai\\netframeworks\\ultralytics\\YOLO_Training\\config.yaml")

image = cv2.imread("C:\\Apor\\MTDK\\ai\\datasets\\ultralytics_dataset\\images\\train\\Hairy_Vetch_13\\IMG_4754.JPG")

results = model(image)[0]

for result in results.boxes.data.tolist():
    x1, y1, x2, y2, score, class_id = result

    if score > 0.5:
        cv2.rectangle(image, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 4)
        cv2.putText(image, results.names[int(class_id)].upper(), (int(x1), int(y1 - 10)),
                    cv2.FONT_HERSHEY_SIMPLEX, 1.3, (0, 255, 0), 3, cv2.LINE_AA)

cv2.imwrite("out.jpg",)