import openvino as ov
import torch
from torch.utils.mobile_optimizer import optimize_for_mobile
import cv2
import numpy as np

# ITT TESZTELTEM A MOBILENETTEKET, HOGY MUKODNEK-E AZ OPENVINO KERETRENSZERBEN

core = ov.Core()
ov_model = core.read_model("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/models/mobilenetv2/exported_model.xml")
compiled_model = core.compile_model(ov_model)
input_layer = compiled_model.input(0)
output_layer = compiled_model.output(0)

image = cv2.imread("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/tools/IMG_4386.JPG")
image = cv2.resize(image, (992, 736))
input_tensor = image.transpose(2, 0, 1)
input_tensor = np.expand_dims(input_tensor, axis=0) 
input_tensor = input_tensor.astype(np.float32) 

results = compiled_model([input_tensor])[output_layer]
print(results)

