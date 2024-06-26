import torchvision
import torch
import torch.nn as nn
import numpy as np
from torch.utils.mobile_optimizer import optimize_for_mobile
from PIL import Image
from torchvision import transforms

# ITT HASZNALTAM FEL A TERVEZESBEN EMLITETT CIKK [34] MEGOLDASAT 
# ES ES KONVERTALNI TUDTAM A .TORCHSCRIPTET .PTH-BA A PYTORCH MOBILE-HOZ 

def xywh2xyxy(x):
    """
    Convert bounding box coordinates from (x, y, width, height) format to (x1, y1, x2, y2) format where (x1, y1) is the
    top-left corner and (x2, y2) is the bottom-right corner.

    Args:
        x (np.ndarray) or (torch.Tensor): The input bounding box coordinates in (x, y, width, height) format.
    Returns:
        y (np.ndarray) or (torch.Tensor): The bounding box coordinates in (x1, y1, x2, y2) format.
    """
    y = x.clone() if isinstance(x, torch.Tensor) else np.copy(x)
    y[..., 0] = x[..., 0] - x[..., 2] / 2  # top left x
    y[..., 1] = x[..., 1] - x[..., 3] / 2  # top left y
    y[..., 2] = x[..., 0] + x[..., 2] / 2  # bottom right x
    y[..., 3] = x[..., 1] + x[..., 3] / 2  # bottom right y
    return y

class WrapperModel2(nn.Module):
  def __init__(self, model: torch.jit._script.RecursiveScriptModule):
    super().__init__()
    # model is the YOLO exported torchscript model
    self.model = model
  
  def forward(self, input_tensor: torch.Tensor, conf_thres: float=0.25):    
    multi_label=False
    max_time_img=0.05
    multi_label=False
    labels=()
    classes=None
    max_nms=30000
    max_wh=33280
    agnostic=False
    # conf_thres=0
    
    prediction = self.model(input_tensor)
    
    # Copy from /ultralytics/yolo/utils/ops.py non_max_suppression
    bs = prediction.shape[0]  # batch size
    nc = (prediction.shape[1] - 4)  # number of classes
    nm = prediction.shape[1] - nc - 4
    mi = 4 + nc  # mask start index
    xc = prediction[:, 4:mi].amax(1) > conf_thres
    
    # Settings
    # min_wh = 2  # (pixels) minimum box width and height
    
    time_limit = 0.5 + max_time_img * bs  # seconds to quit after
    redundant = True  # require redundant detections
    multi_label &= nc > 1  # multiple labels per box (adds 0.5ms/img)
    merge = False  # use merge-NMS
    
    # t = time.time()
    output = [torch.zeros((0, 6 + nm), device=prediction.device)] * bs
    for xi, x in enumerate(prediction):  # image index, image inference
        print(f"prediction: {xi}")
        # Apply constraints
        # x[((x[:, 2:4] < min_wh) | (x[:, 2:4] > max_wh)).any(1), 4] = 0  # width-height
        x = x.transpose(0, -1)[xc[xi]]  # confidence

        # Cat apriori labels if autolabelling
        # if labels and len(labels[xi]):
        #     lb = labels[xi]
        #     v = torch.zeros((len(lb), nc + nm + 5), device=x.device)
        #     v[:, :4] = lb[:, 1:5]  # box
        #     v[range(len(lb)), lb[:, 0].long() + 4] = 1.0  # cls
        #     x = torch.cat((x, v), 0)

        # If none remain process next image
        print(x.shape)
        if not x.shape[0]:
            continue

        # Detections matrix nx6 (xyxy, conf, cls)
        box, cls, mask = x.split((4, nc, nm), 1)
        box = xywh2xyxy(box)  # center_x, center_y, width, height) to (x1, y1, x2, y2)
        # if multi_label:
        #     i, j = (cls > conf_thres).nonzero(as_tuple=False).T
        #     x = torch.cat((box[i], x[i, 4 + j, None], j[:, None].float(), mask[i]), 1)
        # else:  # best class only
        #     conf, j = cls.max(1, keepdim=True)
        #     x = torch.cat((box, conf, j.float(), mask), 1)[conf.view(-1) > conf_thres]
            
        # * This is the "else" condition above
        conf, j = cls.max(1, keepdim=True)
        x = torch.cat((box, conf, j.float(), mask), 1)[conf.view(-1) > conf_thres]

        # Filter by class
        if classes is not None:
            x = x[(x[:, 5:26] == torch.tensor(classes, device=x.device)).any(1)]

        # Apply finite constraint
        # if not torch.isfinite(x).all():
        #     x = x[torch.isfinite(x).all(1)]

        # Check shape
        n = x.shape[0]  # number of boxes
        if not n:  # no boxes
            continue
        x = x[x[:, 4].argsort(descending=True)[:max_nms]]  # sort by confidence and remove excess boxes

        # Batched NMS
        c = x[:, 5:26] * (0 if agnostic else max_wh)  # classes
        print("Classes: ", c)
        boxes, scores = x[:, :4] + c, x[:, 4]  # boxes (offset by class), scores

        # forcefully return on the first prediction (which is OK for inference)
        return x , boxes, scores # actually only need x, while the others could be transformed in Android side from x

def image_to_tensor(image_path, target_size=(640, 640)):
    # Load the image
    image = Image.open(image_path).convert("RGB")
    
    # Define the transformation
    transform = transforms.Compose([
        transforms.Resize(target_size),  # Resize image to 640x640
        transforms.ToTensor(),  # Convert image to tensor
        ])
    
    # Apply the transformation
    tensor = transform(image)
    
    # Add a batch dimension
    tensor = tensor.unsqueeze(0)  # Shape: (1, 3, 640, 640)
    
    return tensor



torchscript_model = torch.jit.load("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/models/yolov6/best.torchscript")
w_model_2 = WrapperModel2(torchscript_model) 
input_tensor = image_to_tensor("/Volumes/APORKA SSD/Allamvizsga/Program/CEED-NN/ai/converters/IMG_4386.JPG")

with torch.inference_mode():
  w_p2, w_boxes2, w_scores2 = w_model_2(input_tensor)

selected2 = torchvision.ops.nms(w_boxes2, w_scores2, 0.45)
print(w_p2[selected2])

scripted_wrapped_model2 = torch.jit.script(w_model_2)
optimized_torchscript_b4_nms_model = optimize_for_mobile(scripted_wrapped_model2)
optimized_torchscript_b4_nms_model.save("yolov6_xbs.pth")