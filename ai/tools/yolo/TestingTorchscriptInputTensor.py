from ultralytics import YOLO
from ultralytics.utils.ops import non_max_suppression
import torch

# ITT TESZTELTEM A .TORCHSCRIPT FORMATUMU YOLOK MUKODESET,
# A KIMENETI TENZOR HELYES MERETET


torch_script_model = torch.jit.load("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/models/yolov8/yolov8.torchscript")
input_tensor = torch.rand(1, 3, 640, 640)
nms_result = None
with torch.inference_mode():
  result = torch_script_model(input_tensor)
  nms_result = non_max_suppression(result)
print(result)
print(f"Tensor size: {len(result)}x{len(result[0])}x{len(result[0][0])}" )

