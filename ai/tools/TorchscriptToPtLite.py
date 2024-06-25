import torch
from torch.utils.mobile_optimizer import optimize_for_mobile

torchscript_model = "./best8.torchscript"
export_model_name = "yolov8.pth"

model = torch.jit.load(torchscript_model)
optimized_model = optimize_for_mobile(model)
optimized_model._save_for_lite_interpreter(export_model_name)

print(f"mobile optimized model exported to {export_model_name}")