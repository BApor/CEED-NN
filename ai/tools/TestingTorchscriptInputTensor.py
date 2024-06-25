from ultralytics import YOLO
from ultralytics.utils.ops import non_max_suppression
import torch

# just as what we did previously
torch_script_model = torch.jit.load("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/backup/best8.torchscript")
input_tensor = torch.rand(1, 3, 640, 640)
nms_result = None
with torch.inference_mode():
  result = torch_script_model(input_tensor)
  nms_result = non_max_suppression(result)
print(result)

# print("Non max supression")

# print(len(nms_result[0]))